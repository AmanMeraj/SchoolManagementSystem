package com.school.schoolmanagement.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Model.IntroItem;
import com.school.schoolmanagement.R;

import java.util.List;

public class IntroAdapter extends RecyclerView.Adapter<IntroAdapter.IntroViewHolder> {

    private final List<IntroItem> introItems;

    public IntroAdapter(List<IntroItem> introItems) {
        this.introItems = introItems;
    }

    @NonNull
    @Override
    public IntroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_intro, parent, false);
        return new IntroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IntroViewHolder holder, int position) {
        holder.bind(introItems.get(position));
    }

    @Override
    public int getItemCount() {
        return introItems.size();
    }

    static class IntroViewHolder extends RecyclerView.ViewHolder {

        private final TextView title, description;
        private final ImageView image;

        public IntroViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            image = itemView.findViewById(R.id.image);
        }

        public void bind(IntroItem item) {
            title.setText(item.getTitle());
            description.setText(item.getDescription());
            image.setImageResource(item.getImageRes());
        }
    }
}

