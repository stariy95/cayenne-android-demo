package org.apache.cayenne.demo.android.cayenne;

import android.util.Log;

import org.apache.cayenne.resource.Resource;
import org.apache.cayenne.resource.ResourceLocator;
import org.apache.cayenne.resource.URLResource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

/**
 * Resource locator that simply converts configuration name into URL as is,
 * without any modifications or checks.
 * It is relies on {@link UrlToAssetUtils} logic.
 */
public class AssetsResourceLocator implements ResourceLocator {

    @Override
    public Collection<Resource> findResources(String name) {
        try {
            return Collections.singleton(new URLResource(new URL(name)));
        } catch (MalformedURLException e) {
            Log.w("Cayenne", "Unable create URL for resource " + name, e);
            return Collections.emptyList();
        }
    }
}
