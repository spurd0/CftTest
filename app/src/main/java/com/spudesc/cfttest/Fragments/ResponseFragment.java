package com.spudesc.cfttest.fragments;

import android.app.Fragment;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.spudesc.cfttest.adapters.PointsAdapter;
import com.spudesc.cfttest.data.Point;
import com.spudesc.cfttest.interfaces.ChartInterface;
import com.spudesc.cfttest.MainActivity;
import com.spudesc.cfttest.R;
import com.spudesc.cfttest.Views.ExpandableHeightGridView;

import java.util.ArrayList;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 23.08.2016.
 */
public class ResponseFragment extends Fragment {
    private ArrayList<Point> mPoints;
    private ChartInterface mChartInterface;
    private Point[] mGraphPoints;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mPoints = bundle.getParcelableArrayList(getResources().getString(R.string.points_array));
            mGraphPoints = (Point[]) bundle.getParcelableArray(getResources().
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
        ((MainActivity) getActivity()).onResponseFragmentCreated(this); // todo remake to event
    }

    private void initViews(){
        final ExpandableHeightGridView gridView = (ExpandableHeightGridView) getView().findViewById(R.id.coordView);
        final GraphView graphView = (GraphView) getView().findViewById(R.id.pointsGraphView);
        Button saveButt = (Button) getView().findViewById(R.id.saveButton);
        saveButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChartInterface.saveScreenshotIntent(graphView);

            }
        });

        prepareGraphView(graphView);
        gridView.setmExpanded(true);
        PointsAdapter adapter = new PointsAdapter(getActivity(), R.id.coordView, mPoints);
        gridView.setAdapter(adapter);
    }

    private void setCustomPaint(LineGraphSeries series) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_light));
        paint.setStrokeWidth(5);
        paint.setPathEffect(new CornerPathEffect(1500f));
        series.setCustomPaint(paint);
    }

    private void prepareGraphView(GraphView graphView) {
        LineGraphSeries series = new LineGraphSeries(mGraphPoints);
        setCustomPaint(series);
        graphView.removeAllSeries();
        graphView.addSeries(series);
        graphView.computeScroll();
        graphView.getViewport().setScalable(true); //works, but conflicts with scrollview
    }

    public void setmChartInterface(ChartInterface mChartInterface) {
        this.mChartInterface = mChartInterface;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(getResources().
                getString(R.string.points_array), mPoints);
        outState.putParcelableArray(getResources().
                getString(R.string.sorted_points_array), mGraphPoints);
    }
}
