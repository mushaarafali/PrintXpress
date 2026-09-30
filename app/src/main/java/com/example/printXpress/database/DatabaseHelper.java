package com.example.printXpress.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.printXpress.utils.SecurityUtils;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "printxpress.db";
    private static final int DB_VERSION = 6;
    private final Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "phone TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL DEFAULT 'customer', " +
                "default_address TEXT)");

        db.execSQL("CREATE TABLE products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "material TEXT NOT NULL, " +
                "size TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "description TEXT, " +
                "image_path TEXT)");

        db.execSQL("CREATE TABLE orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_email TEXT NOT NULL, " +
                "product_name TEXT NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "total_price REAL NOT NULL, " +
                "instructions TEXT, " +
                "delivery_type TEXT NOT NULL, " +
                "design_path TEXT, " +
                "status TEXT NOT NULL DEFAULT 'Processing')");

        db.execSQL("CREATE TABLE offers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "description TEXT NOT NULL)");

        insertDefaultData(db);
    }

    private void insertDefaultData(SQLiteDatabase db) {
        ContentValues admin = new ContentValues();
        admin.put("name", "Admin");
        admin.put("email", "admin@gmail.com");
        admin.put("phone", "0770000000");
        admin.put("password", SecurityUtils.hashPassword("admin123"));
        admin.put("role", "admin");
        db.insert("users", null, admin);

        String pkgName = "android.resource://" + mContext.getPackageName() + "/drawable/";

        addProductSeed(db, "Business Card", "Cards", "Art Board", "3.5 x 2 inch", 1200, "Premium business card printing.", pkgName + "card");
        addProductSeed(db, "Flyer", "Marketing", "Glossy Paper", "A5", 2500, "High quality flyer printing.", pkgName + "flyer");
        addProductSeed(db, "Poster", "Posters", "Matt Paper", "A3", 1800, "Color poster printing.", pkgName + "poster");
        addProductSeed(db, "Banner", "Large Format", "Flex", "6 x 3 ft", 4500, "Outdoor banner printing.", pkgName + "banner");
        addProductSeed(db, "T-Shirt", "Merchandise", "Cotton", "S/M/L/XL", 3500, "Custom printed t-shirts.", pkgName + "tshirt");
        addProductSeed(db, "Mug", "Merchandise", "Ceramic", "Standard", 2200, "Customized mug printing.", pkgName + "mug");

        ContentValues offer1 = new ContentValues();
        offer1.put("title", "Festival Printing Offer");
        offer1.put("description", "Get 15% off for bulk flyers and posters.");
        db.insert("offers", null, offer1);

        ContentValues offer2 = new ContentValues();
        offer2.put("title", "First Order Discount");
        offer2.put("description", "Enjoy 10% off on your very first order with PrintXpress!");
        db.insert("offers", null, offer2);

        ContentValues offer3 = new ContentValues();
        offer3.put("title", "Business Bundle");
        offer3.put("description", "Order 500 Business Cards and get 50 Flyers free.");
        db.insert("offers", null, offer3);
    }

    private void addProductSeed(SQLiteDatabase db, String name, String category, String material, String size, double price, String desc, String imagePath) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("category", category);
        values.put("material", material);
        values.put("size", size);
        values.put("price", price);
        values.put("description", desc);
        values.put("image_path", imagePath);
        db.insert("products", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS offers");
        onCreate(db);
    }

    public boolean registerUser(String name, String email, String phone, String password) {
        if (checkEmailExists(email)) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("phone", phone);
        // Ensure password is hashed before saving
        values.put("password", SecurityUtils.hashPassword(password));
        values.put("role", "customer");

        long result = db.insert("users", null, values);
        android.util.Log.d("DatabaseHelper", "registerUser result: " + result);
        return result != -1;
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE email=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Cursor loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE email=? AND password=?", new String[]{email, password});
    }

    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM products ORDER BY id DESC", null);
    }

    public boolean addProduct(String name, String category, String material, String size, double price, String description, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("category", category);
        values.put("material", material);
        values.put("size", size);
        values.put("price", price);
        values.put("description", description);
        values.put("image_path", imagePath);

        return db.insert("products", null, values) != -1;
    }

    public boolean updateProduct(int id, String name, String category, String material, String size, double price, String description, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("category", category);
        values.put("material", material);
        values.put("size", size);
        values.put("price", price);
        values.put("description", description);
        values.put("image_path", imagePath);
        
        int rows = db.update("products", values, "id=?", new String[]{String.valueOf(id)});
        android.util.Log.d("DatabaseHelper", "Update product ID: " + id + " | Rows affected: " + rows);
        return rows > 0;
    }

    public boolean deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("products", "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean placeOrder(String email, String product, int quantity, double total, String instructions, String deliveryType, String designPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("customer_email", email);
        values.put("product_name", product);
        values.put("quantity", quantity);
        values.put("total_price", total);
        values.put("instructions", instructions);
        values.put("delivery_type", deliveryType);
        values.put("design_path", designPath);
        values.put("status", "Processing");

        return db.insert("orders", null, values) != -1;
    }

    public Cursor getOrdersByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM orders WHERE customer_email=? ORDER BY id DESC", new String[]{email});
    }

    public Cursor getAllOrders() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM orders ORDER BY id DESC", null);
    }

    public Cursor getOrderById(int orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM orders WHERE id=?", new String[]{String.valueOf(orderId)});
    }

    public boolean cancelOrder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", "Cancelled");
        return db.update("orders", values, "id=? AND status='Processing'", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean updateOrderStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        return db.update("orders", values, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean updateProfile(String email, String name, String address, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("default_address", address);
        values.put("phone", phone);
        return db.update("users", values, "email=?", new String[]{email}) > 0;
    }

    public Cursor getUserData(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE email=?", new String[]{email});
    }

    public String getDefaultAddress(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT default_address FROM users WHERE email=?", new String[]{email});
        String resAddress = "";
        if (cursor.moveToFirst()) {
            resAddress = cursor.getString(0);
        }
        cursor.close();
        return resAddress;
    }

    public Cursor getAllOffers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM offers ORDER BY id DESC", null);
    }

    public boolean addOffer(String title, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        return db.insert("offers", null, values) != -1;
    }

    public boolean updateOffer(int id, String title, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        return db.update("offers", values, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteOffer(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("offers", "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id, name, email, phone, role FROM users ORDER BY id DESC", null);
    }
}