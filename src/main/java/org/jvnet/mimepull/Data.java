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
 * @author Jitendra Kotamraju
 */
interface Data {

    /**
     * size of the chunk given by the parser
     *
     * @return size of the chunk
     */
    int size();

    /**
     * TODO: should the return type be ByteBuffer ??
     * Return part's partial data. The data is read only.
     *
     * @return a byte array which contains {#size()} bytes. The returned
     *         array may be larger than {#size()} bytes and contains data
     *         from offset 0.
     */
    byte[] read();

    /**
     * Write this partial data to a file
     *
     * @param file to which the data needs to be written
     * @return file pointer before the write operation(at which the data is
     *         written from)
     */
    long writeTo(DataFile file);

    /**
     * Factory method to create a Data. The implementation could
     * be file based one or memory based one.
     *
     * @param dataHead start of the linked list of data objects
     * @param buf contains partial content for a part
     * @return Data
     */
    Data createNext(DataHead dataHead, ByteBuffer buf);
}
