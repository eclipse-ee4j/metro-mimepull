/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.jvnet.mimepull;

import java.util.concurrent.ScheduledExecutorService;

public abstract class CleanUpExecutorFactory {
    private static final String DEFAULT_PROPERTY_NAME = CleanUpExecutorFactory.class
            .getName();

    protected CleanUpExecutorFactory() {
    }

    public static CleanUpExecutorFactory newInstance() {
        try {
            return (CleanUpExecutorFactory) FactoryFinder.find(DEFAULT_PROPERTY_NAME);
        } catch (RuntimeException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public abstract ScheduledExecutorService getScheduledExecutorService();
}
