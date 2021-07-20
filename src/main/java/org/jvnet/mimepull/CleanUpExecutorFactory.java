/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.jvnet.mimepull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A service to clean up created resources.
 */
public abstract class CleanUpExecutorFactory {

    private static final Logger LOGGER = Logger.getLogger(CleanUpExecutorFactory.class.getName());

    /**
     * Default constructor
     */
    protected CleanUpExecutorFactory() {
    }

    /**
     * Lookup CleanUpExecutorFactory using ServiceLoader
     * @return CleanUpExecutorFactory or null if not found
     */
    public static CleanUpExecutorFactory newInstance() {
        try {
            return FactoryFinder.find(CleanUpExecutorFactory.class);
        } catch (ReflectiveOperationException e) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Not using " + CleanUpExecutorFactory.class.getName(), e);
            }
            return null;
        }
    }

    /**
     * Return ScheduledExecutorService
     * @return ScheduledExecutorService
     */
    public abstract ScheduledExecutorService getScheduledExecutorService();
}
