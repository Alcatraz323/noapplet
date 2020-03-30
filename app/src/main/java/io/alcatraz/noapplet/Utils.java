package io.alcatraz.noapplet;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Utils {
    public static String replace(String url, String key, String value) {
        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(key)) {
            url = url.replaceAll("(" + key + "=[^&]*)", key + "=" + value);
        }
        return url;
    }

    public static String removeMiniProgramNode(String url) {
        if (!TextUtils.isEmpty(url)) {
            url = url.replaceAll("(mini_program.*?)=([^&]*)&", "");
        }
        return url;
    }

    public static Bitmap getLocalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void getHttpBitmap(final String url, final AsyncInterface<Bitmap> asyncInterface) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                URL myFileUrl = null;
                Bitmap bitmap = null;
                try {
                    myFileUrl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                    conn.setConnectTimeout(0);
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                asyncInterface.onDone(bitmap);
            }
        }).start();

    }

    public static void copyToClipboard(String content, Context c) {
        android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", content);
        assert clipboardManager != null;
        clipboardManager.setPrimaryClip(myClip);
        Toast.makeText(c, R.string.toast_copied, Toast.LENGTH_SHORT).show();
    }


    public static int getStatusBarHeight(Context var0) {
        int var1 = var0.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return var0.getResources().getDimensionPixelSize(var1);
    }
}
