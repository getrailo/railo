package railo.runtime.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import railo.commons.lang.CharBuffer;

/**
 * Implementation of a JSpWriter
 */
public class JspWriterImplCharBuffer extends CFMLWriter { 
     
	private static final int BUFFER_SIZE = 1024; 
    
    private Writer out;

    private HttpServletResponse response;

    private boolean flushed;

    //private boolean closed;

    private String headData;
    
    //StringBuffer buffer=new StringBuffer(BUFFER_SIZE);
    private CharBuffer buffer=new CharBuffer(BUFFER_SIZE);
    
    private boolean closed=false;
    
    /**
     * constructor of the class
     * @param response Response Object
     * @param bufferSize buffer Size
     * @param autoFlush do auto flush Content
     */
    public JspWriterImplCharBuffer(HttpServletResponse response, int bufferSize, boolean autoFlush) {
        super(bufferSize, autoFlush);
        this.response=response;
        this.autoFlush=autoFlush;
        this.bufferSize=bufferSize;
        //initBuffer(response.getCharacterEncoding());
    }

    /**
     * constructor of the class
     * @param response Response Object
     */
    public JspWriterImplCharBuffer(HttpServletResponse response) {
        this(response, BUFFER_SIZE, false);
    }
    
    private void _check() throws IOException {
        if(autoFlush && buffer.size()>bufferSize)  {
            _flush();
        }
    }

    /**
     * @throws IOException
     */
    protected void initOut() throws IOException {
        if (out == null) {
            out=response.getWriter();
        }
    }

	/*public void splitForCacheTo(Resource res) throws IOException {
		initOut();
		out=new CacheWriter(out,res);
	} 
	
	public void clearCacheSplit() {
		if(out instanceof CacheWriter) {
			CacheWriter cw = (CacheWriter)out;
			out=cw.getOut();
			if(cw.getCacheFile().exists())cw.getCacheFile().delete();
		}
	} */
    

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
    
    /**
     * 
     * @param headData
     * @throws IOException
     */
    public void appendHTMLHead(String headData) throws IOException {
        if(!flushed) {
            if(this.headData==null)this.headData=headData;
            else this.headData+=headData;
        }
        else throw new IOException("page already flushed");
    }

    /**
     * @see railo.runtime.writer.CFMLWriter#writeHTMLHead(java.lang.String)
     */
    public void writeHTMLHead(String headData) throws IOException {
        if(!flushed) {
        	this.headData=headData;
        }
        else throw new IOException("page already flushed");
    }
    

    
    /** 
     * @see railo.runtime.writer.CFMLWriter#getHTMLHead()
     */
    public String getHTMLHead() throws IOException {
    	if(flushed) throw new IOException("page already flushed");
        return headData==null?"":headData;
    }
    
    /** 
     * @see railo.runtime.writer.CFMLWriter#resetHTMLHead()
     */
    public void resetHTMLHead() throws IOException {
    	if(flushed) throw new IOException("page already flushed");
        headData=null;
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
        if (flushed)  throw new IOException("jsp writer is already flushed");
        clearBuffer();
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#clearBuffer() 
     */ 
    public void clearBuffer() { 
    	//buffer.clear();
        buffer=new CharBuffer(BUFFER_SIZE);
    } 

    /** 
     * @see java.io.Writer#flush() 
     */ 
    public void flush() throws IOException { 
        flushBuffer();
        // weil flushbuffer das out erstellt muss ich nicht mehr checken
        out.flush();
    } 
    /** 
     * @see java.io.Writer#flush() 
     */ 
    private void _flush() throws IOException { 
        flushBuffer();
        // weil flushbuffer das out erstellt muss ich nicht mehr checken
        out.flush();
    } 

    /**
     * Flush the output buffer to the underlying character stream, without
     * flushing the stream itself.  This method is non-private only so that it
     * may be invoked by PrintStream.
     * @throws IOException
     */
    protected final void flushBuffer() throws IOException {
        flushed = true;
        initOut();
        //out.write(__toString());
        _writeOut(out);
        clearBuffer();
    } 
    
    private void _writeOut(Writer writer) throws IOException {
        if(headData==null) {
            headData=null;
            buffer.writeOut(writer);
            return;
            //return buffer.toString();
        }
        String str=buffer.toString();
        int index=str.indexOf("</head>");
        if(index==-1) {
            str= headData.concat(str);
            headData=null;
            writer.write(str);
            return;
            //return str;
        }
        str= str.substring(0,index).concat(headData).concat(str.substring(index));
        headData=null;
        writer.write(str);
        //return str;
    }
    
    /** 
     * @see java.io.Writer#close() 
     */ 
    public void close() throws IOException { 
    	if (response == null || closed) return;
        if(out==null) { 
        	//response.setContentLength(buffer.size());
            out=response.getWriter();
        	_writeOut(out);
            
        	
            //byte[] barr = _toString().getBytes(response.getCharacterEncoding());
            //response.setContentLength(barr.length);
            //ServletOutputStream os = response.getOutputStream();
            //os.write(barr);
            out.flush();
            out.close();
        }
        else {
        	flush();
            out.close();
            out = null;
        }
        closed = true;
    } 

    /** 
     * @see javax.servlet.jsp.JspWriter#getRemaining() 
     */ 
    public int getRemaining() { 
        return bufferSize - buffer.size();
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
		return response.getOutputStream();
	}


} 