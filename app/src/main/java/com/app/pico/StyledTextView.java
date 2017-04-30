package com.app.pico;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by rlou on 4/29/17.
 *
 * We create this class to modify default textview typeface
 */

public class StyledTextView extends TextView {
    private Typeface textTypeface;
    private String tfLoc = "Stellar_Regular.otf";

    public StyledTextView(Context context) {
        super(context);
    }

    public StyledTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context);
    }

    public StyledTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context);
    }

    private void setCustomFont(Context context){
        textTypeface = Typeface.createFromAsset(context.getAssets(), tfLoc);
        this.setTypeface(textTypeface);
    }
}
