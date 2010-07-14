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

    /**
     * @see javax.servlet.ServletResponseWrapper#flushBuffer()
     */
    public void flushBuffer() {
    }

    /**
     * @see javax.servlet.ServletResponseWrapper#getResponse()
     */
    public ServletResponse getResponse() {
        return httpServletResponse;
    }

    /**
     * @see javax.servlet.ServletResponseWrapper#getWriter()
     */
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(DevNullOutputStream.DEV_NULL_OUTPUT_STREAM);
    }

    /**
     * @see javax.servlet.ServletResponseWrapper#reset()
     */
    public void reset() {
    }

    /**
     * @see javax.servlet.ServletResponseWrapper#resetBuffer()
     */
    public void resetBuffer() {
    }

    /**
     * @see javax.servlet.ServletResponseWrapper#setBufferSize(int)
     */
    public void setBufferSize(int size) {
    }

    /**
     * @see javax.servlet.ServletResponseWrapper#setContentLength(int)
     */
    public void setContentLength(int size) {
    }

    /**
     * @see javax.servlet.ServletResponseWrapper#setContentType(java.lang.String)
     */
    public void setContentType(String type) {
    }

    /**
     * @see javax.servlet.ServletResponseWrapper#getOutputStream()
     */
    public ServletOutputStream getOutputStream() throws IOException {
        return new DevNullServletOutputStream();
    }


}