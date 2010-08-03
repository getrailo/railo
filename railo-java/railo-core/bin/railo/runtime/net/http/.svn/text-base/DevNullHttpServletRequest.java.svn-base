package railo.runtime.net.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


public final class DevNullHttpServletRequest extends HttpServletRequestWrapper {

    
    private ByteArrayInputStream barr;

    public DevNullHttpServletRequest(HttpServletRequest req) {
        super(req);
        barr=new ByteArrayInputStream(new byte[]{});
    }

    /**
     * @see javax.servlet.ServletRequest#getContentLength()
     */
    public int getContentLength() {
        return -1;
    }

    /**
     * @see javax.servlet.ServletRequestWrapper#getContentType()
     */
    public String getContentType() {
        return null;
    }

    /**
     * @see javax.servlet.ServletRequestWrapper#getInputStream()
     */
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStreamDummy(barr);
    }

    /**
     * @see javax.servlet.ServletRequestWrapper#getReader()
     */
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(barr));
    }
    
}