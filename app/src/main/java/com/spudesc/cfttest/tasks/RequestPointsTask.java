package com.spudesc.cfttest.tasks;

import com.google.gson.Gson;
import com.spudesc.cfttest.data.ServerResponse;
import com.spudesc.cfttest.interfaces.ServerResponseInterface;
import com.spudesc.cfttest.managers.RequestPointsSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 8/24/2016.
 */
public class RequestPointsTask {
    private String mHttp_url;
    private String mParams;
    private ServerResponseInterface mServerResponseInterface;

    public RequestPointsTask(String http_url, String params, ServerResponseInterface serverResponseInterface) {
        this.mHttp_url = http_url;
        this.mParams = params;
        this.mServerResponseInterface = serverResponseInterface;
        sendRequest();
    }

    private void sendRequest() {
        RequestPointsSender rs = new RequestPointsSender(mHttp_url, mParams);
        try {
            InputStream responseStream = rs.sendRequest();
            if (responseStream == null) {
                mServerResponseInterface.serverErrorResponse(null);
            } else {
                ServerResponse serverResponse = new Gson().fromJson(responseToString(responseStream),
                        ServerResponse.class);
                handleServerResponse(serverResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private String responseToString(InputStream is) {
        String result = new String();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        try {
            while ((inputLine = in.readLine()) != null) {
                result += inputLine;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void handleServerResponse(ServerResponse serverResponse) {
        int result = serverResponse.result;
        switch (result) {
            case 0: {
                mServerResponseInterface.successServerResponse(serverResponse);
                break;
            }
            case -1: {
                mServerResponseInterface.serverIsBusyResponse(serverResponse);
                break;
            }
            case -100: {
                mServerResponseInterface.wrongParamsServerResponse(serverResponse);
                break;
            }
            default: {
                mServerResponseInterface.serverErrorResponse(serverResponse);
                break;
            }
        }

    }

}
