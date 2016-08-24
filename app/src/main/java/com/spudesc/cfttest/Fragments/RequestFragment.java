package com.spudesc.cfttest.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.spudesc.cfttest.Interfaces.RequestInterface;
import com.spudesc.cfttest.R;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 23.08.2016.
 */
public class RequestFragment extends Fragment {
    RequestInterface ri;
    public boolean requestPerformed;
    String TAG = "RequestFragment";
    EditText etCounter;
    int count;
    public ProgressBar pdRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        count = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.request_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        Button goButt = (Button) getView().findViewById(R.id.goButt);
        etCounter = (EditText) getView().findViewById(R.id.etCounter);
        pdRequest = (ProgressBar) getView().findViewById(R.id.pbRequest);
        goButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tempCount = Integer.valueOf(etCounter.getText().toString());
                if (etCounter.getText().length() == 0 || tempCount < 1) {
                    showCountError(getResources().getString(R.string.wrong_params));
                    Log.e(TAG, "count is incorrect");
                }
                else {
                    Log.d(TAG, "count is " + tempCount);
                    ri.requestPoints(tempCount); // disable button + edittext
                    pdRequest.setVisibility(View.VISIBLE);
                    requestPerformed = true;
                    count = tempCount;
                }
            }
        });
    }

    @Override
    public void onPause() {
        if (requestPerformed) {
            ri.cancelRequest();
            requestPerformed = false;
        }
        super.onPause();
    }

    public void setRequestInterface(RequestInterface ri) {
        this.ri = ri;
    }

    public void showCountError(String error) {
        etCounter.setError(error);
    }
}
