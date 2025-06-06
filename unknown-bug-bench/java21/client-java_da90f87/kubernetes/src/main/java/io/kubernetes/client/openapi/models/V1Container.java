/*
Copyright 2023 The Kubernetes Authors.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package io.kubernetes.client.openapi.models;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.kubernetes.client.openapi.models.V1ContainerPort;
import io.kubernetes.client.openapi.models.V1ContainerResizePolicy;
import io.kubernetes.client.openapi.models.V1EnvFromSource;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1Lifecycle;
import io.kubernetes.client.openapi.models.V1Probe;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;
import io.kubernetes.client.openapi.models.V1SecurityContext;
import io.kubernetes.client.openapi.models.V1VolumeDevice;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A single application container that you want to run within a pod.
 */
@ApiModel(description = "A single application container that you want to run within a pod.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2023-12-01T19:05:21.333462Z[Etc/UTC]")
public class V1Container {
  public static final String SERIALIZED_NAME_ARGS = "args";
  @SerializedName(SERIALIZED_NAME_ARGS)
  private List<String> args = null;

  public static final String SERIALIZED_NAME_COMMAND = "command";
  @SerializedName(SERIALIZED_NAME_COMMAND)
  private List<String> command = null;

  public static final String SERIALIZED_NAME_ENV = "env";
  @SerializedName(SERIALIZED_NAME_ENV)
  private List<V1EnvVar> env = null;

  public static final String SERIALIZED_NAME_ENV_FROM = "envFrom";
  @SerializedName(SERIALIZED_NAME_ENV_FROM)
  private List<V1EnvFromSource> envFrom = null;

  public static final String SERIALIZED_NAME_IMAGE = "image";
  @SerializedName(SERIALIZED_NAME_IMAGE)
  private String image;

  public static final String SERIALIZED_NAME_IMAGE_PULL_POLICY = "imagePullPolicy";
  @SerializedName(SERIALIZED_NAME_IMAGE_PULL_POLICY)
  private String imagePullPolicy;

  public static final String SERIALIZED_NAME_LIFECYCLE = "lifecycle";
  @SerializedName(SERIALIZED_NAME_LIFECYCLE)
  private V1Lifecycle lifecycle;

  public static final String SERIALIZED_NAME_LIVENESS_PROBE = "livenessProbe";
  @SerializedName(SERIALIZED_NAME_LIVENESS_PROBE)
  private V1Probe livenessProbe;

  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  private String name;

  public static final String SERIALIZED_NAME_PORTS = "ports";
  @SerializedName(SERIALIZED_NAME_PORTS)
  private List<V1ContainerPort> ports = null;

  public static final String SERIALIZED_NAME_READINESS_PROBE = "readinessProbe";
  @SerializedName(SERIALIZED_NAME_READINESS_PROBE)
  private V1Probe readinessProbe;

  public static final String SERIALIZED_NAME_RESIZE_POLICY = "resizePolicy";
  @SerializedName(SERIALIZED_NAME_RESIZE_POLICY)
  private List<V1ContainerResizePolicy> resizePolicy = null;

  public static final String SERIALIZED_NAME_RESOURCES = "resources";
  @SerializedName(SERIALIZED_NAME_RESOURCES)
  private V1ResourceRequirements resources;

  public static final String SERIALIZED_NAME_RESTART_POLICY = "restartPolicy";
  @SerializedName(SERIALIZED_NAME_RESTART_POLICY)
  private String restartPolicy;

  public static final String SERIALIZED_NAME_SECURITY_CONTEXT = "securityContext";
  @SerializedName(SERIALIZED_NAME_SECURITY_CONTEXT)
  private V1SecurityContext securityContext;

  public static final String SERIALIZED_NAME_STARTUP_PROBE = "startupProbe";
  @SerializedName(SERIALIZED_NAME_STARTUP_PROBE)
  private V1Probe startupProbe;

  public static final String SERIALIZED_NAME_STDIN = "stdin";
  @SerializedName(SERIALIZED_NAME_STDIN)
  private Boolean stdin;

  public static final String SERIALIZED_NAME_STDIN_ONCE = "stdinOnce";
  @SerializedName(SERIALIZED_NAME_STDIN_ONCE)
  private Boolean stdinOnce;

