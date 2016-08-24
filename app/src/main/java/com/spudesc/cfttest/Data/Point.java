package com.spudesc.cfttest.Data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 23.08.2016.
 */
public class Point implements Parcelable {
    public double x;
    public double y;

    protected Point(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
    }

    public static final Creator<Point> CREATOR = new Creator<Point>() {
        @Override
        public Point createFromParcel(Parcel in) {
            return new Point(in);
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(x);
        dest.writeDouble(y);
    }
}
