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

    @Override
    public int getContentLength() {
        return -1;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStreamDummy(barr);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(barr));
    }
    
}