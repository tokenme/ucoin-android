package com.ucoin.ucoinnew.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

public class App extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);
        sContext = getApplicationContext();

        PrettyFormatStrategy strategy = PrettyFormatStrategy.newBuilder()
            .logStrategy(new LogCatStrategy())
            .tag("PLOG")
            .build();
        Logger.addLogAdapter(new AndroidLogAdapter(strategy));
    }

    public static Context getInstance() {
        return sContext;
    }

    public class LogCatStrategy implements LogStrategy {

        @Override
        public void log(int priority, String tag, String message) {
            Log.println(priority, randomKey() + tag, message);
        }

        private int last;

        private String randomKey() {
            int random = (int) (10 * Math.random());
            if (random == last) {
                random = (random + 1) % 10;
            }
            last = random;
            return String.valueOf(random);
        }
    }

}

