package com.school.schoolmanagement.IntroScreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.school.schoolmanagement.Adapters.IntroAdapter;
import com.school.schoolmanagement.Login.ActivityLogin;
import com.school.schoolmanagement.MainActivity;
import com.school.schoolmanagement.Model.IntroItem;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityIntroductionBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityIntroduction extends AppCompatActivity {

    ActivityIntroductionBinding binding;
    IntroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isIntroSeen()) {
            navigateToMainActivity();
            return;
        }

        EdgeToEdge.enable(this);
        binding = ActivityIntroductionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<IntroItem> introItems = new ArrayList<>();
        introItems.add(new IntroItem("Welcome", "This is the first screen", R.drawable.logo_sm));
        introItems.add(new IntroItem("Discover", "Explore amazing features", R.drawable.logo_sm));
        introItems.add(new IntroItem("Get Started", "Let's begin!", R.drawable.logo_sm));

        adapter = new IntroAdapter(introItems);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {}).attach();

        binding.btnSkip.setOnClickListener(v -> {
            markIntroSeen();
            navigateToMainActivity();
        });

        binding.btnGetStarted.setOnClickListener(v -> {
            markIntroSeen();
            navigateToMainActivity();
        });

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == introItems.size() - 1) {
                    binding.btnSkip.setVisibility(View.GONE);
                    binding.btnGetStarted.setVisibility(View.VISIBLE);
                } else {
                    binding.btnSkip.setVisibility(View.VISIBLE);
                    binding.btnGetStarted.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean isIntroSeen() {
        SharedPreferences prefs = getSharedPreferences("intro_prefs", MODE_PRIVATE);
        return prefs.getBoolean("isIntroSeen", false);
    }

    private void markIntroSeen() {
        SharedPreferences prefs = getSharedPreferences("intro_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isIntroSeen", true);
        editor.apply();
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(this, ActivityLogin.class));
        finish();
    }
}
