package com.spudesc.cfttest.Data;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 8/26/2016.
 */
public class States {
    public enum activeFragment{requestFragment, responceFragment, nothing}

    public static activeFragment state = activeFragment.nothing;
}
