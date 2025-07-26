package com.school.schoolmanagement.HelperClasses;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DataExportHelper {

    private Context context;
    private ArrayList<ArrayList<String>> tableData;
    private String fileName;
    private String baseFileName;

    public DataExportHelper(Context context) {
        this.context = context;
        this.baseFileName = "data"; // Default filename
    }

    // Method to export data with dynamic filename
    public void exportData(ArrayList<ArrayList<String>> data, String actionFlag, String fileName) {
        this.tableData = data;
        this.fileName = sanitizeFileName(fileName);
        this.baseFileName = this.fileName;

        switch (actionFlag.toLowerCase()) {
            case "copy":
                copyToClipboard();
                break;
            case "csv":
                exportToCSV();
                break;
            case "excel":
                exportToExcel();
                break;
            case "pdf":
                generatePDF();
                break;
            case "print":
                printData();
                break;
            default:
                Toast.makeText(context, "Unknown action flag.", Toast.LENGTH_SHORT).show();
        }
    }

    // Overloaded method for backward compatibility
    public void exportData(ArrayList<ArrayList<String>> data, String actionFlag) {
        exportData(data, actionFlag, "data");
    }

    // Method to sanitize filename (remove invalid characters)
    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "data";
        }

        // Remove invalid characters and replace spaces with underscores
        return fileName.trim()
                .replaceAll("[\\\\/:*?\"<>|]", "")
                .replaceAll("\\s+", "_")
                .toLowerCase();
    }

    // Method to generate filename with timestamp
    private String generateFileNameWithTimestamp(String extension) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        return baseFileName + "_" + timestamp + "." + extension;
    }

    // Method to generate simple filename
    private String generateFileName(String extension) {
        return baseFileName + "." + extension;
    }

    public void copyToClipboard() {
        StringBuilder builder = new StringBuilder();
        for (ArrayList<String> row : tableData) {
            builder.append(TextUtils.join("\t", row)).append("\n");
        }

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Table Data", builder.toString());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context, "Data copied to clipboard.", Toast.LENGTH_SHORT).show();
    }

    private void exportToCSV() {
        String fileName = generateFileName("csv");
        File file = new File(context.getExternalFilesDir(null), fileName);

        try (FileWriter writer = new FileWriter(file)) {
            for (ArrayList<String> row : tableData) {
                writer.append(TextUtils.join(",", row)).append("\n");
            }
            Log.d("DataExportHelper", "CSV saved at: " + file.getAbsolutePath());

            showFileOpenDialog(file, "CSV file saved successfully!");

        } catch (IOException e) {
            Toast.makeText(context, "CSV export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void exportToExcel() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(baseFileName.replace("_", " "));

        for (int i = 0; i < tableData.size(); i++) {
            Row row = sheet.createRow(i);
            ArrayList<String> rowData = tableData.get(i);
            for (int j = 0; j < rowData.size(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(rowData.get(j));
            }
        }

        String fileName = generateFileName("xlsx");
        File file = new File(context.getExternalFilesDir(null), fileName);

        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
            workbook.close();

            showFileOpenDialog(file, "Excel file saved successfully!");

        } catch (IOException e) {
            Toast.makeText(context, "Excel export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void generatePDF() {
        PdfDocument document = new PdfDocument();
        Paint paint = new Paint();
        paint.setTextSize(10);
        int pageWidth = 595, pageHeight = 842; // A4 size
        int y = 50;
        int margin = 30;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        for (ArrayList<String> row : tableData) {
            canvas.drawText(TextUtils.join("   ", row), margin, y, paint);
            y += 25;
            if (y > pageHeight - 50) {
                document.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, document.getPages().size() + 1).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50;
            }
        }

        document.finishPage(page);

        String fileName = generateFileName("pdf");
        File file = new File(context.getExternalFilesDir(null), fileName);

        try (FileOutputStream out = new FileOutputStream(file)) {
            document.writeTo(out);
            document.close();

            showFileOpenDialog(file, "PDF saved successfully!");

        } catch (IOException e) {
            Toast.makeText(context, "PDF generation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void printData() {
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        if (printManager == null) {
            Toast.makeText(context, "Print service not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        PrintDocumentAdapter printAdapter = new PrintDocumentAdapter() {
            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                                 CancellationSignal cancellationSignal,
                                 LayoutResultCallback callback, Bundle extras) {
                PrintDocumentInfo info = new PrintDocumentInfo.Builder(baseFileName + "_print")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .build();
                callback.onLayoutFinished(info, true);
            }

            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                                CancellationSignal cancellationSignal, WriteResultCallback callback) {
                try (FileOutputStream out = new FileOutputStream(destination.getFileDescriptor())) {
                    for (ArrayList<String> row : tableData) {
                        out.write((TextUtils.join("\t", row) + "\n").getBytes());
                    }
                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                } catch (IOException e) {
                    callback.onWriteFailed(e.getMessage());
                }
            }
        };

        printManager.print(baseFileName + " Print", printAdapter, null);
        Toast.makeText(context, "Sending data to printer...", Toast.LENGTH_SHORT).show();
    }

    private void showFileOpenDialog(File file, String message) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Export Successful")
                .setMessage(message + "\n\nWould you like to open the file?")
                .setPositiveButton("Open", (dialog, which) -> {
                    openFile(file);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void openFile(File file) {
        try {
            Uri fileUri;

            // Use FileProvider for Android 7.0 and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fileUri = FileProvider.getUriForFile(context,
                        context.getPackageName() + ".fileprovider", file);
            } else {
                fileUri = Uri.fromFile(file);
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);

            // Set the appropriate MIME type based on file extension
            String mimeType = getMimeType(file.getName());
            intent.setDataAndType(fileUri, mimeType);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Grant temporary read permission for the content URI
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            // Check if there's an app available to handle this intent
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                // If no app can handle it, show a chooser
                Intent chooser = Intent.createChooser(intent, "Open with");
                chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(chooser);
            }

        } catch (Exception e) {
            Log.e("DataExportHelper", "Error opening file: " + e.getMessage());
            Toast.makeText(context, "Unable to open file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String getMimeType(String fileName) {
        if (fileName.endsWith(".csv")) {
            return "text/csv";
        } else if (fileName.endsWith(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else {
            return "*/*";
        }
    }
}