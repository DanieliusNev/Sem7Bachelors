package com.example.bachelorsrealwear.data.repository;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface SharePointApiService {

    @Multipart
    @POST("sites/try/_api/web/GetFolderByServerRelativeUrl('{folderPath}')/Files/Add(url='{fileName}', overwrite=true)")
    Call<ResponseBody> uploadFile(
            @Part("fileName") RequestBody fileName,
            @Part("folderPath") RequestBody folderPath,
            @Part MultipartBody.Part file
    );

    @GET("drive/root:/{folderPath}:/children")
    Call<String> listFiles(@Path("folderPath") String folderPath);
}
