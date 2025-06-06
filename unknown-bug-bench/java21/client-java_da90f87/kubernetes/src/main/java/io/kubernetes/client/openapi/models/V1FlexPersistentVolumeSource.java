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
import io.kubernetes.client.openapi.models.V1SecretReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FlexPersistentVolumeSource represents a generic persistent volume resource that is provisioned/attached using an exec based plugin.
 */
@ApiModel(description = "FlexPersistentVolumeSource represents a generic persistent volume resource that is provisioned/attached using an exec based plugin.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2023-12-01T19:05:21.333462Z[Etc/UTC]")
public class V1FlexPersistentVolumeSource {
  public static final String SERIALIZED_NAME_DRIVER = "driver";
  @SerializedName(SERIALIZED_NAME_DRIVER)
  private String driver;

  public static final String SERIALIZED_NAME_FS_TYPE = "fsType";
  @SerializedName(SERIALIZED_NAME_FS_TYPE)
  private String fsType;

  public static final String SERIALIZED_NAME_OPTIONS = "options";
  @SerializedName(SERIALIZED_NAME_OPTIONS)
  private Map<String, String> options = null;

  public static final String SERIALIZED_NAME_READ_ONLY = "readOnly";
  @SerializedName(SERIALIZED_NAME_READ_ONLY)
  private Boolean readOnly;

  public static final String SERIALIZED_NAME_SECRET_REF = "secretRef";
  @SerializedName(SERIALIZED_NAME_SECRET_REF)
  private V1SecretReference secretRef;


  public V1FlexPersistentVolumeSource driver(String driver) {

    this.driver = driver;
    return this;
  }

   /**
   * driver is the name of the driver to use for this volume.
   * @return driver
  **/
  @ApiModelProperty(required = true, value = "driver is the name of the driver to use for this volume.")

  public String getDriver() {
    return driver;
  }


  public void setDriver(String driver) {
    this.driver = driver;
  }


  public V1FlexPersistentVolumeSource fsType(String fsType) {

    this.fsType = fsType;
    return this;
  }

   /**
   * fsType is the Filesystem type to mount. Must be a filesystem type supported by the host operating system. Ex. \&quot;ext4\&quot;, \&quot;xfs\&quot;, \&quot;ntfs\&quot;. The default filesystem depends on FlexVolume script.
   * @return fsType
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "fsType is the Filesystem type to mount. Must be a filesystem type supported by the host operating system. Ex. \"ext4\", \"xfs\", \"ntfs\". The default filesystem depends on FlexVolume script.")

  public String getFsType() {
    return fsType;
  }


  public void setFsType(String fsType) {
    this.fsType = fsType;
  }


  public V1FlexPersistentVolumeSource options(Map<String, String> options) {

    this.options = options;
    return this;
  }

  public V1FlexPersistentVolumeSource putOptionsItem(String key, String optionsItem) {
    if (this.options == null) {
      this.options = new HashMap<>();
    }
    this.options.put(key, optionsItem);
    return this;
  }

   /**
   * options is Optional: this field holds extra command options if any.
   * @return options
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "options is Optional: this field holds extra command options if any.")

  public Map<String, String> getOptions() {
    return options;
  }


  public void setOptions(Map<String, String> options) {
    this.options = options;
  }


  public V1FlexPersistentVolumeSource readOnly(Boolean readOnly) {

    this.readOnly = readOnly;
    return this;
  }

   /**
   * readOnly is Optional: defaults to false (read/write). ReadOnly here will force the ReadOnly setting in VolumeMounts.
   * @return readOnly
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "readOnly is Optional: defaults to false (read/write). ReadOnly here will force the ReadOnly setting in VolumeMounts.")

  public Boolean getReadOnly() {
    return readOnly;
  }


  public void setReadOnly(Boolean readOnly) {
    this.readOnly = readOnly;
  }


  public V1FlexPersistentVolumeSource secretRef(V1SecretReference secretRef) {

    this.secretRef = secretRef;
    return this;
  }

   /**
   * Get secretRef
   * @return secretRef
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public V1SecretReference getSecretRef() {
    return secretRef;
  }


  public void setSecretRef(V1SecretReference secretRef) {
    this.secretRef = secretRef;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    V1FlexPersistentVolumeSource v1FlexPersistentVolumeSource = (V1FlexPersistentVolumeSource) o;
    return Objects.equals(this.driver, v1FlexPersistentVolumeSource.driver) &&
        Objects.equals(this.fsType, v1FlexPersistentVolumeSource.fsType) &&
        Objects.equals(this.options, v1FlexPersistentVolumeSource.options) &&
        Objects.equals(this.readOnly, v1FlexPersistentVolumeSource.readOnly) &&
        Objects.equals(this.secretRef, v1FlexPersistentVolumeSource.secretRef);
  }

  @Override
  public int hashCode() {
    return Objects.hash(driver, fsType, options, readOnly, secretRef);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class V1FlexPersistentVolumeSource {\n");
    sb.append("    driver: ").append(toIndentedString(driver)).append("\n");
    sb.append("    fsType: ").append(toIndentedString(fsType)).append("\n");
    sb.append("    options: ").append(toIndentedString(options)).append("\n");
    sb.append("    readOnly: ").append(toIndentedString(readOnly)).append("\n");
    sb.append("    secretRef: ").append(toIndentedString(secretRef)).append("\n");
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
