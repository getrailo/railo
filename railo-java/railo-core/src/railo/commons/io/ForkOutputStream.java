package railo.commons.io;

import java.io.IOException;
import java.io.OutputStream;

public final class ForkOutputStream extends OutputStream {

	private final OutputStream os1;
	private final OutputStream os2;

	public ForkOutputStream(OutputStream os1,OutputStream os2) {
		this.os1=os1;
		this.os2=os2;
	}
	
	@Override
	public void close() throws IOException {
		try {
			os1.close();
		}
		finally {
			os2.close();
		}
	}

	@Override
	public void flush() throws IOException {
		try {
			os1.flush();
		}
		finally {
			os2.flush();
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		os1.write(b, off, len);
		os2.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		os1.write(b);
		os2.write(b);
	}

	public void write(int b) throws IOException {
		os1.write(b);
		os2.write(b);
	}

}
