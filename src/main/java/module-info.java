/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * MIME streaming extension for Java.
 */
module org.jvnet.mimepull {
    requires java.logging;

    exports org.jvnet.mimepull;

    uses org.jvnet.mimepull.CleanUpExecutorFactory;
}
