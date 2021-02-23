package Fonts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by fujitsu on 3/28/2017.
 */

@SuppressLint("AppCompatCustomView")
public class LatoBLack extends TextView {

    private Context context;

    public LatoBLack(Context context) {
        super(context);
        this.context = context;
        applyCustomFont(context);

    }

    public LatoBLack(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);

    }

    public LatoBLack(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {

        Typeface customFont = FontCacheRagular.getTypeface("Bold.ttf", context);

        setTypeface(customFont);
    }

    public void applyLatoBlackFont(final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    applyLatoBlackFont(child);
                }
            } else if (v instanceof TextView) {
                applyCustomFont(context);
            }
        } catch (Exception e) {
        }
    }
}

