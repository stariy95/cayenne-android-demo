package org.apache.cayenne.demo.android.service;

import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;

import org.apache.cayenne.demo.android.activity.ArtistActivity;
import org.apache.cayenne.demo.android.activity.MainActivity;
import org.apache.cayenne.demo.android.cayenne.UrlToAssetUtils;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import dagger.multibindings.IntoMap;

/**
 * Dagger v2 App modules descriptions
 */
@Module(includes = {AndroidSupportInjectionModule.class})
public class AppModule {

    /**
     * Dagger DI provider method for {@link CayenneService}
     *
     * @param context
     *          android context used by {@link DefaultCayenneService} service internally
     * @return Cayenne service implementation
     */
    @Provides
    @Singleton
    public CayenneService cayenneService(Context context) {
        // TODO: Cayenne temp ID functionality uses Localhost address,
        // TODO: i.e. network activity in terms of Android.
        // TODO: Without explicit permission here Cayenne will permanently fail to create new objects.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // This installs "assets:" url schema handler, this may be not optimal solution.
        UrlToAssetUtils.registerHandler(context.getAssets());

        return new DefaultCayenneService(context);
    }

    @Module(subcomponents = AppComponent.MainActivitySubcomponent.class)
    public abstract class MainActivityModule {
        @Binds
        @IntoMap
        @ActivityKey(MainActivity.class)
        abstract AndroidInjector.Factory<? extends Activity> bindMainActivityInjectorFactory(AppComponent.MainActivitySubcomponent.Builder builder);
    }

    @Module(subcomponents = AppComponent.ArtistActivitySubcomponent.class)
    public abstract class ArtistActivityModule {
        @Binds
        @IntoMap
        @ActivityKey(ArtistActivity.class)
        abstract AndroidInjector.Factory<? extends Activity> bindArtistActivityInjectorFactory(AppComponent.ArtistActivitySubcomponent.Builder builder);
    }
}
