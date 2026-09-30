package com.example.printXpress.adapters;

import android.content.Context;
import android.view.*;
import android.widget.TextView;
import android.widget.BaseAdapter;

import com.example.printXpress.models.Offer;

import java.util.ArrayList;

public class OfferAdapter extends BaseAdapter {

    Context context;
    ArrayList<Offer> offers;

    public OfferAdapter(Context context, ArrayList<Offer> offers) {
        this.context = context;
        this.offers = offers;
    }

    @Override
    public int getCount() {
        return offers.size();
    }

    @Override
    public Object getItem(int position) {
        return offers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return offers.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        Offer o = offers.get(position);

        textView.setText(o.title + "\n" + o.description);
        textView.setTextSize(17);
        textView.setPadding(24, 20, 24, 20);

        return textView;
    }
}