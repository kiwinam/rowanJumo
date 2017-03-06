package smart.rowan.apiroute;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

interface JoinUpdateInfoAPI {
    @FormUrlEncoded
    @POST("join_rest.php")
    Call<HttpResponse> joinUpdate(@FieldMap Map<String, String> input);
}
