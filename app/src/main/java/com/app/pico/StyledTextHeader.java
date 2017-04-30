package com.app.pico;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by rlou on 4/29/17.
 *
 * We create this class to modify default textview typeface for headers
 */

public class StyledTextHeader extends TextView {
    private Typeface textTypeface;
    private String tfLoc = "UVF_Funkydori.ttf";

    public StyledTextHeader(Context context) {
        super(context);
    }

    public StyledTextHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context);
    }

    public StyledTextHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context);
    }


    private void setCustomFont(Context context){
        textTypeface = Typeface.createFromAsset(context.getAssets(), tfLoc);
        this.setTypeface(textTypeface);
    }
}
