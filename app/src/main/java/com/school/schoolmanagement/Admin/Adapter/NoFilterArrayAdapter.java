package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.Arrays;
import java.util.List;// Add this as a separate class or inner class
public class NoFilterArrayAdapter extends ArrayAdapter<String> {
    private List<String> items;

    public NoFilterArrayAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.items = items;
    }

    public NoFilterArrayAdapter(Context context, int resource, String[] items) {
        super(context, resource, items);
        this.items = Arrays.asList(items);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                results.values = items;
                results.count = items.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }
}

