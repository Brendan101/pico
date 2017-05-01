package com.app.pico;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Created by rlou on 5/1/17.
 */

public class StyledAutoCompleteTextView extends AutoCompleteTextView {

    private Typeface textTypeface;
    private String tfLoc = "Stellar_Regular.otf";

    public StyledAutoCompleteTextView(Context context) {
        super(context);
    }

    public StyledAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context);
    }

    public StyledAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context);
    }

    private void setCustomFont(Context context){
        textTypeface = Typeface.createFromAsset(context.getAssets(), tfLoc);
        this.setTypeface(textTypeface);
    }
}
