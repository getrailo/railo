package railo.runtime.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.cache.legacy.CacheItem;
import railo.runtime.net.http.HttpServletResponseWrap;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.op.Caster;

/**
 * Implementation of a JSpWriter
 */
public class CFMLWriterImpl extends CFMLWriter { 
     
	private static final int BUFFER_SIZE = 100000;
	//private static final String VERSIONj = Info.getVersionAsString();  
    private OutputStream out;
	private HttpServletResponse response;
    private boolean flushed;
    private StringBuilder htmlHead;
    private StringBuilder htmlBody;
    private StringBuilder buffer=new StringBuilder(BUFFER_SIZE);
    private boolean closed=false;
    private boolean closeConn;
    private boolean showVersion;
    private boolean contentLength;
    private CacheItem cacheItem;
	private HttpServletRequest request;
	private Boolean _allowCompression;
	private PageContext pc;
	private String version; 
    
    /**
     * constructor of the class
     * @param response Response Object
     * @param bufferSize buffer Size
     * @param autoFlush do auto flush Content
     */
    public CFMLWriterImpl(PageContext pc,HttpServletRequest request, HttpServletResponse response, int bufferSize, boolean autoFlush, boolean closeConn, boolean showVersion, boolean contentLength) {
        super(bufferSize, autoFlush);
        this.pc=pc;
        this.request=request;
        this.response=response;
        this.autoFlush=autoFlush;
        this.bufferSize=bufferSize;
        this.closeConn=closeConn;
        this.showVersion=showVersion;
        this.contentLength=contentLength;
        //this.allowCompression=allowCompression;
        version=pc.getConfig().getFactory().getEngine().getInfo().getVersionAsString();
    }

    /* *
     * constructor of the class
     * @param response Response Object
     * /
    public JspWriterImpl(HttpServletResponse response) {
        this(response, BUFFER_SIZE, false);
    }*/
    
    private void _check() throws IOException {
        if(autoFlush && buffer.length()>bufferSize)  {
            _flush(true);
        }
    }

    /**
     * @throws IOException
     */
    protected void initOut() throws IOException {
        if (out == null) {
        	out=getOutputStream(false);
            //out=response.getWriter();
        }
    }


	/**
     * @see javax.servlet.jsp.JspWriter#print(char[]) 
     */ 
    public void print(char[] arg) throws IOException { 
        buffer.append(arg);
        _check();
    }
    
    /**
     * reset configuration of buffer
     * @param bufferSize size of the buffer
     * @param autoFlush does the buffer autoflush
     * @throws IOException
     */
    public void setBufferConfig(int bufferSize, boolean autoFlush) throws IOException {
        this.bufferSize=bufferSize;
        this.autoFlush=autoFlush;
        _check();
    }

	public void appendHTMLBody(String text) throws IOException {

		if (htmlBody == null)
			htmlBody = new StringBuilder(256);

		htmlBody.append(text);
	}

	public void writeHTMLBody(String text) throws IOException {

		if (flushed)    throw new IOException("Page is already flushed");

		htmlBody = new StringBuilder(text);
	}

	public String getHTMLBody() throws IOException {

		if (flushed)    throw new IOException("Page is already flushed");

		return htmlBody == null ? "" : htmlBody.toString();
	}

	public void flushHTMLBody() throws IOException {

		if (htmlBody != null) {

			buffer.append(htmlBody);
			resetHTMLBody();
		}
	}

	/**
	 * @see railo.runtime.writer.CFMLWriter#resetHTMLHead()
	 */
	public void resetHTMLBody() throws IOException {
		if(flushed) throw new IOException("Page is already flushed");
		htmlBody = null;
	}

    /**
     * 
     * @param text
     * @throws IOException
     */
    public void appendHTMLHead(String text) throws IOException {

	    if (flushed)    throw new IOException("Page is already flushed");

        if (htmlHead == null)
	        htmlHead = new StringBuilder(256);

        htmlHead.append(text);
    }
    
    public void writeHTMLHead(String text) throws IOException {

	    if (flushed)    throw new IOException("Page is already flushed");

	    htmlHead = new StringBuilder(text);
    }
    
    /** 
     * @see railo.runtime.writer.CFMLWriter#getHTMLHead()
     */
    public String getHTMLHead() throws IOException {

	    if (flushed)    throw new IOException("Page is already flushed");

        return htmlHead == null ? "" : htmlHead.toString();
    }

	public void flushHTMLHead() throws IOException {

		if (htmlHead != null) {

			buffer.append(htmlHead);
			resetHTMLHead();
		}
	}

    /** 
     * @see railo.runtime.writer.CFMLWriter#resetHTMLHead()
     */
    public void resetHTMLHead() throws IOException {
    	if(flushed) throw new IOException("Page is already flushed");
        htmlHead =null;
    }
    
    /**
     * just a wrapper function for ACF
     * @throws IOException 
     */
    public void initHeaderBuffer() throws IOException{
    	resetHTMLHead();
    }


