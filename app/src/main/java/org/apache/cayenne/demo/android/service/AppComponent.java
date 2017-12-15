package org.apache.cayenne.demo.android.service;

import android.content.Context;

import org.apache.cayenne.demo.android.CayenneApp;
import org.apache.cayenne.demo.android.activity.ArtistActivity;
import org.apache.cayenne.demo.android.activity.MainActivity;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Dagger v2 App and Activities components descriptions
 */
@Singleton
@Component(modules = {
        AppModule.class,
        AppModule.MainActivityModule.class,
        AppModule.ArtistActivityModule.class
})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder context(Context context);
        AppComponent build();
    }

    @Subcomponent()
    interface MainActivitySubcomponent extends AndroidInjector<MainActivity> {
        @Subcomponent.Builder
        abstract class Builder extends AndroidInjector.Builder<MainActivity> {}
    }

    @Subcomponent()
    interface ArtistActivitySubcomponent extends AndroidInjector<ArtistActivity> {
        @Subcomponent.Builder
        abstract class Builder extends AndroidInjector.Builder<ArtistActivity> {}
    }

    void inject(CayenneApp app);
}