  public static final String SERIALIZED_NAME_TERMINATION_MESSAGE_PATH = "terminationMessagePath";
  @SerializedName(SERIALIZED_NAME_TERMINATION_MESSAGE_PATH)
  private String terminationMessagePath;

  public static final String SERIALIZED_NAME_TERMINATION_MESSAGE_POLICY = "terminationMessagePolicy";
  @SerializedName(SERIALIZED_NAME_TERMINATION_MESSAGE_POLICY)
  private String terminationMessagePolicy;

  public static final String SERIALIZED_NAME_TTY = "tty";
  @SerializedName(SERIALIZED_NAME_TTY)
  private Boolean tty;

  public static final String SERIALIZED_NAME_VOLUME_DEVICES = "volumeDevices";
  @SerializedName(SERIALIZED_NAME_VOLUME_DEVICES)
  private List<V1VolumeDevice> volumeDevices = null;

  public static final String SERIALIZED_NAME_VOLUME_MOUNTS = "volumeMounts";
  @SerializedName(SERIALIZED_NAME_VOLUME_MOUNTS)
  private List<V1VolumeMount> volumeMounts = null;

  public static final String SERIALIZED_NAME_WORKING_DIR = "workingDir";
  @SerializedName(SERIALIZED_NAME_WORKING_DIR)
  private String workingDir;


  public V1Container args(List<String> args) {

    this.args = args;
    return this;
  }

  public V1Container addArgsItem(String argsItem) {
    if (this.args == null) {
      this.args = new ArrayList<>();
    }
    this.args.add(argsItem);
    return this;
  }

