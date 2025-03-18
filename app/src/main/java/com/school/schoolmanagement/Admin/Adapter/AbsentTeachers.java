package com.school.schoolmanagement.Admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.school.schoolmanagement.Admin.Model.Teachers;
import com.school.schoolmanagement.R;

import java.util.List;

public class AbsentTeachers  extends RecyclerView.Adapter<AbsentTeachers.MyViewHolder> {

    private Context context;
    private List<Teachers> studentList;

    public AbsentTeachers(Context context, List<Teachers> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.absent_students_admin_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Teachers student = studentList.get(position);
        holder.nameSubject.setText(student.getName() + "\n" + student.getSubject());
        holder.image.setImageResource(student.getImageResId());
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView nameSubject;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            nameSubject = itemView.findViewById(R.id.name_subject);
        }
    }
}


