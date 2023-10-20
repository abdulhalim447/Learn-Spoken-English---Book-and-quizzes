package com.developer_abdul_halim.spoken;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

public class PdfViewActivity2 extends AppCompatActivity {
    private static final int DEFAULT_PAGE = 0;
    private static final String CURRENT_PAGE_KEY = "currentPage";

    private PDFView pdfView;
    private TextView pageNumber;
    private ProgressDialog progressDialog;
    private int currentPage = DEFAULT_PAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view2);

        initializeViews();
        setupProgressDialog();

        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt(CURRENT_PAGE_KEY, DEFAULT_PAGE);
        }

        String pdfUriString = getIntent().getStringExtra("PDF_URI");
        if (pdfUriString != null) {
            Uri pdfUri = Uri.parse(pdfUriString);
            openPdf(pdfUri);
        } else {
            Toast.makeText(this, R.string.no_pdf_found, Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        pdfView = findViewById(R.id.pdfView);
        pageNumber = findViewById(R.id.pageNumber2);
        pageNumber.setOnClickListener(v -> showPageInputDialog());
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_pdf));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void openPdf(Uri uri) {
        pdfView.fromUri(uri)
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
                .onLoad(nbPages -> {progressDialog.dismiss(); })
                .onPageChange((page, pageCount) -> {
                    currentPage = page;
                    pageNumber.setText(String.format("%s / %s", page + 1, pageCount));
                })
                .load();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_PAGE_KEY, currentPage);
    }

    private void showPageInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.goto_page);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            String pageNumberStr = input.getText().toString();
            if (!pageNumberStr.isEmpty()) {
                int desiredPage = Integer.parseInt(pageNumberStr) - 1;
                if (desiredPage >= 0 && desiredPage < pdfView.getPageCount()) {
                    pdfView.jumpTo(desiredPage, true);
                } else {
                    Toast.makeText(this, R.string.invalid_page_number, Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
