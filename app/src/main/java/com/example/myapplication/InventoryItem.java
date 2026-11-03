package com.example.myapplication;
import java.io.Serializable;

    // InventoryItem.java


    public class InventoryItem implements Serializable {
        public long id;               // 0 = new item
        public String name;
        public String category;
        public double quantity;
        public String unit;
        public double pricePerUnit;
        public double lowStockLevel;
        public String supplier;
        public String expiryDate;
        public String notes;

        public InventoryItem(long id, String name, String category, double quantity,
                             String unit, double pricePerUnit, double lowStockLevel,
                             String supplier, String expiryDate, String notes) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.quantity = quantity;
            this.unit = unit;
            this.pricePerUnit = pricePerUnit;
            this.lowStockLevel = lowStockLevel;
            this.supplier = supplier;
            this.expiryDate = expiryDate;
            this.notes = notes;
        }
    }

