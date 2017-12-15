package org.apache.cayenne.demo.android.service;

import android.content.Context;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.demo.android.cayenne.AssetsResourceLocator;
import org.apache.cayenne.demo.android.cayenne.UrlToAssetUtils;
import org.apache.cayenne.resource.ResourceLocator;

/**
 * Default implementation of Cayenne service.
 */
public class DefaultCayenneService implements CayenneService {

    private ServerRuntime runtime;

    private ObjectContext sharedContext;

    DefaultCayenneService(Context context) {
        //TODO: As a side effect it installs "assets:" url schema handler, this may be not optimal solution.
        UrlToAssetUtils.registerHandler(context.getAssets());

        // Build Cayenne runtime with custom resource locator, to be able load it from Android assets.
        runtime = ServerRuntime.builder()
                .addModule(binder -> binder.bind(ResourceLocator.class).to(AssetsResourceLocator.class))
                .addConfig("assets:cayenne-project.xml")
                .build();
        sharedContext = runtime.newContext();
    }

    @Override
    public ServerRuntime getRuntime() {
        return runtime;
    }

    @Override
    public ObjectContext sharedContext() {
        return sharedContext;
    }

    @Override
    public ObjectContext newContext() {
        return runtime.newContext();
    }
}
