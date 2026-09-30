package com.example.printXpress.activities.customer;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printXpress.R;
import com.example.printXpress.activities.LoginActivity;
import com.example.printXpress.adapters.ProductAdapter;
import com.example.printXpress.database.DatabaseHelper;
import com.example.printXpress.models.Product;
import com.example.printXpress.utils.SessionManager;

import androidx.appcompat.widget.SearchView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {

    RecyclerView productRecyclerView;
    BottomNavigationView bottomNavigation;
    SearchView searchView;
    Button catAll, catCards, catMarketing, catPosters, catMerchandise;

    DatabaseHelper db;
    SessionManager session;

    ArrayList<Product> products = new ArrayList<>();
    ArrayList<Product> filteredProducts = new ArrayList<>();
    ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        productRecyclerView = findViewById(R.id.productRecyclerView);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        searchView = findViewById(R.id.searchView);

        catAll = findViewById(R.id.catAll);
        catCards = findViewById(R.id.catCards);
        catMarketing = findViewById(R.id.catMarketing);
        catPosters = findViewById(R.id.catPosters);
        catMerchandise = findViewById(R.id.catMerchandise);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        setupUI();
        setupBottomNavigation();
        setupFilters();

        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadProducts();

        adapter = new ProductAdapter(this, filteredProducts, product -> {
            if ("admin".equalsIgnoreCase(session.getRole())) {
                Toast.makeText(this, "Admin cannot place orders", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!session.isLoggedIn()) {
                Toast.makeText(this, "Please login to place an order", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                openOrderScreen(product);
            }
        });

        productRecyclerView.setAdapter(adapter);

        addSwipeAction();
    }

    private void setupUI() {
        if (session.isLoggedIn()) {
            bottomNavigation.getMenu().clear();
            bottomNavigation.inflateMenu(R.menu.customer_menu_logged);
        } else {
            bottomNavigation.getMenu().clear();
            bottomNavigation.inflateMenu(R.menu.customer_menu_guest);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_products) {
                // Already here
                return true;
            } else if (itemId == R.id.nav_offers) {
                startActivity(new Intent(this, OffersActivity.class));
                return true;
            } else if (itemId == R.id.nav_login) {
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            } else if (itemId == R.id.nav_orders) {
                startActivity(new Intent(this, MyOrdersActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.nav_logout) {
                session.logout();
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                recreate();
                return true;
            }
            return false;
        });
    }

    private void openOrderScreen(Product product) {
        Intent intent = new Intent(ProductListActivity.this, PlaceOrderActivity.class);
        intent.putExtra("product_name", product.name);
        intent.putExtra("price", product.price);
        intent.putExtra("image_path", product.imagePath);
        startActivity(intent);
    }

    private void loadProducts() {
        Cursor cursor = db.getAllProducts();
        products.clear();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    products.add(new Product(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("category")),
                            cursor.getString(cursor.getColumnIndexOrThrow("material")),
                            cursor.getString(cursor.getColumnIndexOrThrow("size")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                            cursor.getString(cursor.getColumnIndexOrThrow("description")),
                            cursor.getString(cursor.getColumnIndexOrThrow("image_path"))
                    ));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        filterProducts("", "All");
    }

    private void setupFilters() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query, "All");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText, "All");
                return true;
            }
        });

        catAll.setOnClickListener(v -> filterProducts(searchView.getQuery().toString(), "All"));
        catCards.setOnClickListener(v -> filterProducts(searchView.getQuery().toString(), "Cards"));
        catMarketing.setOnClickListener(v -> filterProducts(searchView.getQuery().toString(), "Marketing"));
        catPosters.setOnClickListener(v -> filterProducts(searchView.getQuery().toString(), "Posters"));
        catMerchandise.setOnClickListener(v -> filterProducts(searchView.getQuery().toString(), "Merchandise"));
    }

    private void filterProducts(String query, String category) {
        filteredProducts.clear();
        for (Product p : products) {
            boolean matchesQuery = p.name.toLowerCase().contains(query.toLowerCase());
            boolean matchesCategory = category.equals("All") || p.category.equalsIgnoreCase(category);

            if (matchesQuery && matchesCategory) {
                filteredProducts.add(p);
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void addSwipeAction() {
        ItemTouchHelper.SimpleCallback swipeCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                        int position = viewHolder.getAdapterPosition();
                        Product product = filteredProducts.get(position);

                        if ("admin".equalsIgnoreCase(session.getRole())) {
                            Toast.makeText(ProductListActivity.this, "Admin cannot place orders", Toast.LENGTH_SHORT).show();
                            adapter.notifyItemChanged(position);
                            return;
                        }

                        if (!session.isLoggedIn()) {
                            Toast.makeText(ProductListActivity.this, "Please login to order", Toast.LENGTH_SHORT).show();
                            adapter.notifyItemChanged(position);
                            startActivity(new Intent(ProductListActivity.this, LoginActivity.class));
                        } else {
                            openOrderScreen(product);
                            adapter.notifyItemChanged(position);
                        }
                    }
                };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(productRecyclerView);
    }
}
