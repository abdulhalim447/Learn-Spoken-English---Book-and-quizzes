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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Book_Fragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 1000;
    private static final String PROVIDER_AUTHORITY_SUFFIX = ".provider";

    private RecyclerView recyclerView;
    private DownloadAdapter downloadAdapter;
    private final List<File> downloadedPDFs = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View bookView = inflater.inflate(R.layout.fragment_book_, container, false);

        setupRecyclerView(bookView);

        if (hasExternalStoragePermission()) {
            loadDownloadedPDFs();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        return bookView;
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        downloadAdapter = new DownloadAdapter(downloadedPDFs);
        recyclerView.setAdapter(downloadAdapter);
    }

    private boolean hasExternalStoragePermission() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadDownloadedPDFs();
        } else {
            Toast.makeText(getContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
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
                        Log.e("BookFragment", "File does not exist: " + pdfFile.getPath());
                    }
                });
            }

            private void openPdf(File pdfFile) {
                Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + PROVIDER_AUTHORITY_SUFFIX, pdfFile);
                Intent intent = new Intent(requireContext(), PdfViewActivity2.class);
                intent.putExtra("PDF_URI", uri.toString());
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                requireContext().startActivity(intent);
            }
        }
    }
}
