package com.example.printXpress.activities.customer;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.printXpress.R;
import com.example.printXpress.database.DatabaseHelper;

import java.util.ArrayList;

public class OffersActivity extends AppCompatActivity {

    ListView offersList;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        offersList = findViewById(R.id.offersList);
        db = new DatabaseHelper(this);

        Cursor cursor = db.getAllOffers();
        ArrayList<String> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndexOrThrow("title")) +
                    "\n" +
                    cursor.getString(cursor.getColumnIndexOrThrow("description")));
        }

        cursor.close();

        offersList.setAdapter(new ArrayAdapter<>(
                this,
                R.layout.item_premium_list,
                R.id.premiumItemText,
                list
        ));
    }
}