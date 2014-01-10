package railo.runtime.img;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.stream.ImageOutputStreamImpl;

import railo.commons.io.res.Resource;

public class ResourceImageOutputStream extends ImageOutputStreamImpl {

    private Resource res;
	private OutputStream os;

    public ResourceImageOutputStream(Resource res) throws IOException {
    	this.res=res;
    	os=res.getOutputStream();
    }
    public ResourceImageOutputStream(OutputStream os) {
    	this.os=os;
    }

    public int read() throws IOException {
    	throw new IOException("not supported");
    }

    public int read(byte[] b, int off, int len) throws IOException {
    	throw new IOException("not supported");
    }

    public void write(int b) throws IOException {
    	os.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
    	os.write(b,off,len);
    }

    public long length() {
    	if(res==null) throw new RuntimeException("not supported");
        return res.length();
    }

    /**
     * Sets the current stream position and resets the bit offset to
     * 0.  It is legal to seeking past the end of the file; an
     * <code>EOFException</code> will be thrown only if a read is
     * performed.  The file length will not be increased until a write
     * is performed.
     *
     * @exception IndexOutOfBoundsException if <code>pos</code> is smaller
     * than the flushed position.
     * @exception IOException if any other I/O error occurs.
     */
    public void seek(long pos) throws IOException {
    	throw new IOException("not supported");
    }

    @Override
    public void close() throws IOException {
    	try {
            super.close();
    	}
    	finally {
            os.close();
    	}
    }
}
