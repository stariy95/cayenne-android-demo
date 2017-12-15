package org.apache.cayenne.demo.android.cayenne;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * Custom URL stream handler for "assets:" url schema, that allows to
 * use Android assets from Cayenne internals.
 */
public class UrlToAssetUtils {

    public static void registerHandler(final AssetManager manager) {
        URL.setURLStreamHandlerFactory(protocol -> "assets".equals(protocol) ? new URLStreamHandler() {
            protected URLConnection openConnection(URL url) throws IOException {
                return new URLConnection(url) {
                    @Override
                    public void connect() throws IOException {
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return manager.open(url.getFile());
                    }
                };
            }
        } : null);
    }

}
