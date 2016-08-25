package com.spudesc.cfttest.Interfaces;

import com.spudesc.cfttest.Data.ServerResponse;

import java.io.InputStream;

/**
 * Created by Roman Babenko (roman.babenko@sibers.com) on 8/24/2016.
 */
public interface ResponseInterface {
    void successServerResponse(ServerResponse response);
    void busyErrorServerResponse(ServerResponse response);
    void wrongParamsServerResponse(ServerResponse response);
    void serverErrorResponse(ServerResponse response);
}
