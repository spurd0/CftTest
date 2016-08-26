package com.spudesc.cfttest.Fragments;

import android.app.Fragment;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.spudesc.cfttest.Adapters.PointsAdapter;
import com.spudesc.cfttest.Data.Point;
import com.spudesc.cfttest.Data.States;
import com.spudesc.cfttest.Interfaces.ChartInterface;
import com.spudesc.cfttest.MainActivity;
import com.spudesc.cfttest.R;
import com.spudesc.cfttest.Views.ExpandableHeightGridView;

import java.util.ArrayList;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 23.08.2016.
 */
public class ResponseFragment extends Fragment {
    ArrayList<Point> points;
    ChartInterface chartInterface;
    Point[] graphPoints;
    String TAG = "ResponseFragment";

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
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        States.state = States.activeFragment.responceFragment;
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
        ((MainActivity) getActivity()).onResponseFragmentCreated(this); // todo remake to event
    }

    private void initViews(){
        final ExpandableHeightGridView gridView = (ExpandableHeightGridView) getView().findViewById(R.id.coordView);
        final GraphView graphView = (GraphView) getView().findViewById(R.id.pointsGraphView);
        Button saveButt = (Button) getView().findViewById(R.id.saveButton);
        saveButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chartInterface.saveScreenshotIntent(graphView);

            }
        });

        prepareGraphView(graphView);
        gridView.setExpanded(true);
        PointsAdapter adapter = new PointsAdapter(getActivity(), R.id.coordView, points);
        gridView.setAdapter(adapter);
    }

    private void setCustomPaint(LineGraphSeries series) {
          Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(android.R.color.holo_red_light));
        paint.setStrokeWidth(5);
        paint.setPathEffect(new CornerPathEffect(1500f));
        series.setCustomPaint(paint);
    }

    private void prepareGraphView(GraphView graphView) {
        LineGraphSeries series = new LineGraphSeries(graphPoints);
        setCustomPaint(series);
        graphView.removeAllSeries();
        graphView.addSeries(series);
        graphView.computeScroll();
        graphView.getViewport().setScalable(true); //works, but conflicts with scrollview
    }

    public void setChartInterface(ChartInterface chartInterface) {
        this.chartInterface = chartInterface;
    }
}
