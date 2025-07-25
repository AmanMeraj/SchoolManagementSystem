package com.school.schoolmanagement.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.Model.FeesParticular;
import com.school.schoolmanagement.Admin.Model.FeesStructure;
import com.school.schoolmanagement.Admin.Model.FeesStructureRequest;
import com.school.schoolmanagement.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeesParticularsAdapter extends RecyclerView.Adapter<FeesParticularsAdapter.ViewHolder> {

    private Context context;
    private List<FeesParticular> feesParticularsList;
    private OnDataChangeListener onDataChangeListener;

    public interface OnDataChangeListener {
        void onDataChanged(List<FeesParticular> updatedList);
    }

    public FeesParticularsAdapter(Context context) {
        this.context = context;
        this.feesParticularsList = new ArrayList<>();
        // Initialize with one empty editable item
        feesParticularsList.add(new FeesParticular("", "", false));
    }
    public void clearAll() {
        feesParticularsList.clear(); // assuming your list is called feesList
        notifyDataSetChanged();
    }

    public void setOnDataChangeListener(OnDataChangeListener listener) {
        this.onDataChangeListener = listener;
    }

    public void addNewItem() {
        feesParticularsList.add(new FeesParticular("", "", false));
        notifyItemInserted(feesParticularsList.size() - 1);
        if (onDataChangeListener != null) {
            onDataChangeListener.onDataChanged(feesParticularsList);
        }
    }

    public void removeLastItem() {
        if (feesParticularsList.size() > 1) { // Keep at least one item
            int lastPosition = feesParticularsList.size() - 1;
            feesParticularsList.remove(lastPosition);
            notifyItemRemoved(lastPosition);
            if (onDataChangeListener != null) {
                onDataChangeListener.onDataChanged(feesParticularsList);
            }
        }
    }

    public List<FeesParticular> getFeesParticularsList() {
        return feesParticularsList;
    }

    public FeesStructureRequest generateFeesStructureRequest() {
        FeesStructureRequest request = new FeesStructureRequest();
        FeesStructure feesStructure = new FeesStructure();

        for (FeesParticular particular : feesParticularsList) {
            String feesName = particular.getFeesName().trim();
            String amount = particular.getAmount().trim();

            // Only add if both name and amount are not empty
            if (!feesName.isEmpty() && !amount.isEmpty()) {
                // Convert fees name to the appropriate key format
                String feeKey = convertToFeeKey(feesName);
                feesStructure.setFee(feeKey, amount);
            }
        }

        request.setFeesStructure(feesStructure);
        return request;
    }

    /**
     * Convert display name to fee key format
     * e.g., "Tuition Fees" -> "tuitionFees"
     *      "Transport Fees" -> "transportFees"
     *      "Library Fee" -> "libraryFee"
     */
    private String convertToFeeKey(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            return "";
        }

        // Handle common default fees
        String lowerName = displayName.toLowerCase().trim();
        switch (lowerName) {
            case "tuition fees":
                return "tuitionFees";
            case "admission fees":
                return "admissionFees";
            case "examination fees":
                return "examinationFees";
            case "transport fees":
                return "transportFees";
            case "library fees":
                return "libraryFees";
            default:
                // Convert to camelCase for custom fees
                // Remove extra spaces and convert to camelCase
                String[] words = displayName.trim().split("\\s+");
                StringBuilder camelCase = new StringBuilder();

                for (int i = 0; i < words.length; i++) {
                    String word = words[i].toLowerCase();
                    if (i == 0) {
                        camelCase.append(word);
                    } else {
                        // Capitalize first letter of subsequent words
                        camelCase.append(Character.toUpperCase(word.charAt(0)))
                                .append(word.substring(1));
                    }
                }
                return camelCase.toString();
        }
    }

    /**
     * Convert fee key back to display name
     * e.g., "tuitionFees" -> "Tuition Fees"
     *       "transportFees" -> "Transport Fees"
     */
    private String convertToDisplayName(String feeKey) {
        if (feeKey == null || feeKey.trim().isEmpty()) {
            return "";
        }

        // Handle common default fees
        switch (feeKey) {
            case "tuitionFees":
                return "Tuition Fees";
            case "admissionFees":
                return "Admission Fees";
            case "examinationFees":
                return "Examination Fees";
            case "transportFees":
                return "Transport Fees";
            case "libraryFees":
                return "Library Fees";
            default:
                // Convert camelCase to display name
                StringBuilder displayName = new StringBuilder();
                for (int i = 0; i < feeKey.length(); i++) {
                    char c = feeKey.charAt(i);
                    if (i == 0) {
                        displayName.append(Character.toUpperCase(c));
                    } else if (Character.isUpperCase(c)) {
                        displayName.append(" ").append(c);
                    } else {
                        displayName.append(c);
                    }
                }
                return displayName.toString();
        }
    }

    public void populateFromFeesStructure(FeesStructure feesStructure, Map<String, String> displayNamesMap) {
        if (feesStructure == null) {
            // If no structure provided, keep the default single empty item
            return;
        }

        // Clear existing data
        feesParticularsList.clear();

        // Get all fees from the structure
        Map<String, String> allFees = feesStructure.getFeesStructure();

        // Add all fees from the structure
        for (Map.Entry<String, String> entry : allFees.entrySet()) {
            String feeKey = entry.getKey();
            String amount = entry.getValue();
            String displayName = convertToDisplayName(feeKey);
            feesParticularsList.add(new FeesParticular(displayName, amount, false));
        }

        // If no fees were added, add one empty item
        if (feesParticularsList.isEmpty()) {
            feesParticularsList.add(new FeesParticular("", "", false));
        }

        notifyDataSetChanged();

        if (onDataChangeListener != null) {
            onDataChangeListener.onDataChanged(feesParticularsList);
        }
    }

    public void updateList(List<FeesParticular> newList) {
        this.feesParticularsList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_fees_particular, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeesParticular feesParticular = feesParticularsList.get(position);

        // Clear existing text watchers to prevent unwanted triggers
        holder.etInstituteName.setTag(null);
        holder.edtAmount.setTag(null);

        // Set the data
        holder.etInstituteName.setText(feesParticular.getFeesName());
        holder.edtAmount.setText(feesParticular.getAmount());

        // All items are now fully editable
        holder.etInstituteName.setEnabled(true);
        holder.etInstituteName.setFocusable(true);
        holder.etInstituteName.setFocusableInTouchMode(true);

        // Set hint text
        holder.tvHintName.setText("Fees Name");

        // Set up TextWatchers
        setupTextWatcher(holder.etInstituteName, position, true);
        setupTextWatcher(holder.edtAmount, position, false);
    }

    private void setupTextWatcher(EditText editText, int position, boolean isFeesName) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (position < feesParticularsList.size()) {
                    FeesParticular item = feesParticularsList.get(position);
                    if (isFeesName) {
                        item.setFeesName(s.toString());
                    } else {
                        item.setAmount(s.toString());
                    }

                    if (onDataChangeListener != null) {
                        onDataChangeListener.onDataChanged(feesParticularsList);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return feesParticularsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText etInstituteName, edtAmount;
        TextView tvHintName, tvHintAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            etInstituteName = itemView.findViewById(R.id.et_institute_name);
            edtAmount = itemView.findViewById(R.id.edt_marks);
            tvHintName = itemView.findViewById(R.id.tv_hint);
            // Assuming you have a similar TextView for amount hint in your layout
            // tvHintAmount = itemView.findViewById(R.id.tv_hint_amount);
        }
    }
}