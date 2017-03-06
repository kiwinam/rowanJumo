package smart.rowan.apiroute;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiController implements Callback<HttpResponse> {
    private static final String BASE_URL = "http://165.132.110.130/rowan/";

    public void start(Map input) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        UpdateInfoAPI updateInfoAPI = retrofit.create(UpdateInfoAPI.class);
        Call<HttpResponse> call = updateInfoAPI.update(input);
        call.enqueue(this);
    }

    public void joinStart(Map input) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        JoinUpdateInfoAPI joinUpdateInfoAPI = retrofit.create(JoinUpdateInfoAPI.class);
        Call<HttpResponse> call = joinUpdateInfoAPI.joinUpdate(input);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<HttpResponse> call, Response<HttpResponse> response) {
        if (response.isSuccessful()) {
            HttpResponse m = response.body();
            System.out.println("res from test server");
            System.out.println(m);
        } else {
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<HttpResponse> call, Throwable t) {
        t.printStackTrace();
    }
}