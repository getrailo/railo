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
        
    @Override
    public int read() throws IOException {
        return stream.read();
    }

	@Override
	public int readLine(byte[] barr, int arg1, int arg2) throws IOException {
		return stream.read(barr, arg1, arg2);
	}

	@Override
	public int available() throws IOException {
		return stream.available();
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		stream.mark(readlimit);
	}

	@Override
	public boolean markSupported() {
		return stream.markSupported();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return stream.read(b, off, len);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return stream.read(b);
	}

	@Override
	public synchronized void reset() throws IOException {
		stream.reset();
	}

	@Override
	public long skip(long n) throws IOException {
		return stream.skip(n);
	}
}