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
	
	@Override
	public void write(int b) throws IOException {
		os.write(b);
	}

	@Override
	public void close() throws IOException {
		os.close();
	}

	@Override
	public void flush() throws IOException {
		os.flush();
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		os.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		os.write(b);
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
		try {
			os.close();
		}
		catch(Exception e) {}
	}
}
