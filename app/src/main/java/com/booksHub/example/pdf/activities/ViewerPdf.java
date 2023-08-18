package com.booksHub.example.pdf.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.booksHub.example.pdf.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewerPdf extends AppCompatActivity {
    PDFView pdfView;
    Bundle extras;
    String pdf = "";
    WebView webView;
    int length;
    File file;
    ProgressBar pb;
    SharedPreferences sharedpreferences;
    String themkey;
    boolean nightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer_pdf);
        extras = getIntent().getExtras();
        if (extras != null) {
            pdf = extras.getString("link");

        }

        sharedpreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        themkey = sharedpreferences.getString("key", "");
        switch (themkey) {

            case "light":
                nightMode = false;

                break;
            case "dark":
                nightMode = true;

                break;
        }

        pdfView = findViewById(R.id.pdfView);
        pb = findViewById(R.id.pb);

        pdfView.setBackgroundColor(Color.LTGRAY);

        if (pdf == null) {
            Toast.makeText(ViewerPdf.this, "Link Not Available", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            load(pdf);
        }
    }

    private void load(String link) {


        final int[] status = {0};
        Handler handler = new Handler();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);

        final ProgressBar text = (ProgressBar) dialog.findViewById(R.id.progress_horizontal);
        final TextView text2 = dialog.findViewById(R.id.value123);

        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);


        new Thread(() ->
        {

            String fileNamea = link.substring(link.lastIndexOf('-') + 1, link.length());

            //   String fileNameWithoutExtn = fileNamea.substring(0, fileNamea.lastIndexOf('.'));
            // do background stuff here
            String filePath = getExternalCacheDir().getAbsolutePath();
            String fileName = "file" + fileNamea + ".pdf";
            file = new File(filePath, fileName);
            if (file.exists()) {
                try {
                    dialog.dismiss();
                    pb.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> {

                    pdfView.fromFile(file)
                            .enableSwipe(true)
                            .swipeHorizontal(false)
                            .spacing(3)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .pageFitPolicy(FitPolicy.BOTH)
                            .enableAntialiasing(true)
                            .nightMode(nightMode)
                            .onLoad(new OnLoadCompleteListener() {
                                @Override
                                public void loadComplete(int nbPages) {
                                    pb.setVisibility(View.GONE);
                                    //  Toast.makeText(ViewerPdf.this, "complete", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .load();

                });
            } else {
                InputStream inputStream = null;
                try {

                    URL url = new URL(link);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    if (urlConnection.getResponseCode() == 200) {
                        inputStream = new BufferedInputStream(urlConnection.getInputStream());

                        length = urlConnection.getContentLength();


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                while (status[0] < 100) {

                    status[0] += 1;

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.post((Runnable) () -> {

                        text.setProgress(status[0]);
                        text2.setText(String.valueOf(status[0]));

                        if (status[0] == 100) {
                            dialog.dismiss();
                            pb.setVisibility(View.VISIBLE);
                        }
                    });
                }

                InputStream finalInputStream = inputStream;
                long fileSizeInKB = length / 1024;

                String filePaths = getExternalCacheDir().getAbsolutePath();
                String fileNames = "file" + fileNamea + ".pdf";
                file = new File(filePaths, fileNames);
                copyInputStreamToFile(finalInputStream, file);

                runOnUiThread(() -> {
                    if (file.exists()) {
                        Toast.makeText(ViewerPdf.this, fileSizeInKB + "KBs", Toast.LENGTH_SHORT).show();

                    }


                    // OnPostExecute stuff here
                    pdfView.fromFile(file)
                            .enableSwipe(true)
                            .swipeHorizontal(false)
                            .spacing(3)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .pageFitPolicy(FitPolicy.BOTH)
                            .enableAntialiasing(true)
                            .nightMode(nightMode)
                            .onLoad(new OnLoadCompleteListener() {
                                @Override
                                public void loadComplete(int nbPages) {
                                    pb.setVisibility(View.GONE);
                                    Toast.makeText(ViewerPdf.this, "complete", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .load();
//                    ParcelFileDescriptor   mFileDescriptor = null;
//                    try {
//                        mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    // This is the PdfRenderer we use to render the PDF.
//                    if (mFileDescriptor != null) {
//                        try {
//                            PdfRenderer   mPdfRenderer = new PdfRenderer(mFileDescriptor);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }

                });
            }

        }).start();
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    private void copyInputStreamToFile(InputStream in, File file) {
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Ensure that the InputStreams are closed even if there's an exception.
            try {
                if (out != null) {
                    out.close();
                }

                // If you want to close the "in" InputStream yourself then remove this
                // from here but ensure that you close it yourself eventually.
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //        webView = (WebView) findViewById(R.id.pdfViews);
//        webView.getSettings().setJavaScriptEnabled(true);
//
//        //if your filename and fileurl content holder is not named 'filename' and 'fileurl' then change the below name to exactly what it is named
//        String filename = getIntent().getStringExtra("filename");
//        String fileurl = getIntent().getStringExtra("fileurl");
//
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("filename");
//        progressDialog.setMessage("Opening...!!!");
//
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                progressDialog.show();
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                progressDialog.dismiss();
//            }
//        });
//
//        String url = "";
//        ;
//        try {
//
//            url = URLEncoder.encode(pdf, "UTF-8");
//        } catch (Exception ex) {
//
//        }
//
//        webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);
}