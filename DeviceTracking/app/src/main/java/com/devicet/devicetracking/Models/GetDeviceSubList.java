package com.devicet.devicetracking.Models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetDeviceSubList {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("os_type")
    @Expose
    private String osType;
    @SerializedName("activation_type")
    @Expose
    private String activationType;
    @SerializedName("status")
    @Expose
    private String status;

    public GetDeviceSubList(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getActivationType() {
        return activationType;
    }

    public void setActivationType(String activationType) {
        this.activationType = activationType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return  name;
    }
}
