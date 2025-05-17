package com.school.schoolmanagement.Students.TimeTable;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.school.schoolmanagement.Adapters.AdapterTimeSlot;
import com.school.schoolmanagement.Model.TimeSlotModel;
import com.school.schoolmanagement.R;
import com.school.schoolmanagement.databinding.ActivityStudentsTimetableBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityStudentsTimetable extends AppCompatActivity {
ActivityStudentsTimetableBinding binding;

    List<TimeSlotModel> timeSlotList = new ArrayList<>();
    AdapterTimeSlot adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityStudentsTimetableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupRecycler();
        loadData();
    }

    private void setupRecycler() {
        adapter = new AdapterTimeSlot(this, timeSlotList);
        binding.rcTimeslot.setAdapter(adapter);
    }

    private void loadData() {
        timeSlotList.add(new TimeSlotModel("Basic Science", "8:00AM - 8:45AM", true));
        timeSlotList.add(new TimeSlotModel("English Assignment", "9:00AM - 9:15AM", false));
        timeSlotList.add(new TimeSlotModel("Basic Science", "8:00AM - 8:45AM", true));
        timeSlotList.add(new TimeSlotModel("English Assignment", "9:00AM - 9:15AM", false));
        timeSlotList.add(new TimeSlotModel("Basic Science", "8:00AM - 8:45AM", true));
        timeSlotList.add(new TimeSlotModel("English Assignment", "1:00PM - 1:15PM", false));
        timeSlotList.add(new TimeSlotModel("Basic Science", "8:00AM - 8:45AM", true));
        adapter.notifyDataSetChanged();
    }
}