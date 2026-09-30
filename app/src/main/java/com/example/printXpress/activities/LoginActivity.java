package com.example.printXpress.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.printXpress.R;
import com.example.printXpress.activities.admin.AdminDashboardActivity;
import com.example.printXpress.activities.customer.CustomerDashboardActivity;
import com.example.printXpress.activities.customer.ProductListActivity;
import com.example.printXpress.database.DatabaseHelper;
import com.example.printXpress.utils.SecurityUtils;
import com.example.printXpress.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button loginBtn;
    TextView goRegister;

    DatabaseHelper db;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        goRegister = findViewById(R.id.goRegister);

        loginBtn.setOnClickListener(v -> loginUser());

        goRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashedPassword = SecurityUtils.hashPassword(password);
        Cursor cursor = db.loginUser(email, hashedPassword);

        if (cursor != null && cursor.moveToFirst()) {

            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));

            session.createSession(name, email, role);

            cursor.close();

            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

            if (role.equals("admin")) {
                startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
            } else {
                startActivity(new Intent(LoginActivity.this, ProductListActivity.class));
            }

            finish();

        } else {
            if (cursor != null) {
                cursor.close();
            }

            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginActivity.this, ProductListActivity.class));
        finish();
    }
}