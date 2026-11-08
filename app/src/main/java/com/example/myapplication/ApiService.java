package com.example.myapplication;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
public interface ApiService {


        @POST("api/login")
           Call<LoginResponse> login(@Body LoginRequest request);

    @GET("api/stock")
    Call<List<Item>> getItems();
    @GET("api/admin/requests")
    Call<List<ItemRequest1>> getAllEmployeeRequests();

    // Update status to 'approved' or 'declined'
    @PATCH("api/admin/requests/{id}/status")
    Call<ItemRequest1> updateRequestStatus(
            @Path("id") int requestId,
            @Body StatusUpdateRequest body
    );
    // EMPLOYEE: Get only the logged-in employee's requests
    @GET("api/employee/my-requests")
    Call<List<ItemRequest1>> getMyRequests();


    }

