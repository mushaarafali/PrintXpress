package com.example.printXpress.activities.customer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.printXpress.R;

public class DesignGuidelinesActivity extends AppCompatActivity {
    
    Button btnContactSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design_guidelines);

        btnContactSupport = findViewById(R.id.btnContactSupport);

        btnContactSupport.setOnClickListener(v -> {
            try {
                String phoneNumber = "+94770000000"; // Replace with real support number
                String url = "https://api.whatsapp.com/send?phone=" + phoneNumber;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } catch (Exception e) {
                Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}