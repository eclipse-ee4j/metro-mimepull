/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.jvnet.mimepull;

import junit.framework.TestCase;
import junit.framework.AssertionFailedError;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * @author Jitendra Kotamraju
 */
public class StreamTest extends TestCase {

    public void testOrderRead() throws Exception {
        testOrderRead(123456789);
    }

    public void testPartSize9195() throws Exception {
        testOrderRead(9195);
    }

    public void testPartSize9196() throws Exception {
        testOrderRead(9196);
    }

    public void testAllPartSizes() throws Exception {
        for (int size = 0; size < 50000; size++) {
            if (size %1000 == 0) {
                System.out.println("Trying for the size="+size);
            }
            try {
                testOrderRead(size);
            } catch (AssertionFailedError e) {
                System.out.println("Failed for part length " + size + " bytes");
                throw e;
            }
        }
    }

    public void testAllPartSizesForBufferedStream() throws Exception {
        for (int size = 0; size < 50000; size++) {
            if (size %1000 == 0) {
                System.out.println("Trying for the size="+size);
            }
            try {
                testOrderRead(size, getBufferedInputStream(size));
            } catch (AssertionFailedError e) {
                System.out.println("Failed for part length " + size + " bytes");
                throw e;
            }
        }
    }

    private void testOrderRead(int size, InputStream is) throws Exception {
        String boundary = "boundary";
        MIMEConfig config = new MIMEConfig();
        MIMEMessage mm = new MIMEMessage(is, boundary , config);

        MIMEPart partA = mm.getPart("partA");
        verifyPart(partA.read(), 0, size);
        partA.close();

        MIMEPart partB = mm.getPart("partB");
        verifyPart(partB.read(), 1, size);
        partB.close();

        MIMEPart partC = mm.getPart("partC");
        verifyPart(partC.read(), 2, size);
        partC.close();
    }

    private void testOrderRead(int size) throws Exception {
        testOrderRead(size, getInputStream(size));
    }

    // Parts are accessed in order. The data is accessed using readOnce()
    // and there shouldn't be any data stored in temp files.
    public void testOrderReadOnce() throws Exception {
        String boundary = "boundary";
        int size = 123456789;
        MIMEConfig config = new MIMEConfig();
        MIMEMessage mm = new MIMEMessage(getInputStream(size), boundary , config);

        MIMEPart partA = mm.getPart("partA");
        verifyPart(partA.readOnce(), 0, size);
        partA.close();

        MIMEPart partB = mm.getPart("partB");
        verifyPart(partB.readOnce(), 1, size);
        partB.close();

        MIMEPart partC = mm.getPart("partC");
        verifyPart(partC.readOnce(), 2, size);
        partC.close();
    }

    // partB, partA, partC are accessed in that order. Then partA should
    // go to disk. partB, and partC are accessed from in-memory
    public void testOutofOrderRead() throws Exception {
        String boundary = "boundary";
        int size = 12345678;
        MIMEConfig config = new MIMEConfig();
        MIMEMessage mm = new MIMEMessage(getInputStream(size), boundary , config);

        MIMEPart partA = mm.getPart("partA");
        MIMEPart partB = mm.getPart("partB");
        MIMEPart partC = mm.getPart("partC");

        verifyPart(partB.read(), 1, size);
        verifyPart(partA.read(), 0, size);
        verifyPart(partC.read(), 2, size);

        partA.close();
        partB.close();
        partC.close();
    }

    // partB, partA, partC are accessed in that order. Then partA should
    // go to disk. partB, and partC are accessed from in-memory
    public void testOutofOrderReadOnce() throws Exception {
        String boundary = "boundary";
        int size = 12345678;
        MIMEConfig config = new MIMEConfig();
        MIMEMessage mm = new MIMEMessage(getInputStream(size), boundary , config);

        MIMEPart partA = mm.getPart("partA");
        MIMEPart partB = mm.getPart("partB");
        MIMEPart partC = mm.getPart("partC");

        verifyPart(partB.readOnce(), 1, size);
        partB.close();
        verifyPart(partA.readOnce(), 0, size);
        partA.close();
        verifyPart(partC.readOnce(), 2, size);
        partC.close();
    }

    // MIMEPart.read() is called twice
    public void testOutofOrderMultipleRead() throws Exception {
        String boundary = "boundary";
        final int size = 12345678;
        MIMEConfig config = new MIMEConfig();
        MIMEMessage mm = new MIMEMessage(getInputStream(size), boundary , config);

        final MIMEPart partA = mm.getPart("partA");
        MIMEPart partB = mm.getPart("partB");
        MIMEPart partC = mm.getPart("partC");

        verifyPart(partB.readOnce(), 1, size);
        Thread[] threads = new Thread[2];

        for(int i=0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    try {
                        verifyPart(partA.read(), 0, size);
                    } catch(Exception e) {
                        fail();
                    }
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        verifyPart(partC.readOnce(), 2, size);
        for (Thread thread : threads) {
            thread.join();
        }

        partA.close();
        partB.close();
        partC.close();
    }


    /*
    private void verifyPart(InputStream in, int partNo, int size) throws Exception {
        int i = 0;
        int ch;
        while((ch=in.read()) != -1) {
            assertEquals((byte)('A'+(partNo+i++)%26), (byte)ch);
        }
        assertEquals(size, i);
        in.close();
    }
    */

    private void verifyPart(InputStream in, int partNo, int size) throws Exception {
        byte[] buf = new byte[8192];
        int total = 0;
        int len;
        while((len=in.read(buf, 0, buf.length)) != -1) {
            for(int i=0; i < len; i++) {
                assertEquals((byte)('A'+(partNo+total+i)%26), buf[i]);
            }
            total += len;
        }
        assertEquals(size, total);
        in.close();
    }

    /*
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
                        int partNo = data[i]-'1';
                        return (byte)('A'+(partNo+j++)%26);
                    } else {
                        j = 0; i++;
                    }
                }
                return data[i++];
            }
        };

    }

    private InputStream getBufferedInputStream(int size) {
        return new BufferedInputStream(getInputStream(size));
    }

}
