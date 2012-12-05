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
	
    @Override
    public void close(){}

    @Override
    public void flush() {}

    @Override
    public void write(byte[] b, int off, int len) {}

    @Override
    public void write(byte[] b) {}

    @Override
    public void write(int b) {}

}