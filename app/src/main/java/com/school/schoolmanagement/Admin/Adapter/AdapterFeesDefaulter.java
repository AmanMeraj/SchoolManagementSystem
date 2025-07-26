package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Model.FeesDefaulterModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.RowFeesDefaultersBinding;

import java.util.List;

public class AdapterFeesDefaulter extends RecyclerView.Adapter<AdapterFeesDefaulter.ViewHolder> {
    private Context context;
    private List<FeesDefaulterModel.Datum> defaulterList;
    private OnDefaulterActionListener actionListener;

    // Interface for handling actions
    public interface OnDefaulterActionListener {
        void onCallClick(FeesDefaulterModel.Datum defaulter);
        void onSubmitClick(FeesDefaulterModel.Datum defaulter);
        void onItemClick(FeesDefaulterModel.Datum defaulter);
    }

    public AdapterFeesDefaulter(Context context, List<FeesDefaulterModel.Datum> defaulterList) {
        this.context = context;
        this.defaulterList = defaulterList;
    }

    public void setOnDefaulterActionListener(OnDefaulterActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowFeesDefaultersBinding binding = RowFeesDefaultersBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeesDefaulterModel.Datum defaulter = defaulterList.get(position);

        // Bind data to views
        holder.bindData(defaulter, position);

        // Set click listeners
        holder.setClickListeners(defaulter);
    }

    @Override
    public int getItemCount() {
        return defaulterList != null ? defaulterList.size() : 0;
    }

    // Method to update the list
    public void updateList(List<FeesDefaulterModel.Datum> newList) {
        if (newList != null) {
            this.defaulterList.clear();
            this.defaulterList.addAll(newList);
            notifyDataSetChanged();
        }
    }

    // Method to add single item
    public void addItem(FeesDefaulterModel.Datum defaulter) {
        if (defaulter != null) {
            defaulterList.add(defaulter);
            notifyItemInserted(defaulterList.size() - 1);
        }
    }

    // Method to remove item
    public void removeItem(int position) {
        if (position >= 0 && position < defaulterList.size()) {
            defaulterList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, defaulterList.size());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final RowFeesDefaultersBinding binding;

        public ViewHolder(@NonNull RowFeesDefaultersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(FeesDefaulterModel.Datum defaulter, int position) {
            // Bind basic data
            binding.numberTv.setText(defaulter.getNumber() != null ?
                    defaulter.getNumber() : "N/A");
            binding.nameTv.setText(defaulter.getName() != null ?
                    defaulter.getName() : "Unknown");

            // Handle profile image from base64
            if (defaulter.getProfilePicture() != null && !defaulter.getProfilePicture().trim().isEmpty()) {
                try {
                    // Remove data:image/jpeg;base64, or similar prefix if present
                    String base64Image = defaulter.getProfilePicture();
                    if (base64Image.contains(",")) {
                        base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
                    }

                    // Decode base64 to bitmap
                    byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                    if (bitmap != null) {
                        binding.imgProfile.setImageBitmap(bitmap);
                    } else {
                        // Set default image if bitmap is null
                        binding.imgProfile.setImageResource(R.drawable.avatar2);
                    }
                } catch (Exception e) {
                    // Handle any exception during base64 decoding
                    binding.imgProfile.setImageResource(R.drawable.avatar2);
                }
            } else {
                // Set default image if no image available
                binding.imgProfile.setImageResource(R.drawable.avatar2);
            }
        }

        public void setClickListeners(FeesDefaulterModel.Datum defaulter) {
            // Call button click - Opens dial pad with the number
            binding.callBtn.setOnClickListener(v -> {
                String phoneNumber = defaulter.getNumber();
                if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + phoneNumber));
                        context.startActivity(intent);

                        // Notify listener if set
                        if (actionListener != null) {
                            actionListener.onCallClick(defaulter);
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Unable to make call", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Phone number not available", Toast.LENGTH_SHORT).show();
                }
            });

            // Submit button click - Handle fee submission or reminder
            binding.btnSubmit.setOnClickListener(v -> {
                String studentName = defaulter.getName() != null ?
                        defaulter.getName() : "Unknown Student";
                Toast.makeText(context, "Processing for " + studentName, Toast.LENGTH_SHORT).show();

                // Notify listener if set
                if (actionListener != null) {
                    actionListener.onSubmitClick(defaulter);
                }
            });

            // Item click - Show detailed information
            itemView.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onItemClick(defaulter);
                } else {
                    // Default action - show basic info
                    String info = "Student: " + (defaulter.getName() != null ?
                            defaulter.getName() : "N/A") +
                            "\nPhone: " + (defaulter.getNumber() != null ?
                            defaulter.getNumber() : "N/A");
                    Toast.makeText(context, info, Toast.LENGTH_LONG).show();
                }
            });

            // Long click for additional actions
            itemView.setOnLongClickListener(v -> {
                // You can implement context menu or additional options here
                Toast.makeText(context, "Long press detected", Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }
}