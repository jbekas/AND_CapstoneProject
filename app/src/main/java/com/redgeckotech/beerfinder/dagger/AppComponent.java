package com.redgeckotech.beerfinder.dagger;

import android.content.Context;

import com.redgeckotech.beerfinder.BreweryInfoApplication;
import com.redgeckotech.beerfinder.view.BaseActivity;
import com.redgeckotech.beerfinder.view.MainActivity;
import com.redgeckotech.beerfinder.data.UpdaterService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton // Constraints this component to one-per-application or unscoped bindings.
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(BreweryInfoApplication application);

    void inject(UpdaterService service);

    void inject(BaseActivity activity);
    void inject(MainActivity activity);

    //Exposed to sub-graphs.
    Context context();
}