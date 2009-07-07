package railo.runtime.net.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import railo.commons.io.IOUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.type.scope.FormImpl;

/**
 * extends a existing {@link HttpServletRequest} with the possibility to reread the input as many you want.
 */
public final class HoldingInputHTTPServletRequest extends HttpServletRequestWrapper implements Serializable {


	private boolean firstRead=true;
	private byte[] barr;
	private HttpServletRequest req;
	private static final int MIN_STORAGE_SIZE=1*1024*1024;
	private static final int MAX_STORAGE_SIZE=50*1024*1024;
	private static final int SPACIG=1024*1024;

	/**
	 * Constructor of the class
	 * @param req 
	 * @param max how many is possible to re read
	 */
	public HoldingInputHTTPServletRequest(HttpServletRequest req) {
		super(req);
		this.req=req;
	}
	/**
	 * @see javax.servlet.ServletRequestWrapper#getRemoteAddr()
	 */
	public String getRemoteAddr() {
		// TODO Auto-generated method stub
		return req.getRemoteAddr();
	}

	/**
	 * this method still throws a error if want read input stream a second time
	 * this is done to be compatibility with servletRequest class
	 * @see javax.servlet.ServletRequestWrapper#getInputStream()
	 */
	public ServletInputStream getInputStream() throws IOException {
		//if(ba rr!=null) throw new IllegalStateException();
		if(barr==null) {
			if(!firstRead) {
				PageContext pc = ThreadLocalPageContext.get();
				if(pc!=null) {
					return ((FormImpl)pc.formScope()).getInputStream();
				}
				return new ServletInputStreamDummy(new byte[]{});	//throw new IllegalStateException();
			}
			
			firstRead=false;
			if(isToBig(getContentLength())) {
				return super.getInputStream();
			}
			InputStream is=null;
			try {
				barr=IOUtil.toBytes(is=super.getInputStream());
				
				//Resource res = ResourcesImpl.getFileResourceProvider().getResource("/Users/mic/Temp/multipart.txt");
				//IOUtil.copy(new ByteArrayInputStream(barr), res, true);
				
			}
			catch(Throwable t) {
				barr=null;
				return new ServletInputStreamDummy(new byte[]{});	 
			}
			finally {
				IOUtil.closeEL(is);
			}
		}
		
		return new ServletInputStreamDummy(barr);	
	}
	
	private boolean isToBig(int contentLength) {
		if(contentLength<MIN_STORAGE_SIZE) return false;
		if(contentLength>MAX_STORAGE_SIZE) return true;
		Runtime rt = Runtime.getRuntime();
		long av = rt.maxMemory()-rt.totalMemory()+rt.freeMemory();
		return (av-SPACIG)<contentLength;
	}

	/* *
	 * with this method it is possibiliy to rewrite the input as many you want
	 * @return input stream from request
	 * @throws IOException
	 * /
	public ServletInputStream getStoredInputStream() throws IOException {
		if(firstRead || barr!=null) return getInputStream();
		return new ServletInputStreamDummy(new byte[]{});	 
	}*/

	/**
	 *
	 * @see javax.servlet.ServletRequestWrapper#getReader()
	 */
	public BufferedReader getReader() throws IOException {
		String enc = getCharacterEncoding();
		if(StringUtil.isEmpty(enc))enc="iso-8859-1";
		return IOUtil.toBufferedReader(IOUtil.getReader(getInputStream(), enc));
	}
	
	public void clear() {
		barr=null;
	}

}
