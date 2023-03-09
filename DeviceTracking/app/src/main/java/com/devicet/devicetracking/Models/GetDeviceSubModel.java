package com.devicet.devicetracking.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetDeviceSubModel {
    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("manufacture_serial_number")
    @Expose
    private String manufactureSerialNumber;
    @SerializedName("mark_of_compliance")
    @Expose
    private String markOfCompliance;
    @SerializedName("brand_Name")
    @Expose
    private String brandName;
    @SerializedName("model_Name")
    @Expose
    private String modelName;
    @SerializedName("operting_System")
    @Expose
    private String opertingSystem;
    @SerializedName("detection_time")
    @Expose
    private String detectionTime;
    @SerializedName("detection_lat")
    @Expose
    private String detectionLat;
    @SerializedName("detection_long")
    @Expose
    private String detectionLong;
    @SerializedName("network_sim_code")
    @Expose
    private String networkSimCode;
    @SerializedName("product_device_id")
    @Expose
    private String productDeviceId;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("mfg_status")
    @Expose
    private String mfgStatus;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getManufactureSerialNumber() {
        return manufactureSerialNumber;
    }

    public void setManufactureSerialNumber(String manufactureSerialNumber) {
        this.manufactureSerialNumber = manufactureSerialNumber;
    }

    public String getMarkOfCompliance() {
        return markOfCompliance;
    }

    public void setMarkOfCompliance(String markOfCompliance) {
        this.markOfCompliance = markOfCompliance;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getOpertingSystem() {
        return opertingSystem;
    }

    public void setOpertingSystem(String opertingSystem) {
        this.opertingSystem = opertingSystem;
    }

    public String getDetectionTime() {
        return detectionTime;
    }

    public void setDetectionTime(String detectionTime) {
        this.detectionTime = detectionTime;
    }

    public String getDetectionLat() {
        return detectionLat;
    }

    public void setDetectionLat(String detectionLat) {
        this.detectionLat = detectionLat;
    }

    public String getDetectionLong() {
        return detectionLong;
    }

    public void setDetectionLong(String detectionLong) {
        this.detectionLong = detectionLong;
    }

    public String getNetworkSimCode() {
        return networkSimCode;
    }

    public void setNetworkSimCode(String networkSimCode) {
        this.networkSimCode = networkSimCode;
    }

    public String getProductDeviceId() {
        return productDeviceId;
    }

    public void setProductDeviceId(String productDeviceId) {
        this.productDeviceId = productDeviceId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMfgStatus() {
        return mfgStatus;
    }

    public void setMfgStatus(String mfgStatus) {
        this.mfgStatus = mfgStatus;
    }

}
