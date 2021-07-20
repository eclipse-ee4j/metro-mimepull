/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/* FROM mail.jar */
package org.jvnet.mimepull;

import java.io.IOException;

/**
 * A special IOException that indicates a failure to decode data due
 * to an error in the formatting of the data.  This allows applications
 * to distinguish decoding errors from other I/O errors.
 *
 * @author Bill Shannon
 */

public final class DecodingException extends IOException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a {@code DecodingException} with the specified detail message.
     *
     * @param message
     *        The detail message
     */
    public DecodingException(String message) {
	    super(message);
    }
}
