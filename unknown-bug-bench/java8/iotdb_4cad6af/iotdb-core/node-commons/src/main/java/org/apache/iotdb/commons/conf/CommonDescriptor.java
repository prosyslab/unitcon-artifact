/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.commons.conf;

import org.apache.iotdb.commons.enums.HandleSystemErrorStrategy;
import org.apache.iotdb.commons.utils.CommonDateTimeUtils;
import org.apache.iotdb.confignode.rpc.thrift.TGlobalConfig;

import java.io.File;
import java.util.Optional;
import java.util.Properties;

public class CommonDescriptor {

  private final CommonConfig config = new CommonConfig();

  private CommonDescriptor() {}

  public static CommonDescriptor getInstance() {
    return CommonDescriptorHolder.INSTANCE;
  }

  private static class CommonDescriptorHolder {

    private static final CommonDescriptor INSTANCE = new CommonDescriptor();

    private CommonDescriptorHolder() {
      // empty constructor
    }
  }

  public CommonConfig getConfig() {
    return config;
  }

  public String getConfDir() {
    // Check if a config-directory was specified first.
    String confString = System.getProperty(IoTDBConstant.IOTDB_CONF, null);
    // If it wasn't, check if a home directory was provided (This usually contains a config)
    if (confString == null) {
      confString = System.getProperty(IoTDBConstant.IOTDB_HOME, null);
      if (confString != null) {
        confString = confString + File.separatorChar + "conf";
      }
    }
    return confString;
  }

  public void initCommonConfigDir(String systemDir) {
    config.setUserFolder(systemDir + File.separator + "users");
    config.setRoleFolder(systemDir + File.separator + "roles");
    config.setProcedureWalFolder(systemDir + File.separator + "procedure");
  }

