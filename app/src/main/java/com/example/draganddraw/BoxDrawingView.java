package com.example.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;

public class BoxDrawingView extends View {
    private static final String TAG = "BoxDrawingView";
    private static final String PARENT_STATE = "PARENT_STATE";
    private static final String BOXES_ARGS = "Boxes";

    private Box mCurrentBox;
    private List<Box> mBoxes = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    // Используется при создании представления в коде
    public BoxDrawingView(Context context){
        this(context, null);
    }

    // Используется при заполнении представления по разметке XML
    public BoxDrawingView(Context context, AttributeSet attrs){
        super(context, attrs);

        // Прямоугольники рисуются полупрозрачным красным цветом (ARGB)
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        // Фон закрашивается серовато-белым цветом
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
//        Parcelable superState = super.onSaveInstanceState();
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(PARENT_STATE, superState);
//        bundle.putParcelableArray(BOXES_ARGS, mBoxes.toArray(new Box[mBoxes.size()]));
//        return bundle;
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mBoxList = mBoxes;

        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
//        Log.i("one", "restored");
//        Bundle bundle = (Bundle) state;
//        super.onRestoreInstanceState(bundle.getParcelable(PARENT_STATE));
//
//        Box[] boxes = (Box[]) bundle.getParcelableArray(BOXES_ARGS);
//        mBoxes = new ArrayList<>(Arrays.asList(boxes));
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        Log.d(TAG,"Restoring Instance State");
        mBoxes = ss.mBoxList;
    }

    private static class SavedState extends BaseSavedState {
        private List<Box> mBoxList ;

        public SavedState(Parcelable superState) {
            super(superState);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // заполнение фона
        canvas.drawPaint(mBackgroundPaint);

        for (Box box : mBoxes){
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);
            //canvas.drawRect(left, top, right, bottom, mBoxPaint);
            float angle = box.getAngle();
            float px = (box.getOrigin().x+box.getCurrent().x)/2;
            float py = (box.getOrigin().y+box.getCurrent().y)/2;
            canvas.save();
            canvas.rotate(angle, px, py);
            canvas.drawRect(left, top, right, bottom, mBoxPaint);
            canvas.restore();

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        String action = "";

        PointF touchPoint  = null;
        PointF touchPoint2 = null;
        for (int i=0;i<event.getPointerCount();i++) {
            if(event.getPointerId(i)==0)
                touchPoint = new PointF(event.getX(i), event.getY(i));
            if(event.getPointerId(i)==1)
                touchPoint2 = new PointF(event.getX(i), event.getY(i));
        }

        //switch (event.getAction()){
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                //mCurrentBox = new Box(current);
                mCurrentBox = new Box(touchPoint);
                mBoxes.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mCurrentBox.setPointerOrigin(touchPoint2);
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
//                if (mCurrentBox != null){
//                    mCurrentBox.setCurrent(current);
//                }
                if(touchPoint  != null )
                    mCurrentBox.setCurrent(touchPoint);
                if(touchPoint2 != null ) {
                    PointF boxOrigin     = mCurrentBox.getOrigin();
                    PointF pointerOrigin = mCurrentBox.getPointerOrigin();
                    float angle2 = (float) Math.atan2(touchPoint2.y   - boxOrigin.y, touchPoint2.x   - boxOrigin.x);
                    float angle1 = (float) Math.atan2(pointerOrigin.y - boxOrigin.y, pointerOrigin.x - boxOrigin.x);
                    float calculatedAngle = (float) Math.toDegrees(angle2 - angle1);
                    if (calculatedAngle < 0) calculatedAngle += 360;
                    mCurrentBox.setAngle(calculatedAngle);
                    Log.d(TAG, "Set Box Angle " + calculatedAngle);
                }
                    invalidate();
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                mCurrentBox = null;
                break;
        }

        Log.i(TAG, action + " at x=" + current.x + ", y=" + current.y);
        return true;
    }
}
