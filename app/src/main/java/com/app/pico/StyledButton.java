package com.app.pico;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by rlou on 4/29/17.
 *
 * We create this class to modify default button typeface
 */

public class StyledButton extends Button {
    private Typeface textTypeface;
    private String tfLoc = "Stellar_Regular.otf";

    public StyledButton(Context context) {
        super(context);
    }

    public StyledButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context);
    }

    public StyledButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context);
    }

    private void setCustomFont(Context context){
        textTypeface = Typeface.createFromAsset(context.getAssets(), tfLoc);
        this.setTypeface(textTypeface);
    }
}
