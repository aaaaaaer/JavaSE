package com.jl.tcs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者： 吴昶 .
 * 时间: 2018/12/5 14:18
 * 功能简介：贪吃蛇 运动界面
 */
public class TcsView extends View {

    private Paint paint;
    //小蛇的组成点
    private ArrayList<Point> points = new ArrayList<Point>();
    private Point pointFood = new Point();//食物点
    //游戏界面的 方格划分
    private int[][] spoints;
    //方格数组的大小
    private int sizex;
    private int sizey;
    //小蛇的头部对应在数组中的位置
    private int headerX;
    private int headerY;
    //食物的坐标点
    private int foodX;
    private int foodY;
    //偏移量
    private int offsetX = 0;
    private int offsetY = 0;
    //随机数
    private Random random = new Random();
    private boolean isStart = true;
    //屏幕宽高
    private int width;
    private int height;
    //小蛇方块的大小
    private int pointSize = 50;
    //方向
    private int direction = 0;// 0 上 -1 下  1 左 2 右
    //触摸点坐标
    private float tdx;
    private float tdy;
    private boolean isStop = false;
    private TcsScoreListener tcsScoreListener;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    headerNext();
                    break;
            }
            return false;
        }
    });

    private void headerNext() {
        if (points.size() == 0 || isStop || foodX == -1) return;
        //小蛇的移动控制，可以看成是小蛇的首尾发生了移动，
        // 头部根据下一次移动的方向移动到下一格，然后去掉结尾
        switch (direction) {
            case 0:
                headerY--;
                break;
            case -1:
                headerY++;
                break;
            case 1:
                headerX--;
                break;
            case 2:
                headerX++;
                break;
        }
        //判断是否到达边界
        if (headerX < 0 || headerY < 0 || headerX >= sizex || headerY >= sizey) {
            isStop = true;
        } else {
            //判断小蛇是否吃到自己
            if (spoints[headerX][headerY] == 1) {
                isStop = true;
            }
            points.add(0, new Point(headerX, headerY));
            spoints[headerX][headerY] = 1;
            if (tcsScoreListener != null) {
                tcsScoreListener.onTCSScore(points.size());
            }
            if (headerX == foodX && headerY == foodY) {
                foodX = -1;
                foodY = -1;
                initPointFood();
            } else {
                Point move = points.get(points.size() - 1);
                spoints[move.x][move.y] = 0;
                points.remove(points.size() - 1);
            }
            if (!isStop) {
                invalidate();
            }
        }
    }

    public TcsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        //100为时间的发生间隔，即小蛇多久移动一次，此处修改可调节小蛇的速度
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        }, 0, 100);
    }

    public void setTcsScoreListener(TcsScoreListener tcsScoreListener) {
        this.tcsScoreListener = tcsScoreListener;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();

        if (isStart) {
            //根据宽高以及 pointSize 将游戏区域划分成不同的矩形区块，
            sizex = width / pointSize;
            sizey = height / pointSize;
            spoints = new int[sizex][sizey];
            //计算偏移量
            offsetX = (width - (sizex * pointSize)) / 2;
            offsetY = (height - (sizey * pointSize)) / 2;
            //头部起始坐标默认在屏幕中央
            headerX = sizex / 2;
            headerY = sizey / 2;
            spoints[headerX][headerY] = 1;
            points.add(0, new Point(headerX, headerY));
            points.add(1, new Point(headerX, headerY));
            points.add(2, new Point(headerX, headerY));
            points.add(3, new Point(headerX, headerY));
            initPointFood();
            isStart = false;
        }
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            drawPoint(canvas, paint, point);
        }
        drawPoint(canvas, paint, pointFood);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isStop) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    tdx = event.getX();
                    tdy = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    float dx = Math.abs(tdx - event.getX());
                    float dy = Math.abs(tdy - event.getY());
                    if (dx > 40 || dy > 40) {
                        //判断为滑动，不是点击
                        Log.d("*****onTouchEvent****", "/**/*/*//*////*98*8/*9*/89*898565/*** ");
                    } else {
                        if (direction == 0 || direction == -1) {
                            if (tdx > points.get(0).x) {
                                direction = 2;
                            } else {
                                direction = 1;
                            }
                        } else if (direction == 1 || direction == 2) {
                            if (tdy > points.get(0).y) {
                                direction = -1;
                            } else {
                                direction = 0;
                            }
                        }
                        handler.sendEmptyMessage(1);
                    }
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    //根据点的左边画出对应的点
    private void drawPoint(Canvas canvas, Paint paint, Point point) {
        RectF rectF = new RectF(
                offsetX + point.x * pointSize,
                offsetY + point.y * pointSize,
                offsetX + (point.x + 1) * pointSize,
                offsetY + (point.y + 1) * pointSize);
        canvas.drawRect(rectF, paint);
    }

    /**
     * 确定食物点的位置
     */
    private void initPointFood() {
        foodX = random.nextInt(sizex);//获取随机的坐标点
        foodY = random.nextInt(sizey);
        //判断当前坐标点是否在小蛇的轨迹上
        if (spoints[foodX][foodY] == 1) {
            initPointFood();
        } else {
            //标准化坐标位置
            pointFood.set(foodX, foodY);
        }
    }

    /**
     * 根据传入的参数 修改小蛇的运动方向
     *
     * @param dire
     */
    public void changeDirection(int dire) {
        switch (dire) {
            case 0:// 小蛇上下运动时 可修改为左右运动
            case -1:
                if (direction == 1 || direction == 2) {
                    direction = dire;
                }
                break;
            case 1://小蛇左右运动时 可修改方向为上下运动
            case 2:
                if (direction == 0 || direction == -1) {
                    direction = dire;
                }
                break;
        }
    }

    /**
     * 设置重新开始
     */
    public void restart() {
        isStop = false;
        isStart = true;
        points.clear();
        invalidate();
    }
}
