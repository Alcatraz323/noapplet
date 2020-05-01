package io.alcatraz.noapplet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class XposedAppletActivity extends AppCompatActivity {
    public static final String EXTRA_XPOSED_MINI_APP_INFO = "noapplet_mini_app_info";
    public static final String EXTRA_XPOSED_MINI_APP_NAME = "noapplet_mini_app_name";
    public static final String EXTRA_XPOSED_MINI_APP_DESCRIPTION = "noapplet_mini_app_desc";
    public static final String EXTRA_XPOSED_MINI_APP_ICON_URL = "noapplet_mini_app_icon_url";
    public static final String EXTRA_XPOSED_MINI_APP_URL = "noapplet_mini_app_url";

    //Data
    Intent intent;
    String info;
    String name;
    String description;
    String icon_url;
    String app_url;

    //Widgets
    Toolbar toolbar;
    TextView txv_info;
    ImageButton info_expand;
    TextView txv_name;
    TextView txv_desc;
    TextView txv_app_url;

    RelativeLayout bili_panel;
    TextView txv_bili_gen;
    RelativeLayout zhihu_panel;
    TextView txv_zhihu_gen;
    RelativeLayout weibo_panel;
    TextView txv_weibo_gen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xposed);
        initData();
        initViews();
    }

    private void initViews() {
        toolbar = findViewById(R.id.xposed_detail_toolbar);
        txv_info = findViewById(R.id.xposed_raw_info);
        info_expand = findViewById(R.id.xposed_raw_info_expand);
        txv_name = findViewById(R.id.xposed_mini_name);
        txv_desc = findViewById(R.id.xposed_mini_description);
        txv_app_url = findViewById(R.id.xposed_mini_url);
        bili_panel = findViewById(R.id.xposed_bili_element);
        txv_bili_gen = findViewById(R.id.xposed_bili_generate);
        zhihu_panel = findViewById(R.id.xposed_zhihu_element);
        txv_zhihu_gen = findViewById(R.id.xposed_zhihu_generate);
        weibo_panel = findViewById(R.id.xposed_weibo_element);
        txv_weibo_gen = findViewById(R.id.xposed_weibo_generate);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txv_info.setText(info);
        txv_name.setText(name);
        txv_desc.setText(description);
        txv_app_url.setText(app_url);

        info_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txv_info.getMaxLines() == 2) {
                    info_expand.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    txv_info.setMaxLines(Integer.MAX_VALUE);
                } else {
                    info_expand.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    txv_info.setMaxLines(2);
                }
            }
        });

        if (name.equals("哔哩哔哩")) {
            txv_bili_gen.setText(generateBilibiliLink());
            bili_panel.setVisibility(View.VISIBLE);
            bili_panel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(txv_bili_gen.getText().toString())));
                }
            });
            bili_panel.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Utils.copyToClipboard(txv_bili_gen.getText().toString(), XposedAppletActivity.this);
                    return true;
                }
            });
        }

        if (name.equals("知乎")) {
            txv_zhihu_gen.setText(generateZhihuLink());
            zhihu_panel.setVisibility(View.VISIBLE);
            zhihu_panel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(txv_zhihu_gen.getText().toString())));
                }
            });
            zhihu_panel.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Utils.copyToClipboard(txv_zhihu_gen.getText().toString(), XposedAppletActivity.this);
                    return true;
                }
            });
        }

        if (name.equals("微博")) {
            txv_weibo_gen.setText(generateWeiboLink());
            weibo_panel.setVisibility(View.VISIBLE);
            weibo_panel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(txv_weibo_gen.getText().toString())));
                }
            });
            weibo_panel.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Utils.copyToClipboard(txv_weibo_gen.getText().toString(), XposedAppletActivity.this);
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void initData() {
        intent = getIntent();
        info = intent.getStringExtra(EXTRA_XPOSED_MINI_APP_INFO);
        name = intent.getStringExtra(EXTRA_XPOSED_MINI_APP_NAME);
        description = intent.getStringExtra(EXTRA_XPOSED_MINI_APP_DESCRIPTION);
        icon_url = intent.getStringExtra(EXTRA_XPOSED_MINI_APP_ICON_URL);
        app_url = intent.getStringExtra(EXTRA_XPOSED_MINI_APP_URL);
    }

    public String generateBilibiliLink() {
        String[] process_0 = app_url.split("=");
        String[] process_1 = process_0[1].split("&");
        if (app_url.contains("avid")) {
            return "https://www.bilibili.com/video/av" + process_1[0];
        } else if (app_url.contains("epid")) {
            return "https://www.bilibili.com/bangumi/play/ep" + process_1[0];
        } else {
            return "https://www.bilibili.com/video/" + process_1[0];
        }
    }

    public String generateWeiboLink(){
        String[] process_0 = app_url.split("=");
        String[] process_1 = process_0[1].split("&");
        return "https://m.weibo.cn/detail/" + process_1[0];
    }

    public String generateZhihuLink() {
        String[] process_0 = app_url.split("=");
        String[] process_1 = process_0[1].split("&");
        if (app_url.contains("answer")) {
            return "https://www.zhihu.com/answer/" + process_1[0];
        } else if (app_url.contains("question")) {
            return "https://www.zhihu.com/question/" + process_1[0];
        }
        return "Can't generate link";
    }
}
