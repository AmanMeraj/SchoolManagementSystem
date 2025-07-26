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
import com.school.schoolmanagement.Students.Model.LastSubmittedFeesResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FeesSlipPdfGenerator {
    private static final String TAG = "FeesSlipPdfGenerator";
    private Context context;
    private Paint titlePaint, headerPaint, labelPaint, valuePaint, dividerPaint, iconPaint;
    private static final int PAGE_WIDTH = 595; // A4 width in points
    private static final int PAGE_HEIGHT = 842; // A4 height in points
    private static final int MARGIN = 30;
    private static final int CONTENT_WIDTH = PAGE_WIDTH - (2 * MARGIN);
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    public FeesSlipPdfGenerator(Context context) {
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
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
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
            iconPaint.setColor(Color.parseColor("#1976D2")); // Blue background
            iconPaint.setAntiAlias(true);

        } catch (Exception e) {
            Log.e(TAG, "Error initializing paints: " + e.getMessage());
        }
    }

    public void generateFeesSlip(LastSubmittedFeesResponse.Data feesData, String feeMonth) {
        PdfDocument pdfDocument = null;
        FileOutputStream fileOutputStream = null;

        try {
            // Validate fees data
            if (feesData == null) {
                Log.e(TAG, "Fees data is null");
                showError("Fees data is not available");
                return;
            }

            Log.d(TAG, "Starting PDF generation for fees slip");

            // Create PDF document
            pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            // Draw the fees slip content
            drawFeesSlip(canvas, feesData, feeMonth);

            // Finish the page
            pdfDocument.finishPage(page);

            // Save the PDF
            String fileName = generateFileName(feesData);
            savePdfDocument(pdfDocument, fileName);

        } catch (SecurityException e) {
            Log.e(TAG, "Permission error: " + e.getMessage());
            showError("Permission denied. Please grant storage permission.");
        } catch (IOException e) {
            Log.e(TAG, "IO error generating fees slip: " + e.getMessage());
            showError("Error saving fees slip");
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error generating fees slip: " + e.getMessage());
            showError("Error generating fees slip");
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

    private String generateFileName(LastSubmittedFeesResponse.Data feesData) {
        try {
            String studentName = feesData.getName() != null ?
                    feesData.getName().replaceAll("[^a-zA-Z0-9]", "_") : "Student";
            String timestamp = getCurrentTimestamp();
            return "Fees_Slip_" + studentName + "_" + timestamp + ".pdf";
        } catch (Exception e) {
            Log.e(TAG, "Error generating filename: " + e.getMessage());
            return "Fees_Slip_" + getCurrentTimestamp() + ".pdf";
        }
    }

    private void drawFeesSlip(Canvas canvas, LastSubmittedFeesResponse.Data feesData, String feeMonth) {
        try {
            int currentY = MARGIN + 40;

            // Draw main heading
            String heading = "Fees Slip of " + (feeMonth != null ? feeMonth : formatDate(feesData.getDate()));
            canvas.drawText(heading, PAGE_WIDTH / 2, currentY, titlePaint);
            currentY += 60;

            // Draw main card
            currentY = drawMainCard(canvas, feesData, currentY);

            // Draw footer
            drawFooter(canvas);

        } catch (Exception e) {
            Log.e(TAG, "Error drawing fees slip: " + e.getMessage());
            throw e;
        }
    }

    private int drawMainCard(Canvas canvas, LastSubmittedFeesResponse.Data feesData, int currentY) {
        try {
            // Calculate card height
            int cardHeight = 560;

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

            // Draw fee icon
            currentY = drawFeeIcon(canvas, currentY);

            // Draw card title
            canvas.drawText("Last Submitted Fees", PAGE_WIDTH / 2, currentY, headerPaint);
            currentY += 40;

            // Draw information container
            currentY = drawInformationContainer(canvas, feesData, currentY);


            return currentY + 30;

        } catch (Exception e) {
            Log.e(TAG, "Error drawing main card: " + e.getMessage());
            return currentY + 520;
        }
    }

    private int drawFeeIcon(Canvas canvas, int currentY) {
        try {
            int iconSize = 50;
            int iconX = (PAGE_WIDTH - iconSize) / 2;

            // Try to load the fee receipt icon
            Bitmap iconBitmap = null;
            try {
                iconBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.fee_recepit);
            } catch (Exception e) {
                Log.w(TAG, "Could not load fee_recepit drawable: " + e.getMessage());
            }

            if (iconBitmap != null) {
                // Scale the icon
                Bitmap scaledIcon = Bitmap.createScaledBitmap(iconBitmap, iconSize, iconSize, false);
                canvas.drawBitmap(scaledIcon, iconX, currentY, null);

                if (scaledIcon != iconBitmap) {
                    scaledIcon.recycle();
                }
            } else {
                // Draw a placeholder icon (circular with ₹ symbol)
                canvas.drawCircle(iconX + iconSize/2, currentY + iconSize/2, iconSize/2, iconPaint);

                Paint iconTextPaint = new Paint();
                iconTextPaint.setColor(Color.WHITE);
                iconTextPaint.setTextSize(24);
                iconTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                iconTextPaint.setAntiAlias(true);
                iconTextPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("₹", iconX + iconSize/2, currentY + iconSize/2 + 8, iconTextPaint);
            }

            return currentY + iconSize + 20;

        } catch (Exception e) {
            Log.e(TAG, "Error drawing fee icon: " + e.getMessage());
            return currentY + 70;
        }
    }

    private int drawInformationContainer(Canvas canvas, LastSubmittedFeesResponse.Data feesData, int currentY) {
        try {
            // Increased container height to fit all rows
            int containerHeight = 400;  // previously 280

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

            // Draw all fee information fields
            currentY = drawFeeInfoRow(canvas, "Registration/ID",
                    String.valueOf(feesData.getId()), fieldStartX, currentY);

            currentY = drawFeeInfoRow(canvas, "Class",
                    feesData.getStudentClass() != null ? feesData.getStudentClass() : "N/A",
                    fieldStartX, currentY);

            currentY = drawFeeInfoRow(canvas, "Name",
                    feesData.getName() != null ? feesData.getName() : "N/A",
                    fieldStartX, currentY);

            currentY = drawFeeInfoRow(canvas, "Fee Month",
                    feesData.getDate() != null ? formatDate(feesData.getDate()) : "N/A",
                    fieldStartX, currentY);

            currentY = drawFeeInfoRow(canvas, "Total Amount",
                    "₹ " + decimalFormat.format(feesData.getTotalAmount()),
                    fieldStartX, currentY);

            currentY = drawFeeInfoRow(canvas, "Deposit",
                    "₹ " + decimalFormat.format(feesData.getDepositAmount()),
                    fieldStartX, currentY);

            currentY = drawFeeInfoRow(canvas, "Remainings",
                    "₹ " + decimalFormat.format(feesData.getRemainingAmount()),
                    fieldStartX, currentY, true); // Last row, no divider

            return currentY + 35;

        } catch (Exception e) {
            Log.e(TAG, "Error drawing information container: " + e.getMessage());
            return currentY + 380;
        }
    }

    private int drawFeeInfoRow(Canvas canvas, String label, String value, int x, int y) {
        return drawFeeInfoRow(canvas, label, value, x, y, false);
    }

    private int drawFeeInfoRow(Canvas canvas, String label, String value, int x, int y, boolean isLast) {
        try {
            // Draw label
            canvas.drawText(label, x, y, labelPaint);
            y += 18;

            // Draw value with appropriate color for amounts
            Paint valueDisplayPaint = new Paint(valuePaint);
            if (label.contains("Amount") || label.contains("Deposit") || label.contains("Remaining")) {
                if (label.equals("Remainings") && value.contains("0.00")) {
                    valueDisplayPaint.setColor(Color.parseColor("#4CAF50")); // Green for zero remaining
                } else if (label.contains("Amount")) {
                    valueDisplayPaint.setColor(Color.parseColor("#FF5722")); // Red-orange for amounts
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
            Log.e(TAG, "Error drawing fee info row: " + e.getMessage());
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

    private String formatDate(String dateString) {
        try {
            if (dateString == null || dateString.isEmpty()) {
                return "N/A";
            }

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());

            Date date = inputFormat.parse(dateString);
            if (date != null) {
                return outputFormat.format(date);
            }
            return dateString;
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date: " + e.getMessage());
            return dateString;
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
                        Toast.makeText(context, "Fees slip saved to Downloads", Toast.LENGTH_LONG).show()
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