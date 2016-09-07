package com.spudesc.cfttest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.spudesc.cfttest.data.Point;
import com.spudesc.cfttest.R;

import java.util.ArrayList;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 8/25/2016.
 */
public class PointsAdapter extends ArrayAdapter<Point> {
    private ArrayList<Point> mPoints;

    public PointsAdapter(Context context, int resource, ArrayList<Point> mPoints) {
        super(context, resource);
        this.mPoints = mPoints;
    }

    @Override
    public int getCount() {
        return mPoints.size();
    }

    @Override
    public Point getItem(int position) {
        return mPoints.get(position);
    }

    static class ViewHolder {
        public TextView pointName;
        public TextView pointX;
        public TextView pointY;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        Point point = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.table_element, null);
            holder = new ViewHolder();
            holder.pointName = (TextView) view.findViewById(R.id.pointName);
            holder.pointX = (TextView) view.findViewById(R.id.pointX);
            holder.pointY = (TextView) view.findViewById(R.id.pointY);
            view.setTag(holder);
        } else holder = (ViewHolder) view.getTag();

        holder.pointName.setText("Point " + (position + 1) + ":");
        holder.pointX.setText(String.valueOf("X=" + point.x));
        holder.pointY.setText(String.valueOf("Y=" + point.y));

        return view;
    }
}
