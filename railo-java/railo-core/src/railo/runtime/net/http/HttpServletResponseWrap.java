package railo.runtime.net.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import railo.print;
import railo.commons.io.DevNullOutputStream;
import railo.commons.lang.Pair;
import railo.commons.net.URLEncoder;
import railo.runtime.type.dt.DateTimeImpl;



/**
 * 
 */
public final class HttpServletResponseWrap extends HttpServletResponseWrapper implements HttpServletResponse,Serializable {
	
	private Cookie[] cookies=new Cookie[0];
	private Pair[] headers=new Pair[0];
	private int status=200;
	private String statusCode="OK";
	private String charset="ISO-8859-1";
	private int contentLength=-1;
	private String contentType=null;
	private Locale locale=Locale.getDefault();
	private int bufferSize=-1;
	private boolean commited;
	//private byte[] outputDatad;
	private OutputStream out;//=new DevNullOutputStream();
	private boolean outInit=false;
	private PrintWriter writer;
	private ServletOutputStreamDummy outputStream;

	/**
	 * Constructor of the class
	 */
	public HttpServletResponseWrap(HttpServletResponse rsp) {
		this(rsp,DevNullOutputStream.DEV_NULL_OUTPUT_STREAM);
	}

	public HttpServletResponseWrap(HttpServletResponse rsp,OutputStream out) {
		super(rsp);
		this.out=out;
	}
	
	/**
	 * @see javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie)
	 */
	public void addCookie(Cookie cookie) {
		Cookie[] tmp = new Cookie[cookies.length+1];
		for(int i=0;i<cookies.length;i++) {
			tmp[i]=cookies[i];
		}
		tmp[cookies.length]=cookie;
		cookies=tmp;
	}
	
	/**
	 * @see javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
	 */
	public boolean containsHeader(String key) {
		return ReqRspUtil.get(headers, key)!=null;
	}
	
	/**
	 * @see javax.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
	 */
	public String encodeURL(String value) {
		return URLEncoder.encode(value);
	}
	/**
	 * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
	 */
	public String encodeRedirectURL(String url) {
		return URLEncoder.encode(url);
	}
	/**
	 * @see javax.servlet.http.HttpServletResponse#encodeUrl(java.lang.String)
	 */
	public String encodeUrl(String value) {
		return URLEncoder.encode(value);
	}
	/**
	 * @see javax.servlet.http.HttpServletResponse#encodeRedirectUrl(java.lang.String)
	 */
	public String encodeRedirectUrl(String value) {
		return URLEncoder.encode(value);
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#sendError(int, java.lang.String)
	 */
	public void sendError(int code, String codeText) throws IOException {
		// TODO impl
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#sendError(int)
	 */
	public void sendError(int code) throws IOException {
		// TODO impl
	}
	
	/**
	 * @see javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
	 */
	public void sendRedirect(String location) throws IOException {
		addHeader("location",location);
	}
	/**
	 * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
	 */
	public void setDateHeader(String key, long value) {
		setHeader(key, new DateTimeImpl(value,false).castToString());
	}
	
	/**
	 * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
	 */
	public void addDateHeader(String key, long value) {
		addHeader(key, new DateTimeImpl(value,false).castToString());
	}
	
	/**
	 * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
	 */
	public void setHeader(String key, String value) {
		headers=ReqRspUtil.set(headers, key, value);
	}
	
	/**
	 * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
	 */
	public void addHeader(String key, String value) {
		headers=ReqRspUtil.add(headers, key, value);
	}
	
	/**
	 * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
	 */
	public void setIntHeader(String key, int value) {
		setHeader(key, String.valueOf(value));
	}
	
	/**
	 * @see javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String, int)
	 */
	public void addIntHeader(String key, int value) {
		addHeader(key, String.valueOf(value));
	}
	/**
	 * @see javax.servlet.http.HttpServletResponse#setStatus(int)
	 */
	public void setStatus(int status) {
		this.status=status; 
	}
	/**
	 * @see javax.servlet.http.HttpServletResponse#setStatus(int, java.lang.String)
	 */
	public void setStatus(int status, String statusCode) {
		setStatus(status);
		this.statusCode=statusCode;  
	}
	
	/**
	 * @see javax.servlet.ServletResponse#getCharacterEncoding()
	 */
	public String getCharacterEncoding() {
		return charset;
	}
	
	public void setCharacterEncoding(String charset) {
		this.charset = charset;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getOutputStream()
	 */
	public ServletOutputStream getOutputStream() throws IOException {
		print.dumpStack();
		if(outInit) throw new IOException("output already initallised");
		outInit=true;
		return outputStream=new ServletOutputStreamDummy(out);
	}
	
	public ServletOutputStream getExistingOutputStream()  {
		return outputStream;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getWriter()
	 */
	public PrintWriter getWriter() throws IOException {
		print.dumpStack();
		if(outInit) throw new IOException("output already initallised");
		outInit=true;
		return writer= new PrintWriter(out);
	}
	
	public PrintWriter getExistingWriter() {
		return writer;
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#setContentLength(int)
	 */
	public void setContentLength(int contentLength) {
		this.contentLength=contentLength;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
	 */
	public void setContentType(String contentType) {
		this.contentType=contentType;
	}
	/**
	 * @see javax.servlet.ServletResponse#setBufferSize(int)
	 */
	public void setBufferSize(int size) {
		this.bufferSize=size;
	}
	/**
	 * @see javax.servlet.ServletResponse#getBufferSize()
	 */
	public int getBufferSize() {
		return bufferSize;
	}
	/**
	 * @see javax.servlet.ServletResponse#flushBuffer()
	 */
	public void flushBuffer() throws IOException {
		commited = true;
	}
	/**
	 * @see javax.servlet.ServletResponse#resetBuffer()
	 */
	public void resetBuffer() {
		commited = true;
	}
	/**
	 * @see javax.servlet.ServletResponse#isCommitted()
	 */
	public boolean isCommitted() {
		return commited;
	}
	/**
	 * @see javax.servlet.ServletResponse#reset()
	 */
	public void reset() {
		commited = true;
	}
	/**
	 * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale locale) {
		this.locale=locale;
	}
	/**
	 * @see javax.servlet.ServletResponse#getLocale()
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @return the charset
	 */
	public String getCharsetEncoding() {
		return charset;
	}

	/**
	 * @return the commited
	 */
	public boolean isCommited() {
		return commited;
	}

	/**
	 * @return the contentLength
	 */
	public int getContentLength() {
		return contentLength;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @return the cookies
	 */
	public Cookie[] getCookies() {
		return cookies;
	}

	/**
	 * @return the headers
	 */
	public Pair[] getHeaders() {
		return headers;
	}

	/* *
	 * @return the outputData
	 * /
	public byte[] getOutputData() {
		return outputData;
	}

	public void setOutputData(byte[] outputData) {
		this.outputData=outputData;
	}*/

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}
	
}