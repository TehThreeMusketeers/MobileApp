package com.sunicola.setapp.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by kolev on 24/02/2018.
 */



public class UserEnvironment extends View{
    private final Paint blackPaint;
    private Path drawPath;
    private Context ctx;

    private boolean drawing;
    private boolean erasing;

    public UserEnvironment(Context context, AttributeSet attrs){
        super(context, attrs);
        ctx = context;
        blackPaint = new Paint();
        blackPaint.setAntiAlias(true);
        blackPaint.setStrokeWidth(7f);
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStyle(Paint.Style.STROKE);
        blackPaint.setStrokeJoin(Paint.Join.ROUND);


        drawing = false;
        erasing = false;

        drawPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {


        if(drawing){
            canvas.drawPath(drawPath, blackPaint);

            if(erasing){
                Log.d("JOEY", "DIAZ");
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                invalidate();
                erasing = false;
            }
        }

    }

    public void setDrawing(){
        drawing = true;
    }

    public void unsetDrawing(){
        drawing = false;
    }

    //FIXME: This doesn't clear the canvas.
    public void erase(){
        erasing = true;
        invalidate();
    }




    /*
        This SHOULD detect when a finger touches the screen, connect the points
        on MotionEvent.ACTION_MOVE. Next the view is invalidated, forcing it to call
        onDraw again and redraws whatever the user drew.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(drawing){
            // Get the coordinates of the touch event.
            float eventX = event.getX();
            float eventY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Set a new starting point
                    drawPath.moveTo(eventX, eventY);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    // Connect the points
                    drawPath.lineTo(eventX, eventY);
                    break;
                default:
                    return false;
            }

            // Makes our view repaint and call onDraw
            invalidate();
            return true;
        }
        return false;
    }



}
