package com.spudesc.cfttest.Data;

import java.util.ArrayList;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 23.08.2016.
 */
public class Request {

    public static String getRequestPointsCount(int count) {
        String request = "https://demo.bankplus.ru/mobws/json/pointsList";
        ArrayList<RequestParams> params = new ArrayList<RequestParams>();
        params.add(new RequestParams("count", String.valueOf(count)));
        params.add(new RequestParams("version", "1.1"));
        return addParam(request, params);
    }

    private static String addParam(String request, ArrayList<RequestParams> params) {
        StringBuilder result = new StringBuilder(request);
        boolean first = true;

        for (RequestParams param : params) { // TODO make sure that count is first
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(param.getName());
            result.append("=");
            result.append(param.getValue());
        }
        return result.toString();
    }
}
