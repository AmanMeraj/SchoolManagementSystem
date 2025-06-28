package com.school.schoolmanagement.Admin.Students.StudentDetails;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Model.StudentDetails;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Utils.Utility;
import com.school.schoolmanagement.databinding.ActivityStudentsDetailBinding;

import java.util.ArrayList;

public class ActivityStudentsDetail extends Utility {
ActivityStudentsDetailBinding binding;

int studentId;
ArrayList<StudentDetails> responseDetails;
ViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding= ActivityStudentsDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel= new ViewModelProvider(this).get(ViewModel.class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        studentId=getIntent().getIntExtra("student_id",0);
        if(isInternetConnected(this)){

            if(studentId>0){
                getDetails(studentId);
            }else {
                Toast.makeText(this, "No Students with this ID!", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDetails(int studentId) {
        String auth = "Bearer "+ pref.getPrefString(this,pref.user_token);
        showLoading(true);
        viewModel.getStudentsDetails(auth,studentId).observe(this,response->{
            if(response.isSuccess){
                responseDetails=response.data.getData();
                showLoading(false);
                setData(responseDetails);
            }else {
                showLoading(false);
                showToast(response.message);
            }
        });
    }

    private void setData(ArrayList<StudentDetails> response) {
        binding.tvName.setText(response.get(0).getStudentName());
        binding.tvRegistrationNo.setText(String.valueOf(response.get(0).getRollNumber()));
        binding.tvDateOfAdmission.setText(response.get(0).getDateOfAdmission());
        binding.tvclass.setText(response.get(0).getClassName());
//        binding.tvDiscountInFees.setText(response.get(0).get);
        binding.tvDob.setText(response.get(0).getDateOfBirth());
        binding.tvGender.setText(response.get(0).getGender());
        binding.tvDisease.setText(response.get(0).getDiseaseIfAny());
        binding.tvBirthID.setText(response.get(0).getBirthFormIdOrNic());
        binding.tvPreviousSchool.setText(response.get(0).getPreviousSchool());
        binding.tvPreviousIdOrBoardRollNo.setText(response.get(0).getPreviousIdOrBoardRollNumber());
        binding.tvAdittionalNote.setText(response.get(0).getNote());
        binding.tvOrphanStudnet.setText(response.get(0).getOsc());
//        binding.tvReligion.setText(response.get(0).getr);
        binding.tvTotalSibling.setText(String.valueOf(response.get(0).getTotalSibling()));
        binding.tvAddress.setText(response.get(0).getAddress());
        String base64Image = response.get(0).getProfilePicture(); // Assuming this method returns Base64 string
        setBase64ImageWithGlide(base64Image, binding.profileImg);
    }


    private void showLoading(boolean isLoading) {
        Log.d(TAG, "Loading: " + isLoading);
        try {
            if (binding != null) {
                binding.loader.rlLoader.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing/hiding loader: " + e.getMessage(), e);
        }
    }

    private void showToast(String message) {
        Log.d(TAG, "Toast: " + message);
        try {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing toast: " + e.getMessage(), e);
        }
    }


    // Alternative method using Glide (if you're using Glide library)
    private void setBase64ImageWithGlide(String base64String, ImageView imageView) {
        if (base64String != null && !base64String.isEmpty()) {
            try {
                String cleanBase64 = base64String;
                if (base64String.contains(",")) {
                    cleanBase64 = base64String.substring(base64String.indexOf(",") + 1);
                }

                byte[] decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT);

                Glide.with(this)
                        .load(decodedBytes)
                        .placeholder(R.drawable.avatar2)
                        .error(R.drawable.avatar2)
                        .circleCrop() // Optional: make it circular
                        .into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
                Glide.with(this)
                        .load(R.drawable.avatar2)
                        .into(imageView);
            }
        }
    }
}