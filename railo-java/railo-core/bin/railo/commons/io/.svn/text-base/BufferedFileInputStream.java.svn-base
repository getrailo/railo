package railo.commons.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * this class is the short form for  <code>new BufferedInputStream(new FileInputStream())</code>
 */
public final class BufferedFileInputStream extends BufferedInputStream {

    /**
     * constructor of the class
     * @param file
     * @throws FileNotFoundException
     */
    public BufferedFileInputStream(File file) throws FileNotFoundException {
        super(new FileInputStream(file));
    }
    
    /**
     * constructor of the class
     * @param strFile
     * @throws FileNotFoundException
     */
    public BufferedFileInputStream(String strFile) throws FileNotFoundException {
        this(new File(strFile));
    }

}
