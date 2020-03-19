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
 * Mimepull - MIME streaming extension for Java.
 * <br>
 * <br>
 * <a id="properties"><strong>Properties</strong></a>
 * <p>
 * The following properties are supported by Mimepull. These must be set as System properties. The names, types, defaults,
 * and semantics of these properties may change in future releases.
 * </p>
 * <table border="1">
 * <caption>Mimepull System properties</caption>
 * <tr>
 *  <th>Name</th>
 *  <th>Type</th>
 *  <th>Description</th>
 * </tr>
 *
 * <tr>
 *  <td><a id="mail.mime.base64.ignoreerrors">mail.mime.base64.ignoreerrors</a></td>
 *  <td>boolean</td>
 *  <td>If set to {@code true}, the BASE64 decoder will ignore errors in the encoded data, returning EOF. This may be useful
 * when dealing with improperly encoded messages that contain extraneous data at the end of the encoded stream. Note however
 * that errors anywhere in the stream will cause the decoder to stop decoding so this should be used with extreme caution.
 * The default is false.</td>
 * </tr>
 *
 * <tr>
 *  <td><a id="mail.mime.ignoreunknownencoding">mail.mime.ignoreunknownencoding</a></td>
 *  <td>boolean</td>
 *  <td>If set to {@code true}, an unknown value in the {@code Content-Transfer-Encoding} header will be ignored when reading a message
 * and an encoding of "8bit" will be assumed. If set to {@code false}, an exception is thrown for an unknown encoding value.
 * The default is false.</td>
 * </tr>
 *
 * <tr>
 *  <td><a id="mail.mime.uudecode.ignoreerrors">mail.mime.uudecode.ignoreerrors</a></td>
 *  <td>boolean</td>
 *  <td>If set to {@code true}, errors in the encoded format of a uuencoded document will be ignored when reading a message part.
 * If set to {@code false}, an exception is thrown for an incorrectly encoded message part. The default is false.</td>
 * </tr>
 *
 * <tr>
 *  <td><a id="mail.mime.uudecode.ignoremissingbeginend">mail.mime.uudecode.ignoremissingbeginend</a></td>
 *  <td>boolean</td>
 *  <td>If set to {@code true}, a missing "beging" or "end" line in a uuencoded document will be ignored when reading a message part.
 * If set to {@code false}, an exception is thrown for a uuencoded message part without the required "begin" and "end" lines.
 * The default is false.</td>
 * </tr>
 *
 * <tr>
 *  <td><a id="org.jvnet.mimepull.CleanUpExecutorFactory">org.jvnet.mimepull.CleanUpExecutorFactory</a></td>
 *  <td>string</td>
 *  <td>The {@code org.jvnet.mimepull.CleanUpExecutorFactory} property defines fully qualified name for a class that implements
 * {@link org.jvnet.mimepull.CleanUpExecutorFactory}. This property works system wide and overrides whatever is set by the application.
 * There is no default implementation.</td>
 * </tr>
 *
 * <tr>
 *  <td><a id="org.jvnet.mimepull.delay">org.jvnet.mimepull.delay</a></td>
 *  <td>integer</td>
 *  <td>Defines the time to delay first execution of the clean up task run by {@code java.util.concurrent.ScheduledExecutorService}
 * obtained through {@link org.jvnet.mimepull.CleanUpExecutorFactory} and the delay between the termination of one execution
 * and the commencement of the next in seconds. The default is 10 seconds.</td>
 * </tr>
 *
 * <tr>
 *  <td><a id="org.jvnet.mimepull.deletetemponexit">org.jvnet.mimepull.deletetemponexit</a></td>
 *  <td>boolean</td>
 *  <td>If set to {@code true}, temporary files will be marked as {@code deleteOnExit}. This may be typically needed if the application
 * does not properly close {@link org.jvnet.mimepull.MIMEMessage}. Note however that setting this to {@code true} permanently adds an entry
 * to a list of files to be deleted on JVM shutdown leading to a significant memory leak in long running server applications eventually,
 * so this should be used with extreme caution. The default is false.</td>
 * </tr>
 *
 * </table>
 *
 */
module org.jvnet.mimepull {
    requires java.logging;

    exports org.jvnet.mimepull;

    uses org.jvnet.mimepull.CleanUpExecutorFactory;
}
