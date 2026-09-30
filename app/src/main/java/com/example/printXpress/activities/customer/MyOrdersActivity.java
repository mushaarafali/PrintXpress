package com.example.printXpress.activities.customer;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.printXpress.R;
import com.example.printXpress.adapters.OrderAdapter;
import com.example.printXpress.database.DatabaseHelper;
import com.example.printXpress.models.Order;
import com.example.printXpress.utils.SessionManager;

import java.util.ArrayList;

public class MyOrdersActivity extends AppCompatActivity implements OrderAdapter.OnOrderCancelledListener {

    ListView myOrdersList;
    DatabaseHelper db;
    SessionManager session;
    ArrayList<Order> ordersList = new ArrayList<>();
    OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        myOrdersList = findViewById(R.id.myOrdersList);
        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        adapter = new OrderAdapter(this, ordersList, this);
        myOrdersList.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        Cursor cursor = db.getOrdersByEmail(session.getEmail());
        ordersList.clear();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String product = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
            int qty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price"));
            String instructions = cursor.getString(cursor.getColumnIndexOrThrow("instructions"));
            String delivery = cursor.getString(cursor.getColumnIndexOrThrow("delivery_type"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            String designPath = cursor.getString(cursor.getColumnIndexOrThrow("design_path"));

            ordersList.add(new Order(id, session.getEmail(), product, qty, total, instructions, delivery, status, designPath));
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onOrderCancelled() {
        loadOrders();
    }
}