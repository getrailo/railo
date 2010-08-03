package railo.commons.io.auto;

import java.io.IOException;
import java.io.InputStream;

import railo.commons.io.IOUtil;

/**
 * Close the Stream automaticlly when object will destroyed by the garbage
 */
public final class AutoCloseInputStream extends InputStream {
	
	private final InputStream is;

	/**
	 * constructor of the class
	 * @param is
	 */
	public AutoCloseInputStream(InputStream is) {
		this.is=is;
	}
	
	/**
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		return is.read();
	}

	/**
	 * @see java.io.InputStream#available()
	 */
	public int available() throws IOException {
		return is.available();
	}

	/**
	 * @see java.io.InputStream#close()
	 */
	public void close() throws IOException {
		is.close();
	}

	/**
	 * @see java.io.InputStream#mark(int)
	 */
	public synchronized void mark(int readlimit) {
		is.mark(readlimit);
	}

	/**
	 * @see java.io.InputStream#markSupported()
	 */
	public boolean markSupported() {
		return is.markSupported();
	}

	/**
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		return is.read(b, off, len);
	}

	/**
	 * @see java.io.InputStream#read(byte[])
	 */
	public int read(byte[] b) throws IOException {
		return is.read(b);
	}

	/**
	 * @see java.io.InputStream#reset()
	 */
	public synchronized void reset() throws IOException {
		is.reset();
	}

	/**
	 * @see java.io.InputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		return is.skip(n);
	}

	
	/**
	 * @throws Throwable 
	 * @see java.lang.Object#finalize()
	 */
	public void finalize() throws Throwable {
		super.finalize();
		IOUtil.closeEL(is);
	}
}
