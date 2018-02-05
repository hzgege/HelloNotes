package utils;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

import com.amyzhongjie.hellonotes.R;

public class ThemeUtils {
    public static final int DARK_THEME = 2;
    public static final int LIGHT_THEME = 1;
    public static final int AMOLED_THEME = 3;

    private PreferenceUtils SP;
    private Context context;

    private int baseTheme;
    private int primaryColor;

    public ThemeUtils(Context context) {
        this.SP = PreferenceUtils.getInstance(context);
        this.context = context;
        updateTheme();
    }

    public void updateTheme(){
        this.primaryColor = SP.getInt(context.getString(R.string.preference_primary_color),
                getColor(R.color.md_indigo_500));
        baseTheme = SP.getInt(context.getString(R.string.preference_base_theme), LIGHT_THEME);
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public int getBaseTheme(){ return baseTheme; }


    public int getColor(@ColorRes int color) {
        return ContextCompat.getColor(context, color);
    }


    public int getCardBackgroundColor(){
        int color;
        color = getColor(R.color.md_light_cards);
        return color;
    }

    public int getDialogStyle(){
        int style;
        style = R.style.AlertDialog_Light;
        return style;
    }

    public int getPopupToolbarStyle(){
        int style;
        style = R.style.OverflowMenuStyle;
        return style;
    }

}
