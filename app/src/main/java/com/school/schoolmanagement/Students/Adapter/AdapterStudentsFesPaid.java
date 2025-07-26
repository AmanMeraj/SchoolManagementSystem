package com.school.schoolmanagement.Students.Adapter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.Model.StudentDashboardApiResponse3;
import com.school.schoolmanagement.Students.Model.StudentsFeesPaidModel;
import com.school.schoolmanagement.databinding.RowStudentsFeesReportBinding;
import java.text.NumberFormat;
import java.util.Locale;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import java.util.List;



    public class AdapterStudentsFesPaid extends RecyclerView.Adapter<AdapterStudentsFesPaid.PaymentViewHolder> {

        private final List<StudentDashboardApiResponse3.Datum> paymentList;
        private final Context context;

        public AdapterStudentsFesPaid(Context context, List<StudentDashboardApiResponse3.Datum> paymentList) {
            this.context = context;
            this.paymentList = paymentList;
        }

        @NonNull
        @Override
        public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            RowStudentsFeesReportBinding binding = RowStudentsFeesReportBinding.inflate(inflater, parent, false);
            return new PaymentViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
            StudentDashboardApiResponse3.Datum payment = paymentList.get(position);

            // Set basic information
            holder.binding.textViewMonthTitle.setText(payment.getMonth());
            holder.binding.textViewAmount.setText(formatCurrency(payment.getAmount()));
            holder.binding.textViewStatus.setText(payment.getStatus());

            // Set detailed information
            holder.binding.textViewSubmittedDateValue.setText(payment.getSubmittedDate());
            holder.binding.textViewTotalAmountValue.setText(formatCurrency(payment.getTotalAmount()));
            holder.binding.textViewTotalPaidValue.setText(formatCurrency(payment.getTotalPaid()));
            holder.binding.textViewBalanceValue.setText(formatCurrency(payment.getBalance()));

            // Set border color based on status
            if ("Paid".equalsIgnoreCase(payment.getStatus())) {
                ViewCompat.setBackgroundTintList(
                        holder.binding.colorTintCard,
                        ContextCompat.getColorStateList(context, R.color.light_blue)
                );;
                ViewCompat.setBackgroundTintList(
                        holder.binding.textViewStatus,
                        ContextCompat.getColorStateList(context, R.color.light_blue)
                );;
                holder.binding.textViewStatus.setBackgroundResource(R.drawable.background_paid);
            } else {
                ViewCompat.setBackgroundTintList(
                        holder.binding.colorTintCard,
                        ContextCompat.getColorStateList(context, R.color.orange)
                );
                ViewCompat.setBackgroundTintList(
                        holder.binding.textViewStatus,
                        ContextCompat.getColorStateList(context, R.color.orange)
                );
                holder.binding.textViewStatus.setBackgroundResource(R.drawable.background_awaiting);
            }

            // Set expanded state
            holder.binding.detailView.setVisibility(payment.isExpanded() ? ViewGroup.VISIBLE : ViewGroup.GONE);
            holder.binding.imageViewCollapseExpand.setRotation(payment.isExpanded() ? 180 : 0);

            // Set click listener for expand/collapse
            holder.binding.imageViewCollapseExpand.setOnClickListener(v -> {
                payment.setExpanded(!payment.isExpanded());

                float startRotation = payment.isExpanded() ? 0 : 180;
                float endRotation = payment.isExpanded() ? 180 : 0;

                RotateAnimation rotateAnimation = new RotateAnimation(
                        startRotation, endRotation,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                );
                rotateAnimation.setDuration(300);
                rotateAnimation.setFillAfter(true);
                holder.binding.imageViewCollapseExpand.startAnimation(rotateAnimation);

                if (payment.isExpanded()) {
                    holder.binding.detailView.setVisibility(ViewGroup.VISIBLE);
                    holder.binding.detailView.setAlpha(0f);
                    holder.binding.detailView.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .start();
                } else {
                    holder.binding.detailView.animate()
                            .alpha(0f)
                            .setDuration(300)
                            .withEndAction(() -> holder.binding.detailView.setVisibility(ViewGroup.GONE))
                            .start();
                }
            });
        }

        @Override
        public int getItemCount() {
            return paymentList.size();
        }

        private String formatCurrency(double amount) {
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
            return formatter.format(amount);
        }

        public static class PaymentViewHolder extends RecyclerView.ViewHolder {
            final RowStudentsFeesReportBinding binding;

            public PaymentViewHolder(@NonNull RowStudentsFeesReportBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
