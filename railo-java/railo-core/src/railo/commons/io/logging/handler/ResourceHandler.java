
package railo.commons.io.logging.handler;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.ErrorManager;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

import railo.print;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;


public class ResourceHandler extends WriterHandler {
    private MeteredStream meter;
    private final boolean append;
    private final long limit;       // zero => no limit.
    private final int count;
    private Resource[] files;
	private final Charset charset;
    private static final int MAX_LOCKS = 100;
    private static java.util.HashMap<String, String> locks = new java.util.HashMap<String, String>();




    /**
     * Initialize a <tt>FileHandler</tt> to write to the given filename.
     * <p>
     * The <tt>FileHandler</tt> is configured based on <tt>LogManager</tt>
     * properties (or their default values) except that the given pattern
     * argument is used as the filename pattern, the file limit is
     * set to no limit, and the file count is set to one.
     * <p>
     * There is no limit on the amount of data that may be written,
     * so use this with care.
     *
     * @param pattern  the name of the output file
     * @exception  IOException if there are IO problems opening the files.
     * @exception  SecurityException  if a security manager exists and if
     *             the caller does not have <tt>LoggingPermission("control")</tt>.
     * @exception  IllegalArgumentException if pattern is an empty string
     */
    public ResourceHandler(Resource res,Charset charset) throws IOException, SecurityException {
    	this(res,charset,0,1,false);
    }

    /**
     * Initialize a <tt>FileHandler</tt> to write to the given filename,
     * with optional append.
     * <p>
     * The <tt>FileHandler</tt> is configured based on <tt>LogManager</tt>
     * properties (or their default values) except that the given pattern
     * argument is used as the filename pattern, the file limit is
     * set to no limit, the file count is set to one, and the append
     * mode is set to the given <tt>append</tt> argument.
     * <p>
     * There is no limit on the amount of data that may be written,
     * so use this with care.
     *
     * @param pattern  the name of the output file
     * @param append  specifies append mode
     * @exception  IOException if there are IO problems opening the files.
     * @exception  SecurityException  if a security manager exists and if
     *             the caller does not have <tt>LoggingPermission("control")</tt>.
     * @exception  IllegalArgumentException if pattern is an empty string
     */
    public ResourceHandler(Resource res,Charset charset, boolean append) throws IOException, SecurityException {
    	this(res,charset,0,1,append);
    }

    /**
     * Initialize a <tt>FileHandler</tt> to write to a set of files.  When
     * (approximately) the given limit has been written to one file,
     * another file will be opened.  The output will cycle through a set
     * of count files.
     * <p>
     * The <tt>FileHandler</tt> is configured based on <tt>LogManager</tt>
     * properties (or their default values) except that the given pattern
     * argument is used as the filename pattern, the file limit is
     * set to the limit argument, and the file count is set to the
     * given count argument.
     * <p>
     * The count must be at least 1.
     *
     * @param pattern  the pattern for naming the output file
     * @param limit  the maximum number of bytes to write to any one file
     * @param count  the number of files to use
     * @exception  IOException if there are IO problems opening the files.
     * @exception  SecurityException  if a security manager exists and if
     *             the caller does not have <tt>LoggingPermission("control")</tt>.
     * @exception IllegalArgumentException if limit < 0, or count < 1.
     * @exception  IllegalArgumentException if pattern is an empty string
     */
    public ResourceHandler(Resource res,Charset charset, long limit, int count) throws IOException, SecurityException {
    	this(res,charset,limit,count,false);
    }

    /**
     * Initialize a <tt>FileHandler</tt> to write to a set of files
     * with optional append.  When (approximately) the given limit has
     * been written to one file, another file will be opened.  The
     * output will cycle through a set of count files.
     * <p>
     * The <tt>FileHandler</tt> is configured based on <tt>LogManager</tt>
     * properties (or their default values) except that the given pattern
     * argument is used as the filename pattern, the file limit is
     * set to the limit argument, and the file count is set to the
     * given count argument, and the append mode is set to the given
     * <tt>append</tt> argument.
     * <p>
     * The count must be at least 1.
     *
     * @param pattern  the pattern for naming the output file
     * @param limit  the maximum number of bytes to write to any one file
     * @param count  the number of files to use
     * @param append  specifies append mode
     * @exception  IOException if there are IO problems opening the files.
     * @exception  SecurityException  if a security manager exists and if
     *             the caller does not have <tt>LoggingPermission("control")</tt>.
     * @exception IllegalArgumentException if limit < 0, or count < 1.
     * @exception  IllegalArgumentException if pattern is an empty string
     *
     */
    public ResourceHandler(Resource res,Charset charset, long limit, int count, boolean append) throws IOException, SecurityException {
        super();
    	if (limit < 0 || count < 1 || charset==null) {
            throw new IllegalArgumentException();
        }
        this.limit = limit;
        this.count = count;
        this.append = append;
        this.charset=charset;
        setEncoding(charset.name());
        openFiles(res);
    }
    
    
    

