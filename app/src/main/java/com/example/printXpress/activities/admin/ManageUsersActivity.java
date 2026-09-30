package com.example.printXpress.activities.admin;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.printXpress.R;
import com.example.printXpress.database.DatabaseHelper;

import java.util.ArrayList;

public class ManageUsersActivity extends AppCompatActivity {

    ListView manageUsersList;
    DatabaseHelper db;
    ArrayList<String> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        manageUsersList = findViewById(R.id.manageUsersList);
        db = new DatabaseHelper(this);

        loadUsers();
    }

    private void loadUsers() {
        Cursor cursor = db.getAllUsers();
        users.clear();

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));

            users.add("Name: " + name +
                    "\nEmail: " + email +
                    "\nPhone: " + phone +
                    "\nRole: " + role.toUpperCase());
        }

        cursor.close();

        manageUsersList.setAdapter(new ArrayAdapter<>(
                this,
                R.layout.item_premium_list,
                R.id.premiumItemText,
                users
        ));
    }
}