    /** 
     * @see java.io.Writer#write(char[], int, int) 
     */ 
    public void write(char[] cbuf, int off, int len) throws IOException { 
        buffer.append(cbuf,off,len);
        _check();
    }

    /** 
     * @see javax.servlet.jsp.JspWriter#clear() 
     */ 
    public void clear() throws IOException { 
        if (flushed)  throw new IOException("Response buffer is already flushed");
        clearBuffer();
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#clearBuffer() 
     */ 
    public void clearBuffer() { 
    	buffer=new StringBuilder(BUFFER_SIZE);
    } 

    /** 
     * @see java.io.Writer#flush() 
     */ 
    public void flush() throws IOException { 
    	flushBuffer(true);
        // weil flushbuffer das out erstellt muss ich nicht mehr checken
        out.flush();
    } 
    
    /** 
     * @see java.io.Writer#flush() 
     */ 
    private void _flush(boolean closeConn) throws IOException { 
        flushBuffer(closeConn);
        // weil flushbuffer das out erstellt muss ich nicht mehr checken
        out.flush();
        
    } 

    /**
     * Flush the output buffer to the underlying character stream, without
     * flushing the stream itself.  This method is non-private only so that it
     * may be invoked by PrintStream.
     * @throws IOException
     * @throws  
     */
    protected final void flushBuffer(boolean closeConn) throws IOException {
    	if(!flushed && closeConn) {
        	response.setHeader("connection", "close");
        	if(showVersion)response.setHeader("Railo-Version", version);
        	
        }
    	initOut();
    	byte[] barr = _toString(true).getBytes(ReqRspUtil.getCharacterEncoding(null,response));
        
    	if(cacheItem!=null && cacheItem.isValid()) {
    		cacheItem.store(barr, flushed);
        	// writeCache(barr,flushed);
        }
        flushed = true;
        out.write(barr);
        
        buffer=new StringBuilder(BUFFER_SIZE); // to not change to clearBuffer, produce problem with CFMLWriterWhiteSpace.clearBuffer 
    } 
    
    

    private String _toString(boolean releaseHeadData) {

	    if (htmlBody == null && htmlHead == null)
            return buffer.toString();

        String str = buffer.toString();

	    if (htmlHead != null) {

		    int index = StringUtil.indexOfIgnoreCase(str, "</head>");
		    if (index > -1) {

			    str = StringUtil.insertAt(str, htmlHead, index);
		    }
		    else {

			    index = StringUtil.indexOfIgnoreCase(str, "<head>") + 7;
			    if (index > 6) {

				    str = StringUtil.insertAt(str, htmlHead, index);
			    }
			    else {

				    str = htmlHead.append(str).toString();
			    }
		    }
	    }

	    if (htmlBody != null) {

		    int index=StringUtil.indexOfIgnoreCase(str,"</body>");
		    if (index > -1) {

			    str = StringUtil.insertAt(str, htmlBody, index);
		    }
		    else {

			    str += htmlBody.toString();
		    }
	    }

        if (releaseHeadData) {
	        htmlBody = null;
	        htmlHead = null;
        }

        return str;
    }
    
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
        return _toString(false);
    }
    
    
    
    

    /** 
     * @see java.io.Writer#close() 
     */ 
    public void close() throws IOException {
    	if (response == null || closed) return;
    	//boolean closeConn=true;
        if(out==null) { 
        	if(response.isCommitted()) {
        		closed=true;
        		return;
        	}
        	//print.out(_toString());
        	byte[] barr = _toString(true).getBytes(ReqRspUtil.getCharacterEncoding(null,response));
            
        	if(cacheItem!=null)	{
        		cacheItem.store(barr, false);
            	// writeCache(barr,false);
        	}
        	
        	if(closeConn)response.setHeader("connection", "close");
        	if(showVersion)response.setHeader("Railo-Version", version);
        	boolean allowCompression;
            if(barr.length<=512) 
            	allowCompression=false;
            else if(_allowCompression!=null) 
            	allowCompression=_allowCompression.booleanValue();
            else
            	allowCompression=((PageContextImpl)pc).getAllowCompression();
            out = getOutputStream(allowCompression);
	        
        	
        	if(contentLength && !(out instanceof GZIPOutputStream))ReqRspUtil.setContentLength(response,barr.length);
            
                out.write(barr);
	            out.flush();
	            out.close();
            
            
            out = null;
        }
        else {
            _flush(closeConn);
            out.close();
            out = null;
        }
        closed = true;
    } 
    

    private OutputStream getOutputStream(boolean allowCompression) throws IOException {
    	
        if (allowCompression){
    		
            String encodings = ReqRspUtil.getHeader(request, "Accept-Encoding", "");
            if( encodings.indexOf("gzip")!=-1 ) {
    	    	boolean inline=HttpServletResponseWrap.get();
    	    	if(!inline) {
    	    		ServletOutputStream os = response.getOutputStream();
	    	    	response.setHeader("Content-Encoding", "gzip");
	        		return new GZIPOutputStream(os);
    	    	}
    	    }
        }
        return response.getOutputStream();
	}
    
    

