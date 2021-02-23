package Fonts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by fujitsu on 3/28/2017.
 */

@SuppressLint("AppCompatCustomView")
public class LatoBLackBtn extends Button {

    private Context context;

    public LatoBLackBtn(Context context) {
        super(context);
        this.context = context;
        applyCustomFont(context);

    }

    public LatoBLackBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);

    }

    public LatoBLackBtn(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {

        Typeface customFont = FontCacheRagular.getTypeface("Bold.ttf", context);

        setTypeface(customFont);
    }

}

