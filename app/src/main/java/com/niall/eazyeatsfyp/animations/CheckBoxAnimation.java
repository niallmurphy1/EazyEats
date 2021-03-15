package com.niall.eazyeatsfyp.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CheckBox;

public class CheckBoxAnimation extends Animation {

    private int width, startWidth;
    private View view;

    public CheckBoxAnimation(int width, View view){

        this.width = width;
        this.view = view;
        this.startWidth = view.getWidth();

    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        int newWidth = startWidth + (int) ((width-startWidth) * interpolatedTime);

        view.getLayoutParams().width = newWidth;
        view.requestLayout();
        super.applyTransformation(interpolatedTime, t);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
