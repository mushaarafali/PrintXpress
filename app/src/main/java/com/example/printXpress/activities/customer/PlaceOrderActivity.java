package com.example.printXpress.activities.customer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.printXpress.R;
import com.example.printXpress.activities.LoginActivity;
import com.example.printXpress.database.DatabaseHelper;
import com.example.printXpress.utils.SessionManager;

public class PlaceOrderActivity extends AppCompatActivity {

    TextView orderProductName, totalText, fileName;
    ImageView orderProductImage;
    EditText orderQuantity, orderInstructions, deliveryName, deliveryAddress, deliveryPhone;
    Spinner deliverySpinner;
    Button placeOrderBtn, uploadBtn;
    androidx.cardview.widget.CardView deliveryCard;

    DatabaseHelper db;
    SessionManager session;

    String productName, imagePath;
    double price;
    Uri fileUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if ("admin".equalsIgnoreCase(session.getRole())) {
            Toast.makeText(this, "Admin cannot place orders", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_place_order);

        db = new DatabaseHelper(this);

        productName = getIntent().getStringExtra("product_name");
        price = getIntent().getDoubleExtra("price", 0);
        imagePath = getIntent().getStringExtra("image_path");

        orderProductName = findViewById(R.id.orderProductName);
        orderProductImage = findViewById(R.id.orderProductImage);
        totalText = findViewById(R.id.totalText);
        fileName = findViewById(R.id.fileName);

        orderQuantity = findViewById(R.id.orderQuantity);
        orderInstructions = findViewById(R.id.orderInstructions);
        deliveryName = findViewById(R.id.deliveryName);
        deliveryAddress = findViewById(R.id.deliveryAddress);
        deliveryPhone = findViewById(R.id.deliveryPhone);

        deliverySpinner = findViewById(R.id.deliverySpinner);
        placeOrderBtn = findViewById(R.id.placeOrderBtn);
        uploadBtn = findViewById(R.id.uploadBtn);
        deliveryCard = findViewById(R.id.deliveryCard);

        deliveryCard.setVisibility(View.GONE);

        if (productName == null || productName.isEmpty()) {
            productName = "Selected Product";
        }

        orderProductName.setText(productName + "\nLKR " + price);

        if (imagePath != null && !imagePath.isEmpty()) {
            orderProductImage.setImageURI(Uri.parse(imagePath));
        }

        String[] deliveryTypes = {"Take Away", "Home Delivery"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                deliveryTypes
        );

        deliverySpinner.setAdapter(adapter);

        deliverySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = parent.getItemAtPosition(position).toString();

                if (type.equals("Home Delivery")) {
                    deliveryCard.setVisibility(View.VISIBLE);
                    deliveryName.setVisibility(View.VISIBLE);
                    deliveryAddress.setVisibility(View.VISIBLE);
                    deliveryPhone.setVisibility(View.VISIBLE);
                    
                    // Auto-fill address if available
                    String savedAddress = db.getDefaultAddress(session.getEmail());
                    if (savedAddress != null && !savedAddress.isEmpty()) {
                        deliveryAddress.setText(savedAddress);
                    }
                    deliveryName.setText(session.getName());
                } else {
                    deliveryCard.setVisibility(View.GONE);
                    deliveryName.setVisibility(View.GONE);
                    deliveryAddress.setVisibility(View.GONE);
                    deliveryPhone.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        uploadBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(
                    Intent.createChooser(intent, "Select Design Image"),
                    PICK_IMAGE_REQUEST
            );
        });

        // 💰 Real-time Total Calculation
        orderQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        placeOrderBtn.setOnClickListener(v -> placeOrder());
    }

    private void calculateTotal() {
        String qtyText = orderQuantity.getText().toString().trim();
        if (!qtyText.isEmpty()) {
            try {
                int qty = Integer.parseInt(qtyText);
                double total = qty * price;
                totalText.setText(String.format("Total: LKR %.2f", total));
            } catch (NumberFormatException e) {
                totalText.setText("Total: LKR 0.00");
            }
        } else {
            totalText.setText("Total: LKR 0.00");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            fileName.setText("Design image selected");
        }
    }

    private void placeOrder() {
        String email = session.getEmail();

        if (email == null || email.trim().isEmpty()) {
            Toast.makeText(this, "Session error. Please login again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String qtyText = orderQuantity.getText().toString().trim();
        String instructions = orderInstructions.getText().toString().trim();
        String deliveryType = deliverySpinner.getSelectedItem().toString();

        if (qtyText.isEmpty()) {
            Toast.makeText(this, "Enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        int qty;

        try {
            qty = Integer.parseInt(qtyText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter valid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        if (qty <= 0) {
            Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fileUri == null) {
            Toast.makeText(this, "Please upload design image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (deliveryType.equals("Home Delivery")) {
            String name = deliveryName.getText().toString().trim();
            String address = deliveryAddress.getText().toString().trim();
            String phone = deliveryPhone.getText().toString().trim();

            if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Fill delivery name, address and phone", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phone.length() < 10) {
                Toast.makeText(this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            instructions = instructions +
                    "\nDelivery Name: " + name +
                    "\nDelivery Address: " + address +
                    "\nDelivery Phone: " + phone;
        }

        double total = qty * price;
        totalText.setText("Total: LKR " + total);

        String designPath = (fileUri != null) ? fileUri.toString() : "";

        boolean ok = db.placeOrder(
                email,
                productName,
                qty,
                total,
                instructions,
                deliveryType,
                designPath
        );

        if (ok) {
            Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Order failed", Toast.LENGTH_SHORT).show();
        }
    }
}