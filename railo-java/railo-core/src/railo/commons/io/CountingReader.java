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
	@Override
	public void mark(int readAheadLimit) throws IOException {
		reader.mark(readAheadLimit);
	}

	@Override
	public boolean markSupported() {
		return reader.markSupported();
	}

	@Override
	public int read() throws IOException {
		count++;
		return reader.read();
	}

	@Override
	public int read(char[] cbuf) throws IOException {
		
		return reader.read(cbuf);
	}

	@Override
	public int read(CharBuffer arg0) throws IOException {
		return super.read(arg0.array());
	}

	@Override
	public boolean ready() throws IOException {
		// TODO Auto-generated method stub
		return super.ready();
	}

	@Override
	public void reset() throws IOException {
		// TODO Auto-generated method stub
		super.reset();
	}

	@Override
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
