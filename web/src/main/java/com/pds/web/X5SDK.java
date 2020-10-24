package com.pds.web;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import java.util.HashMap;

public class X5SDK {

    private static final String TAG = "X5SDK";

    public static void init(final Application application) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                preInit(application);
            }
        }, 1000);
    }

    private static void preInit(Context context) {
        HashMap map = new HashMap(3);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        preX5(context);
    }

    private static void preX5(Context context) {
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.d(TAG, "x5 core onCoreInitFinished");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.d(TAG, "x5 core onViewInitFinished result = " + b);
            }
        });
    }
}
