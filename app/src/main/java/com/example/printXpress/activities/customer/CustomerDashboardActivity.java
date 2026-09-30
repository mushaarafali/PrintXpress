package com.example.printXpress.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.printXpress.R;
import com.example.printXpress.activities.LoginActivity;
import com.example.printXpress.utils.SessionManager;

public class CustomerDashboardActivity extends AppCompatActivity {

    TextView customerWelcome;
    Button customerLogoutBtn, btnProducts, btnMyOrders, btnOffers, btnGuidelines;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        session = new SessionManager(this);

        customerWelcome = findViewById(R.id.customerWelcome);
        customerLogoutBtn = findViewById(R.id.customerLogoutBtn);
        btnProducts = findViewById(R.id.btnProducts);
        btnMyOrders = findViewById(R.id.btnMyOrders);
        btnOffers = findViewById(R.id.btnOffers);
        btnGuidelines = findViewById(R.id.btnGuidelines);

        customerWelcome.setText("Welcome, " + session.getName());

        btnProducts.setOnClickListener(v -> startActivity(new Intent(this, ProductListActivity.class)));
        btnMyOrders.setOnClickListener(v -> startActivity(new Intent(this, MyOrdersActivity.class)));
        btnOffers.setOnClickListener(v -> startActivity(new Intent(this, OffersActivity.class)));
        btnGuidelines.setOnClickListener(v -> startActivity(new Intent(this, DesignGuidelinesActivity.class)));

        customerLogoutBtn.setOnClickListener(v -> {
            session.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}