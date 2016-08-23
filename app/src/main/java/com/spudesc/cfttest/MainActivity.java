package com.spudesc.cfttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.spudesc.cfttest.Data.Request;
import com.spudesc.cfttest.Interfaces.RequestInterface;

public class MainActivity extends AppCompatActivity implements RequestInterface{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void requestPoints(int count) {
        Request.getRequestPointsCount(count);
    }
}
