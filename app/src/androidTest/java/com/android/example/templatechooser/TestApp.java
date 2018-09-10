
package com.android.example.templatechooser;

import android.app.Application;

import com.android.example.templatechooser.util.CustomTestRunner;

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
