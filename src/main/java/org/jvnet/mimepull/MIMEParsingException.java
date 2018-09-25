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

/**
 * @author Jitendra Kotamraju
 */

/**
 * The <code>MIMEParsingException</code> class is the base
 * exception class for all MIME message parsing exceptions.
 *
 */

public class MIMEParsingException extends java.lang.RuntimeException {

    /**
     * Constructs a new exception with <code>null</code> as its
     * detail message. The cause is not initialized.
     */
    public MIMEParsingException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail
     * message.  The cause is not initialized.
     *
     * @param message The detail message which is later
     *                retrieved using the getMessage method
     */
    public MIMEParsingException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail
     * message and cause.
     *
     * @param message The detail message which is later retrieved
     *                using the getMessage method
     * @param cause   The cause which is saved for the later
     *                retrieval throw by the getCause method
     */
    public MIMEParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new WebServiceException with the specified cause
     * and a detail message of
     * {@code (cause==null ? null : cause.toString())}
     * (which typically contains the
     * class and detail message of {@code cause}).
     *
     * @param cause The cause which is saved for the later
     *              retrieval throw by the getCause method.
     *              (A {@code null} value is permitted, and
     *              indicates that the cause is nonexistent or
     *              unknown.)
     */
    public MIMEParsingException(Throwable cause) {
        super(cause);
    }

}
