package app.example.com.quickler;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH= 3000;
    ImageView imageView;
    AnimationDrawable anim;

    LinearLayout layout;
    Animation myanim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imageView = findViewById(R.id.applogo);
        imageView.setBackgroundResource(R.drawable.logo_anim);
        layout = findViewById(R.id.linearlayout);
        myanim = AnimationUtils.loadAnimation(this,R.anim.fadein);
        anim = (AnimationDrawable) imageView.getBackground();

        layout.startAnimation(myanim);
        anim.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent= new Intent(SplashActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SplashActivity.this.startActivity(intent);
                finish();

            }
        },SPLASH_DISPLAY_LENGTH);
    }
}
