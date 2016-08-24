package com.spudesc.cfttest.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.spudesc.cfttest.Interfaces.RequestInterface;
import com.spudesc.cfttest.R;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 23.08.2016.
 */
public class RequestFragment extends Fragment {
    RequestInterface ri;
    public boolean requestPerformed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.request_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        Button goButt = (Button) getView().findViewById(R.id.goButt);
        final EditText etCounter = (EditText) getView().findViewById(R.id.etCounter);
        goButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // add checker
                Log.d("RequestFragment", "count is " + Integer.valueOf(etCounter.getText().toString()));
                ri.requestPoints(Integer.valueOf(etCounter.getText().toString()));
            }
        });
    }

    @Override
    public void onPause() {
        if (requestPerformed) ri.cancelRequest();
        super.onPause();
    }

    public void setRequestInterface(RequestInterface ri) {
        this.ri = ri;
    }
}
