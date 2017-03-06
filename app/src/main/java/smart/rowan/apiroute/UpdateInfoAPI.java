package smart.rowan.apiroute;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by BangRob on 2017. 2. 4..
 */

public interface UpdateInfoAPI {
    @FormUrlEncoded
    @POST("update_info.php")
    Call<HttpResponse> update(@FieldMap Map<String, String> input);
}
