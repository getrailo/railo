package railo.commons.io;

import java.io.OutputStream;
import java.io.Serializable;

/**
 * dev null output stream, write data to nirvana
 */
public final class DevNullOutputStream extends OutputStream implements Serializable {
	
	public static final DevNullOutputStream DEV_NULL_OUTPUT_STREAM=new DevNullOutputStream();
	
	/**
	 * Constructor of the class
	 */
	private DevNullOutputStream() {}
	
    /**
     * @see java.io.OutputStream#close()
     */
    public void close(){}

    /**
     * @see java.io.OutputStream#flush()
     */
    public void flush() {}

    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public void write(byte[] b, int off, int len) {}

    /**
     * @see java.io.OutputStream#write(byte[])
     */
    public void write(byte[] b) {}

    /**
     * @see java.io.OutputStream#write(int)
     */
    public void write(int b) {}

}