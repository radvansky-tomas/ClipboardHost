package com.radvansky.clipboard;

import android.app.Application;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;
import com.orhanobut.hawk.Storage;

/**
 * Created by tomasradvansky on 11/10/2016.
 */

public class ClipboardApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify
                .with(new FontAwesomeModule());
        Hawk.init(this)
                .setEncryption(new NoEncryption())
                .build();
    }
}