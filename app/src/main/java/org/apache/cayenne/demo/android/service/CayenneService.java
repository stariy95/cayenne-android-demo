package org.apache.cayenne.demo.android.service;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;

/**
 * Cayenne service interface for Cayenne-Android integration demo.
 */
public interface CayenneService {

    /**
     * @return Cayenne runtime
     */
    ServerRuntime getRuntime();

    /**
     * @return shared context instance
     */
    ObjectContext sharedContext();

    /**
     * @return new Object context
     */
    ObjectContext newContext();

}
