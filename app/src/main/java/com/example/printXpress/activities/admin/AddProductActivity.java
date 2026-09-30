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

public class AddProductActivity extends AppCompatActivity {

    EditText productName, productCategory, productPrice, productDescription;
    Button saveProductBtn, selectImageBtn;
    ImageView productImageView;
    DatabaseHelper db;
    Uri imageUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = new DatabaseHelper(this);

        productName = findViewById(R.id.productName);
        productCategory = findViewById(R.id.productCategory);
        productPrice = findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.productDescription);
        productImageView = findViewById(R.id.productImageView);
        selectImageBtn = findViewById(R.id.selectImageBtn);
        saveProductBtn = findViewById(R.id.saveProductBtn);

        selectImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Product Image"), PICK_IMAGE_REQUEST);
        });

        saveProductBtn.setOnClickListener(v -> saveProduct());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            productImageView.setImageURI(imageUri);
        }
    }

    private void saveProduct() {
        String name = productName.getText().toString().trim();
        String category = productCategory.getText().toString().trim();
        String priceText = productPrice.getText().toString().trim();
        String description = productDescription.getText().toString().trim();

        if (name.isEmpty() || category.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceText);
        String imagePath = (imageUri != null) ? imageUri.toString() : "";

        if (price <= 0) {
            Toast.makeText(this, "Price must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // Using default material and size for simplicity
        boolean ok = db.addProduct(name, category, "Standard", "Default", price, description, imagePath);

        if (ok) {
            Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
        }
    }
}