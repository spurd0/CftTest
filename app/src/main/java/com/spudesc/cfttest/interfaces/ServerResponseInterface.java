package com.spudesc.cfttest.interfaces;

import com.spudesc.cfttest.data.ServerResponse;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 8/24/2016.
 */
public interface ServerResponseInterface {
    void successServerResponse(ServerResponse response);
    void serverIsBusyResponse(ServerResponse response);
    void wrongParamsServerResponse(ServerResponse response);
    void serverErrorResponse(ServerResponse response);
}
