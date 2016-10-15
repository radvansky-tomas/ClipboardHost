package com.radvansky.clipboard;

import android.app.Application;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

/**
 * Created by tomasradvansky on 11/10/2016.
 */

public class ClipboardApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify
                .with(new FontAwesomeModule());
    }
}