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

import java.io.InputStream;
import java.util.List;

/**
 * @author Martin Grebac
 */
public class QuotedTest {

    public QuotedTest() {}

    @Test
    public void testMsg() throws Exception {
        InputStream in = getClass().getResourceAsStream("/quoted.txt");
        String boundary = "----=_Part_16_799571960.1350659465464";
        MIMEConfig config = new MIMEConfig();
        MIMEMessage mm = new MIMEMessage(in, boundary , config);
        mm.parseAll();

        List<MIMEPart> parts = mm.getAttachments();
        MIMEPart part1 = parts.get(1);

        Assert.assertTrue(part1.getContentTransferEncoding().equals("quoted-printable"));

        InputStream is = part1.readOnce();
        byte[] buf = new byte[8192];
        int len = is.read(buf, 0, buf.length);
        String str = new  String(buf, 0, len);

        Assert.assertFalse(str.contains("=3D"));

        part1.close();
    }

}
