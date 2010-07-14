package railo.commons.io.auto;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Close the Stream automaticlly when object will destroyed by the garbage
 */
public final class AutoCloseOutputStream extends OutputStream {
	
	private final OutputStream os;

	/**
	 * constructor of the class
	 * @param os
	 */
	public AutoCloseOutputStream(OutputStream os) {
		this.os=os;
	} 
	
	/**
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException {
		os.write(b);
	}

	/**
	 * @see java.io.OutputStream#close()
	 */
	public void close() throws IOException {
		os.close();
	}

	/**
	 * @see java.io.OutputStream#flush()
	 */
	public void flush() throws IOException {
		os.flush();
	}

	/**
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		os.write(b, off, len);
	}

	/**
	 * @see java.io.OutputStream#write(byte[])
	 */
	public void write(byte[] b) throws IOException {
		os.write(b);
	}
	
	/**
	 * @throws Throwable 
	 * @see java.lang.Object#finalize()
	 */
	public void finalize() throws Throwable {
		super.finalize();
		try {
			os.close();
		}
		catch(Exception e) {}
	}
}
