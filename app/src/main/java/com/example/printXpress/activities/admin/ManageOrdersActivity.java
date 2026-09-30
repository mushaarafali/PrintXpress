package com.example.printXpress.activities.admin;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.printXpress.R;
import com.example.printXpress.database.DatabaseHelper;

import java.util.ArrayList;

public class ManageOrdersActivity extends AppCompatActivity {

    ListView manageOrdersList;
    DatabaseHelper db;
    ArrayList<Integer> orderIds = new ArrayList<>();
    ArrayList<String> orders = new ArrayList<>();

    String[] statusList = {"Processing", "Printing", "Ready for Pickup", "Completed", "Cancelled"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_orders);

        manageOrdersList = findViewById(R.id.manageOrdersList);
        db = new DatabaseHelper(this);

        loadOrders();

        manageOrdersList.setOnItemClickListener((parent, view, position, id) -> showStatusDialog(orderIds.get(position)));
    }

    private void loadOrders() {
        Cursor cursor = db.getAllOrders();

        orderIds.clear();
        orders.clear();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("customer_email"));
            String product = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
            int qty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            String designPath = cursor.getString(cursor.getColumnIndexOrThrow("design_path"));

            orderIds.add(id);
            orders.add("Order ID: " + id +
                    "\nCustomer: " + email +
                    "\nProduct: " + product +
                    "\nQty: " + qty +
                    "\nTotal: LKR " + total +
                    "\nStatus: " + status +
                    (designPath != null && !designPath.isEmpty() ? "\nDesign: Uploaded" : "") +
                    "\nTap to update status");
        }

        cursor.close();

        manageOrdersList.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                orders
        ));
    }

    private void showStatusDialog(int orderId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Order Status");

        builder.setItems(statusList, (dialog, which) -> {
            boolean ok = db.updateOrderStatus(orderId, statusList[which]);

            if (ok) {
                Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show();
                loadOrders();
            }
        });

        builder.show();
    }
}