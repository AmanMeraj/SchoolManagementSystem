package com.school.schoolmanagement.HelperClasses;
import android.content.Context;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import com.school.schoolmanagement.Admin.Model.ClassModel;
import com.school.schoolmanagement.GlobalViewModel.ViewModel;
import com.school.schoolmanagement.Utils.Utility;
import java.util.ArrayList;

public class ClassApiHelper extends Utility {

    private ViewModel viewModel;
    private Context context;

    public ClassApiHelper(Context context) {
        this.context = context;
        if (context instanceof ViewModelStoreOwner) {
            this.viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ViewModel.class);
        }
    }

    // Interface for callback
    public interface ClassListCallback {
        // ClassApiHelper.ClassListCallback implementation
        void onSuccess(ArrayList<ClassModel.Data> classList);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }

    // Method to fetch all classes
    public void fetchAllClasses(ClassListCallback callback) {
        if (!isInternetConnected(context)) {
            callback.onError("No Internet Connection!");
            return;
        }

        callback.onLoading(true);
        String auth = "Bearer " + pref.getPrefString(context, pref.user_token);

        if (context instanceof LifecycleOwner) {
            viewModel.getAllClasses(auth).observe((LifecycleOwner) context, response -> {
                callback.onLoading(false);
                if (response != null) {
                    if (response.data != null && response.data.getData() != null) {
                        callback.onSuccess(response.data.getData());
                    } else {
                        callback.onError(response.message != null ? response.message : "No classes found");
                    }
                } else {
                    callback.onError("Failed to fetch classes");
                }
            });
        }
    }

    // Method to get class names for spinner/dropdown
    public ArrayList<String> getClassNames(ArrayList<ClassModel.Data> classList) {
        ArrayList<String> classNames = new ArrayList<>();
        classNames.add("Select Class"); // Default option

        for (ClassModel.Data classData : classList) {
            classNames.add(classData.getClassName()); // Assuming getClassName() method exists
        }

        return classNames;
    }

    // Method to get class ID by position (considering first item is "Select Class")
    public int getClassIdByPosition(ArrayList<ClassModel.Data> classList, int position) {
        if (position > 0 && position <= classList.size()) {
            return classList.get(position - 1).getClassId();
        }
        return -1; // Invalid selection
    }

    // Method to get class data by ID
    public ClassModel.Data getClassById(ArrayList<ClassModel.Data> classList, int classId) {
        for (ClassModel.Data classData : classList) {
            if (classData.getClassId() == classId) {
                return classData;
            }
        }
        return null;
    }

    // Method to find position by class ID (useful for pre-selection)
    public int getPositionByClassId(ArrayList<ClassModel.Data> classList, int classId) {
        for (int i = 0; i < classList.size(); i++) {
            if (classList.get(i).getClassId() == classId) {
                return i + 1; // +1 because first item is "Select Class"
            }
        }
        return 0; // Return to "Select Class" if not found
    }
}