  public void loadCommonProps(Properties properties) {
    config.setAuthorizerProvider(
        properties.getProperty("authorizer_provider_class", config.getAuthorizerProvider()).trim());
    // if using org.apache.iotdb.db.auth.authorizer.OpenIdAuthorizer, openID_url is needed.
    config.setOpenIdProviderUrl(
        properties.getProperty("openID_url", config.getOpenIdProviderUrl()).trim());
    config.setEncryptDecryptProvider(
        properties
            .getProperty(
                "iotdb_server_encrypt_decrypt_provider", config.getEncryptDecryptProvider())
            .trim());

    config.setEncryptDecryptProviderParameter(
        properties.getProperty(
            "iotdb_server_encrypt_decrypt_provider_parameter",
            config.getEncryptDecryptProviderParameter()));

    String[] tierTTLStr = new String[config.getTierTTLInMs().length];
    for (int i = 0; i < tierTTLStr.length; ++i) {
      tierTTLStr[i] = String.valueOf(config.getTierTTLInMs()[i]);
    }
    tierTTLStr =
        properties
            .getProperty("default_ttl_in_ms", String.join(IoTDBConstant.TIER_SEPARATOR, tierTTLStr))
            .split(IoTDBConstant.TIER_SEPARATOR);
    long[] tierTTL = new long[tierTTLStr.length];
    for (int i = 0; i < tierTTL.length; ++i) {
      tierTTL[i] = Long.parseLong(tierTTLStr[i]);
      if (tierTTL[i] < 0) {
        tierTTL[i] = Long.MAX_VALUE;
      }
    }
    config.setTierTTLInMs(tierTTL);

    config.setSyncDir(properties.getProperty("dn_sync_dir", config.getSyncDir()).trim());

    config.setWalDirs(
        properties
            .getProperty("dn_wal_dirs", String.join(",", config.getWalDirs()))
            .trim()
            .split(","));

    config.setRpcThriftCompressionEnabled(
        Boolean.parseBoolean(
            properties
                .getProperty(
                    "cn_rpc_thrift_compression_enable",
                    String.valueOf(config.isRpcThriftCompressionEnabled()))
                .trim()));

    config.setConnectionTimeoutInMS(
        Integer.parseInt(
            properties
                .getProperty(
                    "cn_connection_timeout_ms", String.valueOf(config.getConnectionTimeoutInMS()))
                .trim()));

    config.setSelectorNumOfClientManager(
        Integer.parseInt(
            properties
                .getProperty(
                    "cn_selector_thread_nums_of_client_manager",
                    String.valueOf(config.getSelectorNumOfClientManager()))
                .trim()));

    config.setMaxClientNumForEachNode(
        Integer.parseInt(
            properties
                .getProperty(
                    "cn_max_client_count_for_each_node_in_client_manager",
                    String.valueOf(config.getMaxClientNumForEachNode()))
                .trim()));

    config.setConnectionTimeoutInMS(
        Integer.parseInt(
            properties
                .getProperty(
                    "dn_connection_timeout_ms", String.valueOf(config.getConnectionTimeoutInMS()))
                .trim()));

    config.setRpcThriftCompressionEnabled(
        Boolean.parseBoolean(
            properties
                .getProperty(
                    "dn_rpc_thrift_compression_enable",
                    String.valueOf(config.isRpcThriftCompressionEnabled()))
                .trim()));

    config.setSelectorNumOfClientManager(
        Integer.parseInt(
            properties
                .getProperty(
                    "dn_selector_thread_nums_of_client_manager",
                    String.valueOf(config.getSelectorNumOfClientManager()))
                .trim()));

    config.setMaxClientNumForEachNode(
        Integer.parseInt(
            properties
                .getProperty(
                    "dn_max_client_count_for_each_node_in_client_manager",
                    String.valueOf(config.getMaxClientNumForEachNode()))
                .trim()));

    config.setHandleSystemErrorStrategy(
        HandleSystemErrorStrategy.valueOf(
            properties
                .getProperty(
                    "handle_system_error", String.valueOf(config.getHandleSystemErrorStrategy()))
                .trim()));

    config.setDiskSpaceWarningThreshold(
        Double.parseDouble(
            properties
                .getProperty(
                    "disk_space_warning_threshold",
                    String.valueOf(config.getDiskSpaceWarningThreshold()))
                .trim()));

    config.setTimestampPrecision(
        properties.getProperty("timestamp_precision", config.getTimestampPrecision()).trim());

    config.setTimestampPrecisionCheckEnabled(
        Boolean.parseBoolean(
            properties.getProperty(
                "timestamp_precision_check_enabled",
                String.valueOf(config.isTimestampPrecisionCheckEnabled()))));

    config.setDatanodeTokenTimeoutMS(
        Integer.parseInt(
            properties.getProperty("datanode_token_timeout", String.valueOf(3 * 60 * 1000))));

    loadPipeProps(properties);

    config.setSchemaEngineMode(
        properties.getProperty("schema_engine_mode", String.valueOf(config.getSchemaEngineMode())));

    config.setLastCacheEnable(
        Boolean.parseBoolean(
            properties.getProperty(
                "enable_last_cache", Boolean.toString(config.isLastCacheEnable()))));
    if (config.getSchemaEngineMode().equals("Rocksdb_based")) {
      config.setLastCacheEnable(false);
    }

    config.setTagAttributeTotalSize(
        Integer.parseInt(
            properties.getProperty(
                "tag_attribute_total_size", String.valueOf(config.getTagAttributeTotalSize()))));

    config.setTimePartitionInterval(
        Long.parseLong(
            properties.getProperty(
                "time_partition_interval", String.valueOf(config.getTimePartitionInterval()))));
    config.setDatabaseLimitThreshold(
        Integer.parseInt(
            properties.getProperty(
                "database_limit_threshold", String.valueOf(config.getDatabaseLimitThreshold()))));
    config.setSeriesLimitThreshold(
        Long.parseLong(
            properties.getProperty(
                "cluster_timeseries_limit_threshold",
                String.valueOf(config.getSeriesLimitThreshold()))));
    config.setDeviceLimitThreshold(
        Long.parseLong(
            properties.getProperty(
                "cluster_device_limit_threshold",
                String.valueOf(config.getDeviceLimitThreshold()))));
  }

