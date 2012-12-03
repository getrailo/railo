package railo.commons.net.http.httpclient4;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import railo.commons.io.IOUtil;

public class CachingGZIPInputStream extends InputStream {
	
	private final byte[] barr;
	private GZIPInputStream is; 

	public CachingGZIPInputStream(InputStream is) throws IOException {
		barr=IOUtil.toBytes(is,true);
		this.is=new GZIPInputStream(new ByteArrayInputStream(barr));
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
	public void mark(int readlimit) {
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
	public int read() throws IOException {
		return is.read();
	}

	public InputStream getRawData() {
		return new ByteArrayInputStream(barr);
	}
}
