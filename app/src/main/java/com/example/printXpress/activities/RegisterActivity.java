package com.example.printXpress.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.printXpress.R;
import com.example.printXpress.database.DatabaseHelper;
import com.example.printXpress.utils.Validator;

public class RegisterActivity extends AppCompatActivity {

    EditText regName, regEmail, regPhone, regPassword;
    Button registerBtn;
    TextView goLogin;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);

        regName = findViewById(R.id.regName);
        regEmail = findViewById(R.id.regEmail);
        regPhone = findViewById(R.id.regPhone);
        regPassword = findViewById(R.id.regPassword);
        registerBtn = findViewById(R.id.registerBtn);
        goLogin = findViewById(R.id.goLogin);

        registerBtn.setOnClickListener(v -> registerUser());

        goLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = regName.getText().toString().trim();
        String email = regEmail.getText().toString().trim();
        String phone = regPhone.getText().toString().trim();
        String password = regPassword.getText().toString().trim();

        if (Validator.isEmpty(name) || Validator.isEmpty(email) || 
            Validator.isEmpty(phone) || Validator.isEmpty(password)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Validator.isValidEmail(email)) {
            Toast.makeText(this, "Enter valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Validator.isValidPassword(password)) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Validator.isValidPhone(phone)) {
            Toast.makeText(this, "Enter valid phone number (10 digits)", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted = db.registerUser(name, email, phone, password);

        if (inserted) {
            Toast.makeText(this, "Registration successful", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Email already exists or registration failed", Toast.LENGTH_LONG).show();
        }
    }
}