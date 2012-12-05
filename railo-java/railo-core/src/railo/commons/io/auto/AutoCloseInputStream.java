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
	
	@Override
	public int read() throws IOException {
		return is.read();
	}

	@Override
	public int available() throws IOException {
		return is.available();
	}

	@Override
	public void close() throws IOException {
		is.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		is.mark(readlimit);
	}

	@Override
	public boolean markSupported() {
		return is.markSupported();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return is.read(b, off, len);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return is.read(b);
	}

	@Override
	public synchronized void reset() throws IOException {
		is.reset();
	}

	@Override
	public long skip(long n) throws IOException {
		return is.skip(n);
	}

	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
		IOUtil.closeEL(is);
	}
}
