package com.example.myapplication; // Verify this package matches your actual layout directory name

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestActivity extends AppCompatActivity implements RequestAdapter.OnRequestActionListener {

    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private ApiService apiService;

    // Metric Summary Counters from your top layout layout cards
    private TextView textTotalRequestsCount;
    private TextView textApprovedRequestsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request); // Links your custom XML UI

        // 1. Hook up toolbar items and views
        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> finish()); // Closes screen and goes back on click

        textTotalRequestsCount = findViewById(R.id.textTotalRequestsCount);
        textApprovedRequestsCount = findViewById(R.id.textApprovedRequestsCount);

        // 2. Set up the list manager and view attachments
        recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RequestAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // 3. Initialize Retrofit.
        // Tip: 10.0.2.2 maps directly to your computer's local Deno network pipeline inside the Android Emulator.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( "http://100.72.152.12:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Load rows from Deno server immediately on layout display
        loadRequestsFromServer();
    }

    private void loadRequestsFromServer() {
        apiService.getAllEmployeeRequests().enqueue(new Callback<List<ItemRequest1>>() {
            @Override
            public void onResponse(Call<List<ItemRequest1>> call, Response<List<ItemRequest1>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ItemRequest1> requestsList = response.body();

                    // Run safely back inside the main Android display pipeline
                    runOnUiThread(() -> {
                        // Pass row array data to our adapter cards
                        adapter.updateData(requestsList);

                        // Dynamically update dashboard totals counters
                        calculateDashboardMetrics(requestsList);
                    });
                } else {
                    Toast.makeText(RequestActivity.this, "Server error code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ItemRequest1>> call, Throwable t) {
                Toast.makeText(RequestActivity.this, "Failed to connect to backend server.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void calculateDashboardMetrics(List<ItemRequest1> requests) {
        int total = requests.size();
        int approvedCount = 0;

        // Loop through everything to locate matches
        for (ItemRequest1 req : requests) {
            if ("APPROVED".equalsIgnoreCase(req.status)) {
                approvedCount++;
            }
        }

        // Display results into the XML layout metrics boxes
        textTotalRequestsCount.setText(String.valueOf(total));
        textApprovedRequestsCount.setText(String.valueOf(approvedCount));
    }

    // TRIGGERED ON CARD CLICK: Admin presses green "Approve" button
    @Override
    public void onApprove(ItemRequest1 request) {
        executeStatusChangeOnServer(request.id, "APPROVED", null);
    }

    // TRIGGERED ON CARD CLICK: Admin presses red "Decline" button with text box comments
    @Override
    public void onDecline(ItemRequest1 request, String remarks) {
        executeStatusChangeOnServer(request.id, "DECLINED", remarks);
    }

    private void executeStatusChangeOnServer(int requestId, String status, String remarks) {
        StatusUpdateRequest payload = new StatusUpdateRequest(status, remarks);

        apiService.updateRequestStatus(requestId, payload).enqueue(new Callback<ItemRequest1>() {
            @Override
            public void onResponse(Call<ItemRequest1> call, Response<ItemRequest1> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RequestActivity.this, "Request marked as " + status + " successfully!", Toast.LENGTH_SHORT).show();

                    // Automatically download clean dataset lines to re-render processed entries
                    loadRequestsFromServer();
                } else {
                    Toast.makeText(RequestActivity.this, "Update action failed on server.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ItemRequest1> call, Throwable t) {
                Toast.makeText(RequestActivity.this, "Network connection loss encountered.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
