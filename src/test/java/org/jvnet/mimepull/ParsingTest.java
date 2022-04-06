/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.jvnet.mimepull;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Jitendra Kotamraju
 */
public class ParsingTest {

    public ParsingTest() {}

    @Test
    public void testMsg() throws Exception {
        InputStream in = getClass().getResourceAsStream("/msg.txt");
        String boundary = "----=_Part_4_910054940.1065629194743";
        MIMEConfig config = new MIMEConfig();
        MIMEMessage mm = new MIMEMessage(in, boundary, config);
        mm.parseAll();
        List<MIMEPart> parts = mm.getAttachments();
        Assert.assertEquals(2, parts.size());
        Assert.assertEquals("139912840220.1065629194743.IBM.WEBSERVICES@ibm-7pr28r4m35k", parts.get(0).getContentId());
        Assert.assertEquals("1351327060508.1065629194423.IBM.WEBSERVICES@ibm-7pr28r4m35k", parts.get(1).getContentId());

        {
        byte[] buf = new byte[8192];
        InputStream part0 = parts.get(0).read();
        int len = part0.read(buf, 0, buf.length);
        String str = new  String(buf, 0, len);
        Assert.assertTrue(str.startsWith("<soapenv:Envelope"));
        Assert.assertTrue(str.endsWith("</soapenv:Envelope>"));
        part0.close();
        }

        {
        InputStream part1 = parts.get(1).read();
        Assert.assertEquals((byte)part1.read(), (byte)0xff);
        Assert.assertEquals((byte)part1.read(), (byte)0xd8);
        //ImageIO.read(part1);
        part1.close();
        }
    }

    @Test
    public void testMsg2() {
        InputStream in = getClass().getResourceAsStream("/msg2.txt");
        String boundary = "----=_Part_1_807283631.1066069460327";
        MIMEConfig config = new MIMEConfig();
        MIMEMessage mm = new MIMEMessage(in, boundary, config);
        mm.parseAll();
        List<MIMEPart> parts = mm.getAttachments();
        Assert.assertEquals(2, parts.size());
        Assert.assertEquals("1071294019496.1066069460327.IBM.WEBSERVICES@ibm-7pr28r4m35k", parts.get(0).getContentId());
        Assert.assertEquals("871169419176.1066069460266.IBM.WEBSERVICES@ibm-7pr28r4m35k", parts.get(1).getContentId());
    }

    @Test
    public void testMessage1() throws Exception {
        InputStream in = getClass().getResourceAsStream("/message1.txt");
        String boundary = "----=_Part_7_10584188.1123489648993";
        MIMEConfig config = new MIMEConfig();
        MIMEMessage mm = new MIMEMessage(in, boundary, config);
        mm.parseAll();
        List<MIMEPart> parts = mm.getAttachments();
        Assert.assertEquals(2, parts.size());
        Assert.assertEquals("soapPart", parts.get(0).getContentId());
        Assert.assertEquals("attachmentPart", parts.get(1).getContentId());

        {
        byte[] buf = new byte[18];
        InputStream part0 = parts.get(0).read();
        int len = part0.read(buf, 0, buf.length);
        String str = new  String(buf, 0, len);
        Assert.assertTrue(str.startsWith("<SOAP-ENV:Envelope"));

        Assert.assertEquals(' ', (byte)part0.read());

        buf = new byte[8192];
        len = part0.read(buf, 0, buf.length);
        str = new  String(buf, 0, len);
        Assert.assertTrue(str.endsWith("</SOAP-ENV:Envelope>"));
        part0.close();
        }

        {
        byte[] buf = new byte[8192];
        InputStream part1 = parts.get(1).read();
        int len = part1.read(buf, 0, buf.length);
        String str = new  String(buf, 0, len);
        Assert.assertTrue(str.startsWith("<?xml version"));
        Assert.assertTrue(str.endsWith("</Envelope>\n"));
        part1.close();
        }
    }

    // Test for MIMEPULL-3 issue.
    @Test
    public void testReadEOF() throws Exception {
        InputStream in = getClass().getResourceAsStream("/message1.txt");
        String boundary = "----=_Part_7_10584188.1123489648993";
        MIMEConfig config = new MIMEConfig();
        MIMEMessage mm = new MIMEMessage(in, boundary, config);
        mm.parseAll();
        List<MIMEPart> parts = mm.getAttachments();
        for (MIMEPart part : parts) {
            testInputStream(part.read());
            testInputStream(part.readOnce());
        }
    }

    @SuppressWarnings("empty-statement")
    private void testInputStream(InputStream is) throws IOException {
        while(is.read() != -1);
        Assert.assertEquals(-1, is.read());    // read() after EOF should return -1
        is.close();
        try {
            is.read();
            Assert.fail("read() after close() should throw IOException");
        } catch (IOException ioe) {
            // expected exception
        }
    }

