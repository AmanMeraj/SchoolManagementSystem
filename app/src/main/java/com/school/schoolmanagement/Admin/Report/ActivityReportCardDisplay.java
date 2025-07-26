package com.school.schoolmanagement.Admin.Report;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.rajat.pdfviewer.PdfRendererView;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityReportCardDisplayBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActivityReportCardDisplay extends Utility {

    ActivityReportCardDisplayBinding binding;
    ViewModel viewModel;
    int studentId; // To store the received studentId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityReportCardDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel= new ViewModelProvider(this).get(ViewModel.class);

        // Apply edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(binding.main.getId()), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get studentId from intent
        studentId = getIntent().getIntExtra("STUDENT_ID", -1);
        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (isInternetConnected(this)){
            getPdf();
        }else {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

    }

    public  void getPdf(){
        showLoading("Loading");
        String auth = "Bearer "+pref.getPrefString(this,pref.user_token);
        viewModel.getPdfResult(auth,studentId).observe(this,response ->{
            if(response.isSuccess){
                hideLoading();
                String base64 = response.data.pdfData;
                byte[] pdfBytes = Base64.decode(base64, Base64.DEFAULT);
                try {
                    File file = new File(getCacheDir(), response.data.fileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(pdfBytes);
                    fos.close();

                    binding.pdfView.initWithFile(file); // ✅ Correct way to load file

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else{
                hideLoading();
                Toast.makeText(this, ""+response.message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showLoading(String message) {
        binding.loader.rlLoader.setVisibility(View.VISIBLE);
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    private void hideLoading() {
        // Implement loading indicator hiding if you have one
        // You can add a progress bar or loading overlay here
        binding.loader.rlLoader.setVisibility(View.GONE);
    }


}
