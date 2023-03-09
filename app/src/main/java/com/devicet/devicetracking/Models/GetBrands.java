package com.devicet.devicetracking.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class GetBrands {

@SerializedName("api_status")
@Expose
private Integer apiStatus;
@SerializedName("api_message")
@Expose
private String apiMessage;
@SerializedName("data")
@Expose
private List<GetSubBrands> subBrands = null;

public Integer getApiStatus() {
return apiStatus;
}

public void setApiStatus(Integer apiStatus) {
this.apiStatus = apiStatus;
}

public String getApiMessage() {
return apiMessage;
}

public void setApiMessage(String apiMessage) {
this.apiMessage = apiMessage;
}

public List<GetSubBrands> getSubBrands() {
return subBrands;
}

public void setData(List<GetSubBrands> subBrands) {
this.subBrands = subBrands;
}

}