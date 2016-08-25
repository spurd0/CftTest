package com.spudesc.cfttest.Fragments;

import android.app.Fragment;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.spudesc.cfttest.Adapters.PointsAdapter;
import com.spudesc.cfttest.Data.Point;
import com.spudesc.cfttest.R;
import com.spudesc.cfttest.Views.ExpandableHeightGridView;

import java.util.ArrayList;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 23.08.2016.
 */
public class ResponseFragment extends Fragment {
    ArrayList<Point> points;
    Point[] graphPoints;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            points = bundle.getParcelableArrayList(getResources().getString(R.string.points_array));
            graphPoints = (Point[]) bundle.getParcelableArray(getResources().
                    getString(R.string.sorted_points_array));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chart_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews(){
        ExpandableHeightGridView gv = (ExpandableHeightGridView) getView().findViewById(R.id.coordView);
        GraphView graphView = (GraphView) getView().findViewById(R.id.pointsGraphView);

        prepareGraphView(graphView);
        gv.setExpanded(true);
        PointsAdapter adapter = new PointsAdapter(getActivity(), R.id.coordView, points);
        gv.setAdapter(adapter);
    }


    /**
     * Sets custom paint for graph, can be used to change lines to circle form.
     */
    private void addCustiomPaint(LineGraphSeries series) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(android.R.color.white));
        paint.setStrokeWidth(10);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
        series.setCustomPaint(paint);
    }

    private void prepareGraphView(GraphView graphView) {
        LineGraphSeries series = new LineGraphSeries(graphPoints);
        addCustiomPaint(series);

        graphView.addSeries(series);
        graphView.computeScroll();
        graphView.getViewport().setScalable(true);

    }
}
