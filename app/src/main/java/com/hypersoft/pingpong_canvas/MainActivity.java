package com.hypersoft.pingpong_canvas;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int screenWidth,screenHeight;
    private float x,xMove;

    private int xVelocity;
    private int yVelocity;
    private int initialSpeed = 2;
    private CharSequence text = "Izgubio si od Androida :)";
    private int duration = Toast.LENGTH_SHORT;
    private Toast toast;
    Random random;
    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE // da sakrije navigatio bar za stalno
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    private ConstraintLayout constraintLayout;
    private PaintView paintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        constraintLayout = findViewById(R.id.idRLView);
        paintView = new PaintView(this);
        constraintLayout.addView(paintView);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;//širina ekrana u pikselima
        screenHeight = displayMetrics.heightPixels;// visina ekrana u pikselima
        toast = Toast.makeText(this,text,duration);

        getWindow().getDecorView().setSystemUiVisibility(flags);// da sakrije navigatio bar za stalno

        View.OnTouchListener handleTouch = new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){
                    paintView.setPaddleX(motionEvent.getX());
                  //  paintView.invalidate();
                return true;
            }
        };
        constraintLayout.setOnTouchListener(handleTouch);
        direction_ball();
        ballMoving();
}
    private void direction_ball(){
        random = new Random();
        int randomXDirection = random.nextInt(2);//-1 kreće se gore a 1 kreće se dole
        if (randomXDirection==0)
            randomXDirection--;
        setXDirection(randomXDirection*initialSpeed);

        int randomYDirection = random.nextInt(2);
        if(randomYDirection==0)
            randomYDirection--;
        setYDirection(randomYDirection*initialSpeed);

    }
    synchronized private void setXDirection(int randomXDirection){
        xVelocity = randomXDirection;
        notify();
    }
    synchronized private int getXDirection(){return xVelocity;}
    synchronized private void setYDirection(int randomYDirection){
        yVelocity = randomYDirection;
        notify();
    }
    synchronized private int getYDirection(){return yVelocity;}
    synchronized private void move(){
        paintView.setBallX(paintView.getBallX()+getXDirection());
        paintView.setBallY(paintView.getBallY()+getYDirection());
        paintView.invalidate();// ovo služi da ponovo pokrene iscrtavanje
        notify();
    }
    private void ballMoving(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.interrupted()){
                    try {
                        Thread.sleep(3);
                        move();
                        if (paintView.getBallY() <= 50)
                            setYDirection(-getYDirection());
                        // dodir padle i lopte
                        if (((paintView.getBallY()+60) >= (screenHeight-100)) && (((paintView.getBallX()>=paintView.getPaddleX())&&(paintView.getBallX()<= (paintView.getPaddleX()+150)) || (((paintView.getBallX()+50)<= (paintView.getPaddleX()+150))&&((paintView.getBallX()+50)>= paintView.getPaddleX())))))
                            setYDirection(-getYDirection());
                        //izgubio, resetuj
                        if (paintView.getBallY() >= screenHeight-50){
                            toast.show();
                            setYDirection(-getYDirection());
                        }


                        if (paintView.getBallX() <= 50)
                            setXDirection(-getXDirection());
                        if (paintView.getBallX() >= (screenWidth-60))
                            setXDirection(-getXDirection());
                    }catch (InterruptedException e) {
                        Log.e(TAG, "Uncaught exception,loptica:", e);// mora ovako ili izaziva prekid(e.printStackTrace();)
                    }
                }
            }
        }).start();
    }
}

