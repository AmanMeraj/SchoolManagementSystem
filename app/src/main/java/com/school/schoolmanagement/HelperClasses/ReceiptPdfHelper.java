package com.school.schoolmanagement.HelperClasses;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.school.schoolmanagement.Admin.Model.ReceiptData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReceiptPdfHelper {

    private static final String TAG = "ReceiptPdfHelper";
    private Context context;

    // PDF dimensions
    private static final int PAGE_WIDTH = 595; // A4 width in points
    private static final int PAGE_HEIGHT = 842; // A4 height in points
    private static final int MINI_PAGE_WIDTH = 300; // Mini receipt width
    private static final int MINI_PAGE_HEIGHT = 400; // Mini receipt height

    // Margins and spacing
    private static final int MARGIN = 40;
    private static final int LINE_SPACING = 25;
    private static final int SECTION_SPACING = 40;

    public ReceiptPdfHelper(Context context) {
        this.context = context;
    }

    public void generateAndPrintDetailedReceipt(ReceiptData receiptData, boolean isMiniReceipt) {
        try {
            if (receiptData == null || !receiptData.isDataValid()) {
                showToast("Invalid receipt data");
                return;
            }

            // Generate PDF
            File pdfFile = isMiniReceipt ?
                    generateMiniReceiptPdf(receiptData) :
                    generateDetailedReceiptPdf(receiptData);

            if (pdfFile != null && pdfFile.exists()) {
                // Show options: Print or Share
                showPrintOptions(pdfFile, receiptData.getFormattedReceiptTitle());
            } else {
                showToast("Failed to generate PDF");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error generating and printing receipt: " + e.getMessage(), e);
            showToast("Error generating receipt");
        }
    }

    private File generateDetailedReceiptPdf(ReceiptData receiptData) {
        try {
            // Create PDF document
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

            int yPosition = MARGIN + 20;

            // Header
            yPosition = drawHeader(canvas, paint, yPosition);
            yPosition += SECTION_SPACING;

            // Student Information Section
            yPosition = drawSectionTitle(canvas, paint, "Student Information", yPosition);
            yPosition = drawKeyValue(canvas, paint, "Registration/ID:", receiptData.registrationId, yPosition);
            yPosition = drawKeyValue(canvas, paint, "Student Name:", receiptData.studentName, yPosition);
            yPosition = drawKeyValue(canvas, paint, "Class:", receiptData.className, yPosition);
            yPosition += SECTION_SPACING;

            // Fee Information Section
            yPosition = drawSectionTitle(canvas, paint, "Fee Information", yPosition);
            yPosition = drawKeyValue(canvas, paint, "Fee Month:", receiptData.feesMonth, yPosition);
            yPosition = drawKeyValue(canvas, paint, "Total Amount:", receiptData.totalAmount, yPosition);
            yPosition = drawKeyValue(canvas, paint, "Paid Amount:", receiptData.paidAmount, yPosition);
            yPosition = drawKeyValue(canvas, paint, "Remaining Amount:", receiptData.remainingAmount, yPosition);
            yPosition += SECTION_SPACING;

            // Payment Information Section (if available)
            if (receiptData.paymentDate != null || receiptData.receiptNumber != null) {
                yPosition = drawSectionTitle(canvas, paint, "Payment Information", yPosition);

                if (receiptData.receiptNumber != null) {
                    yPosition = drawKeyValue(canvas, paint, "Receipt Number:", receiptData.receiptNumber, yPosition);
                }
                if (receiptData.paymentDate != null) {
                    yPosition = drawKeyValue(canvas, paint, "Payment Date:", receiptData.paymentDate, yPosition);
                }
                if (receiptData.paymentMode != null) {
                    yPosition = drawKeyValue(canvas, paint, "Payment Mode:", receiptData.paymentMode, yPosition);
                }
                if (receiptData.transactionId != null) {
                    yPosition = drawKeyValue(canvas, paint, "Transaction ID:", receiptData.transactionId, yPosition);
                }
                yPosition += SECTION_SPACING;
            }

            // Footer
            drawFooter(canvas, paint, PAGE_HEIGHT - MARGIN - 40);

            document.finishPage(page);

            // Save PDF to file
            String fileName = "Fee_Receipt_Detailed_" + receiptData.studentName.replaceAll("\\s+", "_") +
                    "_" + System.currentTimeMillis() + ".pdf";
            File pdfFile = savePdfToFile(document, fileName);
            document.close();

            return pdfFile;

        } catch (Exception e) {
            Log.e(TAG, "Error generating detailed receipt PDF: " + e.getMessage(), e);
            return null;
        }
    }

    private File generateMiniReceiptPdf(ReceiptData receiptData) {
        try {
            // Create PDF document with smaller dimensions
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(MINI_PAGE_WIDTH, MINI_PAGE_HEIGHT, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

            int yPosition = 20;
            int miniMargin = 15;

            // Mini Header
            paint.setTextSize(14);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setColor(Color.BLACK);
            canvas.drawText("FEE RECEIPT", miniMargin, yPosition, paint);
            yPosition += 30;

            // Draw line
            paint.setStrokeWidth(1);
            canvas.drawLine(miniMargin, yPosition, MINI_PAGE_WIDTH - miniMargin, yPosition, paint);
            yPosition += 20;

            // Content with smaller font
            paint.setTextSize(10);
            paint.setTypeface(Typeface.DEFAULT);

            yPosition = drawMiniKeyValue(canvas, paint, "Student:", receiptData.studentName, miniMargin, yPosition);
            yPosition = drawMiniKeyValue(canvas, paint, "Class:", receiptData.className, miniMargin, yPosition);
            yPosition = drawMiniKeyValue(canvas, paint, "Month:", receiptData.feesMonth, miniMargin, yPosition);
            yPosition += 10;

            yPosition = drawMiniKeyValue(canvas, paint, "Total:", receiptData.totalAmount, miniMargin, yPosition);
            yPosition = drawMiniKeyValue(canvas, paint, "Paid:", receiptData.paidAmount, miniMargin, yPosition);
            yPosition = drawMiniKeyValue(canvas, paint, "Remaining:", receiptData.remainingAmount, miniMargin, yPosition);
            yPosition += 20;

            // Mini Footer
            paint.setTextSize(8);
            String currentDate = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(new Date());
            canvas.drawText("Generated: " + currentDate, miniMargin, yPosition, paint);

            document.finishPage(page);

            // Save PDF to file
            String fileName = "Fee_Receipt_Mini_" + receiptData.studentName.replaceAll("\\s+", "_") +
                    "_" + System.currentTimeMillis() + ".pdf";
            File pdfFile = savePdfToFile(document, fileName);
            document.close();

            return pdfFile;

        } catch (Exception e) {
            Log.e(TAG, "Error generating mini receipt PDF: " + e.getMessage(), e);
            return null;
        }
    }

    private int drawHeader(Canvas canvas, Paint paint, int yPosition) {
        // School/App Title
        paint.setTextSize(20);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setColor(Color.BLACK);
        canvas.drawText("SCHOOL MANAGEMENT SYSTEM", MARGIN, yPosition, paint);
        yPosition += 30;

        // Receipt title
        paint.setTextSize(16);
        canvas.drawText("FEE PAYMENT RECEIPT", MARGIN, yPosition, paint);
        yPosition += 20;

        // Current date and time
        paint.setTextSize(12);
        paint.setTypeface(Typeface.DEFAULT);
        String currentDate = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date());
        canvas.drawText("Generated on: " + currentDate, MARGIN, yPosition, paint);
        yPosition += 20;

        // Draw line
        paint.setStrokeWidth(2);
        canvas.drawLine(MARGIN, yPosition, PAGE_WIDTH - MARGIN, yPosition, paint);

        return yPosition + 10;
    }

    private int drawSectionTitle(Canvas canvas, Paint paint, String title, int yPosition) {
        paint.setTextSize(14);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setColor(Color.BLUE);
        canvas.drawText(title, MARGIN, yPosition, paint);
        yPosition += 25;

        // Draw underline
        paint.setStrokeWidth(1);
        canvas.drawLine(MARGIN, yPosition - 5, MARGIN + paint.measureText(title), yPosition - 5, paint);

        return yPosition;
    }

    private int drawKeyValue(Canvas canvas, Paint paint, String key, String value, int yPosition) {
        paint.setTextSize(12);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setColor(Color.BLACK);

        // Draw key
        canvas.drawText(key, MARGIN, yPosition, paint);

        // Draw value (right-aligned)
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        String displayValue = value != null ? value : "N/A";
        float valueWidth = paint.measureText(displayValue);
        canvas.drawText(displayValue, PAGE_WIDTH - MARGIN - valueWidth, yPosition, paint);

        return yPosition + LINE_SPACING;
    }

    private int drawMiniKeyValue(Canvas canvas, Paint paint, String key, String value, int xPosition, int yPosition) {
        // Draw key and value on same line for mini receipt
        String displayValue = value != null ? value : "N/A";
        String text = key + " " + displayValue;
        canvas.drawText(text, xPosition, yPosition, paint);
        return yPosition + 15;
    }

    private void drawFooter(Canvas canvas, Paint paint, int yPosition) {
        paint.setTextSize(10);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setColor(Color.GRAY);

        canvas.drawText("This is a computer-generated receipt.", MARGIN, yPosition, paint);
        canvas.drawText("For any queries, please contact the school administration.", MARGIN, yPosition + 15, paint);

        // Draw footer line
        paint.setStrokeWidth(1);
        canvas.drawLine(MARGIN, yPosition - 10, PAGE_WIDTH - MARGIN, yPosition - 10, paint);
    }

    private File savePdfToFile(PdfDocument document, String fileName) {
        try {
            File downloadsDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "SchoolReceipts");
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }

            File pdfFile = new File(downloadsDir, fileName);
            FileOutputStream fos = new FileOutputStream(pdfFile);
            document.writeTo(fos);
            fos.close();

            Log.d(TAG, "PDF saved to: " + pdfFile.getAbsolutePath());
            return pdfFile;

        } catch (IOException e) {
            Log.e(TAG, "Error saving PDF: " + e.getMessage(), e);
            return null;
        }
    }

    private void showPrintOptions(File pdfFile, String title) {
        try {
            // Create intent chooser for print/share options
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");

            Uri pdfUri = FileProvider.getUriForFile(context,
                    context.getPackageName() + ".fileprovider", pdfFile);
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent chooser = Intent.createChooser(shareIntent, "Print or Share Receipt");
            context.startActivity(chooser);

            showToast("Receipt generated successfully!");

        } catch (Exception e) {
            Log.e(TAG, "Error showing print options: " + e.getMessage(), e);

            // Fallback: Try to open with default PDF viewer
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri pdfUri = FileProvider.getUriForFile(context,
                        context.getPackageName() + ".fileprovider", pdfFile);
                intent.setDataAndType(pdfUri, "application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);

                showToast("Receipt saved. Opening with PDF viewer...");
            } catch (Exception e2) {
                Log.e(TAG, "Error opening PDF: " + e2.getMessage(), e2);
                showToast("Receipt saved to: " + pdfFile.getAbsolutePath());
            }
        }
    }

    public void printPdfDirectly(File pdfFile, String jobName) {
        try {
            PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);

            if (printManager != null) {
                PrintDocumentAdapter adapter = new PrintDocumentAdapter() {
                    @Override
                    public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes1, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {

                    }

                    @Override
                    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {

                    }
                };
                printManager.print(jobName, adapter, new PrintAttributes.Builder().build());
            } else {
                showToast("Print service not available");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error printing PDF directly: " + e.getMessage(), e);
            showToast("Error accessing print service");
        }
    }

    private void showToast(String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing toast: " + e.getMessage(), e);
        }
    }
}
