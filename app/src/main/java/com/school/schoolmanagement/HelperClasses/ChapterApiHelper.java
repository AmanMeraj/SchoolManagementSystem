package com.school.schoolmanagement.HelperClasses;

import android.content.Context;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import com.school.schoolmanagement.Model.AddChapter;
import com.school.schoolmanagement.Model.ChapterResponse;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Utils.Utility;
import java.util.ArrayList;

public class ChapterApiHelper extends Utility {

    private ViewModel viewModel;
    private Context context;

    public ChapterApiHelper(Context context) {
        this.context = context;
        if (context instanceof ViewModelStoreOwner) {
            this.viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ViewModel.class);
        }
    }

    // Interface for callback when fetching chapters
    public interface ChapterListCallback {
        void onSuccess(ArrayList<ChapterResponse.Datum> chapterList);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }

    // Interface for callback when adding chapter
    public interface AddChapterCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }

    // Interface for callback when updating chapter
    public interface UpdateChapterCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }

    // Interface for callback when deleting chapter
    public interface DeleteChapterCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }

    // Method to fetch chapters by subject ID
    public void fetchChaptersBySubject(int subjectId, ChapterListCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        callback.onLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner) {
            viewModel.getChapter(auth, subjectId).observe((LifecycleOwner) context, response -> {
                callback.onLoading(false);
                if (response != null) {
                    if (response.data != null && response.data.getData() != null) {
                        callback.onSuccess(response.data.getData());
                    } else {
                        callback.onError(response.message != null ? response.message : "No chapters found");
                    }
                } else {
                    callback.onError("Failed to fetch chapters");
                }
            });
        }
    }

    // Method to add a new chapter
    public void addNewChapter(AddChapter addChapter, AddChapterCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        callback.onLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner) {
            viewModel.addChapter(auth, addChapter).observe((LifecycleOwner) context, response -> {
                callback.onLoading(false);
                if (response != null) {
                    if (response.isSuccess) {
                        callback.onSuccess("Chapter added successfully");
                    } else {
                        callback.onError(response.message != null ? response.message : "Failed to add chapter");
                    }
                } else {
                    callback.onError("Failed to add chapter");
                }
            });
        }
    }

    // Method to update an existing chapter
    public void updateChapter(int chapterId, AddChapter addChapter, UpdateChapterCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        callback.onLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner) {
            viewModel.updateChapter(auth, chapterId, addChapter).observe((LifecycleOwner) context, response -> {
                callback.onLoading(false);
                if (response != null) {
                    if (response.isSuccess) {
                        callback.onSuccess("Chapter updated successfully");
                    } else {
                        callback.onError(response.message != null ? response.message : "Failed to update chapter");
                    }
                } else {
                    callback.onError("Failed to update chapter");
                }
            });
        }
    }

    // Method to delete a chapter
    public void deleteChapter(int chapterId, DeleteChapterCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        callback.onLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner) {
            viewModel.deleteChapter(auth, chapterId).observe((LifecycleOwner) context, response -> {
                callback.onLoading(false);
                if (response != null) {
                    if (response.isSuccess) {
                        callback.onSuccess("Chapter deleted successfully");
                    } else {
                        callback.onError(response.message != null ? response.message : "Failed to delete chapter");
                    }
                } else {
                    callback.onError("Failed to delete chapter");
                }
            });
        }
    }

    // Method to get chapter names for spinner/dropdown
    public ArrayList<String> getChapterNames(ArrayList<ChapterResponse.Datum> chapterList) {
        ArrayList<String> chapterNames = new ArrayList<>();
        chapterNames.add("Select Chapter"); // Default option

        for (ChapterResponse.Datum chapterData : chapterList) {
            chapterNames.add(chapterData.getChapterName());
        }

        return chapterNames;
    }

    // Method to get chapter ID by position (considering first item is "Select Chapter")
    public int getChapterIdByPosition(ArrayList<ChapterResponse.Datum> chapterList, int position) {
        if (position > 0 && position <= chapterList.size()) {
            return chapterList.get(position - 1).getId();
        }
        return -1; // Invalid selection
    }

    // Method to get chapter data by ID
    public ChapterResponse.Datum getChapterById(ArrayList<ChapterResponse.Datum> chapterList, int chapterId) {
        for (ChapterResponse.Datum chapterData : chapterList) {
            if (chapterData.getId() == chapterId) {
                return chapterData;
            }
        }
        return null;
    }

    // Method to find position by chapter ID (useful for pre-selection)
    public int getPositionByChapterId(ArrayList<ChapterResponse.Datum> chapterList, int chapterId) {
        for (int i = 0; i < chapterList.size(); i++) {
            if (chapterList.get(i).getId() == chapterId) {
                return i + 1; // +1 because first item is "Select Chapter"
            }
        }
        return 0; // Return to "Select Chapter" if not found
    }

    // Method to get chapter by name
    public ChapterResponse.Datum getChapterByName(ArrayList<ChapterResponse.Datum> chapterList, String chapterName) {
        for (ChapterResponse.Datum chapterData : chapterList) {
            if (chapterData.getChapterName().equals(chapterName)) {
                return chapterData;
            }
        }
        return null;
    }

    // Method to check if chapter exists by name
    public boolean isChapterExists(ArrayList<ChapterResponse.Datum> chapterList, String chapterName) {
        return getChapterByName(chapterList, chapterName) != null;
    }

    // Method to get chapters count for a subject
    public int getChaptersCount(ArrayList<ChapterResponse.Datum> chapterList) {
        return chapterList != null ? chapterList.size() : 0;
    }

    // Method to filter chapters by search query
    public ArrayList<ChapterResponse.Datum> filterChapters(ArrayList<ChapterResponse.Datum> chapterList, String searchQuery) {
        ArrayList<ChapterResponse.Datum> filteredList = new ArrayList<>();

        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return chapterList;
        }

        String query = searchQuery.toLowerCase().trim();
        for (ChapterResponse.Datum chapter : chapterList) {
            if (chapter.getChapterName().toLowerCase().contains(query)) {
                filteredList.add(chapter);
            }
        }

        return filteredList;
    }

    // Method to sort chapters alphabetically
    public ArrayList<ChapterResponse.Datum> sortChaptersAlphabetically(ArrayList<ChapterResponse.Datum> chapterList, boolean ascending) {
        ArrayList<ChapterResponse.Datum> sortedList = new ArrayList<>(chapterList);

        if (ascending) {
            sortedList.sort((c1, c2) -> c1.getChapterName().compareToIgnoreCase(c2.getChapterName()));
        } else {
            sortedList.sort((c1, c2) -> c2.getChapterName().compareToIgnoreCase(c1.getChapterName()));
        }

        return sortedList;
    }

    // Method to get chapter by subject name
    public ArrayList<ChapterResponse.Datum> getChaptersBySubjectName(ArrayList<ChapterResponse.Datum> chapterList, String subjectName) {
        ArrayList<ChapterResponse.Datum> filteredList = new ArrayList<>();

        for (ChapterResponse.Datum chapter : chapterList) {
            if (chapter.getSubjectName().equals(subjectName)) {
                filteredList.add(chapter);
            }
        }

        return filteredList;
    }
}