    /*private void writeCache(byte[] barr,boolean append) throws IOException {
    	cacheItem.store(barr, append);
    	//IOUtil.copy(new ByteArrayInputStream(barr), cacheItem.getResource().getOutputStream(append),true,true);
    	//MetaData.getInstance(cacheItem.getDirectory()).add(cacheItem.getName(), cacheItem.getRaw());
	}*/

    /** 
     * @see javax.servlet.jsp.JspWriter#getRemaining() 
     */ 
    public int getRemaining() { 
        return bufferSize - buffer.length();
    }

    /** 
     * @see javax.servlet.jsp.JspWriter#newLine() 
     */ 
    public void newLine() throws IOException { 
        println();
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#print(boolean) 
     */ 
    public void print(boolean arg) throws IOException { 
        print(arg?new char[]{'t','r','u','e'}:new char[]{'f','a','l','s','e'}); 
    }

    /** 
     * @see javax.servlet.jsp.JspWriter#print(char) 
     */ 
    public void print(char arg) throws IOException { 
        buffer.append(arg);
        _check();
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#print(int) 
     */ 
    public void print(int arg) throws IOException { 
        _print(String.valueOf(arg)); 
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#print(long) 
     */ 
    public void print(long arg) throws IOException { 
        _print(String.valueOf(arg)); 

    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#print(float) 
     */ 
    public void print(float arg) throws IOException { 
        _print(String.valueOf(arg)); 
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#print(double) 
     */ 
    public void print(double arg) throws IOException { 
        _print(String.valueOf(arg)); 
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#print(java.lang.String) 
     */ 
    public void print(String arg) throws IOException { 
        buffer.append(arg);
        _check();
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#print(java.lang.Object) 
     */ 
    public void print(Object arg) throws IOException { 
        _print(String.valueOf(arg)); 
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#println() 
     */ 
    public void println() throws IOException { 
        _print("\n"); 

    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#println(boolean) 
     */ 
    public void println(boolean arg) throws IOException { 
        print(arg?new char[]{'t','r','u','e','\n'}:new char[]{'f','a','l','s','e','\n'}); 
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#println(char) 
     */ 
    public void println(char arg) throws IOException { 
        print(new char[]{arg,'\n'}); 
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#println(int) 
     */ 
    public void println(int arg) throws IOException { 
        print(arg); 
        println(); 
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#println(long) 
     */ 
    public void println(long arg) throws IOException { 
        print(arg); 
        println(); 
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#println(float) 
     */ 
    public void println(float arg) throws IOException { 
        print(arg); 
        println(); 
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#println(double) 
     */ 
    public void println(double arg) throws IOException { 
        print(arg); 
        println(); 
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#println(char[]) 
     */ 
    public void println(char[] arg) throws IOException { 
        print(arg); 
        println(); 
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#println(java.lang.String) 
     */ 
    public void println(String arg) throws IOException { 
        _print(arg); 
        println(); 
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#println(java.lang.Object) 
     */ 
    public void println(Object arg) throws IOException { 
        print(arg); 
        println(); 
    }
    
    /** 
     * @see java.io.Writer#write(char[]) 
     */ 
    public void write(char[] cbuf) throws IOException { 
        print(cbuf); 
    } 

    /** 
     * @see java.io.Writer#write(int) 
     */ 
    public void write(int c) throws IOException { 
        print(c); 
    } 

    /** 
     * @see java.io.Writer#write(java.lang.String, int, int) 
     */ 
    public void write(String str, int off, int len) throws IOException { 
        write(str.toCharArray(),off,len);
    } 

    /** 
     * @see java.io.Writer#write(java.lang.String) 
     */ 
    public void write(String str) throws IOException { 
    	buffer.append(str);
        _check();
    }
    
    /**
	 * @see railo.runtime.writer.CFMLWriter#writeRaw(java.lang.String)
	 */
	public void writeRaw(String str) throws IOException {
		_print(str);
	}

    /**
     * @return Returns the flushed.
     */
    public boolean isFlushed() {
        return flushed;
    }

    public void setClosed(boolean closed) {
        this.closed=closed;
    }

    private void _print(String arg) throws IOException { 
        buffer.append(arg);
        _check();
    }

	/**
	 * @see railo.runtime.writer.CFMLWriter#getResponseStream()
	 */
	public OutputStream getResponseStream() throws IOException {
		initOut();
		return out;
	}

	public void doCache(railo.runtime.cache.legacy.CacheItem ci) {
		this.cacheItem=ci;
	}

	/**
	 * @return the cacheResource
	 */
	public CacheItem getCacheItem() {
		return cacheItem;
	}
	

	// only for compatibility to other vendors
	public String getString() {
		return toString();
	}

	@Override
	public void setAllowCompression(boolean allowCompression) {
		this._allowCompression=Caster.toBoolean(allowCompression);
	}
	
	

} 
