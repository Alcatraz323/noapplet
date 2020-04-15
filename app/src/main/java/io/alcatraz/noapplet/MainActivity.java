package io.alcatraz.noapplet;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    RelativeLayout about;
    Toolbar toolbar;
    Vibrator vibrator;
    Easter easter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        easter=new Easter(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        about = findViewById(R.id.main_about_element);
        toolbar = findViewById(R.id.main_toolbar);

        setSupportActionBar(toolbar);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vibrator.hasVibrator()){
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            vibrator.cancel();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(100,VibrationEffect.DEFAULT_AMPLITUDE));
                            }else {
                                vibrator.vibrate(100);
                            }
                        }
                    });

                }
                easter.shortClick();
            }
        });
        about.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(vibrator.hasVibrator()){
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            vibrator.cancel();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                vibrator.vibrate(200);
                            }
                        }
                    });
                }
                easter.longClick();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_menu_item_1:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Alcatraz323/noapplet")));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
