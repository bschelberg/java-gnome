/*
 * java-gnome, a UI library for writing GTK and GNOME programs from Java!
 *
 * Copyright © 2008-2010 Operational Dynamics Consulting, Pty Ltd
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License version
 * 2 ("GPL") as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GPL for more details.
 *
 * You should have received a copy of the GPL along with this program. If not,
 * see http://www.gnu.org/licenses/. The authors of this program may be
 * contacted through http://java-gnome.sourceforge.net/.
 */
package org.freedesktop.cairo;

import org.gnome.gtk.Gtk;

/**
 * @author Andrew Cowie
 */
public class SnapshotMatrixScale extends SnapshotMatrix
{
    public SnapshotMatrixScale() {
        super("scale");
    }

    protected void draw(Context cr) {
        final Matrix matrix;

        super.drawAxis(cr);
        super.drawBox(cr, true);

        matrix = new Matrix();
        matrix.scale(-0.8, 1.6);
        cr.transform(matrix);

        super.drawBox(cr, false);
    }

    public static void main(String[] args) {
        Gtk.init(args);
        runExample(new SnapshotMatrixScale());
        Gtk.main();
    }
}
