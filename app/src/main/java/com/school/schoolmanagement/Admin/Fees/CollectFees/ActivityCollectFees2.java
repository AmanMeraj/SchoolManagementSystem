package com.school.schoolmanagement.Admin.Fees.CollectFees;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.school.schoolmanagement.Admin.Adapter.AdapterCollectFees2;
import com.school.schoolmanagement.Admin.Model.FeeItem;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityCollectFees2Binding;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollectFees2 extends AppCompatActivity implements AdapterCollectFees2.OnFeeAmountChangedListener {
    ActivityCollectFees2Binding binding;
    private AdapterCollectFees2 adapter;
    private List<FeeItem> feeItemList;
    ViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityCollectFees2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel= new ViewModelProvider(this).get(ViewModel.class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    @Override
    public void onFeeAmountChanged(int position, String newAmount) {
        // Handle fee amount changes
        // For example, update total fee calculation
        updateTotalFees();
    }

    private void updateTotalFees() {
        int totalFees = 0;

        for (FeeItem item : feeItemList) {
            try {
                totalFees += Integer.parseInt(item.getFeeAmount());
            } catch (NumberFormatException e) {
                // Handle parsing error
            }
        }

        // Update UI with total fees if needed
        // binding.textTotalFees.setText(String.valueOf(totalFees));
    }
}