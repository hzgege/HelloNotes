package com.amyzhongjie.hellonotes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

public class SplashActivity extends Activity {

    private LinearLayout splash;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splash = (LinearLayout) findViewById(R.id.ll_splash);

        /**
         * 利用动画实现
         */
        start();
    }

    /**
     * 动画
     */
    private void start() {
        // 动画集合
        AnimationSet set = new AnimationSet(false);

        // 缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        // 设置动画时间
        scaleAnimation.setDuration(2000);
        // 保持动画状态
        scaleAnimation.setFillAfter(true);

        // 渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        // 设置动画时间
        alphaAnimation.setDuration(2000);
        // 保持动画状态
        alphaAnimation.setFillAfter(true);

        // 添加动画
        set.addAnimation(scaleAnimation);
        set.addAnimation(alphaAnimation);
       /*
        * 设置动画的监听事件，当动画运行完成后，启动新的activity
   	    */
        set.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 跳转到主界面
                startMainActivity();
            }
        });

        splash.startAnimation(set);
    }


    /**
     * 跳转到主界面
     */
    private void startMainActivity() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
