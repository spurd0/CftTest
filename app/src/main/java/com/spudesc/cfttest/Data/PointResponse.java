package com.spudesc.cfttest.Data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 23.08.2016.
 */
public class PointResponse implements Parcelable {
    double x;
    double y;


    protected PointResponse(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
    }

    public static final Creator<PointResponse> CREATOR = new Creator<PointResponse>() {
        @Override
        public PointResponse createFromParcel(Parcel in) {
            return new PointResponse(in);
        }

        @Override
        public PointResponse[] newArray(int size) {
            return new PointResponse[size];
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
