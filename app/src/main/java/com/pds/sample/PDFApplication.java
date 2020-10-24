package com.pds.sample;

import android.app.Application;
import com.pds.web.X5SDK;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/24 8:47 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class PDFApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        X5SDK.init(this);
    }
}
