package com.example.myapplication;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class    RetrofitClient {

    private static final String BASE_URL = "http://192.168.254.100:8000/";
//    private static final String BASE_URL = "http://10.5.50.38:8000/";

    private static Retrofit retrofit;




    public static ApiService getApi() {
        if (retrofit == null) {


            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.254.100:8000/")
//                   / .baseUrl("http://10.5.50.38:8000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}

