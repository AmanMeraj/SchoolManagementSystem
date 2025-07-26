package com.school.schoolmanagement.Accountant.FeeInvoice;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Admin.Adapter.AdapterFeesDefaulter;
import com.school.schoolmanagement.Admin.Model.FeesDefaulterModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityAccountantFeesDefaultersBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityAccountantFeesDefaulters extends AppCompatActivity {
    ActivityAccountantFeesDefaultersBinding binding;
    private AdapterFeesDefaulter adapter;
    private List<FeesDefaulterModel> defaulterList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityAccountantFeesDefaultersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
}