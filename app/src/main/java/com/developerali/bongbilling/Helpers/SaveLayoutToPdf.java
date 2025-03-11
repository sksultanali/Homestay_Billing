package com.developerali.bongbilling.Helpers;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SaveLayoutToPdf {

    Activity activity;
    View layout;
    String name;

    public SaveLayoutToPdf(Activity activity, View layout, String name) {
        this.activity = activity;
        this.layout = layout;
        this.name = name;
    }

    public void savePdfToDownloads() {
        String pdfName = name.isEmpty() ? "output" : name;

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), pdfName + "_output.pdf");
        try {
            OutputStream outputStream = new FileOutputStream(file);
            generatePdf(outputStream, FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file));
            outputStream.close();
            Toast.makeText(activity, "PDF saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Failed to save PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void generatePdf(OutputStream outputStream, Uri fileUri) {
        // Step 1: Layout ke Bitmap e convert kora
        layout.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(layout.getWidth(), layout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        layout.draw(canvas);
        layout.setDrawingCacheEnabled(false);

        // Step 2: PdfDocument create kora
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        // Step 3: Bitmap ke PDF page e draw kora
        Canvas pdfCanvas = page.getCanvas();
        pdfCanvas.drawBitmap(bitmap, 0, 0, null);
        pdfDocument.finishPage(page);

        // Step 4: PDF file save kora using OutputStream
        try {
            pdfDocument.writeTo(outputStream);
            openPdf(fileUri);
            Toast.makeText(activity, "PDF saved successfully!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Failed to save PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("PDF_PATH", "File path23: " + e.getMessage());
        } finally {
            // Step 5: PdfDocument close kora
            pdfDocument.close();
        }
    }

    private void openPdf(Uri fileUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Verify that there's an app to handle the intent
        PackageManager packageManager = activity.getPackageManager();
        if (intent.resolveActivity(packageManager) != null) {
            activity.startActivity(intent);
        } else {
            Toast.makeText(activity, "No app found to open PDF!", Toast.LENGTH_SHORT).show();
        }
    }
}
