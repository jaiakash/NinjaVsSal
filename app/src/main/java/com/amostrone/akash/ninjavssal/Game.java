package com.amostrone.akash.ninjavssal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.View;

public class Game extends View {

    Rect enemy;
    Rect player;

    Paint paint_enemy;
    Paint paint_player;
    Paint paint_score;


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
        player.bottom=height-50;
        player.top=height-100;
        player.left=50;
        player.right=100;
        canvas.drawRect(player,paint_player);

        paint_enemy.setColor(Color.RED);
        enemy.bottom=height-50;
        enemy.top=height-100;
        enemy.left=width-100;
        enemy.right=width-50;
        canvas.drawRect(enemy,paint_enemy);

        paint_score.setColor(Color.BLACK);
        paint_score.setTextSize(45);
        canvas.drawText("High Score : "+0,100,75,paint_score);
        canvas.drawText("Score : "+0,width-400,75,paint_score);
    }
}
