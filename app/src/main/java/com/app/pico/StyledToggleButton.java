package com.app.pico;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ToggleButton;

/**
 * Created by rlou on 4/29/17.
 *
 * We create this class to modify default button typeface
 */

public class StyledToggleButton extends ToggleButton {
    private Typeface textTypeface;
    private String tfLoc = "Stellar_Regular.otf";

    public StyledToggleButton(Context context) {
        super(context);
    }

    public StyledToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context);
    }

    public StyledToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context);
    }

    private void setCustomFont(Context context){
        textTypeface = Typeface.createFromAsset(context.getAssets(), tfLoc);
        this.setTypeface(textTypeface);
    }
}
