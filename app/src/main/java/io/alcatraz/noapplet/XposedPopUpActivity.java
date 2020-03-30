package io.alcatraz.noapplet;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class XposedPopUpActivity extends AppCompatActivity {
    Button cancel;
    Button confirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xposed_popup);
        cancel = findViewById(R.id.xp_popup_cancel);
        confirm = findViewById(R.id.xp_popup_confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                ComponentName noapplet
                        = new ComponentName("io.alcatraz.noapplet",
                        "io.alcatraz.noapplet.XposedAppletActivity");
                intent.setComponent(noapplet);
                startActivity(intent);
                finish();
            }
        });
    }
}
