package railo.commons.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 */
public final class CountingOutputStream extends OutputStream {
    
    private final OutputStream os;
    private int count=0;

    /**
     * @param os
     */
    public CountingOutputStream(OutputStream os) {
        this.os=os;
    }
    
    /**
     * @see java.io.OutputStream#close()
     */
    public void close() throws IOException {
        os.close();
    }

    /**
     * @see java.io.OutputStream#flush()
     */
    public void flush() throws IOException {
        os.flush();
    }

    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public void write(byte[] b, int off, int len) throws IOException {
        count+=len;
        os.write(b, off, len);
    }

    /**
     * @see java.io.OutputStream#write(byte[])
     */
    public void write(byte[] b) throws IOException {
        count+=b.length;
        os.write(b);
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    public void write(int b) throws IOException {
        count++;
        os.write(b);
    }

    /**
     * @return Returns the count.
     */
    public int getCount() {
        return count;
    }

}
