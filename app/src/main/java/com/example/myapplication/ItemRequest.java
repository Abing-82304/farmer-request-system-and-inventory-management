package com.example.myapplication;

public class ItemRequest {

    private int id;
    private String itemName;
    private String quantity;
    private String unit;
    private String farmArea;
    private String purpose;
    private String status;
    private String remarks;

    public ItemRequest(
            int id,
            String itemName,
            String quantity,
            String unit,
            String farmArea,
            String purpose,
            String status,
            String remarks) {

        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unit = unit;
        this.farmArea = farmArea;
        this.purpose = purpose;
        this.status = status;
        this.remarks = remarks;
    }

    public int getId() { return id; }

    public String getItemName() { return itemName; }

    public String getQuantity() { return quantity; }

    public String getUnit() { return unit; }

    public String getFarmArea() { return farmArea; }

    public String getPurpose() { return purpose; }

    public String getStatus() { return status; }

    public String getRemarks() { return remarks; }
}
