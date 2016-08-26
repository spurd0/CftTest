package com.spudesc.cfttest.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.spudesc.cfttest.Data.States;
import com.spudesc.cfttest.Interfaces.RequestInterface;
import com.spudesc.cfttest.MainActivity;
import com.spudesc.cfttest.R;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 23.08.2016.
 */
public class RequestFragment extends Fragment {
    String COUNT_KEY = "count";
    RequestInterface requestInterface;
    public boolean requestPerformed;
    String text;

    EditText etCounter;
    ProgressBar pbRequest;
    Button goButt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        States.state = States.activeFragment.requestFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.request_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews(savedInstanceState);
        ((MainActivity) getActivity()).onRequestFragmentCreated(this); // todo remake to event
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(COUNT_KEY, text);
    }

    private void initViews(Bundle savedInstanceState) {
        goButt = (Button) getView().findViewById(R.id.goButt);
        etCounter = (EditText) getView().findViewById(R.id.etCounter);
        pbRequest = (ProgressBar) getView().findViewById(R.id.pbRequest);
        goButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etCounter.getText().toString().length() > 0) {
                    int count = Integer.valueOf(etCounter.getText().toString());
                    requestInterface.requestPoints(count);
                    requestPerformed = true;
                } else {
                    showParamsError(getResources().getString(R.string.wrong_params));
                }
            }
        });

        etCounter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                text = s.toString();
            }
        });

        if (savedInstanceState != null) {
            etCounter.setText(savedInstanceState.getString(COUNT_KEY));
        }
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

    public void showParamsError(final String error) {
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