    private void open(Resource res, boolean append) throws IOException {
        int len = 0;
        if (append) {
            len = (int)res.length();
        }
        if(len>0 && !doneHeader)doneHeader=true;
        BufferedOutputStream bos = IOUtil.toBufferedOutputStream(res.getOutputStream(append));
        meter = new MeteredStream(bos, len);
        setWriter(IOUtil.getWriter(meter, charset));
    }


    // Private method to open the set of output files, based on the
    // configured instance variables.
    private void openFiles(Resource res) throws IOException {
        LogManager manager = LogManager.getLogManager();
        manager.checkAccess();
        
        // We register our own ErrorManager during initialization
        // so we can record exceptions.
        InitializationErrorManager em = new InitializationErrorManager();
        setErrorManager(em);

        // Create a lock file.  This grants us exclusive access
        // to our set of output files, as long as we are alive.
        int unique = -1;
        
        files = new Resource[count];
        files[0]=res;
        if(res.exists() && res.isDirectory()) throw new IOException(res+" is a directory and cannot be handled as file"); 
        Resource parent = res.getParentResource();
        if(!parent.exists()) parent.createDirectory(true);
        
        String name=res.getName();
        for (int i = 1; i < count; i++) {
            files[i] = parent.getRealResource(name+"."+i+".bak");
        }

        // Create the initial log file.
        if (append) {
            open(files[0], true);
        } else {
            rotate();
        }

        // Did we detect any exceptions during initialization?
        Exception ex = em.lastException;
        if (ex != null) {
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else if (ex instanceof SecurityException) {
                throw (SecurityException) ex;
            } else {
                throw new IOException("Exception: " + ex);
            }
        }

        // Install the normal default ErrorManager.
        setErrorManager(new ErrorManager());
    }

    
    // Rotate the set of output files
    private synchronized void rotate() {
        Level oldLevel = getLevel();
        setLevel(Level.OFF);

        super.close();
        for (int i = count-2; i >= 0; i--) {
            Resource f1 = files[i];
            Resource f2 = files[i+1];
            if (f1.exists()) {
                if (f2.exists()) {
                    f2.delete();
                }
                f1.renameTo(f2);
            }
        }
        try {
            open(files[0], false);
        } catch (IOException ix) {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            reportError(null, ix, ErrorManager.OPEN_FAILURE);

        }
        setLevel(oldLevel);
    }

    /**
     * Format and publish a <tt>LogRecord</tt>.
     *
     * @param  record  description of the log event. A null record is
     *                 silently ignored and is not published
     */
    public synchronized void publish(LogRecord record) {
    	if (!isLoggable(record)) {
            return;
        }
    	super.publish(record);
    	flush();
        if (limit > 0 && meter.written >= limit) {
            // We performed access checks in the "init" method to make sure
            // we are only initialized from trusted code.  So we assume
            // it is OK to write the target files, even if we are
            // currently being called from untrusted code.
            // So it is safe to raise privilege here.
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    rotate();
                    return null;
                }
            });
        }
    }

    /**
     * Close all the files.
     *
     * @exception  SecurityException  if a security manager exists and if
     *             the caller does not have <tt>LoggingPermission("control")</tt>.
     */
    public synchronized void close() throws SecurityException {
        super.close();
    }

    private static class InitializationErrorManager extends ErrorManager {
        Exception lastException;
        public void error(String msg, Exception ex, int code) {
            lastException = ex;
        }
    }

    // Private native method to check if we are in a set UID program.
    private static native boolean isSetUID();
    
    

    // A metered stream is a subclass of OutputStream that
    //   (a) forwards all its output to a target stream
    //   (b) keeps track of how many bytes have been written
    private class MeteredStream extends OutputStream {
        OutputStream out;
        int written;

        MeteredStream(OutputStream out, int written) {
            this.out = out;
            this.written = written;
        }

        public void write(int b) throws IOException {
            out.write(b);
            written++;
        }

        public void write(byte buff[]) throws IOException {
            out.write(buff);
            written += buff.length;
        }

        public void write(byte buff[], int off, int len) throws IOException {
            out.write(buff,off,len);
            written += len;
        }

        public void flush() throws IOException {
            out.flush();
        }

        public void close() throws IOException {
            out.close();
        }
    }
}