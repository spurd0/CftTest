package com.spudesc.cfttest.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            points = bundle.getParcelableArrayList(getResources().getString(R.string.points_array));
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
        gv.setExpanded(true);
        PointsAdapter adapter = new PointsAdapter(getActivity(), R.id.coordView, points);
        gv.setAdapter(adapter);
        Log.d("TAG", "Size is " + adapter.getCount());
    }
}
