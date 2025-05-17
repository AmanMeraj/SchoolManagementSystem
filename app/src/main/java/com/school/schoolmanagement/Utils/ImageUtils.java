package com.school.schoolmanagement.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Utility class for handling image operations like compression and conversion
 */
public class ImageUtils {
    private static final String TAG = "ImageUtils";
    private static final int MAX_IMAGE_SIZE_KB = 100; // Maximum size in KB
    private static final int QUALITY_START = 100; // Initial quality
    private static final int QUALITY_DECREMENT = 5; // Quality decrement step
    private static final int MIN_QUALITY = 30; // Minimum quality threshold

    /**
     * Compresses a Base64 encoded image string to ensure it's under the specified size limit
     *
     * @param base64Image The original Base64 encoded image string
     * @return The compressed Base64 encoded image string
     */
    public static String compressBase64Image(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            return base64Image;
        }

        try {
            // Decode Base64 string to byte array
            byte[] decodedString = base64ToByteArray(base64Image);

            // Convert byte array to bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            if (bitmap == null) {
                Log.e(TAG, "Failed to decode base64 string to bitmap");
                return base64Image;
            }

            // Check if the image is already smaller than the max size
            if (decodedString.length <= MAX_IMAGE_SIZE_KB * 1024) {
                return base64Image;
            }

            // Start compression
            int quality = QUALITY_START;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

            // Gradually reduce quality until it's under the size limit or hit minimum quality
            while (outputStream.toByteArray().length > MAX_IMAGE_SIZE_KB * 1024 && quality > MIN_QUALITY) {
                outputStream.reset(); // Clear the output stream
                quality -= QUALITY_DECREMENT;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                Log.d(TAG, "Compressing image to quality: " + quality +
                        ", Size: " + (outputStream.toByteArray().length / 1024) + "KB");
            }

            // If we're still over the limit with minimum quality, then resize the bitmap
            if (outputStream.toByteArray().length > MAX_IMAGE_SIZE_KB * 1024) {
                bitmap = resizeBitmap(bitmap);
                outputStream.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            }

            // Convert back to Base64
            byte[] compressedBytes = outputStream.toByteArray();
            String compressedBase64 = Base64.encodeToString(compressedBytes, Base64.DEFAULT);

            Log.d(TAG, "Original size: " + (base64Image.length() / 1024) + "KB, " +
                    "Compressed size: " + (compressedBase64.length() / 1024) + "KB");

            return compressedBase64;
        } catch (Exception e) {
            Log.e(TAG, "Error compressing image: " + e.getMessage());
            return base64Image; // Return original if compression fails
        }
    }

    /**
     * Resizes a bitmap to reduce its dimensions by half
     *
     * @param bitmap The original bitmap
     * @return The resized bitmap
     */
    private static Bitmap resizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Calculate new dimensions (50% of original)
        int newWidth = width / 2;
        int newHeight = height / 2;

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    /**
     * Converts a Base64 encoded string to a byte array
     *
     * @param base64String The Base64 encoded string
     * @return The decoded byte array
     */
    public static byte[] base64ToByteArray(String base64String) {
        // Remove data URL prefix if present (e.g., "data:image/jpeg;base64,")
        if (base64String.contains(",")) {
            base64String = base64String.substring(base64String.indexOf(",") + 1);
        }

        return Base64.decode(base64String, Base64.DEFAULT);
    }

    /**
     * Converts a bitmap to a Base64 encoded string
     *
     * @param bitmap The bitmap to convert
     * @return The Base64 encoded string
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}