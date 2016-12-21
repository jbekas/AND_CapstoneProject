package com.redgeckotech.beerfinder;


import android.app.Application;
import android.content.Intent;

import com.redgeckotech.beerfinder.dagger.AppComponent;
import com.redgeckotech.beerfinder.dagger.AppModule;
import com.redgeckotech.beerfinder.dagger.DaggerAppComponent;
import com.redgeckotech.beerfinder.data.UpdaterService;

import timber.log.Timber;

public class BreweryInfoApplication extends Application {

    private AppComponent applicationComponent;

    @Override
    public void onCreate() {

        super.onCreate();

        initializeInjector();
        getApplicationComponent().inject(this);

        Timber.plant(new Timber.DebugTree());

        startService(new Intent(this, UpdaterService.class));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Dagger2
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public void initializeInjector() {
        this.applicationComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getApplicationComponent() {
        return this.applicationComponent;
    }

}

