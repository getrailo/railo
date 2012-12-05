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
    
    @Override
    public void close() throws IOException {
        os.close();
    }

    @Override
    public void flush() throws IOException {
        os.flush();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        count+=len;
        os.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        count+=b.length;
        os.write(b);
    }

    @Override
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
