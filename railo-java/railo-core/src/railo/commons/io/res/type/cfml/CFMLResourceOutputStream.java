package railo.commons.io.res.type.cfml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import railo.commons.lang.ExceptionUtil;

public final class CFMLResourceOutputStream extends OutputStream {
	private ByteArrayOutputStream baos;
	private CFMLResource res;
	
	public CFMLResourceOutputStream(CFMLResource res) {
		this.res=res;
		baos = new ByteArrayOutputStream();
	}
	
	@Override
	public void close() throws IOException {
		baos.close();
		
		try {
			res.setBinary(baos.toByteArray());
		} 
		catch (Throwable t) {
			throw ExceptionUtil.toIOException(t);
		}
		finally {
			res.getResourceProvider().unlock(res);
		}
	}

	@Override
	public void flush() throws IOException {
		baos.flush();
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		baos.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		baos.write(b);
	}

	@Override
	public void write(int b) throws IOException {
		baos.write(b);
	}
}
