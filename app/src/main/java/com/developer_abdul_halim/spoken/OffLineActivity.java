package com.developer_abdul_halim.spoken;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class OffLineActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1000;
    private static final String PROVIDER_AUTHORITY_SUFFIX = ".provider";

    private RecyclerView recyclerView;
    private DownloadAdapter downloadAdapter;
    private final List<File> downloadedPDFs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_off_line);
        setupRecyclerView();

        if (hasExternalStoragePermission()) {
            loadDownloadedPDFs();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        downloadAdapter = new DownloadAdapter(downloadedPDFs);
        recyclerView.setAdapter(downloadAdapter);
    }

    private boolean hasExternalStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadDownloadedPDFs();
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDownloadedPDFs() {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Spoken English");
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".pdf")) {
                        downloadedPDFs.add(file);
                    }
                }
            }
            downloadAdapter.notifyDataSetChanged();
        }
    }

    public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder> {
        private final List<File> downloadedPDFs;

        public DownloadAdapter(List<File> downloadedPDFs) {
            this.downloadedPDFs = downloadedPDFs;
        }

        @NonNull
        @Override
        public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download, parent, false);
            return new DownloadViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {
            holder.bind(downloadedPDFs.get(position));
        }

        @Override
        public int getItemCount() {
            return downloadedPDFs.size();
        }

        class DownloadViewHolder extends RecyclerView.ViewHolder {
            private final TextView fileNameTextView;

            public DownloadViewHolder(@NonNull View itemView) {
                super(itemView);
                fileNameTextView = itemView.findViewById(R.id.fileNameTextView);
            }

            public void bind(File pdfFile) {
                fileNameTextView.setText(pdfFile.getName());
                itemView.setOnClickListener(v -> {
                    if (pdfFile.exists()) {
                        openPdf(pdfFile);
                    } else {
                        Log.e("OffLineActivity", "File does not exist: " + pdfFile.getPath());
                    }
                });
            }

            private void openPdf(File pdfFile) {
                Uri uri = FileProvider.getUriForFile(OffLineActivity.this, getPackageName() + PROVIDER_AUTHORITY_SUFFIX, pdfFile);
                Intent intent = new Intent(OffLineActivity.this, PdfViewActivity2.class);
                intent.putExtra("PDF_URI", uri.toString());
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        }
    }

}