package railo.runtime.crypt;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 */
public final class EncryptOutputStream extends OutputStream {

    @Override
    public void close() throws IOException {
        super.close();
    }
    @Override
    public void flush() throws IOException {
        super.flush();
    }
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
    }
    @Override
    public void write(byte[] b) throws IOException {
        super.write(b);
    }
    @Override
    public void write(int b) throws IOException {
        
    }

    public static void main(String[] args) {
        
    }
}