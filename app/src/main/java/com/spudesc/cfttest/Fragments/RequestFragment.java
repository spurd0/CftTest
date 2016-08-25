package com.spudesc.cfttest.Fragments;

import android.app.Fragment;
import android.os.Bundle;
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
    String TAG = "RequestFragment";
    RequestInterface requestInterface;
    public boolean requestPerformed;

    EditText etCounter;
    ProgressBar pbRequest;
    Button goButt;

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
        goButt = (Button) getView().findViewById(R.id.goButt);
        etCounter = (EditText) getView().findViewById(R.id.etCounter);
        pbRequest = (ProgressBar) getView().findViewById(R.id.pbRequest);
        goButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etCounter.getText().toString().length() > 0) {
                    int tempCount = Integer.valueOf(etCounter.getText().toString());
                    Log.d(TAG, "count is " + tempCount);
                    requestInterface.requestPoints(tempCount);
                    requestPerformed = true;
                } else {
                    showCountError(getResources().getString(R.string.wrong_params));
                }
            }
        });
    }

    @Override
    public void onPause() {
        if (requestPerformed) {
            requestInterface.cancelRequest();
            requestPerformed = false;
        }
        super.onPause();
    }

    public void setRequestInterface(RequestInterface ri) {
        this.requestInterface = ri;
    }

    public void showCountError(final String error) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                etCounter.setError(error);
            }
        });
    }

    public void setViews(final boolean performed) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                etCounter.setEnabled(!performed);
                goButt.setEnabled(!performed);
                if (performed) {
                    pbRequest.setVisibility(View.VISIBLE);
                } else {
                    pbRequest.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
