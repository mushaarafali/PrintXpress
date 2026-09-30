package com.example.printXpress.activities.admin;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.printXpress.R;
import com.example.printXpress.database.DatabaseHelper;

public class UpdateOrderStatusActivity extends AppCompatActivity {

    TextView updateOrderInfo;
    Spinner statusUpdateSpinner;
    Button btnSubmitStatusUpdate;
    DatabaseHelper db;
    int orderId;

    String[] statusList = {"Processing", "Printing", "Ready for Pickup", "Completed", "Cancelled"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_order_status);

        db = new DatabaseHelper(this);
        orderId = getIntent().getIntExtra("order_id", -1);

        updateOrderInfo = findViewById(R.id.updateOrderInfo);
        statusUpdateSpinner = findViewById(R.id.statusUpdateSpinner);
        btnSubmitStatusUpdate = findViewById(R.id.btnSubmitStatusUpdate);

        if (orderId == -1) {
            Toast.makeText(this, "Invalid Order ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statusList);
        statusUpdateSpinner.setAdapter(adapter);

        loadOrderData();

        btnSubmitStatusUpdate.setOnClickListener(v -> {
            String newStatus = statusUpdateSpinner.getSelectedItem().toString();
            if (db.updateOrderStatus(orderId, newStatus)) {
                Toast.makeText(this, "Order status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOrderData() {
        Cursor cursor = db.getOrderById(orderId);
        if (cursor != null && cursor.moveToFirst()) {
            String customer = cursor.getString(cursor.getColumnIndexOrThrow("customer_email"));
            String product = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
            int qty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            String currentStatus = cursor.getString(cursor.getColumnIndexOrThrow("status"));

            String info = "Order ID: #" + orderId +
                    "\nCustomer: " + customer +
                    "\nProduct: " + product +
                    "\nQuantity: " + qty +
                    "\nCurrent Status: " + currentStatus;

            updateOrderInfo.setText(info);

            // Set spinner to current status
            for (int i = 0; i < statusList.length; i++) {
                if (statusList[i].equalsIgnoreCase(currentStatus)) {
                    statusUpdateSpinner.setSelection(i);
                    break;
                }
            }

            // If cancelled, hide update button
            if ("Cancelled".equalsIgnoreCase(currentStatus)) {
                btnSubmitStatusUpdate.setVisibility(View.GONE);
                statusUpdateSpinner.setEnabled(false);
                updateOrderInfo.append("\n\n(This order is cancelled and cannot be updated)");
            }

            cursor.close();
        } else {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}