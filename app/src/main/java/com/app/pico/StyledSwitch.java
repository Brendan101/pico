package com.app.pico;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by rlou on 5/4/17.
 */

public class StyledSwitch extends SwitchCompat {


    public StyledSwitch(Context context) {
        super(context);
    }

    public StyledSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMinWidth(1);
    }

    public StyledSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMinWidth(1);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        try {
            Field switchWidth = SwitchCompat.class.getDeclaredField("mSwitchWidth");
            switchWidth.setAccessible(true);

            int currWidth = (int) switchWidth.get(this);
            switchWidth.setInt(this, (int)(0.8f*currWidth));

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        this.changeColor(checked);
    }

    private void changeColor(boolean isChecked) {
        // Need API > 4.1 (level 16)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                if(isChecked) {
                    setTrackDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.track_selected, null));
                    setThumbDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.thumb_selected, null));

                } else {
                    setTrackDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.track, null));
                    setThumbDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.thumb, null));
                }
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("VERSION", "android version too low");
        }
    }
}
