package com.developer_abdul_halim.spoken;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PDFActivity extends AppCompatActivity {

    private static final String PDF_LINK_KEY = "PDF_LINK_KEY";

    private PDFView pdfView;
    private TextView pageNumber;
    private int currentPage = 0;
    private String PDFURL;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullscreen();
        setContentView(R.layout.activity_pdfactivity);

        initProgressDialog();
        initViews();

        if (!isConnectedToInternet()) {
            Toast.makeText(this, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPdfFromUrl();
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(PDFActivity.this);
        progressDialog.setMessage(getString(R.string.progress_loading_pdf));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    private void setFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initViews() {
        pdfView = findViewById(R.id.pdfView);
        pageNumber = findViewById(R.id.pageNumber);
        pageNumber.setOnClickListener(this::showPageInputDialog);

        PDFURL = getIntent().getStringExtra(PDF_LINK_KEY);
        if (PDFURL == null || PDFURL.trim().isEmpty()) {
            Toast.makeText(this, R.string.error_invalid_pdf_link, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void loadPdfFromUrl() {
        progressDialog.show();
        new RetrievePDFfromUrl().execute(PDFURL);
    }

    private class RetrievePDFfromUrl extends AsyncTask<String, Void, InputStream> {
        private Exception exception = null;

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                exception = e;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {


            if (exception != null) {
                Log.e("RetrievePDFError", exception.getMessage(), exception);
                Toast.makeText(PDFActivity.this, R.string.error_loading_pdf, Toast.LENGTH_SHORT).show();
                return;
            }


            if (inputStream == null) {
                Toast.makeText(PDFActivity.this, R.string.error_loading_pdf, Toast.LENGTH_SHORT).show();
                return;
            }

            displayPdf(inputStream);
        }
    }
    private void displayPdf(InputStream inputStream) {
        pdfView.fromStream(inputStream)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(currentPage)
                .enableAnnotationRendering(false)
                .password(null)
                .enableAntialiasing(true)
                .spacing(0)
                .scrollHandle(new DefaultScrollHandle(this))
                .pageFitPolicy(FitPolicy.WIDTH)
                .onLoad(nbPages -> {
                    progressDialog.dismiss();
                })
                .onError(throwable -> { // Handling error during rendering
                    progressDialog.dismiss();
                    Toast.makeText(PDFActivity.this, R.string.error_loading_pdf, Toast.LENGTH_SHORT).show();
                })
                .onPageChange((page, pageCount) -> {
                    currentPage = page;
                    pageNumber.setText(String.format("%s / %s", page + 1, pageCount));
                })
                .load();
    }

    private void showPageInputDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_goto_page_title);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
            String pageNumberStr = input.getText().toString();
            if (!pageNumberStr.isEmpty()) {
                int desiredPage = Integer.parseInt(pageNumberStr) - 1;
                if (desiredPage >= 0 && desiredPage < pdfView.getPageCount()) {
                    pdfView.jumpTo(desiredPage, true);
                } else {
                    Toast.makeText(PDFActivity.this, R.string.error_invalid_page_number, Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