  private void loadPipeProps(Properties properties) {
    config.setPipeHardlinkBaseDirName(
        properties.getProperty("pipe_hardlink_base_dir_name", config.getPipeHardlinkBaseDirName()));
    config.setPipeHardlinkTsFileDirName(
        properties.getProperty(
            "pipe_hardlink_tsfile_dir_name", config.getPipeHardlinkTsFileDirName()));
    config.setPipeHardlinkWALDirName(
        properties.getProperty("pipe_hardlink_wal_dir_name", config.getPipeHardlinkWALDirName()));
    config.setPipeHardLinkWALEnabled(
        Boolean.parseBoolean(
            properties.getProperty(
                "pipe_hardlink_wal_enabled",
                Boolean.toString(config.getPipeHardLinkWALEnabled()))));

    config.setPipeDataStructureTabletRowSize(
        Integer.parseInt(
            properties.getProperty(
                "pipe_data_structure_tablet_row_size",
                String.valueOf(config.getPipeDataStructureTabletRowSize()))));

    config.setPipeSubtaskExecutorMaxThreadNum(
        Integer.parseInt(
            properties.getProperty(
                "pipe_subtask_executor_max_thread_num",
                Integer.toString(config.getPipeSubtaskExecutorMaxThreadNum()))));
    if (config.getPipeSubtaskExecutorMaxThreadNum() <= 0) {
      config.setPipeSubtaskExecutorMaxThreadNum(5);
    }
    config.setPipeSubtaskExecutorBasicCheckPointIntervalByConsumedEventCount(
        Integer.parseInt(
            properties.getProperty(
                "pipe_subtask_executor_basic_check_point_interval_by_consumed_event_count",
                String.valueOf(
                    config.getPipeSubtaskExecutorBasicCheckPointIntervalByConsumedEventCount()))));
    config.setPipeSubtaskExecutorBasicCheckPointIntervalByTimeDuration(
        Long.parseLong(
            properties.getProperty(
                "pipe_subtask_executor_basic_check_point_interval_by_time_duration",
                String.valueOf(
                    config.getPipeSubtaskExecutorBasicCheckPointIntervalByTimeDuration()))));
    config.setPipeSubtaskExecutorPendingQueueMaxBlockingTimeMs(
        Long.parseLong(
            properties.getProperty(
                "pipe_subtask_executor_pending_queue_max_blocking_time_ms",
                String.valueOf(config.getPipeSubtaskExecutorPendingQueueMaxBlockingTimeMs()))));
    config.setPipeSubtaskExecutorCronHeartbeatEventIntervalSeconds(
        Long.parseLong(
            properties.getProperty(
                "pipe_subtask_executor_cron_heartbeat_event_interval_seconds",
                String.valueOf(config.getPipeSubtaskExecutorCronHeartbeatEventIntervalSeconds()))));

    config.setPipeExtractorAssignerDisruptorRingBufferSize(
        Integer.parseInt(
            Optional.ofNullable(
                    properties.getProperty("pipe_source_assigner_disruptor_ring_buffer_size"))
                .orElse(
                    properties.getProperty(
                        "pipe_extractor_assigner_disruptor_ring_buffer_size",
                        String.valueOf(
                            config.getPipeExtractorAssignerDisruptorRingBufferSize())))));
    config.setPipeExtractorAssignerDisruptorRingBufferEntrySizeInBytes( // 1MB
        Integer.parseInt(
            Optional.ofNullable(
                    properties.getProperty(
                        "pipe_source_assigner_disruptor_ring_buffer_entry_size_in_bytes"))
                .orElse(
                    properties.getProperty(
                        "pipe_extractor_assigner_disruptor_ring_buffer_entry_size_in_bytes",
                        String.valueOf(
                            config
                                .getPipeExtractorAssignerDisruptorRingBufferEntrySizeInBytes())))));
    config.setPipeExtractorMatcherCacheSize(
        Integer.parseInt(
            Optional.ofNullable(properties.getProperty("pipe_source_matcher_cache_size"))
                .orElse(
                    properties.getProperty(
                        "pipe_extractor_matcher_cache_size",
                        String.valueOf(config.getPipeExtractorMatcherCacheSize())))));

    config.setPipeConnectorHandshakeTimeoutMs(
        Long.parseLong(
            Optional.ofNullable(properties.getProperty("pipe_sink_handshake_timeout_ms"))
                .orElse(
                    properties.getProperty(
                        "pipe_connector_handshake_timeout_ms",
                        String.valueOf(config.getPipeConnectorHandshakeTimeoutMs())))));
    config.setPipeConnectorTransferTimeoutMs(
        Long.parseLong(
            Optional.ofNullable(properties.getProperty("pipe_sink_timeout_ms"))
                .orElse(
                    properties.getProperty(
                        "pipe_connector_timeout_ms",
                        String.valueOf(config.getPipeConnectorTransferTimeoutMs())))));
    config.setPipeConnectorReadFileBufferSize(
        Integer.parseInt(
            Optional.ofNullable(properties.getProperty("pipe_sink_read_file_buffer_size"))
                .orElse(
                    properties.getProperty(
                        "pipe_connector_read_file_buffer_size",
                        String.valueOf(config.getPipeConnectorReadFileBufferSize())))));
    config.setPipeConnectorRetryIntervalMs(
        Long.parseLong(
            Optional.ofNullable(properties.getProperty("pipe_sink_retry_interval_ms"))
                .orElse(
                    properties.getProperty(
                        "pipe_connector_retry_interval_ms",
                        String.valueOf(config.getPipeConnectorRetryIntervalMs())))));
    config.setPipeConnectorPendingQueueSize(
        Integer.parseInt(
            Optional.ofNullable(properties.getProperty("pipe_sink_pending_queue_size"))
                .orElse(
                    properties.getProperty(
                        "pipe_connector_pending_queue_size",
                        String.valueOf(config.getPipeConnectorPendingQueueSize())))));
    config.setPipeConnectorRPCThriftCompressionEnabled(
        Boolean.parseBoolean(
            Optional.ofNullable(properties.getProperty("pipe_sink_rpc_thrift_compression_enabled"))
                .orElse(
                    properties.getProperty(
                        "pipe_connector_rpc_thrift_compression_enabled",
                        String.valueOf(config.isPipeConnectorRPCThriftCompressionEnabled())))));

    config.setPipeAsyncConnectorSelectorNumber(
        Integer.parseInt(
            Optional.ofNullable(properties.getProperty("pipe_sink_selector_number"))
                .orElse(
                    properties.getProperty(
                        "pipe_async_connector_selector_number",
                        String.valueOf(config.getPipeAsyncConnectorSelectorNumber())))));
    config.setPipeAsyncConnectorMaxClientNumber(
        Integer.parseInt(
            Optional.ofNullable(properties.getProperty("pipe_sink_max_client_number"))
                .orElse(
                    properties.getProperty(
                        "pipe_async_connector_max_client_number",
                        String.valueOf(config.getPipeAsyncConnectorMaxClientNumber())))));

    config.setSeperatedPipeHeartbeatEnabled(
        Boolean.parseBoolean(
            properties.getProperty(
                "pipe_heartbeat_seperated_mode_enabled",
                String.valueOf(config.isSeperatedPipeHeartbeatEnabled()))));
    config.setPipeHeartbeatIntervalSecondsForCollectingPipeMeta(
        Integer.parseInt(
            properties.getProperty(
                "pipe_heartbeat_interval_seconds_for_collecting_pipe_meta",
                String.valueOf(config.getPipeHeartbeatIntervalSecondsForCollectingPipeMeta()))));
    config.setPipeMetaSyncerInitialSyncDelayMinutes(
        Long.parseLong(
            properties.getProperty(
                "pipe_meta_syncer_initial_sync_delay_minutes",
                String.valueOf(config.getPipeMetaSyncerInitialSyncDelayMinutes()))));
    config.setPipeMetaSyncerSyncIntervalMinutes(
        Long.parseLong(
            properties.getProperty(
                "pipe_meta_syncer_sync_interval_minutes",
                String.valueOf(config.getPipeMetaSyncerSyncIntervalMinutes()))));
    config.setPipeMetaSyncerAutoRestartPipeCheckIntervalRound(
        Long.parseLong(
            properties.getProperty(
                "pipe_meta_syncer_auto_restart_pipe_check_interval_round",
                String.valueOf(config.getPipeMetaSyncerAutoRestartPipeCheckIntervalRound()))));
    config.setPipeAutoRestartEnabled(
        Boolean.parseBoolean(
            properties.getProperty(
                "pipe_auto_restart_enabled", String.valueOf(config.getPipeAutoRestartEnabled()))));

    config.setPipeAirGapReceiverEnabled(
        Boolean.parseBoolean(
            properties.getProperty(
                "pipe_air_gap_receiver_enabled",
                Boolean.toString(config.getPipeAirGapReceiverEnabled()))));
    config.setPipeAirGapReceiverPort(
        Integer.parseInt(
            properties.getProperty(
                "pipe_air_gap_receiver_port",
                Integer.toString(config.getPipeAirGapReceiverPort()))));

    config.setPipeMaxAllowedPendingTsFileEpochPerDataRegion(
        Integer.parseInt(
            properties.getProperty(
                "pipe_max_allowed_pending_tsfile_epoch_per_data_region",
                String.valueOf(config.getPipeMaxAllowedPendingTsFileEpochPerDataRegion()))));
    config.setPipeMaxAllowedPinnedMemTableCount(
        Integer.parseInt(
            properties.getProperty(
                "pipe_max_allowed_pinned_memtable_count",
                String.valueOf(config.getPipeMaxAllowedPinnedMemTableCount()))));

    config.setPipeMemoryManagementEnabled(
        Boolean.parseBoolean(
            properties.getProperty(
                "pipe_memory_management_enabled",
                String.valueOf(config.getPipeMemoryManagementEnabled()))));
    config.setPipeMemoryAllocateMaxRetries(
        Integer.parseInt(
            properties.getProperty(
                "pipe_memory_allocate_max_retries",
                String.valueOf(config.getPipeMemoryAllocateMaxRetries()))));
    config.setPipeMemoryAllocateRetryIntervalInMs(
        Long.parseLong(
            properties.getProperty(
                "pipe_memory_allocate_retry_interval_in_ms",
                String.valueOf(config.getPipeMemoryAllocateRetryIntervalInMs()))));
    config.setPipeMemoryAllocateMinSizeInBytes(
        Long.parseLong(
            properties.getProperty(
                "pipe_memory_allocate_min_size_in_bytes",
                String.valueOf(config.getPipeMemoryAllocateMinSizeInBytes()))));
    config.setPipeMemoryAllocateForTsFileSequenceReaderInBytes(
        Long.parseLong(
            properties.getProperty(
                "pipe_memory_allocate_for_tsfile_sequence_reader_in_bytes",
                String.valueOf(config.getPipeMemoryAllocateForTsFileSequenceReaderInBytes()))));
    config.setPipeMemoryExpanderIntervalSeconds(
        Long.parseLong(
            properties.getProperty(
                "pipe_memory_expander_interval_seconds",
                String.valueOf(config.getPipeMemoryExpanderIntervalSeconds()))));
    config.setPipeLeaderCacheMemoryUsagePercentage(
        Float.parseFloat(
            properties.getProperty(
                "pipe_leader_cache_memory_usage_percentage",
                String.valueOf(config.getPipeLeaderCacheMemoryUsagePercentage()))));
  }

  public void loadGlobalConfig(TGlobalConfig globalConfig) {
    config.setTimestampPrecision(globalConfig.timestampPrecision);
    config.setTimePartitionInterval(
        CommonDateTimeUtils.convertMilliTimeWithPrecision(
            globalConfig.timePartitionInterval, config.getTimestampPrecision()));
    config.setSchemaEngineMode(globalConfig.schemaEngineMode);
    config.setTagAttributeTotalSize(globalConfig.tagAttributeTotalSize);
    config.setDiskSpaceWarningThreshold(globalConfig.getDiskSpaceWarningThreshold());
  }
}
