package com.amostrone.akash.ninjavssal;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.concurrent.ThreadLocalRandom;

public class Game extends View {

    Rect enemy;
    Rect player;
    RectF joystick;

    Paint paint_enemy;
    Paint paint_player;
    Paint paint_score;
    Paint paint_joystick;

    int enemy_x=-1;
    int player_y=-1;
    int max_jump=200;
    int max_jump_joystick=100;
    int enemy_speed=3;
    int position_js=0;
    float stamina=100;
    float stamina_add=0.005f;
    float score_add=0.01f;

    float score_val = 0;
    float high_score_val = 0;
    int kills=0;

    boolean isPaused=false;

    int[] drawable_enemy = {R.drawable.enemy1, R.drawable.enemy2, R.drawable.enemy3, R.drawable.enemy4, R.drawable.enemy5, R.drawable.enemy6};
    int random_enemy_drawable = ThreadLocalRandom.current().nextInt(0, drawable_enemy.length);

    MediaPlayer ring_background= MediaPlayer.create(getContext(),R.raw.background);

    boolean clicked=false;

    public Game(Context context) {
        super(context);
        player = new Rect();
        enemy = new Rect();
        joystick = new RectF();
        paint_enemy = new Paint();
        paint_player = new Paint();
        paint_score = new Paint();
        paint_joystick = new Paint();

        paint_score.setAntiAlias(true);
        paint_score.setUnderlineText(true);
        paint_score.setFakeBoldText(true);
        Typeface typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD);
        paint_score.setTypeface(typeface);

        paint_score.setColor(Color.RED);
        paint_score.setTextSize(45);

        //Background music
        ring_background.setLooping(true);
        ring_background.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int middle=getWidth()/2;
        int width=getWidth();
        int height=getHeight();

        paint_player.setColor(Color.GREEN);
        if(player_y<=0)player_y=height-75;
        if(clicked)player_y-=5;
        if(player_y<=(height-75-max_jump)) clicked=false;
        if(!clicked && player_y<height-75)player_y+=3;
        player.bottom=player_y-150;
        player.top=player_y-280;
        player.left=60;
        player.right=160;
        Drawable plyr = ContextCompat.getDrawable(getContext(), R.drawable.player);
        plyr.setBounds(player.left, player.top, player.right, player.bottom);
        plyr.draw(canvas);
        //canvas.drawRect(player,paint_player);

        enemy.bottom=height-210;
        enemy.top=height-355;
        if(enemy_x<=0){
            respawnNewEnemy();
        }
        enemy.left=enemy_x-50;
        enemy.right=enemy_x+50;
        paint_enemy.setColor(Color.RED);
        Drawable enmy = ContextCompat.getDrawable(getContext(),drawable_enemy[random_enemy_drawable]);
        enmy.setBounds(enemy.left, enemy.top, enemy.right, enemy.bottom);
        enmy.draw(canvas);
        //canvas.drawRect(enemy,paint_enemy);

        high_score_val=gethigh_score();
        canvas.drawText("High Score : "+String.format("%.02f", high_score_val),75,75,paint_score);
        canvas.drawText("Kills : "+kills,width-400,150,paint_score);
        canvas.drawText("Score : "+String.format("%.02f", score_val),width-400,75,paint_score);
        canvas.drawText("Stamina : "+String.format("%.01f", stamina),middle-200,75,paint_score);

        // Joystick Draw
        paint_joystick.setColor(Color.LTGRAY);
        if(position_js==0){
            position_js=height-100;
        }
        joystick.top=position_js-50;
        joystick.bottom=position_js+50;
        if(position_js!=height-100){
            position_js-=(position_js-(height-100))/10;
        }
        if(position_js<=height-250){
            position_js=height-250;
        }
        else if(position_js>=height){
            position_js=height;
        }
        joystick.left=100;
        joystick.right=200;
        canvas.drawRoundRect(width-200,height-150,width-100,height-50,25,25,paint_joystick);
        canvas.drawRoundRect(joystick.left,joystick.top,joystick.right,joystick.bottom,50,50,paint_joystick);

        //When enemy and player collide, Game Over
        if(Rect.intersects(enemy,player)) {
            GameOver();
        }

        if(!isPaused)enemy_x-=enemy_speed;
        if(!isPaused)score_val+=score_add;
        if(stamina<100 && !isPaused){
            stamina+=stamina_add;
        }
        postInvalidate();
    }

    private void respawnNewEnemy() {
        enemy_x=getWidth()-75;
        enemy_speed+=1;
        random_enemy_drawable = ThreadLocalRandom.current().nextInt(0, drawable_enemy.length);
    }

    private void GameOver(){
        Toast.makeText(getContext(), "Game Over, Your Score is "+String.format("%.02f", score_val), Toast.LENGTH_SHORT).show();
        sethigh_score(score_val);

        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(400);

        isPaused=true;

        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setTitle("Game Over")
                .setMessage("Your Score is "+String.format("%.02f", score_val)+" & You killed "+kills+" Zombies.")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        clicked=false;
                        isPaused=false;

                        stamina = 100;
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity mn = new MainActivity();
                        mn.exit();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        score_val=0;
        enemy_x=0;
        kills=0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {

            case MotionEvent.ACTION_DOWN:

                // For movement joystick
                if((event.getX()<=joystick.right &&  event.getX()>=joystick.left) &&
                        (event.getY()>=getHeight()-150 && event.getY()<=getHeight()-50)) {
                    if(position_js>getHeight()-100-max_jump_joystick && position_js<getHeight()-100+max_jump_joystick) {
                        clicked = true;
                        position_js = (int) event.getY();
                    }
                }

                // For attack joystick
                if(event.getX()>=(getWidth()-200) && event.getX()<=(getWidth()-100) &&
                        event.getY()>=(getHeight()-150) && event.getY()<=(getHeight()-50) ){
                    stamina-=25;
                    if(stamina<=0)
                        GameOver();

                    // Checking for enemy killing
                    if((enemy.left-player.left)<300) {
                        kills++;
                        respawnNewEnemy();
                    }
                        //Toast.makeText(getContext(), enemy.left-player.left+"Not Killed", Toast.LENGTH_SHORT).show();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_MOVE:
                if(clicked && event.getY()>getHeight()-100-max_jump_joystick && event.getY()<getHeight()-100+max_jump_joystick){
                    position_js = (int) event.getY();
                }
                break;
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
