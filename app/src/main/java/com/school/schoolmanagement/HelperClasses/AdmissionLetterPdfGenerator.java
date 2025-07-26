package com.school.schoolmanagement.HelperClasses;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.core.content.ContextCompat;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.AdmissionConfirmation.ActivityStudentsAdmissionConfirmation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdmissionLetterPdfGenerator {
    private static final String TAG = "AdmissionPdfGenerator";
    private Context context;
    private Paint titlePaint, headerPaint, contentPaint, valuePaint, dividerPaint;
    private static final int PAGE_WIDTH = 595; // A4 width in points
    private static final int PAGE_HEIGHT = 842; // A4 height in points
    private static final int MARGIN = 30;
    private static final int CONTENT_WIDTH = PAGE_WIDTH - (2 * MARGIN);

    public AdmissionLetterPdfGenerator(Context context) {
        this.context = context;
        initializePaints();
    }

    private void initializePaints() {
        try {
            // Title paint (Admission Confirmation)
            titlePaint = new Paint();
            titlePaint.setColor(Color.BLACK);
            titlePaint.setTextSize(24);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setAntiAlias(true);
            titlePaint.setTextAlign(Paint.Align.CENTER);

            // Header paint (field labels)
            headerPaint = new Paint();
            headerPaint.setColor(Color.BLACK);
            headerPaint.setTextSize(14);
            headerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            headerPaint.setAntiAlias(true);

            // Content paint (student name)
            contentPaint = new Paint();
            contentPaint.setColor(Color.BLACK);
            contentPaint.setTextSize(18);
            contentPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            contentPaint.setAntiAlias(true);
            contentPaint.setTextAlign(Paint.Align.CENTER);

            // Value paint (field values)
            valuePaint = new Paint();
            valuePaint.setColor(Color.parseColor("#1976D2")); // Blue color
            valuePaint.setTextSize(12);
            valuePaint.setAntiAlias(true);

            // Divider paint
            dividerPaint = new Paint();
            dividerPaint.setColor(Color.parseColor("#E0E0E0"));
            dividerPaint.setStrokeWidth(1);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing paints: " + e.getMessage());
        }
    }

    public void generateAdmissionLetter(ActivityStudentsAdmissionConfirmation.AdmissionData admissionData) {
        PdfDocument pdfDocument = null;
        FileOutputStream fileOutputStream = null;

        try {
            // Validate admission data
            if (admissionData == null) {
                Log.e(TAG, "Admission data is null");
                showError("Student data is not available");
                return;
            }

            if (!admissionData.isDataValid()) {
                Log.e(TAG, "Admission data is invalid");
                showError("Invalid student data");
                return;
            }

            Log.d(TAG, "Starting PDF generation for: " + admissionData.studentName);

            // Create PDF document
            pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            // Draw the admission letter content
            drawAdmissionLetter(canvas, admissionData);

            // Finish the page
            pdfDocument.finishPage(page);

            // Save the PDF
            String fileName = generateFileName(admissionData);
            savePdfDocument(pdfDocument, fileName);

        } catch (SecurityException e) {
            Log.e(TAG, "Permission error: " + e.getMessage());
            showError("Permission denied. Please grant storage permission.");
        } catch (IOException e) {
            Log.e(TAG, "IO error generating admission letter: " + e.getMessage());
            showError("Error saving admission letter");
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error generating admission letter: " + e.getMessage());
            showError("Error generating admission letter");
        } finally {
            // Clean up resources
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (pdfDocument != null) {
                    pdfDocument.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error closing resources: " + e.getMessage());
            }
        }
    }

    private String generateFileName(ActivityStudentsAdmissionConfirmation.AdmissionData admissionData) {
        try {
            String baseName = admissionData.getFormattedAdmissionTitle();
            String timestamp = getCurrentTimestamp();
            return baseName + "_" + timestamp + ".pdf";
        } catch (Exception e) {
            Log.e(TAG, "Error generating filename: " + e.getMessage());
            return "Admission_Letter_" + getCurrentTimestamp() + ".pdf";
        }
    }

    private void drawAdmissionLetter(Canvas canvas, ActivityStudentsAdmissionConfirmation.AdmissionData admissionData) {
        try {
            int currentY = MARGIN + 50;

            // Draw title
            canvas.drawText("Admission Confirmation", PAGE_WIDTH / 2, currentY, titlePaint);
            currentY += 80;

            // Draw profile image with proper fallback
            currentY = drawProfileImageWithFallback(canvas, admissionData.profileImageBase64, currentY);

            // Draw student name
            String studentName = admissionData.studentName != null ? admissionData.studentName : "Student Name";
            canvas.drawText(studentName, PAGE_WIDTH / 2, currentY, contentPaint);
            currentY += 50;

            // Draw information card
            currentY = drawInformationCard(canvas, admissionData, currentY);

            // Draw footer
            drawFooter(canvas);

        } catch (Exception e) {
            Log.e(TAG, "Error drawing admission letter: " + e.getMessage());
            throw e;
        }
    }

    private int drawProfileImageWithFallback(Canvas canvas, String profileImageBase64, int currentY) {
        try {
            Bitmap profileBitmap = null;

            // Try to decode base64 image first
            if (profileImageBase64 != null && !profileImageBase64.trim().isEmpty()) {
                profileBitmap = decodeBase64Image(profileImageBase64);
            }

            // If base64 failed or is null, load default avatar
            if (profileBitmap == null) {
                try {
                    profileBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar2);
                    Log.d(TAG, "Using default avatar2 drawable");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to load default avatar: " + e.getMessage());
                }
            }

            if (profileBitmap != null) {
                int imageSize = 100;
                int imageX = (PAGE_WIDTH - imageSize) / 2;

                // Draw outer circular background (orange background like in XML)
                Paint outerBackgroundPaint = new Paint();
                outerBackgroundPaint.setColor(Color.parseColor("#FF9800")); // Orange color
                outerBackgroundPaint.setAntiAlias(true);
                canvas.drawCircle(imageX + imageSize/2, currentY + imageSize/2, (imageSize/2) + 10, outerBackgroundPaint);

                // Draw inner circular background
                Paint innerBackgroundPaint = new Paint();
                innerBackgroundPaint.setColor(Color.parseColor("#FFB74D")); // Lighter orange
                innerBackgroundPaint.setAntiAlias(true);
                canvas.drawCircle(imageX + imageSize/2, currentY + imageSize/2, (imageSize/2) + 5, innerBackgroundPaint);

                // Draw profile image in circular shape
                Bitmap circularBitmap = getCircularBitmap(profileBitmap, imageSize);
                if (circularBitmap != null) {
                    canvas.drawBitmap(circularBitmap, imageX, currentY, null);
                    circularBitmap.recycle(); // Free memory
                }

                // Only recycle if it's not the original resource bitmap
                if (!isResourceBitmap(profileBitmap)) {
                    profileBitmap.recycle();
                }

                currentY += imageSize + 30;
            } else {
                // If everything fails, draw a placeholder circle
                int imageSize = 100;
                int imageX = (PAGE_WIDTH - imageSize) / 2;

                Paint placeholderPaint = new Paint();
                placeholderPaint.setColor(Color.parseColor("#FF9800"));
                placeholderPaint.setAntiAlias(true);
                canvas.drawCircle(imageX + imageSize/2, currentY + imageSize/2, imageSize/2, placeholderPaint);

                // Draw initials or placeholder text
                Paint textPaint = new Paint();
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(30);
                textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                textPaint.setAntiAlias(true);
                textPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("?", imageX + imageSize/2, currentY + imageSize/2 + 10, textPaint);

                currentY += imageSize + 30;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error drawing profile image: " + e.getMessage());
            currentY += 130; // Continue without image
        }
        return currentY;
    }

    private boolean isResourceBitmap(Bitmap bitmap) {
        // Simple check - resource bitmaps typically shouldn't be recycled
        // You might want to implement a more sophisticated check
        return bitmap != null && !bitmap.isRecycled();
    }

    private int drawInformationCard(Canvas canvas, ActivityStudentsAdmissionConfirmation.AdmissionData admissionData, int currentY) {
        try {
            // Calculate card height dynamically - increased for wrapped text
            int cardHeight = 380; // Increased height to accommodate wrapped text

            // Draw information card background
            RectF cardRect = new RectF(MARGIN, currentY, PAGE_WIDTH - MARGIN, currentY + cardHeight);
            Paint cardPaint = new Paint();
            cardPaint.setColor(Color.parseColor("#F5F5F5"));
            cardPaint.setAntiAlias(true);
            canvas.drawRoundRect(cardRect, 10, 10, cardPaint);

            // Draw card border
            Paint borderPaint = new Paint();
            borderPaint.setColor(Color.parseColor("#E0E0E0"));
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(2);
            borderPaint.setAntiAlias(true);
            canvas.drawRoundRect(cardRect, 10, 10, borderPaint);

            currentY += 30;
            int fieldStartX = MARGIN + 20;

            // Draw student information fields with proper spacing
            currentY = drawInfoField(canvas, "Registration/ID",
                    admissionData.registrationId != null ? admissionData.registrationId : "N/A",
                    fieldStartX, currentY);
            currentY = drawDivider(canvas, fieldStartX, currentY);

            currentY = drawInfoField(canvas, "Admission Date",
                    admissionData.admissionDate != null ? admissionData.admissionDate : "N/A",
                    fieldStartX, currentY);
            currentY = drawDivider(canvas, fieldStartX, currentY);

            currentY = drawInfoField(canvas, "Account Status",
                    admissionData.accountStatus != null ? admissionData.accountStatus : "ACTIVE",
                    fieldStartX, currentY);
            currentY = drawDivider(canvas, fieldStartX, currentY);

            currentY = drawInfoField(canvas, "Username",
                    admissionData.username != null ? admissionData.username : "N/A",
                    fieldStartX, currentY);
            currentY = drawDivider(canvas, fieldStartX, currentY);

            currentY = drawInfoField(canvas, "Password",
                    admissionData.password != null ? admissionData.password : "N/A",
                    fieldStartX, currentY);

            return currentY + 40; // Add some bottom margin

        } catch (Exception e) {
            Log.e(TAG, "Error drawing information card: " + e.getMessage());
            return currentY + 400; // Return fallback position with increased height
        }
    }

    private void drawFooter(Canvas canvas) {
        try {
            int footerY = PAGE_HEIGHT - 100;
            Paint footerPaint = new Paint();
            footerPaint.setColor(Color.GRAY);
            footerPaint.setTextSize(10);
            footerPaint.setTextAlign(Paint.Align.CENTER);
            footerPaint.setAntiAlias(true);
            canvas.drawText("Generated on: " + getCurrentDate(), PAGE_WIDTH / 2, footerY, footerPaint);
        } catch (Exception e) {
            Log.e(TAG, "Error drawing footer: " + e.getMessage());
        }
    }

    private int drawInfoField(Canvas canvas, String label, String value, int x, int y) {
        try {
            // Draw label
            canvas.drawText(label, x, y, headerPaint);
            y += 25;

            // Draw value with proper color based on field type
            Paint valueCopyPaint = new Paint(valuePaint);
            if (label.equals("Account Status") && "ACTIVE".equalsIgnoreCase(value)) {
                valueCopyPaint.setColor(Color.parseColor("#4CAF50")); // Green for active status
            }

            String displayValue = value != null ? value : "N/A";

            // Handle long text (especially passwords) by wrapping
            if (displayValue.length() > 40) {
                y = drawWrappedText(canvas, displayValue, x, y, valueCopyPaint, PAGE_WIDTH - MARGIN - 40);
            } else {
                canvas.drawText(displayValue, x, y, valueCopyPaint);
                y += 30;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error drawing info field: " + e.getMessage());
        }
        return y;
    }

    private int drawWrappedText(Canvas canvas, String text, int x, int y, Paint paint, int maxWidth) {
        try {
            if (text == null || text.isEmpty()) {
                canvas.drawText("N/A", x, y, paint);
                return y + 30;
            }

            // Calculate available width for text
            int availableWidth = maxWidth - x;

            // Measure text width
            float textWidth = paint.measureText(text);

            if (textWidth <= availableWidth) {
                // Text fits in one line
                canvas.drawText(text, x, y, paint);
                return y + 30;
            }

            // Text needs to be wrapped
            String[] words = text.split("(?<=\\G.{20})"); // Split every 20 characters for long passwords
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                String testLine = currentLine.toString() + word;
                float testWidth = paint.measureText(testLine);

                if (testWidth <= availableWidth && currentLine.length() == 0) {
                    currentLine.append(word);
                } else if (testWidth <= availableWidth) {
                    currentLine.append(word);
                } else {
                    // Draw current line and start new line
                    if (currentLine.length() > 0) {
                        canvas.drawText(currentLine.toString(), x, y, paint);
                        y += 20; // Line height
                        currentLine = new StringBuilder(word);
                    } else {
                        // Single word is too long, truncate it
                        String truncated = word.substring(0, Math.min(word.length(), 25)) + "...";
                        canvas.drawText(truncated, x, y, paint);
                        y += 20;
                    }
                }
            }

            // Draw remaining text
            if (currentLine.length() > 0) {
                canvas.drawText(currentLine.toString(), x, y, paint);
                y += 20;
            }

            return y + 10; // Add some extra spacing after wrapped text

        } catch (Exception e) {
            Log.e(TAG, "Error drawing wrapped text: " + e.getMessage());
            canvas.drawText("Error displaying text", x, y, paint);
            return y + 30;
        }
    }

    private Bitmap decodeBase64Image(String base64String) {
        if (base64String == null || base64String.trim().isEmpty()) {
            Log.d(TAG, "Base64 string is null or empty");
            return null;
        }

        try {
            // Remove data URL prefix if present
            String cleanBase64 = base64String.trim();
            if (cleanBase64.startsWith("data:image")) {
                int commaIndex = cleanBase64.indexOf(",");
                if (commaIndex != -1) {
                    cleanBase64 = cleanBase64.substring(commaIndex + 1);
                }
            }

            // Remove any whitespace
            cleanBase64 = cleanBase64.replaceAll("\\s", "");

            byte[] decodedString = Base64.decode(cleanBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            if (bitmap == null) {
                Log.w(TAG, "Failed to decode base64 image - bitmap is null");
            } else {
                Log.d(TAG, "Successfully decoded base64 image");
            }

            return bitmap;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Invalid base64 string: " + e.getMessage());
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error decoding base64 image: " + e.getMessage());
            return null;
        }
    }

    private Bitmap getCircularBitmap(Bitmap bitmap, int size) {
        if (bitmap == null) {
            return null;
        }

        try {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
            Bitmap circularBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(circularBitmap);

            Paint paint = new Paint();
            paint.setAntiAlias(true);

            canvas.drawCircle(size / 2, size / 2, size / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(scaledBitmap, 0, 0, paint);

            // Clean up scaled bitmap if it's different from original
            if (scaledBitmap != bitmap) {
                scaledBitmap.recycle();
            }

            return circularBitmap;
        } catch (Exception e) {
            Log.e(TAG, "Error creating circular bitmap: " + e.getMessage());
            return null;
        }
    }

    private void savePdfDocument(PdfDocument pdfDocument, String fileName) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            // Check external storage state
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                throw new IOException("External storage not available");
            }

            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists() && !downloadsDir.mkdirs()) {
                throw new IOException("Cannot create downloads directory");
            }

            File pdfFile = new File(downloadsDir, fileName);
            Log.d(TAG, "Saving PDF to: " + pdfFile.getAbsolutePath());

            fileOutputStream = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(fileOutputStream);
            fileOutputStream.flush();

            Log.d(TAG, "PDF saved successfully: " + pdfFile.getAbsolutePath());

            // Show success message on UI thread
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "Admission letter saved to Downloads", Toast.LENGTH_LONG).show()
                );
            }

            // Open the PDF file
            openPdfFile(pdfFile);

        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing file output stream: " + e.getMessage());
                }
            }
        }
    }

    private void openPdfFile(File pdfFile) {
        try {
            Uri pdfUri = FileProvider.getUriForFile(context,
                    context.getApplicationContext().getPackageName() + ".fileprovider", pdfFile);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening PDF: " + e.getMessage());
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "PDF saved. Please check Downloads folder", Toast.LENGTH_SHORT).show()
                );
            }
        }
    }

    private String getCurrentTimestamp() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            return sdf.format(new Date());
        } catch (Exception e) {
            Log.e(TAG, "Error getting timestamp: " + e.getMessage());
            return String.valueOf(System.currentTimeMillis());
        }
    }

    private String getCurrentDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy HH:mm", Locale.getDefault());
            return sdf.format(new Date());
        } catch (Exception e) {
            Log.e(TAG, "Error getting current date: " + e.getMessage());
            return new Date().toString();
        }
    }

    private int drawDivider(Canvas canvas, int x, int y) {
        try {
            canvas.drawLine(x, y, PAGE_WIDTH - MARGIN - 20, y, dividerPaint);
            y += 15;
        } catch (Exception e) {
            Log.e(TAG, "Error drawing divider: " + e.getMessage());
        }
        return y;
    }

    private void showError(String message) {
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).runOnUiThread(() ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            );
        }
    }
}