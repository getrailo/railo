package railo.commons.io.logging.handler;

import java.io.*;
import java.util.logging.*;

import railo.print;

public class WriterHandler extends Handler {
	protected boolean doneHeader;
    private Writer writer;


    /**
     * Create a <tt>StreamHandler</tt> with a given <tt>Formatter</tt>
     * and output stream.
     * <p>
     * @param out         the target output stream
     * @param formatter   Formatter to be used to format output
     */
    public WriterHandler(Writer writer) {
        this.writer = writer;
    }
    
    protected WriterHandler() {
		
	}

    protected void setWriter(Writer writer) {
		this.writer=writer;
	}
    

	/**
     * Format and publish a <tt>LogRecord</tt>.
     * <p>
     * The <tt>StreamHandler</tt> first checks if there is an <tt>OutputStream</tt>
     * and if the given <tt>LogRecord</tt> has at least the required log level.
     * If not it silently returns.  If so, it calls any associated
     * <tt>Filter</tt> to check if the record should be published.  If so,
     * it calls its <tt>Formatter</tt> to format the record and then writes
     * the result to the current output stream.
     * <p>
     * If this is the first <tt>LogRecord</tt> to be written to a given
     * <tt>OutputStream</tt>, the <tt>Formatter</tt>'s "head" string is
     * written to the stream before the <tt>LogRecord</tt> is written.
     *
     * @param  record  description of the log event. A null record is
     *                 silently ignored and is not published
     */
    public synchronized void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        String msg;
        try {
            msg = getFormatter().format(record);
        } catch (Exception ex) {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
            return;
        }

        try {
            if (!doneHeader) {
                writer.write(getFormatter().getHead(this));
                doneHeader = true;
            }
            writer.write(msg);
        } catch (Exception ex) {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            reportError(null, ex, ErrorManager.WRITE_FAILURE);
        }
    }


    /**
     * Check if this <tt>Handler</tt> would actually log a given <tt>LogRecord</tt>.
     * <p>
     * This method checks if the <tt>LogRecord</tt> has an appropriate level and
     * whether it satisfies any <tt>Filter</tt>.  It will also return false if
     * no output stream has been assigned yet or the LogRecord is Null.
     * <p>
     * @param record  a <tt>LogRecord</tt>
     * @return true if the <tt>LogRecord</tt> would be logged.
     *
     */
    public boolean isLoggable(LogRecord record) {
        if (writer == null || record == null) {
            return false;
        }
        return super.isLoggable(record);
    }

    /**
     * Flush any buffered messages.
     */
    public synchronized void flush() {
        if (writer != null) {
            try {
                writer.flush();
            } catch (Exception ex) {
                // We don't want to throw an exception here, but we
                // report the exception to any registered ErrorManager.
                reportError(null, ex, ErrorManager.FLUSH_FAILURE);
            }
        }
    }

    private synchronized void flushAndClose() throws SecurityException {
        if (writer != null) {
            try {
                if (!doneHeader) {
                    writer.write(getFormatter().getHead(this));
                    doneHeader = true;
                }
                writer.write(getFormatter().getTail(this));
                writer.flush();
                writer.close();
            } catch (Exception ex) {
                // We don't want to throw an exception here, but we
                // report the exception to any registered ErrorManager.
                reportError(null, ex, ErrorManager.CLOSE_FAILURE);
            }
            writer = null;
        }
    }

    /**
     * Close the current output stream.
     * <p>
     * The <tt>Formatter</tt>'s "tail" string is written to the stream before it
     * is closed.  In addition, if the <tt>Formatter</tt>'s "head" string has not
     * yet been written to the stream, it will be written before the
     * "tail" string.
     *
     * @exception  SecurityException  if a security manager exists and if
     *             the caller does not have LoggingPermission("control").
     */
    public synchronized void close() throws SecurityException {
        flushAndClose();
    }
}