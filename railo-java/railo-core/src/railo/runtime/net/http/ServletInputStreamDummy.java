package railo.runtime.net.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

/**
 * implementation of <code>ServletInputStream</code>.
 */
public final class ServletInputStreamDummy extends ServletInputStream
{
    private InputStream stream;
    
    /**
     * @param data
     */
    public ServletInputStreamDummy(byte[] data) {
        stream = new ByteArrayInputStream(data);
    }
    
    /**
     * @param barr
     */
    public ServletInputStreamDummy(InputStream is) {
        stream = is;
    }
        
    /**
     * @see java.io.InputStream#read()
     */
    public int read() throws IOException {
        return stream.read();
    }

	/**
	 * @see javax.servlet.ServletInputStream#readLine(byte[], int, int)
	 */
	public int readLine(byte[] barr, int arg1, int arg2) throws IOException {
		return stream.read(barr, arg1, arg2);
	}

	/**
	 *
	 * @see java.io.InputStream#available()
	 */
	public int available() throws IOException {
		return stream.available();
	}

	/**
	 *
	 * @see java.io.InputStream#close()
	 */
	public void close() throws IOException {
		stream.close();
	}

	/**
	 *
	 * @see java.io.InputStream#mark(int)
	 */
	public synchronized void mark(int readlimit) {
		stream.mark(readlimit);
	}

	/**
	 *
	 * @see java.io.InputStream#markSupported()
	 */
	public boolean markSupported() {
		return stream.markSupported();
	}

	/**
	 *
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		return stream.read(b, off, len);
	}

	/**
	 *
	 * @see java.io.InputStream#read(byte[])
	 */
	public int read(byte[] b) throws IOException {
		return stream.read(b);
	}

	/**
	 *
	 * @see java.io.InputStream#reset()
	 */
	public synchronized void reset() throws IOException {
		stream.reset();
	}

	/**
	 *
	 * @see java.io.InputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		return stream.skip(n);
	}
}