package com.kalaungthwar.writer;

import android.app.Activity;
import android.app.PrintManager;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.Base64;

public class MainActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(android.graphics.Color.rgb(248, 247, 244));
        getWindow().setNavigationBarColor(android.graphics.Color.rgb(248, 247, 244));
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        webView = new WebView(this);
        setContentView(webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                installNativeHelpers();
            }
        });
        webView.loadUrl("file:///android_asset/index.html");
    }

    private void installNativeHelpers() {
        String script =
            "(function(){" +
            "if(window.__nativeHelpersInstalled)return;window.__nativeHelpersInstalled=true;" +
            "const originalCreate=URL.createObjectURL.bind(URL);const blobs={};" +
            "URL.createObjectURL=function(blob){const u=originalCreate(blob);blobs[u]=blob;return u;};" +
            "const originalClick=HTMLAnchorElement.prototype.click;" +
            "HTMLAnchorElement.prototype.click=function(){" +
            "const u=this.href;const name=this.download||'ကလောင်သွား-export.docx';" +
            "if(u&&u.startsWith('blob:')&&blobs[u]&&window.AndroidBridge){" +
            "const reader=new FileReader();reader.onloadend=function(){AndroidBridge.saveDataUrl(name,reader.result);};reader.readAsDataURL(blobs[u]);" +
            "}else{originalClick.call(this);}};" +
            "window.print=function(){if(window.AndroidBridge)AndroidBridge.printPage();};" +
            "})();";
        webView.evaluateJavascript(script, null);
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void saveDataUrl(String fileName, String dataUrl) {
            try {
                int comma = dataUrl.indexOf(',');
                if (comma < 0) throw new IllegalArgumentException("Invalid data URL");
                String header = dataUrl.substring(0, comma);
                String mime = "application/octet-stream";
                int colon = header.indexOf(':');
                int semi = header.indexOf(';');
                if (colon >= 0 && semi > colon) mime = header.substring(colon + 1, semi);
                byte[] bytes = Base64.getDecoder().decode(dataUrl.substring(comma + 1));

                String safeName = fileName == null ? "export.docx" : fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, safeName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, mime);
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/KalaungThwar");
                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri == null) throw new Exception("Cannot create download");
                try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                    if (out == null) throw new Exception("Cannot open download");
                    out.write(bytes);
                }
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        "ဖိုင်ကို Downloads/KalaungThwar ထဲ သိမ်းပြီးပါပြီ", Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        "ဖိုင်သိမ်းမရပါ: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }

        @JavascriptInterface
        public void printPage() {
            runOnUiThread(() -> {
                try {
                    PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                    if (printManager != null) {
                        printManager.print("KalaungThwar", webView.createPrintDocumentAdapter("KalaungThwar"), null);
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Print မလုပ်နိုင်ပါ", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
