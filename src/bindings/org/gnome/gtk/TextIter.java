/*
 * TextIter.java
 *
 * Copyright (c) 2007-2008 Operational Dynamics Consulting Pty Ltd, and Others
 *
 * The code in this file, and the library it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" plus the "Classpath Exception" (you may link to this code as a
 * library into other programs provided you don't make a derivation of it).
 * See the LICENCE file for the terms governing usage and redistribution.
 */
package org.gnome.gtk;

import org.gnome.glib.Boxed;

/*
 * A TextIter defines a position inside a {@link TextBuffer}. 
 * TextIters are invalidated when the buffer contents change in a way that
 * affects the number of characters in the buffer. 
 * 
 * @since 4.0.8
 * @author Stefan Prelle
 */
public final class TextIter extends Boxed
{
    private TextBuffer buffer;

    protected TextIter(long pointer) {
        super(pointer);
    }

    /**
     * Allocate a blank TextIter structure. This is done by declaring one
     * locally, copying it, and returning the pointer to the copy.
     * 
     * <p>
     * <b>For use by bindings hackers only!</b>
     */
    TextIter(TextBuffer buffer) {
        super(GtkTextIterOverride.createTextIter());

        this.buffer = buffer;
    }

    void setBuffer(TextBuffer buffer) {
        this.buffer = buffer;
    }

    /**
     * Returns the {@link TextBuffer} this iterator is associated with.
     */
    TextBuffer getBuffer() {
        if (buffer == null) {
            throw new IllegalStateException(
                    "\nSorry, this TextIter doesn't have its internal reference to its parent TextBuffer set");
        }
        return buffer;
    }

    protected void release() {
        GtkTextIter.free(this);
    }

    /**
     * Creates an independent copy of the iterator.
     * 
     * <p>
     * <i>This is basically the same as a clone() on normal Java object would
     * do, but in this case passed on to the native GTK layer.</i>
     * 
     * @since 4.0.8
     */
    public TextIter copy() {
        return GtkTextIter.copy(this);
    }

    /**
     * Returns the character offset of an iterator. Each character in a
     * TextBuffer has an offset, starting with 0 for the first character in
     * the buffer. Use
     * {@link TextBuffer.getIterAtOffset(int) getIterAtOffset()} to convert an
     * offset back into an iterator.
     * 
     * @return A character offset from the start of the TextBuffer.
     * @since 4.0.8
     */
    public int getOffset() {
        return GtkTextIter.getOffset(this);
    }

    /**
     * Sets the iterator to a character offset within the entire TextBuffer,
     * starting with 0.
     * 
     * @param charOffset
     *            A character number within the buffer.
     * @since 4.0.8
     */
    public void setOffset(int charOffset) {
        GtkTextIter.setOffset(this, charOffset);
    }

    /**
     * Returns the line number containing the iterator. Lines in a TextBuffer
     * are numbered beginning with 0 for the first line in the buffer.
     * 
     * @since 4.0.8
     */
    public int getLine() {
        return GtkTextIter.getLine(this);
    }

    /**
     * Moves the iterator to the start of the given line. If
     * <code>lineNumber</code> is negative or larger than the number of
     * lines in the TextBuffer, it moves the iterator to the start of the last
     * line in the TextBuffer.
     * 
     * <p>
     * Line numbers in TextBuffers are counted from 0.
     * 
     * @since 4.0.8
     */
    public void setLine(int lineNumber) {
        GtkTextIter.setLine(this, lineNumber);
    }

    /**
     * Returns the character offset of the iterator, counting from the start
     * of a newline-terminated line. The first character on the line has
     * offset 0. So basically this is a _getColumn()_ function.
     * 
     * @return Offset from start of line
     * @since 4.0.8
     */
    public int getLineOffset() {
        return GtkTextIter.getLineOffset(this);
    }

    /**
     * Moves the iterator within a line, to a new character (not byte) offset.
     * The given character offset must be less than or equal to the number of
     * characters in the line; if equal, the iterator moves to the start of
     * the next line.
     * 
     * @since 4.0.8
     * @param column
     *            A character offset relative to the start of iter's current
     *            line
     */
    public void setLineOffset(int column) {
        GtkTextIter.setLineOffset(this, column);
    }

    /**
     * Like {@link #getLineOffset()} but not counting those characters
     * that are tagged "invisible".
     * 
     * @return Offset from start of line
     * @since 4.0.8
     */
    public int getVisibleLineOffset() {
        return GtkTextIter.getVisibleLineOffset(this);
    }

    /**
     * Like {@link #setLineOffset(int)} but not counting those characters
     * that are tagged "invisible".
     * 
     * @param column
     *            A character offset relative to the start of iter's current
     *            line
     * @since 4.0.8
     */
    public void setVisibleLineOffset(int column) {
        GtkTextIter.setVisibleLineOffset(this, column);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("TextIter(");
        buf.append(getLineOffset());
        buf.append(":");
        buf.append(getLine());
        buf.append(",");
        buf.append(getOffset());
        buf.append(")");
        return buf.toString();
    }
    
    /**
     * Returns the character at the position this TextIter points to.
     * 
     * @return The UTF-8 character. A char value of 0xFFFC indicates
     * a non-character element (such as an embedded image). A value of
     * 0 indicates the buffer end.
     * 
     * @since 4.0.8
     */
    public char getChar() {
        return GtkTextIter.getChar(this);
    }
}
