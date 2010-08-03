package railo.commons.io.res.type.datasource;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

import railo.runtime.exp.AlwaysThrow;

public class DatasourceResourceOutputStream extends OutputStream {

	private final DataWriter dw;
	private final OutputStream os;

	/**
	 * Constructor of the class
	 * @param res
	 * @param os
	 */
	public DatasourceResourceOutputStream(DataWriter dw, OutputStream os) {
		this.dw=dw;
		this.os=os;
	}
	
	/**
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException {
		os.write(b);
	}

	/**
	 *
	 * @see java.io.OutputStream#close()
	 */
	public void close() throws IOException {
		os.close();
		try {
			dw.join();
		} catch (InterruptedException e) {
			throw new AlwaysThrow(e.getMessage());
		}

   	 
		SQLException ioe=dw.getException();
		if(ioe!=null) {
			throw new AlwaysThrow(ioe.getMessage());
		}
	}

	/**
	 *
	 * @see java.io.OutputStream#flush()
	 */
	public void flush() throws IOException {
		os.flush();
	}

	/**
	 *
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		os.write(b, off, len);
	}

	/**
	 *
	 * @see java.io.OutputStream#write(byte[])
	 */
	public void write(byte[] b) throws IOException {
		os.write(b);
	}

	/**
	 * @return the os
	 */
	public OutputStream getOutputStream() {
		return os;
	}

}