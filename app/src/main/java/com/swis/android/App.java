package com.swis.android;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.swis.android.util.PrefsHelper;


import io.fabric.sdk.android.Fabric;

public class App extends MultiDexApplication {

    private static final String TAG = App.class.getSimpleName();
    private static App instance;
    private Context context;
    private PrefsHelper prefsHelper;
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        initApplication();
        context = getApplicationContext();


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public PrefsHelper getPrefsHelper() {
        if(prefsHelper==null)
            initApplication();
        return prefsHelper;
    }

    public Context getAppContext() {
        return context;
    }

    private void initApplication() {
        instance = this;
        prefsHelper = new PrefsHelper(this);
    }
}