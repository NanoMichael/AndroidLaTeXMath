package io.nano.tex.test;

import android.app.Application;

import io.nano.tex.LaTeX;

/**
 * Created by nano on 18-11-12
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LaTeX.instance().init(this);
    }
}
