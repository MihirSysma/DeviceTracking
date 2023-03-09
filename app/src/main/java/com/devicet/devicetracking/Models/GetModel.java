package com.devicet.devicetracking.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class GetModel {

@SerializedName("api_status")
@Expose
private Integer apiStatus;
@SerializedName("api_message")
@Expose
private String apiMessage;
@SerializedName("data")
@Expose
private List<GetModelSub> data = null;

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

public List<GetModelSub> getData() {
return data;
}

public void setData(List<GetModelSub> data) {
this.data = data;
}

}