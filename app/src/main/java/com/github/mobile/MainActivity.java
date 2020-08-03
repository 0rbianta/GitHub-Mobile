package com.github.mobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private String USERAGENT="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36";
    private WebView wv;
    private ImageView imgGear;
    private CardView cvLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                AlertDialog.Builder alrt = new AlertDialog.Builder(MainActivity.this);
                alrt.setCancelable(false);
                alrt.setTitle("Warning!");
                alrt.setMessage("If you want to download files, please give file access. Otherwise, the file cannot be downloaded.");
                alrt.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions, 1);
                    }
                });
                alrt.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"You can give file access in settings", Toast.LENGTH_LONG).show();
                    }
                });
                alrt.show();
            }
        }

        wv=findViewById(R.id.wv);
        imgGear=findViewById(R.id.imgGear);
        cvLoad=findViewById(R.id.cvLoad);
        loadWebView();

        try{
        wv.setDownloadListener(new DownloadListener()
        {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                request.setMimeType(mimeType);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading File...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                                url, contentDisposition, mimeType));
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
            }});
        }catch (Exception e){Toast.makeText(getApplicationContext(),"You can give file access in settings", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),"Unable to start download", Toast.LENGTH_LONG).show();}
    }

    private void AnimateGear(){
         Animation aniRotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
         imgGear.startAnimation(aniRotate);
    }

    public void exit(){
        AlertDialog.Builder alrt = new AlertDialog.Builder(MainActivity.this);
        alrt.setMessage("Do you want to exit the application?");
        alrt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                System.exit(0);
            }
        });
        alrt.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alrt.show();
    }

    public void btnExitClick(View v){exit();}

    public void btnBackClick(View v){
            wv.goBack();
    }

    public void btnNextClick(View v){
            wv.goForward();
    }

    public void btnHomeClick(View v){
        loadWebView();
    }

    public void btnRefreshClick(View v){
        wv.reload();
    }

    private void loadWebView(){
        wv.getSettings().setUserAgentString(USERAGENT);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient());
        wv.loadUrl("https://github.com/login");
    }


    public class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            cvLoad.setVisibility(View.VISIBLE);
            AnimateGear();
            wv.setVisibility(View.GONE);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            cvLoad.setVisibility(View.GONE);
            AnimateGear();
            wv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed(){
            wv.goBack();
    }
}