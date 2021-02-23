package util;

import android.content.Context;
import android.graphics.Canvas;

import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;

import gropal.in.R;

public class HamburgerDrawable extends DrawerArrowDrawable {

    public HamburgerDrawable(Context context){
        super(context);
        setColor(context.getResources().getColor(R.color.white));
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);

        setBarLength(40.0f);
        setBarThickness(7.0f);
        setGapSize(7.0f);

    }
}
