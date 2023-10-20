package com.developer_abdul_halim.spoken;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class BookConfirmation extends AppCompatActivity {

    String PDFURL;
    String PDFURL_TITLE;
    TextView bookTitleOnline, bookTitleOffline;
    ImageButton goToOffline;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_confirmation);

        bookTitleOnline = findViewById(R.id.bookTitleOnline);
        bookTitleOffline = findViewById(R.id.bookTitleOffline);
        goToOffline = findViewById(R.id.goToOffline);

        goToOffline.setOnClickListener(v -> {
            startActivity(new Intent(BookConfirmation.this,OffLineActivity.class));
        });

        Intent intent = getIntent();
        if (intent != null) {
            PDFURL = intent.getStringExtra("pdflink");
            PDFURL_TITLE = intent.getStringExtra("Title");

            if (PDFURL_TITLE != null) {
                bookTitleOnline.setText("'"+PDFURL_TITLE+"'"+ " বইটি অনলাইনে পড়ুন");
            }

            bookTitleOnline.setOnClickListener(v -> {
                if (PDFURL != null) {
                    Intent intent1 = new Intent(BookConfirmation.this, PDFActivity.class);
                    intent1.putExtra("PDF_LINK_KEY", PDFURL);
                    startActivity(intent1);
                } else {
                    Toast.makeText(BookConfirmation.this, "Invalid PDF link!", Toast.LENGTH_SHORT).show();
                }
            });

            bookTitleOffline.setOnClickListener(v -> {

                DownloadPDF();

            });


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadPDF(PDFURL);
        } else {
            Toast.makeText(BookConfirmation.this, "Permission denied...!", Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadPDF(String url) {
        try {
            URL urlObject = new URL(url);
            String originalFilename = urlObject.getFile();
            if (originalFilename != null && originalFilename.contains("/")) {
                originalFilename = originalFilename.substring(originalFilename.lastIndexOf('/') + 1);
                originalFilename = "/Spoken English/" + "/" + originalFilename;

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                request.setTitle("Download");
                request.setDescription("Downloading PDF...");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, originalFilename);

                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                if (manager != null) {
                    manager.enqueue(request);
                } else {
                    Toast.makeText(BookConfirmation.this, "Unable to download file.", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(BookConfirmation.this, "Invalid PDF link!", Toast.LENGTH_SHORT).show();
        }
    }

    private void DownloadPDF() {
        new AlertDialog.Builder(this)
                .setTitle("ডাউনলোড করতে চান?")
                .setMessage("আপনি কি অফলাইনে পড়ার জন্য বইটি ডাউনলোড করতে চান?")
                .setPositiveButton("হ্যাঁ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (PDFURL != null) {
                            Toast.makeText(BookConfirmation.this, "The Book is Downloading..", Toast.LENGTH_LONG).show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                    ActivityCompat.requestPermissions(BookConfirmation.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
                                } else {
                                    downloadPDF(PDFURL);
                                }
                            }
                        } else {
                            Toast.makeText(BookConfirmation.this, "Invalid PDF link!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("না", null)
                .setIcon(R.drawable.icon_download)
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // This will navigate back to the parent activity as defined in the manifest
    }
}
