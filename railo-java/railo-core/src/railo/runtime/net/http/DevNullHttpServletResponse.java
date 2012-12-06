package railo.runtime.net.http;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import railo.commons.io.DevNullOutputStream;
import railo.commons.io.DevNullServletOutputStream;

public final class DevNullHttpServletResponse extends HttpServletResponseWrapper {

    private HttpServletResponse httpServletResponse;

    /**
     * constructor of the class
     * @param httpServletResponse
     */
    public DevNullHttpServletResponse(HttpServletResponse httpServletResponse) {
        super(httpServletResponse);
        this.httpServletResponse=httpServletResponse;
    }

    @Override
    public void flushBuffer() {
    }

    @Override
    public ServletResponse getResponse() {
        return httpServletResponse;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(DevNullOutputStream.DEV_NULL_OUTPUT_STREAM);
    }

    @Override
    public void reset() {
    }

    @Override
    public void resetBuffer() {
    }

    @Override
    public void setBufferSize(int size) {
    }

    @Override
    public void setContentLength(int size) {
    }

    @Override
    public void setContentType(String type) {
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new DevNullServletOutputStream();
    }


}