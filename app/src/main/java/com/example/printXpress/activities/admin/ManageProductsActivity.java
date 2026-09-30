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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.printXpress.R;
import com.example.printXpress.database.DatabaseHelper;
import com.example.printXpress.models.Product;

import java.util.ArrayList;

public class ManageProductsActivity extends AppCompatActivity {

    ListView manageProductsList;
    DatabaseHelper db;
    ArrayList<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        manageProductsList = findViewById(R.id.manageProductsList);
        db = new DatabaseHelper(this);

        loadProducts();
    }

    private void loadProducts() {
        Cursor cursor = db.getAllProducts();
        productList.clear();

        while (cursor.moveToNext()) {
            Product p = new Product(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("category")),
                    cursor.getString(cursor.getColumnIndexOrThrow("material")),
                    cursor.getString(cursor.getColumnIndexOrThrow("size")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                    cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    cursor.getString(cursor.getColumnIndexOrThrow("image_path"))
            );
            productList.add(p);
        }
        cursor.close();

        AdminProductAdapter adapter = new AdminProductAdapter(this, productList);
        manageProductsList.setAdapter(adapter);
    }

    private class AdminProductAdapter extends ArrayAdapter<Product> {
        public AdminProductAdapter(Context context, ArrayList<Product> products) {
            super(context, 0, products);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_product_admin, parent, false);
            }

            Product product = getItem(position);
            TextView info = convertView.findViewById(R.id.adminProductInfo);
            Button btnEdit = convertView.findViewById(R.id.btnEditProduct);
            Button btnDelete = convertView.findViewById(R.id.btnDeleteProduct);

            info.setText("Name: " + product.name + "\nCategory: " + product.category + "\nPrice: LKR " + product.price);

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), EditProductActivity.class);
                intent.putExtra("product_id", product.id);
                intent.putExtra("name", product.name);
                intent.putExtra("category", product.category);
                intent.putExtra("price", product.price);
                intent.putExtra("description", product.description);
                intent.putExtra("image_path", product.imagePath);
                startActivity(intent);
            });

            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Product")
                        .setMessage("Are you sure you want to delete this product?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            if (db.deleteProduct(product.id)) {
                                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                loadProducts();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            return convertView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }
}