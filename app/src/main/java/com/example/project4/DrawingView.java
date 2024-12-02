package com.example.project4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

    private Paint paint;
    private Bitmap bitmap;
    private Canvas canvas;
    private Path path; // 경로를 추적할 Path 객체

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE); // 선을 그릴 때 사용
        path = new Path(); // 경로 객체 초기화
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawPath(path, paint); // 경로를 그리기
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y); // 터치가 시작된 위치로 경로 이동
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y); // 터치가 이동하면서 선을 그리기
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        invalidate(); // 화면을 다시 그리도록 요청
        return true;
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas); // 경로와 그림을 그리기
        return bitmap;
    }

    public void clearCanvas() {
        bitmap.eraseColor(Color.WHITE);  // 캔버스를 하얀색으로 지우기
        path.reset(); // 경로 초기화
        invalidate(); // 화면을 다시 그리도록 요청
    }
}