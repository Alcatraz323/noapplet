package io.alcatraz.noapplet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Set;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ActiveShareActivity extends AppCompatActivity {
    //Data
    Uri uri;
    String scheme_url;
    Intent original_intent;

    String title;
    String source;
    String url;
    String description;
    String image_path;
    boolean isImageLocal = false;
    boolean hasImage = false;
    boolean isApplet = true;

    //Widgets
    ImageView imageView;
    AppBarLayout appBarLayout;
    Toolbar toolbar;
    TextView detail_title;
    TextView detail_description;
    TextView detail_target_url;
    TextView detail_isapplet;
    ImageView applet_indicator;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_share);
        loadData();
        initViews();
    }

    private void initViews() {
        imageView = findViewById(R.id.share_detail_image);
        appBarLayout = findViewById(R.id.share_detail_app_bar);
        toolbar = findViewById(R.id.share_detail_toolbar);
        detail_title = findViewById(R.id.detail_title);
        detail_description = findViewById(R.id.detail_description);
        detail_target_url = findViewById(R.id.detail_target_url);
        detail_isapplet = findViewById(R.id.detail_is_applet);
        applet_indicator = findViewById(R.id.detail_is_applet_indicator);
        fab = findViewById(R.id.detail_share);

        toolbar.setTitle(source);
        detail_title.setText(title);
        detail_description.setText(description);
        detail_target_url.setText(url);
        detail_isapplet.setText(isApplet ? R.string.detail_is_applet_yes : R.string.detail_is_applet_no);
        applet_indicator.setImageResource(isApplet ? R.drawable.ic_cloud_queue_black_24dp : R.drawable.ic_cloud_off_black_24dp);
        setSupportActionBar(toolbar);
        adjustToolbar();

        setImageWithPermission();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String process_scheme = Utils.removeMiniProgramNode(scheme_url);
                String new_scheme = Utils.replace(process_scheme,"req_type","MQ==");
                Toast.makeText(ActiveShareActivity.this,R.string.toast_step_2,Toast.LENGTH_SHORT).show();
                Uri new_uri = Uri.parse(new_scheme);
                original_intent.setData(new_uri);
                original_intent.setComponent(null);

                startActivity(original_intent);
            }
        });
    }

    private void adjustToolbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int paddingTop = toolbar.getPaddingTop();
            int paddingBottom = toolbar.getPaddingBottom();
            int paddingLeft = toolbar.getPaddingLeft();
            int paddingRight = toolbar.getPaddingRight();

            int statusBarHeight = Utils.getStatusBarHeight(this);

            ViewGroup.LayoutParams params = toolbar.getLayoutParams();
            int height = params.height;

            paddingTop += statusBarHeight;
            height += statusBarHeight;

            params.height = height;
            toolbar.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
    }

    private void loadData() {
        original_intent = getIntent();
        uri = original_intent.getData();
        Bundle ex = original_intent.getExtras();
        if(ex!=null) {
            Set<String> strings = ex.keySet();
            Log.e("h","g");
        }

        if (uri != null) {
            scheme_url = uri.toString();
            title = decodeKeyValue("title");
            source = decodeKeyValue("app_name");
            url = decodeKeyValue("url");
            description = decodeKeyValue("description");
            if (scheme_url.contains("image_url=")) {
                image_path = decodeKeyValue("image_url");
                isImageLocal = false;
                hasImage = true;
            } else {
                if (scheme_url.contains("file_data=")) {
                    image_path = decodeKeyValue("file_data");
                    isImageLocal = true;
                    hasImage = true;
                }
            }

            isApplet = scheme_url.contains("mini_program");
        }
    }

    @SuppressLint("InlinedApi")
    private void setImageWithPermission() {
        if (hasImage) {
            if (isImageLocal) {
                PermissionsUtils.getInstance().checkPermissions(this, new PermissionsUtils.IPermissionsResult() {
                    @Override
                    public void passPermissions() {
                        imageView.setImageBitmap(Utils.getLocalBitmap(image_path));
                    }

                    @Override
                    public void forbidPermissions() {
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET);
            } else {
                Utils.getHttpBitmap(image_path, new AsyncInterface<Bitmap>() {
                    @Override
                    public void onDone(final Bitmap result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(result);
                            }
                        });

                    }
                });
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @SuppressWarnings("ConstantConditions")
    public String decodeKeyValue(String key) {
        try {
            String param = uri.getQueryParameter(key).replaceAll(" ", "+");
            return new String(Base64.decode(param, Base64.DEFAULT));
        } catch (Exception e) {
            return getString(R.string.none_or_decode_failure);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
