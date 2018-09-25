/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.jvnet.mimepull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

/**
 * @author Michal Gajdos (michal.gajdos at oracle.com)
 */
public class FileTest extends TestCase {

    private static final String BOUNDARY = "boundary";
    private static final int PART_SIZE = 4 * 8192;

    public void testMoveTo() throws Exception {
        // Mime message.
        final MIMEConfig config = new MIMEConfig();
        config.setMemoryThreshold(4096);
        final MIMEMessage message = new MIMEMessage(getInputStream(PART_SIZE), BOUNDARY, config);
        final MIMEPart part = message.getAttachments().get(0);

        // Temp file to move MIME temp file to.
        final File tempFile = File.createTempFile("ship", "it");
        tempFile.deleteOnExit();

        // Move the file.
        part.moveTo(tempFile);

        // Check the file length.
        assertEquals("Destination temp file doesn't seem to have expected size.", PART_SIZE, tempFile.length());

    }

    /**
     * partA's content ABC...ZAB...
     * partB's content BCD...ZAB...
     * partC's content CDE...ZAB...
     */
    private InputStream getInputStream(final int size) {
        final byte[] data = (
            "--boundary\r\n"+
            "Content-Type: text/plain\r\n"+
            "Content-Id: partA\r\n\r\n"+
            "1\r\n"+
            "--boundary\r\n"+
            "Content-Type: text/plain\r\n"+
            "Content-ID: partB\r\n\r\n"+
            "2\r\n"+
            "--boundary\r\n"+
            "Content-Type: text/plain\r\n"+
            "Content-ID: partC\r\n\r\n"+
            "3\r\n"+
            "--boundary--").getBytes();

        return new InputStream() {
            int i, j;

            @Override
            public int read() throws IOException {
                if (i >= data.length) {
                    return -1;
                } else if (data[i] == '1' || data[i] == '2' || data[i] == '3') {
                    if (j < size) {
                        int partNo = data[i] - '1';
                        return (byte) ('A' + (partNo + j++) % 26);
                    } else {
                        j = 0;
                        i++;
                    }
                }
                return data[i++];
            }
        };
    }

    public static void main(String[] args) throws Exception {
        new FileTest().testMoveTo();
    }
}
