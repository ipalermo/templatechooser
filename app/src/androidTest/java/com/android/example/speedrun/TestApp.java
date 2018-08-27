
package com.android.example.speedrun;

import android.app.Application;

import com.android.example.speedrun.util.CustomTestRunner;

/**
 * We use a separate App for tests to prevent initializing dependency injection.
 *
 * See {@link CustomTestRunner}.
 */
public class TestApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
