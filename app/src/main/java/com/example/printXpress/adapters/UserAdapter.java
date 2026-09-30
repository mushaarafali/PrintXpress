package com.example.printXpress.adapters;

import android.content.Context;
import android.view.*;
import android.widget.TextView;
import android.widget.BaseAdapter;

import com.example.printXpress.models.User;

import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {

    Context context;
    ArrayList<User> users;

    public UserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return users.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        User u = users.get(position);

        textView.setText("ID: " + u.id +
                "\nName: " + u.name +
                "\nEmail: " + u.email +
                "\nPhone: " + u.phone +
                "\nRole: " + u.role);

        textView.setTextSize(16);
        textView.setPadding(24, 20, 24, 20);

        return textView;
    }
}