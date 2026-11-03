package com.example.myapplication;

public class StatusUpdateRequest {
    public String status;
    public String admin_remarks; // Changed field name to match backend query destructuring

    public StatusUpdateRequest(String status, String admin_remarks) {
        this.status = status;
        this.admin_remarks = admin_remarks;
    }
}

