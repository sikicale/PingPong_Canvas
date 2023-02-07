package com.hypersoft.pingpong_canvas;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int screenWidth,screenHeight;

    private int xVelocity;
    private int yVelocity;
    final int initialSpeed = 2;
    final CharSequence text = "Izgubio si od Androida :)";
    final int duration = Toast.LENGTH_SHORT;
    private Toast toast;
    Random random;
    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE // da sakrije navigatio bar za stalno
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    ConstraintLayout constraintLayout;
    private PaintView paintView;
    RectF rectCircle;//uokviri lopticu i prati radi koalizije
    SoundPool soundPool;
    int beep1Id,beep2Id,beep3Id,loseLifeId,explodeId;


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

        soundPool = new SoundPool.Builder().setMaxStreams(10).build();
        beep1Id = soundPool.load(this,R.raw.assets_beep1,1);
        beep2Id = soundPool.load(this,R.raw.assets_beep2,1);
        beep3Id = soundPool.load(this,R.raw.assets_beep3,1);
        loseLifeId = soundPool.load(this,R.raw.assets_loselife,1);
        explodeId = soundPool.load(this,R.raw.assets_explode,1);


        View.OnTouchListener handleTouch = new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){
               // Log.d(TAG,"Koordinata X paddle:" + motionEvent.getX());
                if (motionEvent.getX() <= screenWidth-150)
                    paintView.setPaddleX(motionEvent.getX());
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
        //kuglu zaokružiš četvorouglom da bi mogao da radiš intersects(kolizija), ali ga ne iscrtavaš drawRect()
        rectCircle = new RectF(paintView.getBallX()-paintView.getArcLeft(),paintView.getBallY()-paintView.getArcLeft(),paintView.getBallX()+paintView.getArcLeft(),paintView.getBallY()+paintView.getArcLeft());
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
                        // Check for ball colliding with a brick
                        for(int i=0;i<paintView.getNumBricks();i++){
                            if (paintView.bricks[i].getVisible()){
                                if (RectF.intersects(paintView.bricks[i].getRect(),rectCircle)){//ispituje koaliziju
                                    soundPool.play(explodeId,1,1,1,0,1);
                                    paintView.bricks[i].setInvisible();
                                    setYDirection(-getYDirection());
                                }
                            }
                        }
                        if (paintView.getBallY() <= 50) {
                            setYDirection(-getYDirection());
                            soundPool.play(beep2Id,1,1,1,0,1);
                        }
                        // dodir padle i lopte
                        if (((paintView.getBallY()+60) >= (screenHeight-100)) && (((paintView.getBallX()>=paintView.getPaddleX())&&(paintView.getBallX()<= (paintView.getPaddleX()+150)) || (((paintView.getBallX()+50)<= (paintView.getPaddleX()+150))&&((paintView.getBallX()+50)>= paintView.getPaddleX()))))) {
                            setYDirection(-getYDirection());
                            soundPool.play(beep1Id,1,1,1,0,1);
                        }
                        //izgubio, resetuj
                        if (paintView.getBallY() >= screenHeight-50){
                            soundPool.play(loseLifeId,1,1,1,0,1);
                            toast.show();
                            setYDirection(-getYDirection());
                        }


                        if (paintView.getBallX() <= 50) {
                            soundPool.play(beep3Id,1,1,1,0,1);
                            setXDirection(-getXDirection());
                        }
                        if (paintView.getBallX() >= (screenWidth-60)) {
                            setXDirection(-getXDirection());
                            soundPool.play(beep3Id,1,1,1,0,1);
                        }
                    }catch (InterruptedException e) {
                        Log.e(TAG, "Uncaught exception,loptica:", e);// mora ovako ili izaziva prekid(e.printStackTrace();)
                    }
                }
            }
        }).start();
    }
}

