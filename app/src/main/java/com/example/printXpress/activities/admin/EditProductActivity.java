package com.example.printXpress.activities.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.printXpress.R;
import com.example.printXpress.database.DatabaseHelper;

public class EditProductActivity extends AppCompatActivity {

    EditText editProductName, editProductCategory, editProductPrice, editProductDescription;
    ImageView editProductImageView;
    Button updateProductBtn, btnChangeImage;
    DatabaseHelper db;
    int productId;
    Uri imageUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        db = new DatabaseHelper(this);

        productId = getIntent().getIntExtra("product_id", -1);
        String name = getIntent().getStringExtra("name");
        String category = getIntent().getStringExtra("category");
        double price = getIntent().getDoubleExtra("price", 0);
        String desc = getIntent().getStringExtra("description");
        String imagePath = getIntent().getStringExtra("image_path");

        editProductName = findViewById(R.id.editProductName);
        editProductCategory = findViewById(R.id.editProductCategory);
        editProductPrice = findViewById(R.id.editProductPrice);
        editProductDescription = findViewById(R.id.editProductDescription);
        editProductImageView = findViewById(R.id.editProductImageView);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        updateProductBtn = findViewById(R.id.updateProductBtn);

        editProductName.setText(name);
        editProductCategory.setText(category);
        editProductPrice.setText(String.valueOf(price));
        editProductDescription.setText(desc);

        if (imagePath != null && !imagePath.isEmpty()) {
            imageUri = Uri.parse(imagePath);
            editProductImageView.setImageURI(imageUri);
        }

        btnChangeImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Product Image"), PICK_IMAGE_REQUEST);
        });

        updateProductBtn.setOnClickListener(v -> updateProduct());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            editProductImageView.setImageURI(imageUri);
        }
    }

    private void updateProduct() {
        String name = editProductName.getText().toString().trim();
        String category = editProductCategory.getText().toString().trim();
        String priceText = editProductPrice.getText().toString().trim();
        String desc = editProductDescription.getText().toString().trim();

        if (name.isEmpty() || category.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceText);
        String finalImagePath = (imageUri != null) ? imageUri.toString() : "";

        android.util.Log.d("EditProduct", "Updating ID: " + productId + " | Name: " + name);

        if (db.updateProduct(productId, name, category, "Default", "Standard", price, desc, finalImagePath)) {
            Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }
}