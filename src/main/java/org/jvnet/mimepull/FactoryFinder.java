/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.jvnet.mimepull;

import java.util.Iterator;
import java.util.ServiceLoader;

class FactoryFinder {

    static <T> T find(Class<T> factoryId) throws ClassNotFoundException, ReflectiveOperationException {
        String systemProp = System.getProperty(factoryId.getName());
        if (systemProp != null) {
            return newInstance(factoryId, systemProp);
        }

        Iterator<T> loader = ServiceLoader.load(factoryId).iterator();
        if (loader.hasNext()) {
            return loader.next();
        }

        return null;
    }

    static <T> T newInstance(Class<T> cls, String className) throws ClassNotFoundException, ReflectiveOperationException {
        @SuppressWarnings("unchecked")
        Class<T> providerClass = (Class<T>) FactoryFinder.class.getClassLoader().loadClass(className);
        T instance = providerClass.getConstructor().newInstance();
        return instance;
    }

}
