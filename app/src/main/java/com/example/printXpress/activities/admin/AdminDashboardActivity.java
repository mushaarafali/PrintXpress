package com.example.printXpress.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.printXpress.R;
import com.example.printXpress.activities.LoginActivity;
import com.example.printXpress.utils.SessionManager;

public class AdminDashboardActivity extends AppCompatActivity {

    TextView adminWelcome;
    Button adminLogoutBtn, btnAddProduct, btnManageProducts, btnManageOrders, btnManageUsers, btnManageOffers;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        session = new SessionManager(this);

        adminWelcome = findViewById(R.id.adminWelcome);
        adminLogoutBtn = findViewById(R.id.adminLogoutBtn);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnManageProducts = findViewById(R.id.btnManageProducts);
        btnManageOrders = findViewById(R.id.btnManageOrders);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnManageOffers = findViewById(R.id.btnManageOffers);

        adminWelcome.setText("Welcome System Admin!");

        btnAddProduct.setOnClickListener(v -> startActivity(new Intent(this, AddProductActivity.class)));
        btnManageProducts.setOnClickListener(v -> startActivity(new Intent(this, ManageProductsActivity.class)));
        btnManageOrders.setOnClickListener(v -> startActivity(new Intent(this, ManageOrdersActivity.class)));
        btnManageUsers.setOnClickListener(v -> startActivity(new Intent(this, ManageUsersActivity.class)));
        btnManageOffers.setOnClickListener(v -> startActivity(new Intent(this, ManageOffersActivity.class)));

        adminLogoutBtn.setOnClickListener(v -> {
            session.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}