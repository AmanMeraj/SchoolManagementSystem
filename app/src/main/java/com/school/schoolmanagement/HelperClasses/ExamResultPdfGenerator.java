package com.school.schoolmanagement.HelperClasses;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.school.schoolmanagement.R;
import com.school.schoolmanagement.Students.Model.StudentsExamResultResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExamResultPdfGenerator {
    private static final String TAG = "ExamResultPdfGenerator";
    private Context context;
    private Paint titlePaint, headerPaint, labelPaint, valuePaint, dividerPaint, iconPaint;
    private static final int PAGE_WIDTH = 595; // A4 width in points
    private static final int PAGE_HEIGHT = 842; // A4 height in points
    private static final int MARGIN = 30;
    private static final int CONTENT_WIDTH = PAGE_WIDTH - (2 * MARGIN);

    public ExamResultPdfGenerator(Context context) {
        this.context = context;
        initializePaints();
    }

    private void initializePaints() {
        try {
            // Title paint (main heading)
            titlePaint = new Paint();
            titlePaint.setColor(Color.BLACK);
            titlePaint.setTextSize(20);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setAntiAlias(true);
            titlePaint.setTextAlign(Paint.Align.CENTER);

            // Header paint (card title)
            headerPaint = new Paint();
            headerPaint.setColor(Color.BLACK);
            headerPaint.setTextSize(18);
            headerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            headerPaint.setAntiAlias(true);
            headerPaint.setTextAlign(Paint.Align.CENTER);

            // Label paint (field labels)
            labelPaint = new Paint();
            labelPaint.setColor(Color.BLACK);
            labelPaint.setTextSize(12);
            labelPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            labelPaint.setAntiAlias(true);

            // Value paint (field values)
            valuePaint = new Paint();
            valuePaint.setColor(Color.parseColor("#1976D2")); // Blue color
            valuePaint.setTextSize(12);
            valuePaint.setAntiAlias(true);

            // Divider paint
            dividerPaint = new Paint();
            dividerPaint.setColor(Color.parseColor("#E0E0E0"));
            dividerPaint.setStrokeWidth(1);

            // Icon background paint
            iconPaint = new Paint();
            iconPaint.setColor(Color.parseColor("#FF9800")); // Orange background like in XML
            iconPaint.setAntiAlias(true);

        } catch (Exception e) {
            Log.e(TAG, "Error initializing paints: " + e.getMessage());
        }
    }

    public void generateExamResultSlip(StudentsExamResultResponse.Data examResultData, String examName) {
        PdfDocument pdfDocument = null;
        FileOutputStream fileOutputStream = null;

        try {
            // Validate exam result data
            if (examResultData == null) {
                Log.e(TAG, "Exam result data is null");
                showError("Exam result data is not available");
                return;
            }

            Log.d(TAG, "Starting PDF generation for exam result");

            // Create PDF document
            pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            // Draw the exam result content
            drawExamResult(canvas, examResultData, examName);

            // Finish the page
            pdfDocument.finishPage(page);

            // Save the PDF
            String fileName = generateFileName(examResultData);
            savePdfDocument(pdfDocument, fileName);

        } catch (SecurityException e) {
            Log.e(TAG, "Permission error: " + e.getMessage());
            showError("Permission denied. Please grant storage permission.");
        } catch (IOException e) {
            Log.e(TAG, "IO error generating exam result: " + e.getMessage());
            showError("Error saving exam result");
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error generating exam result: " + e.getMessage());
            showError("Error generating exam result");
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

    private String generateFileName(StudentsExamResultResponse.Data examResultData) {
        try {
            String studentName = examResultData.getName() != null ?
                    examResultData.getName().replaceAll("[^a-zA-Z0-9]", "_") : "Student";
            String timestamp = getCurrentTimestamp();
            return "Exam_Result_" + studentName + "_" + timestamp + ".pdf";
        } catch (Exception e) {
            Log.e(TAG, "Error generating filename: " + e.getMessage());
            return "Exam_Result_" + getCurrentTimestamp() + ".pdf";
        }
    }

    private void drawExamResult(Canvas canvas, StudentsExamResultResponse.Data examResultData, String examName) {
        try {
            int currentY = MARGIN ;

            // Draw main heading
            String heading = "";
            canvas.drawText(heading, PAGE_WIDTH / 2, currentY, titlePaint);
            currentY += 60;

            // Draw main card
            currentY = drawMainCard(canvas, examResultData, examName, currentY);

            // Draw footer
            drawFooter(canvas);

        } catch (Exception e) {
            Log.e(TAG, "Error drawing exam result: " + e.getMessage());
            throw e;
        }
    }

    private int drawMainCard(Canvas canvas, StudentsExamResultResponse.Data examResultData, String examName, int currentY) {
        try {
            // Calculate card height
            int cardHeight = 680;

            // Draw card background with shadow effect
            RectF shadowRect = new RectF(MARGIN + 3, currentY + 3, PAGE_WIDTH - MARGIN + 3, currentY + cardHeight + 3);
            Paint shadowPaint = new Paint();
            shadowPaint.setColor(Color.parseColor("#CCCCCC"));
            shadowPaint.setAntiAlias(true);
            canvas.drawRoundRect(shadowRect, 15, 15, shadowPaint);

            // Draw main card background
            RectF cardRect = new RectF(MARGIN, currentY, PAGE_WIDTH - MARGIN, currentY + cardHeight);
            Paint cardPaint = new Paint();
            cardPaint.setColor(Color.WHITE);
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

            // Draw card title
            canvas.drawText("Exam Result", PAGE_WIDTH / 2, currentY, headerPaint);
            currentY += 40;

            // Draw student avatar
            currentY = drawStudentAvatar(canvas, currentY);

            // Draw student name
            String studentName = examResultData.getName() != null ? examResultData.getName() : "Student Name";
            Paint namePaint = new Paint();
            namePaint.setColor(Color.BLACK);
            namePaint.setTextSize(20);
            namePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            namePaint.setAntiAlias(true);
            namePaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(studentName, PAGE_WIDTH / 2, currentY, namePaint);
            currentY += 40;

            // Draw information container
            currentY = drawInformationContainer(canvas, examResultData, currentY);

            return currentY + 30;

        } catch (Exception e) {
            Log.e(TAG, "Error drawing main card: " + e.getMessage());
            return currentY + 620;
        }
    }

    private int drawStudentAvatar(Canvas canvas, int currentY) {
        try {
            int avatarSize = 120;
            int avatarX = (PAGE_WIDTH - avatarSize) / 2;

            // Draw outer circle (orange background like in XML)
            Paint outerCirclePaint = new Paint();
            outerCirclePaint.setColor(Color.parseColor("#FF9800")); // Orange color
            outerCirclePaint.setAntiAlias(true);
            canvas.drawCircle(avatarX + avatarSize/2, currentY + avatarSize/2, avatarSize/2, outerCirclePaint);

            // Draw inner circle (slightly smaller)
            Paint innerCirclePaint = new Paint();
            innerCirclePaint.setColor(Color.parseColor("#FFB74D")); // Lighter orange
            innerCirclePaint.setAntiAlias(true);
            canvas.drawCircle(avatarX + avatarSize/2, currentY + avatarSize/2, (avatarSize/2) - 5, innerCirclePaint);

            // Try to load the avatar image
            Bitmap avatarBitmap = null;
            try {
                avatarBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar2);
            } catch (Exception e) {
                Log.w(TAG, "Could not load avatar2 drawable: " + e.getMessage());
            }

            if (avatarBitmap != null) {
                // Scale the avatar image
                int imageSize = avatarSize - 20;
                Bitmap scaledAvatar = Bitmap.createScaledBitmap(avatarBitmap, imageSize, imageSize, false);
                canvas.drawBitmap(scaledAvatar, avatarX + 10, currentY + 10, null);

                if (scaledAvatar != avatarBitmap) {
                    scaledAvatar.recycle();
                }
            } else {
                // Draw a placeholder avatar (person icon)
                Paint avatarTextPaint = new Paint();
                avatarTextPaint.setColor(Color.WHITE);
                avatarTextPaint.setTextSize(40);
                avatarTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                avatarTextPaint.setAntiAlias(true);
                avatarTextPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("👤", avatarX + avatarSize/2, currentY + avatarSize/2 + 15, avatarTextPaint);
            }

            return currentY + avatarSize + 20;

        } catch (Exception e) {
            Log.e(TAG, "Error drawing student avatar: " + e.getMessage());
            return currentY + 140;
        }
    }

    private int drawInformationContainer(Canvas canvas, StudentsExamResultResponse.Data examResultData, int currentY) {
        try {
            // Container height to fit all rows
            int containerHeight = 390;

            // Draw container background
            RectF containerRect = new RectF(MARGIN + 20, currentY, PAGE_WIDTH - MARGIN - 20, currentY + containerHeight);
            Paint containerPaint = new Paint();
            containerPaint.setColor(Color.parseColor("#F8F9FA"));
            containerPaint.setAntiAlias(true);
            canvas.drawRoundRect(containerRect, 8, 8, containerPaint);

            // Draw container border
            Paint containerBorderPaint = new Paint();
            containerBorderPaint.setColor(Color.parseColor("#E9ECEF"));
            containerBorderPaint.setStyle(Paint.Style.STROKE);
            containerBorderPaint.setStrokeWidth(1);
            containerBorderPaint.setAntiAlias(true);
            canvas.drawRoundRect(containerRect, 8, 8, containerBorderPaint);

            currentY += 25;
            int fieldStartX = MARGIN + 35;

            // Draw all exam result information fields
            currentY = drawExamInfoRow(canvas, "Registration/ID",
                    String.valueOf(examResultData.getId()), fieldStartX, currentY);

            currentY = drawExamInfoRow(canvas, "Class",
                    examResultData.getStudentClass() != null ? examResultData.getStudentClass() : "N/A",
                    fieldStartX, currentY);

            currentY = drawExamInfoRow(canvas, "Name",
                    examResultData.getName() != null ? examResultData.getName() : "N/A",
                    fieldStartX, currentY);

            currentY = drawExamInfoRow(canvas, "Subject Counts",
                    String.valueOf(examResultData.getSubjectCount()),
                    fieldStartX, currentY);

            currentY = drawExamInfoRow(canvas, "Total Marks",
                    String.valueOf(examResultData.getTotalMarks()),
                    fieldStartX, currentY);

            currentY = drawExamInfoRow(canvas, "Obtained Marks",
                    String.valueOf(examResultData.getObtainedMarks()),
                    fieldStartX, currentY);

            currentY = drawExamInfoRow(canvas, "Percentage",
                    String.format("%.1f%%", examResultData.getPercentage()),
                    fieldStartX, currentY, true); // Last row, no divider

            return currentY + 35;

        } catch (Exception e) {
            Log.e(TAG, "Error drawing information container: " + e.getMessage());
            return currentY + 350;
        }
    }

    private int drawExamInfoRow(Canvas canvas, String label, String value, int x, int y) {
        return drawExamInfoRow(canvas, label, value, x, y, false);
    }

    private int drawExamInfoRow(Canvas canvas, String label, String value, int x, int y, boolean isLast) {
        try {
            // Draw label
            canvas.drawText(label, x, y, labelPaint);
            y += 18;

            // Draw value with appropriate color for marks and percentage
            Paint valueDisplayPaint = new Paint(valuePaint);
            if (label.contains("Marks") || label.contains("Percentage")) {
                if (label.equals("Obtained Marks")) {
                    valueDisplayPaint.setColor(Color.parseColor("#4CAF50")); // Green for obtained marks
                } else if (label.equals("Percentage")) {
                    float percentage = Float.parseFloat(value.replace("%", ""));
                    if (percentage >= 75) {
                        valueDisplayPaint.setColor(Color.parseColor("#4CAF50")); // Green for good percentage
                    } else if (percentage >= 50) {
                        valueDisplayPaint.setColor(Color.parseColor("#FF9800")); // Orange for average
                    } else {
                        valueDisplayPaint.setColor(Color.parseColor("#F44336")); // Red for low percentage
                    }
                } else {
                    valueDisplayPaint.setColor(Color.parseColor("#2196F3")); // Blue for total marks
                }
            }

            canvas.drawText(value, x, y, valueDisplayPaint);
            y += 20;

            // Draw divider if not the last row
            if (!isLast) {
                canvas.drawLine(x, y, PAGE_WIDTH - MARGIN - 35, y, dividerPaint);
                y += 15;
            }

            return y;

        } catch (Exception e) {
            Log.e(TAG, "Error drawing exam info row: " + e.getMessage());
            return y + 53;
        }
    }

    private void drawFooter(Canvas canvas) {
        try {
            int footerY = PAGE_HEIGHT - 80;

            // Draw school info or footer text
            Paint footerPaint = new Paint();
            footerPaint.setColor(Color.GRAY);
            footerPaint.setTextSize(10);
            footerPaint.setTextAlign(Paint.Align.CENTER);
            footerPaint.setAntiAlias(true);

            canvas.drawText("Generated on: " + getCurrentDate(), PAGE_WIDTH / 2, footerY, footerPaint);
            canvas.drawText("School Management System", PAGE_WIDTH / 2, footerY + 15, footerPaint);

        } catch (Exception e) {
            Log.e(TAG, "Error drawing footer: " + e.getMessage());
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
                        Toast.makeText(context, "Exam result saved to Downloads", Toast.LENGTH_LONG).show()
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

    private void showError(String message) {
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).runOnUiThread(() ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            );
        }
    }
}
