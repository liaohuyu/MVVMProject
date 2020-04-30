package com.android.myapplication.app;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.library.http.HttpUtils;

public class App extends MultiDexApplication {
    private static App app;

    public static App getInstance() {
        return app;
    }

    private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            StackTraceElement[] elements = ex.getStackTrace();

            StringBuilder reason = new StringBuilder(ex.toString());

            if (elements != null && elements.length > 0) {

                for (StackTraceElement element : elements) {

                    reason.append("\n");

                    reason.append(element.toString());

                }

            }
            Log.e("11111111", reason.toString());
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
       /* if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        LeakCanary.enableDisplayLeakActivity(this);*/
        HttpUtils.getInstance().init(this);
        Thread.setDefaultUncaughtExceptionHandler(restartHandler);
    }
}
