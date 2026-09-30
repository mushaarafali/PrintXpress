package com.example.printXpress.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.printXpress.R;
import com.example.printXpress.database.DatabaseHelper;
import com.example.printXpress.models.Order;

import java.util.ArrayList;

public class OrderAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Order> orders;
    private DatabaseHelper db;
    private OnOrderCancelledListener listener;

    public interface OnOrderCancelledListener {
        void onOrderCancelled();
    }

    public OrderAdapter(Context context, ArrayList<Order> orders, OnOrderCancelledListener listener) {
        this.context = context;
        this.orders = orders;
        this.db = new DatabaseHelper(context);
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return orders.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        }

        Order order = orders.get(position);

        TextView idText = convertView.findViewById(R.id.orderIdText);
        TextView productText = convertView.findViewById(R.id.orderProductText);
        TextView priceText = convertView.findViewById(R.id.orderPriceText);
        TextView statusText = convertView.findViewById(R.id.orderStatusText);
        Button cancelBtn = convertView.findViewById(R.id.cancelOrderBtn);

        idText.setText("Order ID: #" + order.id);
        productText.setText(order.productName + " (x" + order.quantity + ")");
        priceText.setText("Total: LKR " + String.format("%.2f", order.totalPrice));
        statusText.setText("Status: " + order.status);

        if ("Processing".equalsIgnoreCase(order.status)) {
            cancelBtn.setVisibility(View.VISIBLE);
            statusText.setTextColor(context.getResources().getColor(R.color.status_pending));
            cancelBtn.setOnClickListener(v -> showCancelDialog(order.id));
        } else {
            cancelBtn.setVisibility(View.GONE);
            if ("Cancelled".equalsIgnoreCase(order.status)) {
                statusText.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            } else {
                statusText.setTextColor(context.getResources().getColor(R.color.status_success));
            }
        }

        return convertView;
    }

    private void showCancelDialog(int orderId) {
        new AlertDialog.Builder(context)
                .setTitle("Cancel Order")
                .setMessage("Are you sure you want to cancel this order?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    if (db.cancelOrder(orderId)) {
                        Toast.makeText(context, "Order cancelled", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onOrderCancelled();
                        }
                    } else {
                        Toast.makeText(context, "Could not cancel order", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}