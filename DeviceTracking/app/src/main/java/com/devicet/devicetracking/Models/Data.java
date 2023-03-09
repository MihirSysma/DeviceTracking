package com.devicet.devicetracking.Models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Data {

@SerializedName("access_token")
@Expose
private String accessToken;
@SerializedName("expiry")
@Expose
private Integer expiry;

public String getAccessToken() {
return accessToken;
}

public void setAccessToken(String accessToken) {
this.accessToken = accessToken;
}

public Integer getExpiry() {
return expiry;
}

public void setExpiry(Integer expiry) {
this.expiry = expiry;
}

}