package naturenet.org.naturenet.data.legacy;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Field;

public interface NatureNetAPI {

    @FormUrlEncoded
    @POST("/account/login")
    Call<Response> login(@Field("username") String username, @Field("password") String password);
}
