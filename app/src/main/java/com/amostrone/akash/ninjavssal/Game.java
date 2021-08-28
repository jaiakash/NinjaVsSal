package com.amostrone.akash.ninjavssal;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class Game extends View {

    Rect enemy;
    Rect player;

    Paint paint_enemy;
    Paint paint_player;
    Paint paint_score;

    int enemy_x=-1;
    int player_y=-1;
    int max_jump=100;

    float score_val = 0;
    float high_score_val = 0;

    boolean clicked=false;

    public Game(Context context) {
        super(context);
        player = new Rect();
        enemy = new Rect();
        paint_enemy = new Paint();
        paint_player = new Paint();
        paint_score = new Paint();

        paint_score.setAntiAlias(true);
        paint_score.setUnderlineText(true);
        paint_score.setFakeBoldText(true);
        Typeface typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD);
        paint_score.setTypeface(typeface);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int middle=getWidth()/2;
        int width=getWidth();
        int height=getHeight();

        paint_player.setColor(Color.GREEN);

        if(player_y<=0)player_y=height-75;
        if(clicked)player_y--;
        if(player_y<=(height-75-max_jump)) clicked=false;
        if(!clicked && player_y<height-75)player_y++;

        player.bottom=player_y+25;
        player.top=player_y-25;
        player.left=50;
        player.right=100;
        canvas.drawRect(player,paint_player);

        enemy.bottom=height-50;
        enemy.top=height-100;
        if(enemy_x<=0)enemy_x=width-75;
        enemy.left=enemy_x-25;
        enemy.right=enemy_x+25;
        paint_enemy.setColor(Color.RED);
        canvas.drawRect(enemy,paint_enemy);

        paint_score.setColor(Color.BLACK);
        paint_score.setTextSize(45);
        high_score_val=gethigh_score();
        canvas.drawText("High Score : "+String.format("%.02f", high_score_val),100,75,paint_score);
        canvas.drawText("Score : "+String.format("%.02f", score_val),width-400,75,paint_score);

        //When enemy and player collide, Game Over
        if(Rect.intersects(enemy,player)) {
            Toast.makeText(getContext(), "Game Over, Your Score is "+String.format("%.02f", score_val), Toast.LENGTH_SHORT).show();
            sethigh_score(score_val);

            Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(400);
            score_val=0;
            enemy_x=0;
        }

        enemy_x-=3;
        score_val+=0.01;
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // get pointer index from the event object
        int pointerIndex = event.getActionIndex();

        // get pointer ID
        int pointerId = event.getPointerId(pointerIndex);

        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {

            case MotionEvent.ACTION_DOWN:
                clicked=true;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
        }
        postInvalidate();

        return true;
    }

    public void sethigh_score(float h){

        SharedPreferences sharedPref = getContext().getSharedPreferences("high_score",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        float old_high_score = gethigh_score();

        if(h>old_high_score) {
            editor.putFloat("high_score", h);
            editor.apply();
        }
    }

    public float gethigh_score(){

        SharedPreferences sharedPref = getContext().getSharedPreferences("high_score",MODE_PRIVATE);

        float defaultValue = 0;
        return sharedPref.getFloat("high_score", defaultValue);
    }
}
