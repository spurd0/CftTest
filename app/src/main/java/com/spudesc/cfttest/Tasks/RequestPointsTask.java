package com.spudesc.cfttest.Tasks;

import com.google.gson.Gson;
import com.spudesc.cfttest.Data.ServerResponse;
import com.spudesc.cfttest.Interfaces.ServerResponseInterface;
import com.spudesc.cfttest.Managers.RequestPointsSender;

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
    private String http_url;
    private String params;
    private ServerResponseInterface serverResponseInterface;

    public RequestPointsTask(String http_url, String params, ServerResponseInterface serverResponseInterface) {
        this.http_url = http_url;
        this.params = params;
        this.serverResponseInterface = serverResponseInterface;
        sendRequest();
    }

    private void sendRequest() {
        RequestPointsSender rs = new RequestPointsSender(http_url, params);
        try {
            InputStream responseStream = rs.sendRequest();
            if (responseStream == null) {
                serverResponseInterface.serverErrorResponse(null);
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
                serverResponseInterface.successServerResponse(serverResponse);
                break;
            }
            case -1: {
                serverResponseInterface.serverIsBusyResponse(serverResponse);
                break;
            }
            case -100: {
                serverResponseInterface.wrongParamsServerResponse(serverResponse);
                break;
            }
            default: {
                serverResponseInterface.serverErrorResponse(serverResponse);
                break;
            }
        }

    }

}