    @Test
    public void testEmptyPart() throws Exception {
        InputStream in = getClass().getResourceAsStream("/emptypart.txt");
        String boundary = "----=_Part_7_10584188.1123489648993";
        MIMEConfig config = new MIMEConfig();
        MIMEMessage mm = new MIMEMessage(in, boundary, config);
        mm.parseAll();
        List<MIMEPart> parts = mm.getAttachments();
        Assert.assertEquals(2, parts.size());
        Assert.assertEquals("soapPart", parts.get(0).getContentId());
        Assert.assertEquals("attachmentPart", parts.get(1).getContentId());
        {
            InputStream is = parts.get(0).read();
            while(is.read() != -1) {
                Assert.fail("There should be any bytes since this is empty part");
            }
        }
        {
            byte[] buf = new byte[8192];
            InputStream part1 = parts.get(1).read();
            int len = part1.read(buf, 0, buf.length);
            String str = new String(buf, 0, len);
            Assert.assertTrue(str.startsWith("<?xml version"));
            Assert.assertTrue(str.endsWith("</Envelope>\n"));
            part1.close();
        }
    }

    @Test
    public void testNoHeaders() {
        InputStream in = getClass().getResourceAsStream("/noheaders.txt");
        String boundary = "----=_Part_7_10584188.1123489648993";
        MIMEConfig config = new MIMEConfig();
        MIMEMessage mm = new MIMEMessage(in, boundary, config);
        mm.parseAll();
        List<MIMEPart> parts = mm.getAttachments();
        Assert.assertEquals(2, parts.size());
        Assert.assertEquals("0", parts.get(0).getContentId());
        Assert.assertEquals("1", parts.get(1).getContentId());
    }

    @Test
    public void testOneByte() {
        InputStream in = getClass().getResourceAsStream("/onebyte.txt");
        String boundary = "boundary";
        MIMEConfig config = new MIMEConfig();
        MIMEMessage mm = new MIMEMessage(in, boundary, config);
        mm.parseAll();
        List<MIMEPart> parts = mm.getAttachments();
        Assert.assertEquals(2, parts.size());
        Assert.assertEquals("0", parts.get(0).getContentId());
        Assert.assertEquals("1", parts.get(1).getContentId());
    }

    @Test
    public void testBoundaryWhiteSpace() {
        InputStream in = getClass().getResourceAsStream("/boundary-lwsp.txt");
        String boundary = "boundary";
        MIMEConfig config = new MIMEConfig();
        MIMEMessage mm = new MIMEMessage(in, boundary, config);
        mm.parseAll();
        List<MIMEPart> parts = mm.getAttachments();
        Assert.assertEquals(2, parts.size());
        Assert.assertEquals("part1", parts.get(0).getContentId());
        Assert.assertEquals("part2", parts.get(1).getContentId());
    }

    @Test
    public void testBoundaryInBody() {
        InputStream in = getClass().getResourceAsStream("/boundary-in-body.txt");
        String boundary = "boundary";
        MIMEConfig config = new MIMEConfig();
        MIMEMessage mm = new MIMEMessage(in, boundary, config);
        mm.parseAll();
        List<MIMEPart> parts = mm.getAttachments();
        Assert.assertEquals(2, parts.size());
        Assert.assertEquals("part1", parts.get(0).getContentId());
        Assert.assertEquals("part2", parts.get(1).getContentId());
    }

    @Test
    public void testNoClosingBoundary() {

        boolean gotException = false;
        try {
            String fileName = "/msg-no-closing-boundary.txt";
            InputStream in = getClass().getResourceAsStream(fileName);
            Assert.assertNotNull("Failed to load test data from " + fileName, in);
            MIMEConfig config = new MIMEConfig();
            String boundary = "----=_Part_4_910054940.1065629194743";
            MIMEMessage mm = new MIMEMessage(in, boundary, config);
            mm.parseAll();
        } catch (MIMEParsingException e) {
            gotException = true;
            String msg = e.getMessage();
            Assert.assertNotNull(msg);
            Assert.assertTrue(msg.contains("no closing MIME boundary"));
        }
        Assert.assertTrue(gotException);
    }

    @Test
    public void testInvalidClosingBoundary() {

        boolean gotException = false;
        try {
            String fileName = "/msg-invalid-closing-boundary.txt";
            InputStream in = getClass().getResourceAsStream(fileName);
            Assert.assertNotNull("Failed to load test data from " + fileName, in);
            MIMEConfig config = new MIMEConfig();
            String boundary = "----=_Part_4_910054940.1065629194743";
            MIMEMessage mm = new MIMEMessage(in, boundary, config);
            mm.parseAll();
        } catch (MIMEParsingException e) {
            gotException = true;
            String msg = e.getMessage();
            Assert.assertNotNull(msg);
            Assert.assertTrue(msg.contains("no closing MIME boundary"));
        }
        Assert.assertTrue(gotException);
    }

    @Test
    public void testInvalidMimeMessage() {
        final String invalidMessage = "--boundary\n"
                + "Content-Id: part1\n"
                + "\n"
                + "1";
        final String boundary = "boundary";
        final MIMEMessage message = new MIMEMessage(new ByteArrayInputStream(invalidMessage.getBytes()), boundary);

        try {
            message.getAttachments();
            Assert.fail("Given message is un-parseable. An exception should have been raised");
        } catch (final MIMEParsingException expected) {
            final MIMEPart part = message.getPart(0);

            // Check that part is not closed yet.
            Assert.assertFalse("Part should not be closed at this point", part.isClosed());

            message.close();

            // Check that part is not closed yet.
            Assert.assertTrue("Part should be closed by now", part.isClosed());
        }
    }

}
