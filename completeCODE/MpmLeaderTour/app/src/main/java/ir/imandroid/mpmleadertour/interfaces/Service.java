package ir.imandroid.mpmleadertour.interfaces;

import com.google.gson.JsonObject;

import java.util.List;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Service {
    @POST("/api/Mail")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("name") RequestBody name);

    @POST("/api/nabgss")
    Call<JsonObject> sendEnteghadPishnahad(@Query("token") String token, @Query("pid") int pid, @Query("tell") String tell, @Query("mail") String mail, @Query("text") String text);

}

