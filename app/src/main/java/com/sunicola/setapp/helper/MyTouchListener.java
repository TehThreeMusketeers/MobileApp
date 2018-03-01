package com.sunicola.setapp.helper;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by kolev on 01/03/2018.
 */

// This defines your touch listener
public class MyTouchListener implements View.OnTouchListener {
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                    view);
            view.startDrag(data, shadowBuilder, view, 0);
            view.setVisibility(View.INVISIBLE);
            return true;
        } else {
            return false;
        }
    }
}

