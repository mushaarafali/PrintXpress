package com.example.printXpress.activities.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.printXpress.R;
import com.example.printXpress.database.DatabaseHelper;

import java.util.ArrayList;

public class ManageOffersActivity extends AppCompatActivity {

    EditText offerTitle, offerDescription;
    Button saveOfferBtn;
    ListView offersListView;
    DatabaseHelper db;

    private static class OfferModel {
        int id;
        String title, description;
        OfferModel(int id, String title, String description) {
            this.id = id;
            this.title = title;
            this.description = description;
        }
    }

    ArrayList<OfferModel> offersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_offers);

        db = new DatabaseHelper(this);

        offerTitle = findViewById(R.id.offerTitle);
        offerDescription = findViewById(R.id.offerDescription);
        saveOfferBtn = findViewById(R.id.saveOfferBtn);
        offersListView = findViewById(R.id.offersListView);

        loadOffers();

        saveOfferBtn.setOnClickListener(v -> {
            String title = offerTitle.getText().toString().trim();
            String desc = offerDescription.getText().toString().trim();

            if (title.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.addOffer(title, desc)) {
                Toast.makeText(this, "Offer added", Toast.LENGTH_SHORT).show();
                offerTitle.setText("");
                offerDescription.setText("");
                loadOffers();
            }
        });
    }

    private void loadOffers() {
        Cursor cursor = db.getAllOffers();
        offersList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            offersList.add(new OfferModel(id, title, desc));
        }
        cursor.close();

        AdminOfferAdapter adapter = new AdminOfferAdapter(this, offersList);
        offersListView.setAdapter(adapter);
    }

    private class AdminOfferAdapter extends ArrayAdapter<OfferModel> {
        public AdminOfferAdapter(Context context, ArrayList<OfferModel> offers) {
            super(context, 0, offers);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_offer_admin, parent, false);
            }

            OfferModel offer = getItem(position);
            TextView info = convertView.findViewById(R.id.adminOfferInfo);
            Button btnEdit = convertView.findViewById(R.id.btnEditOffer);
            Button btnDelete = convertView.findViewById(R.id.btnDeleteOffer);

            info.setText("Title: " + offer.title + "\nDescription: " + offer.description);

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), EditOfferActivity.class);
                intent.putExtra("offer_id", offer.id);
                intent.putExtra("title", offer.title);
                intent.putExtra("description", offer.description);
                startActivity(intent);
            });

            btnDelete.setOnClickListener(v -> {
                if (db.deleteOffer(offer.id)) {
                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    loadOffers();
                }
            });

            return convertView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOffers();
    }
}