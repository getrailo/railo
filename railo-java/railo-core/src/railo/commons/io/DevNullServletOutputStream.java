package railo.commons.io;

import javax.servlet.ServletOutputStream;

/**
 * ServletOutputStream impl.
 */
public final class DevNullServletOutputStream extends ServletOutputStream {
    
    @Override
    public void close() {}

    @Override
    public void flush() {}

    @Override
    public void write(byte[] b, int off, int len) {}

    @Override
    public void write(byte[] b) {}

    @Override
    public void write(int b) {}

    @Override
    public void print(boolean b) {}

    @Override
    public void print(char c) {}

    @Override
    public void print(double d) {}

    @Override
    public void print(float f) {}

    @Override
    public void print(int i) {}

    @Override
    public void print(long l) {}

    @Override
    public void print(String str) {}

    @Override
    public void println() {}

    @Override
    public void println(boolean b) {}

    @Override
    public void println(char c) {}

    @Override
    public void println(double d) {}

    @Override
    public void println(float f) {}

    @Override
    public void println(int i) {}

    @Override
    public void println(long l) {}

    @Override
    public void println(String str) {}

}
