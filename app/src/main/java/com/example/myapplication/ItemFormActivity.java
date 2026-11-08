package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

public class ItemFormActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM = "extra_item";

    private InventoryItem editingItem;

    private final String[] categories = {
            "Seeds", "Fertilizer", "Pesticide", "Animal Feed",
            "Veterinary / Medicine", "Tools", "Fuel", "Other"
    };
    private final String[] units = {"kg", "g", "L", "mL", "bag", "sack", "pcs", "box"};

    private AutoCompleteTextView actCategory, actUnit;
    private EditText etName, etQty, etPrice, etReorder, etSupplier, etExpiry, etNotes;
    private TextInputLayout tilName, tilCategory, tilQty, tilUnit, tilPrice, tilExpiry;
    private Button btnSave, btnDelete;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_form);

        tvTitle = findViewById(R.id.tvTitle);
        etName = findViewById(R.id.etName);
        etQty = findViewById(R.id.etQty);
        etPrice = findViewById(R.id.etPrice);
        etReorder = findViewById(R.id.etReorder);
        etSupplier = findViewById(R.id.etSupplier);
        etExpiry = findViewById(R.id.etExpiry);
        etNotes = findViewById(R.id.etNotes);
        actCategory = findViewById(R.id.actCategory);
        actUnit = findViewById(R.id.actUnit);
        tilName = findViewById(R.id.tilName);
        tilCategory = findViewById(R.id.tilCategory);
        tilQty = findViewById(R.id.tilQty);
        tilUnit = findViewById(R.id.tilUnit);
        tilPrice = findViewById(R.id.tilPrice);
        tilExpiry = findViewById(R.id.tilExpiry);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        actCategory.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, categories));
        actUnit.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, units));

        // Expiry date picker
        etExpiry.setOnClickListener(v -> showDatePicker());
        tilExpiry.setEndIconOnClickListener(v -> showDatePicker());

        // Edit mode?
        editingItem = (InventoryItem) getIntent().getSerializableExtra(EXTRA_ITEM);
        if (editingItem != null) fillForm(editingItem);

        btnSave.setOnClickListener(v -> save());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) ->
                etExpiry.setText(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void fillForm(InventoryItem item) {
        tvTitle.setText("Edit Item");
        btnSave.setText("Update Item");
        btnDelete.setVisibility(View.VISIBLE);

        etName.setText(item.name);
        actCategory.setText(item.category, false);
        etQty.setText(String.valueOf(item.quantity));
        actUnit.setText(item.unit, false);
        etPrice.setText(String.valueOf(item.pricePerUnit));
        etReorder.setText(String.valueOf(item.lowStockLevel));
        etSupplier.setText(item.supplier);
        etExpiry.setText(item.expiryDate);
        etNotes.setText(item.notes);
    }

    private String val(EditText e) {
        return e.getText().toString().trim();
    }

    private double num(EditText e) {
        try { return Double.parseDouble(val(e)); }
        catch (NumberFormatException ex) { return 0.0; }
    }

    private boolean require(EditText input, TextInputLayout layout, String msg) {
        if (val(input).isEmpty()) {
            layout.setError(msg);
            return false;
        }
        layout.setError(null);
        return true;
    }

    private void save() {
        boolean ok = true;
        ok &= require(etName, tilName, "Enter item name");
        ok &= require(actCategory, tilCategory, "Choose a category");
        ok &= require(etQty, tilQty, "Enter quantity");
        ok &= require(actUnit, tilUnit, "Choose unit");
        ok &= require(etPrice, tilPrice, "Enter price");
        if (!ok) return;

        InventoryItem item = new InventoryItem(
                editingItem != null ? editingItem.id : 0,
                val(etName), val(actCategory), num(etQty), val(actUnit),
                num(etPrice), num(etReorder), val(etSupplier),
                val(etExpiry), val(etNotes));

        // TODO: connect to your database (Room / SQLite / Firebase)
        // if (item.id == 0) dao.insert(item); else dao.update(item);

        Toast.makeText(this,
                editingItem == null ? "Item added" : "Item updated",
                Toast.LENGTH_SHORT).show();
        finish();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete item?")
                .setMessage("This can't be undone.")
                .setPositiveButton("Delete", (d, w) -> {
                    // TODO: dao.delete(editingItem);
                    Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
