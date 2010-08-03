package railo.commons.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

public final class CountingReader extends Reader {
	
	private final Reader reader;
    private int count=0;

    public CountingReader(Reader reader) {
        this.reader=reader;
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
	 *
	 * @see java.io.Reader#read()
	 */
	public int read() throws IOException {
		count++;
		return reader.read();
	}

	/**
	 *
	 * @see java.io.Reader#read(char[])
	 */
	public int read(char[] cbuf) throws IOException {
		
		return reader.read(cbuf);
	}

	/**
	 *
	 * @see java.io.Reader#read(java.nio.CharBuffer)
	 */
	public int read(CharBuffer arg0) throws IOException {
		return super.read(arg0.array());
	}

	/**
	 *
	 * @see java.io.Reader#ready()
	 */
	public boolean ready() throws IOException {
		// TODO Auto-generated method stub
		return super.ready();
	}

	/**
	 *
	 * @see java.io.Reader#reset()
	 */
	public void reset() throws IOException {
		// TODO Auto-generated method stub
		super.reset();
	}

	/**
	 *
	 * @see java.io.Reader#skip(long)
	 */
	public long skip(long n) throws IOException {
		// TODO Auto-generated method stub
		return super.skip(n);
	}

	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	public int read(char[] cbuf, int off, int len) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

}
