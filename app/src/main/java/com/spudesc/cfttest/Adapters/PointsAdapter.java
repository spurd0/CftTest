package com.spudesc.cfttest.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.spudesc.cfttest.Data.Point;
import com.spudesc.cfttest.R;

import java.util.ArrayList;

/**
 * Created by Roman Babenko (roman.babenko@sibers.com) on 8/25/2016.
 */
public class PointsAdapter extends ArrayAdapter<Point> {
    Context context;
    ArrayList<Point> points;

    public PointsAdapter(Context context, int resource, ArrayList<Point> points) {
        super(context, resource);
        this.context = context;
        this.points = points;
    }

    @Override
    public int getCount() {
        return points.size();
    }

    @Override
    public Point getItem(int position) {
        return points.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Point point = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.table_element, null);
        }

        ((TextView) convertView.findViewById(R.id.pointName))
                .setText("Point " + (position + 1) + ":");
        ((TextView) convertView.findViewById(R.id.pointX))
                .setText(String.valueOf("X=" + point.x));
        ((TextView) convertView.findViewById(R.id.pointY))
                .setText(String.valueOf("Y=" + point.y));

        return convertView;
    }
}
