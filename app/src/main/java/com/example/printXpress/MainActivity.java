package com.example.printXpress;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.printXpress.activities.customer.ProductListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        startActivity(new Intent(this, ProductListActivity.class));
        finish();
    }
}