package com.school.schoolmanagement.Adapters;

import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.school.schoolmanagement.Model.ChapterResponse;
import com.school.schoolmanagement.R;

import java.util.ArrayList;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private ArrayList<ChapterResponse.Datum> chapterList;
    private OnChapterClickListener listener;

    public interface OnChapterClickListener {
        void onChapterEditClick(ChapterResponse.Datum chapter, int position);
        void onChapterDeleteClick(ChapterResponse.Datum chapter, int position);
    }

    public ChapterAdapter(ArrayList<ChapterResponse.Datum> chapterList, OnChapterClickListener listener) {
        this.chapterList = chapterList != null ? chapterList : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter_row, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        ChapterResponse.Datum chapter = chapterList.get(position);

        // Set serial number
        holder.tvSerial.setText(String.valueOf(position + 1));

        // Set subject name
        holder.tvSubject.setText(chapter.subjectName != null ? chapter.subjectName : "N/A");

        // Set chapter title
        holder.tvType.setText(chapter.chapterName != null ? chapter.chapterName : "N/A");

        // Set up click listeners for existing buttons
        holder.editBtn.setOnClickListener(v -> {
            if (listener != null) {
                android.util.Log.d("ChapterAdapter", "Edit clicked for chapter: " + chapter.chapterName + ", position: " + position);
                listener.onChapterEditClick(chapter, position);
            }
        });

        holder.deleteBtn.setOnClickListener(v -> {
            if (listener != null) {
                android.util.Log.d("ChapterAdapter", "Delete clicked for chapter: " + chapter.chapterName + ", position: " + position);
                listener.onChapterDeleteClick(chapter, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    // Method to update the list
    public void updateChapterList(ArrayList<ChapterResponse.Datum> newChapterList) {
        this.chapterList = newChapterList != null ? newChapterList : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Method to add a single chapter
    public void addChapter(ChapterResponse.Datum chapter) {
        if (chapter != null) {
            chapterList.add(chapter);
            notifyItemInserted(chapterList.size() - 1);
        }
    }

    // Method to remove a chapter
    public void removeChapter(int position) {
        if (position >= 0 && position < chapterList.size()) {
            chapterList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, chapterList.size());
        }
    }

    // Method to get chapter at position
    public ChapterResponse.Datum getChapterAt(int position) {
        if (position >= 0 && position < chapterList.size()) {
            return chapterList.get(position);
        }
        return null;
    }

    // Method to clear all chapters
    public void clearChapters() {
        int size = chapterList.size();
        chapterList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvSerial, tvSubject, tvType;
        ImageView editBtn, deleteBtn;
        LinearLayout actionLayout;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSerial = itemView.findViewById(R.id.tv_serial);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvType = itemView.findViewById(R.id.tv_type);
            editBtn = itemView.findViewById(R.id.edit_btn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
            actionLayout = itemView.findViewById(R.id.action_layout);

            Log.d("ChapterAdapter", "ViewHolder initialized");
        }

    }
}