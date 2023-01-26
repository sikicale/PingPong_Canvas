package com.hypersoft.pingpong_canvas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;

public class PaintView extends View {
    private float screenWidth,screenHeight;
    Paint otherPaint;// below we are creating variables for our paint
    private float arcLeft;// and a floating variable for our left arc.
    private float axisX = 200;
    private float axisY = 200;
    private float paddleAxisXl = 300;
    public void setBallX(float axisX){
        this.axisX = axisX;
    }
    public void setBallY(float axisY){
        this.axisY = axisY;
    }
    public float getBallX(){
        return  axisX;
    }
    public float getBallY(){
        return  axisY;
    }
    public void setPaddleX(float axisX){this.paddleAxisXl = axisX;}
    public float getPaddleX(){return paddleAxisXl;}


    @SuppressLint("ResourceAsColor")
    public PaintView(Context context){
        super(context);

        DisplayMetrics displayMetrics = new DisplayMetrics(); //  we are creating a display metrics
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);// on line we are getting display metrics.
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        arcLeft = pxFromDp(context,15);// on  line we are assigning the value to the arc left.

        otherPaint = new Paint(); // on  line we are creating a new variable for our paint
    }
    public static float pxFromDp(final Context context,final float dp){//  method is use to generate px from DP.
        return dp * context.getResources().getDisplayMetrics().density;
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        otherPaint.setStyle(Paint.Style.FILL); // on line we are setting style to out paint.

        otherPaint.setColor(getResources().getColor(R.color.purple_200)); // on line we are changing the color for our paint.

        canvas.drawCircle(axisX, axisY, arcLeft, otherPaint); // on line we are drawing a circle and passing width, height, left arc and paint to add color.
        canvas.drawRect(paddleAxisXl,screenHeight-100,paddleAxisXl+150,screenHeight-50,otherPaint);
       }
}
