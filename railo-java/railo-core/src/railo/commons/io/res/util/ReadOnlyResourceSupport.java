package railo.commons.io.res.util;

import java.io.IOException;
import java.io.OutputStream;

public abstract class ReadOnlyResourceSupport extends ResourceSupport {

	/**
	 * @see railo.commons.io.res.Resource#createDirectory(boolean)
	 */
	public void createDirectory(boolean createParentWhenNotExists) throws IOException {
		throw new IOException("this is a read-only resource, can't create directory ["+this+"]");
	}

	/**
	 * @see railo.commons.io.res.Resource#createFile(boolean)
	 */
	public void createFile(boolean createParentWhenNotExists) throws IOException {
		throw new IOException("this is a read-only resource, can't create file ["+this+"]");
	}

	/**
	 * @see railo.commons.io.res.Resource#isWriteable()
	 */
	public boolean isWriteable() {
		return false;
	}

	/**
	 * @see railo.commons.io.res.Resource#remove(boolean)
	 */
	public void remove(boolean force) throws IOException {
		throw new IOException("this is a read-only resource, can't remove ["+this+"]");

	}

	/**
	 * @see railo.commons.io.res.Resource#setLastModified(long)
	 */
	public boolean setLastModified(long time) {
		return false;
	}

	/**
	 * @see railo.commons.io.res.Resource#setMode(int)
	 */
	public void setMode(int mode) throws IOException {
		throw new IOException("this is a read-only resource, can't change mode of ["+this+"]");
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#setReadable(boolean)
	 */
	public boolean setReadable(boolean value) {
		//throw new IOException("this is a read-only resource, can't change access of ["+this+"]");
		return false;
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#setWritable(boolean)
	 */
	public boolean setWritable(boolean value) {
		//throw new IOException("this is a read-only resource, can't change access of ["+this+"]");
		return false;
	}

	public OutputStream getOutputStream(boolean append) throws IOException {
		throw new IOException("this is a read-only resource, can't write to it ["+this+"]");
	}

	/**
	 * @see railo.commons.io.res.Resource#getMode()
	 */
	public int getMode() {
		return 0444;
	}
}
