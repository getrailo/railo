

package railo.commons.io.auto;

import java.io.IOException;
import java.io.Reader;

/**
 * Close the Reader automaticlly when object will destroyed by the garbage
 */
public final class AutoCloseReader extends Reader {
	
	private final Reader reader;

	/**
	 * constructor of the class
	 * @param reader
	 */
	public AutoCloseReader(Reader reader) {
		this.reader=reader;
	}

	/**
	 * @see java.io.Reader#close()
	 */
	public void close() throws IOException {
		reader.close();
	}

	/**
	 * @see java.io.Reader#mark(int)
	 */
	public void mark(int readAheadLimit) throws IOException {
		reader.mark(readAheadLimit);
	}

	/**
	 * @see java.io.Reader#markSupported()
	 */
	public boolean markSupported() {
		return reader.markSupported();
	}

	/**
	 * @see java.io.Reader#read()
	 */
	public int read() throws IOException {
		return reader.read();
	}

	/**
	 * @see java.io.Reader#read(char[], int, int)
	 */
	public int read(char[] cbuf, int off, int len) throws IOException {
		return reader.read(cbuf,off,len);
	}

	/**
	 * @see java.io.Reader#read(char[])
	 */
	public int read(char[] cbuf) throws IOException {
		return reader.read(cbuf);
	}

	/**
	 * @see java.io.Reader#ready()
	 */
	public boolean ready() throws IOException {
		return reader.ready();
	}

	/**
	 * @see java.io.Reader#reset()
	 */
	public void reset() throws IOException {
		reader.reset();
	}

	/**
	 * @see java.io.Reader#skip(long)
	 */
	public long skip(long n) throws IOException {
		return reader.skip(n);
	}
	
	/**
	 * @throws Throwable 
	 * @see java.lang.Object#finalize()
	 */
	public void finalize() throws Throwable {
		super.finalize();
		try {
			reader.close();
		}
		catch(Exception e) {}
	}

}
