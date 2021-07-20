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

import java.nio.ByteBuffer;

/**
 * @author Kohsuke Kawaguchi
 */
final class Chunk {
    volatile Chunk next;
    volatile Data data;

    public Chunk(Data data) {
        this.data = data;
    }

    /**
     * Creates a new chunk and adds to linked list.
     *
     * @param dataHead of the linked list
     * @param buf MIME part partial data
     * @return created chunk
     */
    public Chunk createNext(DataHead dataHead, ByteBuffer buf) {
        return next = new Chunk(data.createNext(dataHead, buf));
    }
}