   /**
   * Arguments to the entrypoint. The container image&#39;s CMD is used if this is not provided. Variable references $(VAR_NAME) are expanded using the container&#39;s environment. If a variable cannot be resolved, the reference in the input string will be unchanged. Double $$ are reduced to a single $, which allows for escaping the $(VAR_NAME) syntax: i.e. \&quot;$$(VAR_NAME)\&quot; will produce the string literal \&quot;$(VAR_NAME)\&quot;. Escaped references will never be expanded, regardless of whether the variable exists or not. Cannot be updated. More info: https://kubernetes.io/docs/tasks/inject-data-application/define-command-argument-container/#running-a-command-in-a-shell
   * @return args
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Arguments to the entrypoint. The container image's CMD is used if this is not provided. Variable references $(VAR_NAME) are expanded using the container's environment. If a variable cannot be resolved, the reference in the input string will be unchanged. Double $$ are reduced to a single $, which allows for escaping the $(VAR_NAME) syntax: i.e. \"$$(VAR_NAME)\" will produce the string literal \"$(VAR_NAME)\". Escaped references will never be expanded, regardless of whether the variable exists or not. Cannot be updated. More info: https://kubernetes.io/docs/tasks/inject-data-application/define-command-argument-container/#running-a-command-in-a-shell")

  public List<String> getArgs() {
    return args;
  }


  public void setArgs(List<String> args) {
    this.args = args;
  }


  public V1Container command(List<String> command) {

    this.command = command;
    return this;
  }

  public V1Container addCommandItem(String commandItem) {
    if (this.command == null) {
      this.command = new ArrayList<>();
    }
    this.command.add(commandItem);
    return this;
  }

   /**
   * Entrypoint array. Not executed within a shell. The container image&#39;s ENTRYPOINT is used if this is not provided. Variable references $(VAR_NAME) are expanded using the container&#39;s environment. If a variable cannot be resolved, the reference in the input string will be unchanged. Double $$ are reduced to a single $, which allows for escaping the $(VAR_NAME) syntax: i.e. \&quot;$$(VAR_NAME)\&quot; will produce the string literal \&quot;$(VAR_NAME)\&quot;. Escaped references will never be expanded, regardless of whether the variable exists or not. Cannot be updated. More info: https://kubernetes.io/docs/tasks/inject-data-application/define-command-argument-container/#running-a-command-in-a-shell
   * @return command
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Entrypoint array. Not executed within a shell. The container image's ENTRYPOINT is used if this is not provided. Variable references $(VAR_NAME) are expanded using the container's environment. If a variable cannot be resolved, the reference in the input string will be unchanged. Double $$ are reduced to a single $, which allows for escaping the $(VAR_NAME) syntax: i.e. \"$$(VAR_NAME)\" will produce the string literal \"$(VAR_NAME)\". Escaped references will never be expanded, regardless of whether the variable exists or not. Cannot be updated. More info: https://kubernetes.io/docs/tasks/inject-data-application/define-command-argument-container/#running-a-command-in-a-shell")

  public List<String> getCommand() {
    return command;
  }


  public void setCommand(List<String> command) {
    this.command = command;
  }


  public V1Container env(List<V1EnvVar> env) {

    this.env = env;
    return this;
  }

  public V1Container addEnvItem(V1EnvVar envItem) {
    if (this.env == null) {
      this.env = new ArrayList<>();
    }
    this.env.add(envItem);
    return this;
  }

   /**
   * List of environment variables to set in the container. Cannot be updated.
   * @return env
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "List of environment variables to set in the container. Cannot be updated.")

  public List<V1EnvVar> getEnv() {
    return env;
  }


  public void setEnv(List<V1EnvVar> env) {
    this.env = env;
  }


  public V1Container envFrom(List<V1EnvFromSource> envFrom) {

    this.envFrom = envFrom;
    return this;
  }

  public V1Container addEnvFromItem(V1EnvFromSource envFromItem) {
    if (this.envFrom == null) {
      this.envFrom = new ArrayList<>();
    }
    this.envFrom.add(envFromItem);
    return this;
  }

   /**
   * List of sources to populate environment variables in the container. The keys defined within a source must be a C_IDENTIFIER. All invalid keys will be reported as an event when the container is starting. When a key exists in multiple sources, the value associated with the last source will take precedence. Values defined by an Env with a duplicate key will take precedence. Cannot be updated.
   * @return envFrom
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "List of sources to populate environment variables in the container. The keys defined within a source must be a C_IDENTIFIER. All invalid keys will be reported as an event when the container is starting. When a key exists in multiple sources, the value associated with the last source will take precedence. Values defined by an Env with a duplicate key will take precedence. Cannot be updated.")

  public List<V1EnvFromSource> getEnvFrom() {
    return envFrom;
  }


  public void setEnvFrom(List<V1EnvFromSource> envFrom) {
    this.envFrom = envFrom;
  }


  public V1Container image(String image) {

    this.image = image;
    return this;
  }

   /**
   * Container image name. More info: https://kubernetes.io/docs/concepts/containers/images This field is optional to allow higher level config management to default or override container images in workload controllers like Deployments and StatefulSets.
   * @return image
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Container image name. More info: https://kubernetes.io/docs/concepts/containers/images This field is optional to allow higher level config management to default or override container images in workload controllers like Deployments and StatefulSets.")

  public String getImage() {
    return image;
  }


  public void setImage(String image) {
    this.image = image;
  }


  public V1Container imagePullPolicy(String imagePullPolicy) {

    this.imagePullPolicy = imagePullPolicy;
    return this;
  }

   /**
   * Image pull policy. One of Always, Never, IfNotPresent. Defaults to Always if :latest tag is specified, or IfNotPresent otherwise. Cannot be updated. More info: https://kubernetes.io/docs/concepts/containers/images#updating-images
   * @return imagePullPolicy
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Image pull policy. One of Always, Never, IfNotPresent. Defaults to Always if :latest tag is specified, or IfNotPresent otherwise. Cannot be updated. More info: https://kubernetes.io/docs/concepts/containers/images#updating-images")

  public String getImagePullPolicy() {
    return imagePullPolicy;
  }


  public void setImagePullPolicy(String imagePullPolicy) {
    this.imagePullPolicy = imagePullPolicy;
  }


  public V1Container lifecycle(V1Lifecycle lifecycle) {

    this.lifecycle = lifecycle;
    return this;
  }

   /**
   * Get lifecycle
   * @return lifecycle
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public V1Lifecycle getLifecycle() {
    return lifecycle;
  }


  public void setLifecycle(V1Lifecycle lifecycle) {
    this.lifecycle = lifecycle;
  }


  public V1Container livenessProbe(V1Probe livenessProbe) {

    this.livenessProbe = livenessProbe;
    return this;
  }

   /**
   * Get livenessProbe
   * @return livenessProbe
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public V1Probe getLivenessProbe() {
    return livenessProbe;
  }


  public void setLivenessProbe(V1Probe livenessProbe) {
    this.livenessProbe = livenessProbe;
  }


  public V1Container name(String name) {

    this.name = name;
    return this;
  }

   /**
   * Name of the container specified as a DNS_LABEL. Each container in a pod must have a unique name (DNS_LABEL). Cannot be updated.
   * @return name
  **/
  @ApiModelProperty(required = true, value = "Name of the container specified as a DNS_LABEL. Each container in a pod must have a unique name (DNS_LABEL). Cannot be updated.")

  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


