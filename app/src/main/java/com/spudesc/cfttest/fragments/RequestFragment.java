package com.spudesc.cfttest.fragments;

import android.app.Fragment;
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

import com.spudesc.cfttest.interfaces.RequestInterface;
import com.spudesc.cfttest.MainActivity;
import com.spudesc.cfttest.R;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 23.08.2016.
 */
public class RequestFragment extends Fragment {
    static final String TAG = "RequestFragment";
    private RequestInterface mRequestInterface;

    private EditText mEtCounter;
    private ProgressBar mPbRequest;
    private Button mGoButt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        try {
            mRequestInterface = (RequestInterface) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, getActivity().toString()
                    + " must implement RequestInterface");
        }
    }

    private void initViews() {
        mGoButt = (Button) getView().findViewById(R.id.goButt);
        mEtCounter = (EditText) getView().findViewById(R.id.etCounter);
        mPbRequest = (ProgressBar) getView().findViewById(R.id.pbRequest);
        mGoButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEtCounter.getText().toString().length() > 0) {
                    int count = Integer.valueOf(mEtCounter.getText().toString());
                    if (mRequestInterface != null) mRequestInterface.requestPoints(count);
                    else Log.e(TAG, getActivity().toString()
                            + " must implement RequestInterface");
                } else {
                    showParamsError(getResources().getString(R.string.wrong_params));
                }
            }
        });
    }

    public void showParamsError(final String error) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEtCounter.setError(error);
            }
        });
    }

    public void setViews(final boolean performed) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEtCounter.setEnabled(!performed);
                mGoButt.setEnabled(!performed);
                if (performed) {
                    mPbRequest.setVisibility(View.VISIBLE);
                } else {
                    mPbRequest.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
