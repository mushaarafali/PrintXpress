package com.example.printXpress.activities.customer;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.printXpress.R;
import com.example.printXpress.database.DatabaseHelper;
import com.example.printXpress.utils.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    EditText nameEdit, phoneEdit, addressEdit;
    TextView emailText;
    Button btnSave;
    SessionManager session;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        session = new SessionManager(this);
        db = new DatabaseHelper(this);

        nameEdit = findViewById(R.id.profileNameEdit);
        phoneEdit = findViewById(R.id.profilePhoneEdit);
        addressEdit = findViewById(R.id.profileAddressEdit);
        emailText = findViewById(R.id.profileEmailText);
        btnSave = findViewById(R.id.btnSaveProfile);

        loadUserData();

        btnSave.setOnClickListener(v -> {
            String name = nameEdit.getText().toString().trim();
            String phone = phoneEdit.getText().toString().trim();
            String address = addressEdit.getText().toString().trim();
            String email = session.getEmail();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.updateProfile(email, name, address, phone)) {
                session.updateProfile(name, phone);
                session.updateAddress(address);
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        String email = session.getEmail();
        emailText.setText(email);
        
        Cursor cursor = db.getUserData(email);
        if (cursor != null && cursor.moveToFirst()) {
            nameEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            phoneEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            addressEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow("default_address")));
            cursor.close();
        } else {
            // Fallback to session data if DB query fails
            nameEdit.setText(session.getName());
            phoneEdit.setText(session.getPhone());
            addressEdit.setText(session.getAddress());
        }
    }
}