  public V1Container ports(List<V1ContainerPort> ports) {

    this.ports = ports;
    return this;
  }

  public V1Container addPortsItem(V1ContainerPort portsItem) {
    if (this.ports == null) {
      this.ports = new ArrayList<>();
    }
    this.ports.add(portsItem);
    return this;
  }

   /**
   * List of ports to expose from the container. Not specifying a port here DOES NOT prevent that port from being exposed. Any port which is listening on the default \&quot;0.0.0.0\&quot; address inside a container will be accessible from the network. Modifying this array with strategic merge patch may corrupt the data. For more information See https://github.com/kubernetes/kubernetes/issues/108255. Cannot be updated.
   * @return ports
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "List of ports to expose from the container. Not specifying a port here DOES NOT prevent that port from being exposed. Any port which is listening on the default \"0.0.0.0\" address inside a container will be accessible from the network. Modifying this array with strategic merge patch may corrupt the data. For more information See https://github.com/kubernetes/kubernetes/issues/108255. Cannot be updated.")

  public List<V1ContainerPort> getPorts() {
    return ports;
  }


  public void setPorts(List<V1ContainerPort> ports) {
    this.ports = ports;
  }


  public V1Container readinessProbe(V1Probe readinessProbe) {

    this.readinessProbe = readinessProbe;
    return this;
  }

   /**
   * Get readinessProbe
   * @return readinessProbe
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public V1Probe getReadinessProbe() {
    return readinessProbe;
  }


  public void setReadinessProbe(V1Probe readinessProbe) {
    this.readinessProbe = readinessProbe;
  }


  public V1Container resizePolicy(List<V1ContainerResizePolicy> resizePolicy) {

    this.resizePolicy = resizePolicy;
    return this;
  }

  public V1Container addResizePolicyItem(V1ContainerResizePolicy resizePolicyItem) {
    if (this.resizePolicy == null) {
      this.resizePolicy = new ArrayList<>();
    }
    this.resizePolicy.add(resizePolicyItem);
    return this;
  }

   /**
   * Resources resize policy for the container.
   * @return resizePolicy
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Resources resize policy for the container.")

  public List<V1ContainerResizePolicy> getResizePolicy() {
    return resizePolicy;
  }


  public void setResizePolicy(List<V1ContainerResizePolicy> resizePolicy) {
    this.resizePolicy = resizePolicy;
  }


  public V1Container resources(V1ResourceRequirements resources) {

    this.resources = resources;
    return this;
  }

   /**
   * Get resources
   * @return resources
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public V1ResourceRequirements getResources() {
    return resources;
  }


  public void setResources(V1ResourceRequirements resources) {
    this.resources = resources;
  }


  public V1Container restartPolicy(String restartPolicy) {

    this.restartPolicy = restartPolicy;
    return this;
  }

   /**
   * RestartPolicy defines the restart behavior of individual containers in a pod. This field may only be set for init containers, and the only allowed value is \&quot;Always\&quot;. For non-init containers or when this field is not specified, the restart behavior is defined by the Pod&#39;s restart policy and the container type. Setting the RestartPolicy as \&quot;Always\&quot; for the init container will have the following effect: this init container will be continually restarted on exit until all regular containers have terminated. Once all regular containers have completed, all init containers with restartPolicy \&quot;Always\&quot; will be shut down. This lifecycle differs from normal init containers and is often referred to as a \&quot;sidecar\&quot; container. Although this init container still starts in the init container sequence, it does not wait for the container to complete before proceeding to the next init container. Instead, the next init container starts immediately after this init container is started, or after any startupProbe has successfully completed.
   * @return restartPolicy
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "RestartPolicy defines the restart behavior of individual containers in a pod. This field may only be set for init containers, and the only allowed value is \"Always\". For non-init containers or when this field is not specified, the restart behavior is defined by the Pod's restart policy and the container type. Setting the RestartPolicy as \"Always\" for the init container will have the following effect: this init container will be continually restarted on exit until all regular containers have terminated. Once all regular containers have completed, all init containers with restartPolicy \"Always\" will be shut down. This lifecycle differs from normal init containers and is often referred to as a \"sidecar\" container. Although this init container still starts in the init container sequence, it does not wait for the container to complete before proceeding to the next init container. Instead, the next init container starts immediately after this init container is started, or after any startupProbe has successfully completed.")

  public String getRestartPolicy() {
    return restartPolicy;
  }


  public void setRestartPolicy(String restartPolicy) {
    this.restartPolicy = restartPolicy;
  }


  public V1Container securityContext(V1SecurityContext securityContext) {

    this.securityContext = securityContext;
    return this;
  }

   /**
   * Get securityContext
   * @return securityContext
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public V1SecurityContext getSecurityContext() {
    return securityContext;
  }


  public void setSecurityContext(V1SecurityContext securityContext) {
    this.securityContext = securityContext;
  }


  public V1Container startupProbe(V1Probe startupProbe) {

    this.startupProbe = startupProbe;
    return this;
  }

   /**
   * Get startupProbe
   * @return startupProbe
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public V1Probe getStartupProbe() {
    return startupProbe;
  }


  public void setStartupProbe(V1Probe startupProbe) {
    this.startupProbe = startupProbe;
  }


  public V1Container stdin(Boolean stdin) {

    this.stdin = stdin;
    return this;
  }

   /**
   * Whether this container should allocate a buffer for stdin in the container runtime. If this is not set, reads from stdin in the container will always result in EOF. Default is false.
   * @return stdin
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Whether this container should allocate a buffer for stdin in the container runtime. If this is not set, reads from stdin in the container will always result in EOF. Default is false.")

  public Boolean getStdin() {
    return stdin;
  }


  public void setStdin(Boolean stdin) {
    this.stdin = stdin;
  }


  public V1Container stdinOnce(Boolean stdinOnce) {

    this.stdinOnce = stdinOnce;
    return this;
  }

   /**
   * Whether the container runtime should close the stdin channel after it has been opened by a single attach. When stdin is true the stdin stream will remain open across multiple attach sessions. If stdinOnce is set to true, stdin is opened on container start, is empty until the first client attaches to stdin, and then remains open and accepts data until the client disconnects, at which time stdin is closed and remains closed until the container is restarted. If this flag is false, a container processes that reads from stdin will never receive an EOF. Default is false
   * @return stdinOnce
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Whether the container runtime should close the stdin channel after it has been opened by a single attach. When stdin is true the stdin stream will remain open across multiple attach sessions. If stdinOnce is set to true, stdin is opened on container start, is empty until the first client attaches to stdin, and then remains open and accepts data until the client disconnects, at which time stdin is closed and remains closed until the container is restarted. If this flag is false, a container processes that reads from stdin will never receive an EOF. Default is false")

  public Boolean getStdinOnce() {
    return stdinOnce;
  }


  public void setStdinOnce(Boolean stdinOnce) {
    this.stdinOnce = stdinOnce;
  }


  public V1Container terminationMessagePath(String terminationMessagePath) {

    this.terminationMessagePath = terminationMessagePath;
    return this;
  }

   /**
   * Optional: Path at which the file to which the container&#39;s termination message will be written is mounted into the container&#39;s filesystem. Message written is intended to be brief final status, such as an assertion failure message. Will be truncated by the node if greater than 4096 bytes. The total message length across all containers will be limited to 12kb. Defaults to /dev/termination-log. Cannot be updated.
   * @return terminationMessagePath
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Optional: Path at which the file to which the container's termination message will be written is mounted into the container's filesystem. Message written is intended to be brief final status, such as an assertion failure message. Will be truncated by the node if greater than 4096 bytes. The total message length across all containers will be limited to 12kb. Defaults to /dev/termination-log. Cannot be updated.")

  public String getTerminationMessagePath() {
    return terminationMessagePath;
  }


  public void setTerminationMessagePath(String terminationMessagePath) {
    this.terminationMessagePath = terminationMessagePath;
  }


  public V1Container terminationMessagePolicy(String terminationMessagePolicy) {

    this.terminationMessagePolicy = terminationMessagePolicy;
    return this;
  }

   /**
   * Indicate how the termination message should be populated. File will use the contents of terminationMessagePath to populate the container status message on both success and failure. FallbackToLogsOnError will use the last chunk of container log output if the termination message file is empty and the container exited with an error. The log output is limited to 2048 bytes or 80 lines, whichever is smaller. Defaults to File. Cannot be updated.
   * @return terminationMessagePolicy
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Indicate how the termination message should be populated. File will use the contents of terminationMessagePath to populate the container status message on both success and failure. FallbackToLogsOnError will use the last chunk of container log output if the termination message file is empty and the container exited with an error. The log output is limited to 2048 bytes or 80 lines, whichever is smaller. Defaults to File. Cannot be updated.")

  public String getTerminationMessagePolicy() {
    return terminationMessagePolicy;
  }


  public void setTerminationMessagePolicy(String terminationMessagePolicy) {
    this.terminationMessagePolicy = terminationMessagePolicy;
  }


  public V1Container tty(Boolean tty) {

    this.tty = tty;
    return this;
  }

   /**
   * Whether this container should allocate a TTY for itself, also requires &#39;stdin&#39; to be true. Default is false.
   * @return tty
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Whether this container should allocate a TTY for itself, also requires 'stdin' to be true. Default is false.")

  public Boolean getTty() {
    return tty;
  }


  public void setTty(Boolean tty) {
    this.tty = tty;
  }


  public V1Container volumeDevices(List<V1VolumeDevice> volumeDevices) {

    this.volumeDevices = volumeDevices;
    return this;
  }

  public V1Container addVolumeDevicesItem(V1VolumeDevice volumeDevicesItem) {
    if (this.volumeDevices == null) {
      this.volumeDevices = new ArrayList<>();
    }
    this.volumeDevices.add(volumeDevicesItem);
    return this;
  }

   /**
   * volumeDevices is the list of block devices to be used by the container.
   * @return volumeDevices
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "volumeDevices is the list of block devices to be used by the container.")

  public List<V1VolumeDevice> getVolumeDevices() {
    return volumeDevices;
  }


  public void setVolumeDevices(List<V1VolumeDevice> volumeDevices) {
    this.volumeDevices = volumeDevices;
  }


  public V1Container volumeMounts(List<V1VolumeMount> volumeMounts) {

    this.volumeMounts = volumeMounts;
    return this;
  }

  public V1Container addVolumeMountsItem(V1VolumeMount volumeMountsItem) {
    if (this.volumeMounts == null) {
      this.volumeMounts = new ArrayList<>();
    }
    this.volumeMounts.add(volumeMountsItem);
    return this;
  }

   /**
   * Pod volumes to mount into the container&#39;s filesystem. Cannot be updated.
   * @return volumeMounts
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Pod volumes to mount into the container's filesystem. Cannot be updated.")

  public List<V1VolumeMount> getVolumeMounts() {
    return volumeMounts;
  }


  public void setVolumeMounts(List<V1VolumeMount> volumeMounts) {
    this.volumeMounts = volumeMounts;
  }


  public V1Container workingDir(String workingDir) {

    this.workingDir = workingDir;
    return this;
  }

   /**
   * Container&#39;s working directory. If not specified, the container runtime&#39;s default will be used, which might be configured in the container image. Cannot be updated.
   * @return workingDir
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Container's working directory. If not specified, the container runtime's default will be used, which might be configured in the container image. Cannot be updated.")

  public String getWorkingDir() {
    return workingDir;
  }


  public void setWorkingDir(String workingDir) {
    this.workingDir = workingDir;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    V1Container v1Container = (V1Container) o;
    return Objects.equals(this.args, v1Container.args) &&
        Objects.equals(this.command, v1Container.command) &&
        Objects.equals(this.env, v1Container.env) &&
        Objects.equals(this.envFrom, v1Container.envFrom) &&
        Objects.equals(this.image, v1Container.image) &&
        Objects.equals(this.imagePullPolicy, v1Container.imagePullPolicy) &&
        Objects.equals(this.lifecycle, v1Container.lifecycle) &&
        Objects.equals(this.livenessProbe, v1Container.livenessProbe) &&
        Objects.equals(this.name, v1Container.name) &&
        Objects.equals(this.ports, v1Container.ports) &&
        Objects.equals(this.readinessProbe, v1Container.readinessProbe) &&
        Objects.equals(this.resizePolicy, v1Container.resizePolicy) &&
        Objects.equals(this.resources, v1Container.resources) &&
        Objects.equals(this.restartPolicy, v1Container.restartPolicy) &&
        Objects.equals(this.securityContext, v1Container.securityContext) &&
        Objects.equals(this.startupProbe, v1Container.startupProbe) &&
        Objects.equals(this.stdin, v1Container.stdin) &&
        Objects.equals(this.stdinOnce, v1Container.stdinOnce) &&
        Objects.equals(this.terminationMessagePath, v1Container.terminationMessagePath) &&
        Objects.equals(this.terminationMessagePolicy, v1Container.terminationMessagePolicy) &&
        Objects.equals(this.tty, v1Container.tty) &&
        Objects.equals(this.volumeDevices, v1Container.volumeDevices) &&
        Objects.equals(this.volumeMounts, v1Container.volumeMounts) &&
        Objects.equals(this.workingDir, v1Container.workingDir);
  }

  @Override
  public int hashCode() {
    return Objects.hash(args, command, env, envFrom, image, imagePullPolicy, lifecycle, livenessProbe, name, ports, readinessProbe, resizePolicy, resources, restartPolicy, securityContext, startupProbe, stdin, stdinOnce, terminationMessagePath, terminationMessagePolicy, tty, volumeDevices, volumeMounts, workingDir);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class V1Container {\n");
    sb.append("    args: ").append(toIndentedString(args)).append("\n");
    sb.append("    command: ").append(toIndentedString(command)).append("\n");
    sb.append("    env: ").append(toIndentedString(env)).append("\n");
    sb.append("    envFrom: ").append(toIndentedString(envFrom)).append("\n");
    sb.append("    image: ").append(toIndentedString(image)).append("\n");
    sb.append("    imagePullPolicy: ").append(toIndentedString(imagePullPolicy)).append("\n");
    sb.append("    lifecycle: ").append(toIndentedString(lifecycle)).append("\n");
    sb.append("    livenessProbe: ").append(toIndentedString(livenessProbe)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    ports: ").append(toIndentedString(ports)).append("\n");
    sb.append("    readinessProbe: ").append(toIndentedString(readinessProbe)).append("\n");
    sb.append("    resizePolicy: ").append(toIndentedString(resizePolicy)).append("\n");
    sb.append("    resources: ").append(toIndentedString(resources)).append("\n");
    sb.append("    restartPolicy: ").append(toIndentedString(restartPolicy)).append("\n");
    sb.append("    securityContext: ").append(toIndentedString(securityContext)).append("\n");
    sb.append("    startupProbe: ").append(toIndentedString(startupProbe)).append("\n");
    sb.append("    stdin: ").append(toIndentedString(stdin)).append("\n");
    sb.append("    stdinOnce: ").append(toIndentedString(stdinOnce)).append("\n");
    sb.append("    terminationMessagePath: ").append(toIndentedString(terminationMessagePath)).append("\n");
    sb.append("    terminationMessagePolicy: ").append(toIndentedString(terminationMessagePolicy)).append("\n");
    sb.append("    tty: ").append(toIndentedString(tty)).append("\n");
    sb.append("    volumeDevices: ").append(toIndentedString(volumeDevices)).append("\n");
    sb.append("    volumeMounts: ").append(toIndentedString(volumeMounts)).append("\n");
    sb.append("    workingDir: ").append(toIndentedString(workingDir)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
