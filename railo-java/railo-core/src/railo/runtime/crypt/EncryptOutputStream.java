package railo.runtime.crypt;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 */
public final class EncryptOutputStream extends OutputStream {

    /**
     * @see java.io.OutputStream#close()
     */
    public void close() throws IOException {
        super.close();
    }
    /**
     * @see java.io.OutputStream#flush()
     */
    public void flush() throws IOException {
        super.flush();
    }
    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
    }
    /**
     * @see java.io.OutputStream#write(byte[])
     */
    public void write(byte[] b) throws IOException {
        super.write(b);
    }
    /**
     * @see java.io.OutputStream#write(int)
     */
    public void write(int b) throws IOException {
        
    }

    public static void main(String[] args) {
        
    }
}