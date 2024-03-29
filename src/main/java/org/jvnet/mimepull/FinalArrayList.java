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

import java.util.ArrayList;
import java.util.Collection;

/**
 * {@link java.util.ArrayList} with the final keyword.
 *
 * <p>
 * This gives HotSpot a better hint that all methods can be inlined.
 *
 * @author Kohsuke Kawaguchi
 */
final class FinalArrayList<T> extends ArrayList<T> {

    private static final long serialVersionUID = 7996571518730043311L;

    public FinalArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public FinalArrayList() {
    }

    public FinalArrayList(Collection<? extends T> ts) {
        super(ts);
    }
}

