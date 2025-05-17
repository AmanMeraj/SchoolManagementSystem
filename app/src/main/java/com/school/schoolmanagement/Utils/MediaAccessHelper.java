package com.school.schoolmanagement.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Helper class to handle photo and media access in a privacy-friendly way
 * compatible with Android 14+ Selected Photos Access
 */
public class MediaAccessHelper {
    private static final String TAG = "MediaAccessHelper";

    // Constants
    private static final int PERMISSIONS_REQUEST_CODE = 1001;
    private static final String[] REQUIRED_PERMISSIONS_BELOW_API_33 = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String[] REQUIRED_PERMISSIONS_API_33_PLUS = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
    };

    private final FragmentActivity activity;
    private final ContentResolver contentResolver;
    private final PhotoSelectionCallback callback;
    private Uri currentPhotoUri;

    // Activity Result Launcher for photo picking (Android 14+ compatible)
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    // Activity Result Launcher for camera
    private final ActivityResultLauncher<Uri> takePicture;

    public interface PhotoSelectionCallback {
        void onPhotoSelected(Uri photoUri);
        void onMultiplePhotosSelected(List<Uri> photoUris);
        void onSelectionCancelled();
    }

    public MediaAccessHelper(FragmentActivity activity, PhotoSelectionCallback callback) {
        this.activity = activity;
        this.contentResolver = activity.getContentResolver();
        this.callback = callback;

        // Setup photo picker launcher (works with Android 14+ Selected Photos Access)
        this.pickMedia = activity.registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            Log.d(TAG, "Selected photo: " + result);
                            callback.onPhotoSelected(result);
                        } else {
                            Log.d(TAG, "No photo selected");
                            callback.onSelectionCancelled();
                        }
                    }
                });

        // Setup camera launcher
        this.takePicture = activity.registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result && currentPhotoUri != null) {
                            Log.d(TAG, "Photo captured: " + currentPhotoUri);
                            callback.onPhotoSelected(currentPhotoUri);
                        } else {
                            Log.d(TAG, "Photo capture cancelled or failed");
                            callback.onSelectionCancelled();
                        }
                    }
                });
    }

    /**
     * Launch the photo picker for a single image
     * Compatible with Android 14+ Selected Photos Access
     */
    public void pickImage() {
        // Use the modern photo picker (no permissions needed for Selected Photos Access)
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    /**
     * Launch the photo picker for multiple images
     * Compatible with Android 14+ Selected Photos Access
     */
    public void pickMultipleImages() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+ use the modern multi-select photo picker
            activity.registerForActivityResult(
                    new ActivityResultContracts.PickMultipleVisualMedia(),
                    uris -> {
                        if (!uris.isEmpty()) {
                            Log.d(TAG, "Selected photos: " + uris.size());
                            callback.onMultiplePhotosSelected(uris);
                        } else {
                            Log.d(TAG, "No photos selected");
                            callback.onSelectionCancelled();
                        }
                    }).launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        } else {
            // Legacy approach for older Android versions
            checkAndRequestPermissions(() -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                activity.startActivityForResult(Intent.createChooser(intent, "Select Pictures"),
                        PERMISSIONS_REQUEST_CODE);
            });
        }
    }

    /**
     * Launch the camera to take a photo
     */
    public void takePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE);
                return;
            }
        }

        // Create file for camera output
        File photoFile = createImageFile();
        if (photoFile != null) {
            currentPhotoUri = FileProvider.getUriForFile(activity,
                    activity.getPackageName() + ".fileprovider",
                    photoFile);
            takePicture.launch(currentPhotoUri);
        } else {
            Toast.makeText(activity, "Could not create image file", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Create a file to store a captured image
     */
    private File createImageFile() {
        try {
            String imageFileName = "JPEG_" + UUID.randomUUID().toString();
            File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (Exception e) {
            Log.e(TAG, "Error creating image file", e);
            return null;
        }
    }

    /**
     * Check and request necessary permissions for pre-Android 14 devices
     */
    public void checkAndRequestPermissions(Runnable onPermissionsGranted) {
        // For Android 14+, we use the photo picker which doesn't need permissions
        if (Build.VERSION.SDK_INT >= 34) {
            onPermissionsGranted.run();
            return;
        }

        // For Android 13, we need READ_MEDIA_IMAGES
        String[] requiredPermissions =
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ?
                        REQUIRED_PERMISSIONS_API_33_PLUS : REQUIRED_PERMISSIONS_BELOW_API_33;

        List<String> permissionsToRequest = new ArrayList<>();

        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (permissionsToRequest.isEmpty()) {
            // All permissions already granted
            onPermissionsGranted.run();
        } else {
            // Request permissions
            ActivityCompat.requestPermissions(activity,
                    permissionsToRequest.toArray(new String[0]),
                    PERMISSIONS_REQUEST_CODE);

            // Note: You need to handle onRequestPermissionsResult in your Activity
            // and call onPermissionsGranted there if appropriate
        }
    }

    /**
     * Show dialog explaining why we need storage permissions (for pre-Android 14)
     */
    public void showPermissionRationaleDialog() {
        new AlertDialog.Builder(activity)
                .setTitle("Storage Permissions Required")
                .setMessage("This app needs access to your photos to upload pictures. " +
                        "Please grant storage permission.")
                .setPositiveButton("Grant Permission", (dialog, which) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                            intent.setData(uri);
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                            intent.setData(uri);
                            activity.startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        activity.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Process activity result for older Android versions
     * Call this from your Activity's onActivityResult
     */
    public void processActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSIONS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    // Handle multiple images
                    int count = data.getClipData().getItemCount();
                    List<Uri> uris = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        uris.add(imageUri);
                    }
                    callback.onMultiplePhotosSelected(uris);
                } else if (data.getData() != null) {
                    // Handle single image
                    Uri imageUri = data.getData();
                    callback.onPhotoSelected(imageUri);
                }
            } else {
                callback.onSelectionCancelled();
            }
        }
    }
}