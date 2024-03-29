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

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents an attachment part in a MIME message. MIME message parsing is done
 * lazily using a pull parser, so the part may not have all the data. {@link #read}
 * and {@link #readOnce} may trigger the actual parsing the message. In fact,
 * parsing of an attachment part may be triggered by calling {@link #read} methods
 * on some other attachment parts. All this happens behind the scenes so the
 * application developer need not worry about these details.
 *
 * @author Jitendra Kotamraju, Martin Grebac
 */
public class MIMEPart implements Closeable {

    private static final Logger LOGGER = Logger.getLogger(MIMEPart.class.getName());

    private volatile boolean closed;
    private volatile InternetHeaders headers;
    private volatile String contentId;
    private String contentType;
    private String contentTransferEncoding;

    volatile boolean parsed;    // part is parsed or not
    final MIMEMessage msg;
    private final DataHead dataHead;

    private final Object lock = new Object();

    MIMEPart(MIMEMessage msg) {
        this.msg = msg;
        this.dataHead = new DataHead(this);
    }

    MIMEPart(MIMEMessage msg, String contentId) {
        this(msg);
        this.contentId = contentId;
    }

    /**
     * Can get the attachment part's content multiple times. That means
     * the full content needs to be there in memory or on the file system.
     * Calling this method would trigger parsing for the part's data. So
     * do not call this unless it is required(otherwise, just wrap MIMEPart
     * into a object that returns InputStream for e.g DataHandler)
     *
     * @return data for the part's content
     */
    public InputStream read() {
        InputStream is = null;
        try {
            is = MimeUtility.decode(dataHead.read(), contentTransferEncoding);
        } catch (DecodingException ex) { //ignore
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
        return is;
    }

    /**
     * Cleans up any resources that are held by this part (for e.g. deletes
     * the temp file that is used to serve this part's content). After
     * calling this, one shouldn't call {@link #read()} or {@link #readOnce()}
     */
    @Override
    public void close() {
        if (!closed) {
            synchronized (lock) {
                if (!closed) {
                    dataHead.close();
                    closed = true;
                }
            }
        }
    }

    /**
     * Can get the attachment part's content only once. The content
     * will be lost after the method. Content data is not be stored
     * on the file system or is not kept in the memory for the
     * following case:
     *   - Attachement parts contents are accessed sequentially
     *
     * In general, take advantage of this when the data is used only
     * once.
     *
     * @return data for the part's content
     */
    public InputStream readOnce() {
        InputStream is = null;
        try {
            is = MimeUtility.decode(dataHead.readOnce(), contentTransferEncoding);
        } catch (DecodingException ex) { //ignore
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
        return is;
    }

    /**
     * Send the content to the File
     * @param f file to store the content
     */
    public void moveTo(File f) {
        dataHead.moveTo(f);
    }

    /**
     * Returns Content-ID MIME header for this attachment part
     *
     * @return Content-ID of the part
     */
    public String getContentId() {
        if (contentId == null) {
            getHeaders();
        }
        return contentId;
    }

    /**
     * Returns Content-Transfer-Encoding MIME header for this attachment part
     *
     * @return Content-Transfer-Encoding of the part
     */
    public String getContentTransferEncoding() {
        if (contentTransferEncoding == null) {
            getHeaders();
        }
        return contentTransferEncoding;
    }

    /**
     * Returns Content-Type MIME header for this attachment part
     *
     * @return Content-Type of the part
     */
    public String getContentType() {
        if (contentType == null) {
            getHeaders();
        }
        return contentType;
    }

    private void getHeaders() {
        // Trigger parsing for the part headers
        while(headers == null) {
            if (!msg.makeProgress()) {
                if (headers == null) {
                    throw new IllegalStateException("Internal Error. Didn't get Headers even after complete parsing.");
                }
            }
        }
    }

    /**
     * Return all the values for the specified header.
     * Returns <code>null</code> if no headers with the
     * specified name exist.
     *
     * @param	name header name
     * @return	list of header values, or null if none
     */
    public List<String> getHeader(String name) {
        getHeaders();
        assert headers != null;
        return headers.getHeader(name);
    }

    /**
     * Return all the headers
     *
     * @return list of Header objects
     */
    public List<? extends Header> getAllHeaders() {
        getHeaders();
        assert headers != null;
        return headers.getAllHeaders();
    }

    /**
     * Callback to set headers
     *
     * @param headers MIME headers for the part
     */
    void setHeaders(InternetHeaders headers) {
        this.headers = headers;
        List<String> ct = getHeader("Content-Type");
        this.contentType = (ct == null) ? "application/octet-stream" : ct.get(0);
        List<String> cte = getHeader("Content-Transfer-Encoding");
        this.contentTransferEncoding = (cte == null) ? "binary" : cte.get(0);
    }

    /**
     * Callback to notify that there is a partial content for the part
     *
     * @param buf content data for the part
     */
    void addBody(ByteBuffer buf) {
        dataHead.addBody(buf);
    }

    /**
     * Callback to indicate that parsing is done for this part
     * (no more update events for this part)
     */
    void doneParsing() {
        parsed = true;
        dataHead.doneParsing();
    }

    /**
     * Callback to set Content-ID for this part
     * @param cid Content-ID of the part
     */
    void setContentId(String cid) {
        this.contentId = cid;
    }

    /**
     * Callback to set Content-Transfer-Encoding for this part
     * @param cte Content-Transfer-Encoding of the part
     */
    void setContentTransferEncoding(String cte) {
        this.contentTransferEncoding = cte;
    }

    /**
     * Return {@code true} if this part has already been closed, {@code false} otherwise.
     *
     * @return {@code true} if this part has already been closed, {@code false} otherwise.
     */
    public boolean isClosed() {
        return closed;
    }

    @Override
    public String toString() {
        return "Part="+contentId+":"+contentTransferEncoding;
    }

}
