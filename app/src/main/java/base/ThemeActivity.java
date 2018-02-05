package base;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.amyzhongjie.hellonotes.R;
import com.mikepenz.iconics.context.IconicsContextWrapper;

import utils.ColorPaletteUtils;
import utils.PreferenceUtils;
import utils.ThemeUtils;

/**
 * Created by hzgg on 2017/12/16.
 */

public class ThemeActivity extends AppCompatActivity {
    private ThemeUtils themeUtils;
    private PreferenceUtils SP;
    private boolean coloredNavBar;
    private boolean obscuredStatusBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SP = PreferenceUtils.getInstance(getApplicationContext());
        themeUtils = new ThemeUtils(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTheme();
    }

    public void updateTheme() {
        themeUtils.updateTheme();
        coloredNavBar = SP.getBoolean(getString(R.string.preference_colored_nav_bar), false);
        obscuredStatusBar = SP.getBoolean(getString(R.string.preference_translucent_status_bar), true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // NOTE: icons stuff
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setNavBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isNavigationBarColored()) getWindow().setNavigationBarColor(getPrimaryColor());
            else
                getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000));
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isTranslucentStatusBar())
                getWindow().setStatusBarColor(ColorPaletteUtils.getObscuredColor(getPrimaryColor()));
            else
                getWindow().setStatusBarColor(getPrimaryColor());
        }
    }

    public boolean isNavigationBarColored() {
        return coloredNavBar;
    }

    public boolean isTranslucentStatusBar() {
        return obscuredStatusBar;
    }


    public int getPrimaryColor() {
        return themeUtils.getPrimaryColor();
    }

    public int getCardBackgroundColor() {
        return themeUtils.getCardBackgroundColor();
    }

    public int getDialogStyle() {
        return themeUtils.getDialogStyle();
    }

    protected int getPopupToolbarStyle() {
        return themeUtils.getPopupToolbarStyle();
    }

}
