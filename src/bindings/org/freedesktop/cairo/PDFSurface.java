/*
 * PDFSurface.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the library it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" plus the "Classpath Exception" (you may link to this code as a
 * library into other programs provided you don't make a derivation of it).
 * See the LICENCE file for the terms governing usage and redistribution.
 */
package org.freedesktop.cairo;

import java.io.IOException;

/**
 * A Surface that will be rendered to a file in the Portable Document Format.
 * 
 * <p>
 * <i>Cairo's PDF support is still nascent but is improving steadily! Wherever
 * possible graphics drawn in your Context will be rendered in vector form in
 * the PDF; when that is not available the PDF backend will fallback to
 * rendering bitmaps of the desired content.</i>
 * 
 * @author Andrew Cowie
 * @since 4.0.10
 */
public class PDFSurface extends Surface
{
    protected PDFSurface(long pointer) {
        super(pointer);
    }

    /**
     * Create a new PDFSurface, supplying the file you want to write to and
     * the size of the page you are creating. The <code>width</code> and
     * <code>height</code> parameters are specified in <i>points</i>, where 1
     * point equals 1/72<sup>nd</sup> of an inch.
     * 
     * @throws IOException
     *             If you do not have write permissions on the given file.
     * @since 4.0.10
     */
    public PDFSurface(String filename, int width, int height) throws IOException {
        super(CairoSurface.createSurfacePdf(filename, width, height));
        final Status status;

        status = CairoSurface.status(this);
        if (status == Status.WRITE_ERROR) {
            throw new IOException("Cairo reports it cannot open " + filename + " for writing");
        }
        checkStatus(status);
    }

    public void showPage() {
        CairoSurface.showPage(this);
        checkStatus();
    }

    public void copyPage() {
        CairoSurface.copyPage(this);
        checkStatus();
    }
}