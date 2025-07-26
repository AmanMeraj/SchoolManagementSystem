package com.school.schoolmanagement.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Admin.GeneralSettings.AccountFeesInvoice.ActivityAccountFeesInvoice;
import com.school.schoolmanagement.R;

import java.io.InputStream;
import java.util.List;

public class BankAccountAdapter extends RecyclerView.Adapter<BankAccountAdapter.ViewHolder> {

    private List<ActivityAccountFeesInvoice.BankAccount> bankAccountList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(ActivityAccountFeesInvoice.BankAccount account, int position);
        void onDeleteClick(ActivityAccountFeesInvoice.BankAccount account, int position);
    }

    public BankAccountAdapter(List<ActivityAccountFeesInvoice.BankAccount> bankAccountList, Context context) {
        this.bankAccountList = bankAccountList;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bank_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityAccountFeesInvoice.BankAccount account = bankAccountList.get(position);

        holder.txtBankName.setText(account.getBankName());
        holder.txtAccountNumber.setText(account.getAccountNumber());

        // Load bank logo
        if (!account.getLogoUri().isEmpty()) {
            try {
                Uri uri = Uri.parse(account.getLogoUri());
                InputStream imageStream = context.getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                holder.imgBankLogo.setImageBitmap(bitmap);
            } catch (Exception e) {
                holder.imgBankLogo.setImageResource(R.drawable.placeholder);
            }
        } else {
            holder.imgBankLogo.setImageResource(R.drawable.placeholder);
        }

        // Set click listeners
        holder.imgEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(account, position);
            }
        });

        holder.imgDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(account, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bankAccountList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtBankName;
        ImageView imgBankLogo;
        TextView txtAccountNumber;
        ImageView imgEdit;
        ImageView imgDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBankName = itemView.findViewById(R.id.txt_bank_name);
            imgBankLogo = itemView.findViewById(R.id.img_bank_logo);
            txtAccountNumber = itemView.findViewById(R.id.txt_account_number);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }
}