package com.spudesc.cfttest.tasks;

import com.spudesc.cfttest.data.RequestParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 23.08.2016.
 */
public class RequestBuilder {

    public static String[] getRequestPointsParams(int count) throws NoSuchAlgorithmException, IOException, KeyManagementException {
        String request = "https://demo.bankplus.ru/mobws/json/pointsList/";
        ArrayList<RequestParams> params = new ArrayList<RequestParams>();
        params.add(new RequestParams("count", String.valueOf(count)));
        params.add(new RequestParams("version", "1.1"));
        String[] requestParams = new String[2];
        requestParams[0] = request;
        requestParams[1] = getParams(params);
        return requestParams;
    }

    private static String getParams(ArrayList<RequestParams> params) {
        StringBuilder parameters = new StringBuilder();
        boolean first = true;

        for (RequestParams param : params) {
            if (first) {
                first = false;
            } else {
                parameters.append("&");
            }
            try {
                parameters.append(URLEncoder.encode(param.getName(), "UTF-8"));
                parameters.append("=");
                parameters.append(URLEncoder.encode(param.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return parameters.toString();
    }

}
