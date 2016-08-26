package com.spudesc.cfttest.Interfaces;

import com.spudesc.cfttest.Data.ServerResponse;

import java.io.InputStream;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 8/24/2016.
 */
public interface ServerResponseInterface {
    void successServerResponse(ServerResponse response);
    void busyErrorServerResponse(ServerResponse response);
    void wrongParamsServerResponse(ServerResponse response);
    void serverErrorResponse(ServerResponse response);
}
