package com.school.schoolmanagement.Admin.Fees.Defaulters;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Admin.Adapter.AdapterFeesDefaulter;
import com.school.schoolmanagement.Admin.Model.FeesDefaulterModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityFeesDefaultersBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityFeesDefaulters extends AppCompatActivity {
ActivityFeesDefaultersBinding binding;
    private AdapterFeesDefaulter adapter;
    private List<FeesDefaulterModel> defaulterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityFeesDefaultersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        defaulterList = new ArrayList<>();
        defaulterList.add(new FeesDefaulterModel("78945632222", "Aman Meraj"));
        defaulterList.add(new FeesDefaulterModel("98765432100", "John Doe"));

        adapter = new AdapterFeesDefaulter(this, defaulterList);
        binding.rcDefaulters.setAdapter(adapter);
    }
}