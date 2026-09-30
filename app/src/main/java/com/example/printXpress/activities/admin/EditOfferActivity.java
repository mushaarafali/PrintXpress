package com.example.printXpress.activities.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.printXpress.R;
import com.example.printXpress.database.DatabaseHelper;

public class EditOfferActivity extends AppCompatActivity {

    EditText editOfferTitle, editOfferDescription;
    Button updateOfferBtn;
    DatabaseHelper db;
    int offerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_offer);

        db = new DatabaseHelper(this);

        offerId = getIntent().getIntExtra("offer_id", -1);
        String title = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("description");

        editOfferTitle = findViewById(R.id.editOfferTitle);
        editOfferDescription = findViewById(R.id.editOfferDescription);
        updateOfferBtn = findViewById(R.id.updateOfferBtn);

        editOfferTitle.setText(title);
        editOfferDescription.setText(desc);

        updateOfferBtn.setOnClickListener(v -> {
            String newTitle = editOfferTitle.getText().toString().trim();
            String newDesc = editOfferDescription.getText().toString().trim();

            if (newTitle.isEmpty() || newDesc.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.updateOffer(offerId, newTitle, newDesc)) {
                Toast.makeText(this, "Offer updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}