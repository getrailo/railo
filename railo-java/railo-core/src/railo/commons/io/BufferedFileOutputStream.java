package railo.commons.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


/**
 * this class is the short form for  <code>new BufferedOutputStream(new FileOutputStream())</code>
 */
public final class BufferedFileOutputStream extends BufferedOutputStream {

    /**
     * @param file
     * @param append 
     * @throws FileNotFoundException
     */
    public BufferedFileOutputStream(File file, boolean append) throws FileNotFoundException {
        super(new FileOutputStream(file,append));
    }

    /**
     * @param file
     * @throws FileNotFoundException
     */
    public BufferedFileOutputStream(File file) throws FileNotFoundException {
        super(new FileOutputStream(file));
    }

}
