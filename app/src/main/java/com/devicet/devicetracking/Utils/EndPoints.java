package com.devicet.devicetracking.Utils;

import com.devicet.devicetracking.Models.DetectionModel;
import com.devicet.devicetracking.Models.GetBrands;
import com.devicet.devicetracking.Models.GetDevice;
import com.devicet.devicetracking.Models.GetDeviceModel;
import com.devicet.devicetracking.Models.GetModel;
import com.devicet.devicetracking.Models.LoginModel;
import com.devicet.devicetracking.Models.RegisterModel;
import com.devicet.devicetracking.Models.TokenModel;
import com.devicet.devicetracking.Models.UpdateStatus;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface EndPoints {
    @FormUrlEncoded
    @POST(ApiConstants.SECRET_END_POINTS)
    Call<TokenModel> getToken(@Field("secret") String secret);

    @FormUrlEncoded
    @POST(ApiConstants.DETECTION_List)
    Call<GetDeviceModel> getDeviceList(@Header("authorization") String auth, @Field("user_id") String user_id);

    @GET(ApiConstants.OS_LIST)
    Call<GetDevice> getDevice(@Header("authorization") String auth);

    @GET(ApiConstants.ADD_PC)
    Call<GetDevice> getPcLaptop(@Header("authorization") String auth);

    @FormUrlEncoded
    @POST(ApiConstants.BRAND_LIST)
    Call<GetBrands> getBrands(@Header("authorization") String auth, @Field("operatings_id") int operatings_id);

    @FormUrlEncoded
    @POST(ApiConstants.MODEL_LIST)
    Call<GetModel> getModels(@Header("authorization") String auth, @Field("brands_id") int user_id);

    @FormUrlEncoded
    @POST(ApiConstants.REGISTER_END_POINTS)
    Call<RegisterModel> register(@Header("authorization") String auth, @Field("name") String name,
                                 @Field("email") String email, @Field("password") String password,
                                 @Field("contact") String contact, @Field("gender") String gender,
                                 @Field("country_of_origin") String country_of_origin,
                                 @Field("username") String username
    );

    @FormUrlEncoded
    @POST(ApiConstants.DETECTION_LOGIN)
    Call<LoginModel> login(@Header("authorization") String auth,
                           @Field("username") String username,
                           @Field("password") String password
    );

    @FormUrlEncoded
    @POST(ApiConstants.ADD_DETECTION_END_POINTS)
    Call<DetectionModel> detections(@Header("authorization") String auth, @Field("device_brand") String device_brand,
                                    @Field("device_model") String device_model, @Field("device_os") String device_os,
                                    @Field("imei") String imei, @Field("imei2") String imei_2,
                                    @Field("detection_lat") String detection_lat, @Field("detection_long") String detection_long,
                                    @Field("user_id") int user_id, @Field("status") String status,
                                    @Field("network_one")String networkTypeOne,
                                    @Field("network_two")String networkTypeTwo,
                                    @Field("network_sim_code") int network_sim_code, // last 4 digits of mobile num
                                    @Field("network_sim_code_2") int network_sim_code_2,
                                    @Field("manufacture_serial_number") String manufacture_serial_number,
                                    @Field("mark_of_compliance") String mark_of_compliance,
                                    @Field("product_device_id") String product_device_id,
                                    @Field("address") String address, @Field("type") String type
    );

    @FormUrlEncoded
    @POST(ApiConstants.ADD_DETECTION_END_POINTS)
    Call<DetectionModel> detectionOtherDevice(@Header("authorization") String auth, @Field("device_brand") String device_brand,
                                    @Field("device_model") String device_model, @Field("device_os") String device_os,
                                    @Field("detection_lat") String detection_lat, @Field("detection_long") String detection_long,
                                    @Field("user_id") int user_id, @Field("status") String status,
                                    @Field("manufacture_serial_number") String manufacture_serial_number,
                                    @Field("mark_of_compliance") String mark_of_compliance,
                                    @Field("product_device_id") String product_device_id,
                                    @Field("address") String address, @Field("type") String type
    );


    @FormUrlEncoded
    @POST(ApiConstants.UPDATE_STATUS)
    Call<UpdateStatus> updateStatus(@Header("authorization") String auth, @Field("user_id") String user_id,
                                    @Field("imei") String imei, @Field("detection_lat") String detection_lat,
                                    @Field("detection_long") String detection_long, @Field("address") String address,
                                    @Field("status") String status, @Field("type") String type,
                                    @Field("mfg_status") String mfg_status
    );

}
