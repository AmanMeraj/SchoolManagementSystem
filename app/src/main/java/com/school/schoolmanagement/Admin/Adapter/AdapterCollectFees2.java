package com.school.schoolmanagement.Admin.Adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.school.schoolmanagement.Admin.Model.FeeItem;
import com.school.schoolmanagement.databinding.RowCollectFees2Binding;

import java.util.List;

public class AdapterCollectFees2 extends RecyclerView.Adapter<AdapterCollectFees2.FeeViewHolder> {

    private List<FeeItem> feeItemList;
    private OnFeeAmountChangedListener listener;

    public AdapterCollectFees2(List<FeeItem> feeItemList) {
        this.feeItemList = feeItemList;
    }

    public void setOnFeeAmountChangedListener(OnFeeAmountChangedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowCollectFees2Binding binding = RowCollectFees2Binding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FeeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FeeViewHolder holder, int position) {
        FeeItem feeItem = feeItemList.get(position);
        holder.bind(feeItem);
    }

    @Override
    public int getItemCount() {
        return feeItemList.size();
    }

    public class FeeViewHolder extends RecyclerView.ViewHolder {
        private RowCollectFees2Binding binding;
        private TextWatcher textWatcher;

        public FeeViewHolder(RowCollectFees2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FeeItem feeItem) {
            // Remove previous TextWatcher to prevent multiple registrations
            if (textWatcher != null) {
                binding.editFeeAmount.removeTextChangedListener(textWatcher);
            }

            // Set data
            binding.textItemNumber.setText(String.valueOf(feeItem.getItemNumber()));
            binding.textFeeName.setText(feeItem.getFeeName());
            binding.editFeeAmount.setText(feeItem.getFeeAmount());

            // Create new TextWatcher
            textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Not needed
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Not needed
                }

                @Override
                public void afterTextChanged(Editable s) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String newAmount = s.toString();
                        feeItemList.get(position).setFeeAmount(newAmount);

                        if (listener != null) {
                            listener.onFeeAmountChanged(position, newAmount);
                        }
                    }
                }
            };

            // Set the TextWatcher
            binding.editFeeAmount.addTextChangedListener(textWatcher);
        }
    }

    public interface OnFeeAmountChangedListener {
        void onFeeAmountChanged(int position, String newAmount);
    }
}
