package com.example.printXpress.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.printXpress.R;
import com.example.printXpress.models.Product;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    Context context;
    ArrayList<Product> products;
    OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(Context context, ArrayList<Product> products, OnProductClickListener listener) {
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productDetails, productPrice;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productDetails = itemView.findViewById(R.id.productDetails);
            productPrice = itemView.findViewById(R.id.productPrice);
        }
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_premium, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product p = products.get(position);

        holder.productName.setText(p.name);
        holder.productDetails.setText(p.category + " • " + p.material + " • " + p.size);
        holder.productPrice.setText("LKR " + p.price);

        if (p.imagePath != null && !p.imagePath.isEmpty()) {
            try {
                holder.productImage.setImageURI(Uri.parse(p.imagePath));
            } catch (Exception e) {
                setCategoryPlaceholder(holder.productImage, p.category);
            }
        } else {
            setCategoryPlaceholder(holder.productImage, p.category);
        }

        holder.itemView.setOnClickListener(v -> listener.onProductClick(p));
    }

    private void setCategoryPlaceholder(ImageView iv, String category) {
        if (category.equalsIgnoreCase("Cards")) {
            iv.setImageResource(R.drawable.ic_products); // Geometric shapes
        } else if (category.equalsIgnoreCase("Marketing") || category.equalsIgnoreCase("Posters")) {
            iv.setImageResource(R.drawable.ic_offers); // Tag/Poster feel
        } else if (category.equalsIgnoreCase("Merchandise")) {
            iv.setImageResource(R.drawable.ic_profile); // Person/T-shirt feel
        } else {
            iv.setImageResource(R.drawable.ic_launcher_foreground);
        }
        iv.setPadding(20, 20, 20, 20);
        iv.setBackgroundColor(context.getResources().getColor(R.color.premium_blue_light));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public Product getProductAt(int position) {
        return products.get(position);
    }

    public void removeProduct(int position) {
        products.remove(position);
        notifyItemRemoved(position);
    }
}