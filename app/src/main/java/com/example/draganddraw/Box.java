package com.example.draganddraw;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

public class Box implements Parcelable {
    private PointF mOrigin;
    private PointF mCurrent;
    private PointF mPointerOrigin;
    private float  mAngle;

    public Box(PointF origin){
        mOrigin = origin;
        mCurrent = origin;
    }

    public PointF getCurrent(){
        return mCurrent;
    }

    public void setCurrent(PointF current){
        mCurrent = current;
    }

    public PointF getOrigin(){
        return mOrigin;
    }

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    public PointF getPointerOrigin() {
        return mPointerOrigin;
    }

    public void setPointerOrigin(PointF pointerOrigin) {
        mPointerOrigin = pointerOrigin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        mOrigin.writeToParcel(dest, flags);
        mCurrent.writeToParcel(dest, flags);
    }

    private Box (Parcel in) {
        mOrigin.readFromParcel(in);
        mCurrent.readFromParcel(in);
    }

    public static final Parcelable.Creator<Box> CREATOR = new Parcelable.Creator<Box>() {
        /**
         * Return a new box from the data in the specified parcel.
         */
        public Box createFromParcel(Parcel in) {
            return new Box(in);
        }

        /**
         * Return an array of boxes of the specified size.
         */
        public Box[] newArray(int size) {
            return new Box[size];
        }
    };
}
