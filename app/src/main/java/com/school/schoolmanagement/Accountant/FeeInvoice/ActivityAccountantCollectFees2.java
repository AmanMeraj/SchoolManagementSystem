package com.school.schoolmanagement.Accountant.FeeInvoice;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Admin.Adapter.AdapterCollectFees2;
import com.school.schoolmanagement.Admin.Model.FeeItem;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityAccountantCollectFees2Binding;

import java.util.ArrayList;
import java.util.List;

public class ActivityAccountantCollectFees2 extends AppCompatActivity implements AdapterCollectFees2.OnFeeAmountChangedListener{
    ActivityAccountantCollectFees2Binding binding;
    private AdapterCollectFees2 adapter;
    private List<FeeItem> feeItemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityAccountantCollectFees2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupFeeList();

        // Setup RecyclerView
        setupRecyclerView();
    }

    private void setupFeeList() {
        feeItemList = new ArrayList<>();
        feeItemList.add(new FeeItem(1, "MONTHLY FEE", "2343"));
        feeItemList.add(new FeeItem(2, "ADMISSION FEE", "263"));
        feeItemList.add(new FeeItem(3, "TRANSPORT FEE", "663"));
        feeItemList.add(new FeeItem(4, "LIBRARY FEE", "2363"));
    }

    private void setupRecyclerView() {
        adapter = new AdapterCollectFees2(feeItemList);
        adapter.setOnFeeAmountChangedListener(this);
        binding.rcFeesCollection.setAdapter(adapter);
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