package railo.commons.io.res.util;

import java.io.IOException;
import java.io.OutputStream;

public abstract class ReadOnlyResourceSupport extends ResourceSupport {

	@Override
	public void createDirectory(boolean createParentWhenNotExists) throws IOException {
		throw new IOException("this is a read-only resource, can't create directory ["+this+"]");
	}

	@Override
	public void createFile(boolean createParentWhenNotExists) throws IOException {
		throw new IOException("this is a read-only resource, can't create file ["+this+"]");
	}

	@Override
	public boolean isWriteable() {
		return false;
	}

	@Override
	public void remove(boolean force) throws IOException {
		throw new IOException("this is a read-only resource, can't remove ["+this+"]");

	}

	@Override
	public boolean setLastModified(long time) {
		return false;
	}

	@Override
	public void setMode(int mode) throws IOException {
		throw new IOException("this is a read-only resource, can't change mode of ["+this+"]");
	}

	@Override
	public boolean setReadable(boolean value) {
		//throw new IOException("this is a read-only resource, can't change access of ["+this+"]");
		return false;
	}

	@Override
	public boolean setWritable(boolean value) {
		//throw new IOException("this is a read-only resource, can't change access of ["+this+"]");
		return false;
	}

	public OutputStream getOutputStream(boolean append) throws IOException {
		throw new IOException("this is a read-only resource, can't write to it ["+this+"]");
	}

	@Override
	public int getMode() {
		return 0444;
	}
}
