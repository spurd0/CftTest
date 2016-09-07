package com.spudesc.cfttest.data;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 23.08.2016.
 */
public class RequestParams {
    private String mName;
    private String mValue;

    public RequestParams(String name, String value) {
        this.mName = name;
        this.mValue = value;
    }

    public String getName() {
        return mName;
    }

    public String getValue() {
        return mValue;
    }
}
