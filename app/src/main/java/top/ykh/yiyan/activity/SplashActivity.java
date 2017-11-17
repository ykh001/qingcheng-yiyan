package top.ykh.yiyan.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import top.ykh.yiyan.R;

public class SplashActivity extends AppCompatActivity {

    private boolean isAlive = true;
    private ImageView iv_splash_bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        iv_splash_bg = (ImageView)findViewById(R.id.iv_splash_bg);
        int random = 1+(int)(Math.random()*5);

        if(random==1){
            iv_splash_bg.setImageDrawable(getResources().getDrawable(R.drawable.s1));
        }else if(random==2){
            iv_splash_bg.setImageDrawable(getResources().getDrawable(R.drawable.s2));
        }else if(random==3){
            iv_splash_bg.setImageDrawable(getResources().getDrawable(R.drawable.s3));
        }else if(random==4){
            iv_splash_bg.setImageDrawable(getResources().getDrawable(R.drawable.s4));
        }else if(random==5){
            iv_splash_bg.setImageDrawable(getResources().getDrawable(R.drawable.s5));
        }else if(random==6){
            iv_splash_bg.setImageDrawable(getResources().getDrawable(R.drawable.s6));
        }


        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isAlive) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isAlive = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isAlive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isAlive = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAlive = false;
    }
}
