package org.drulabs.petescafe.app;

import android.app.Application;

import org.drulabs.petescafe.di.AppComponent;
import org.drulabs.petescafe.di.AppModule;
import org.drulabs.petescafe.di.DaggerAppComponent;

public class CafeApp extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
