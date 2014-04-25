package railo.runtime.writer;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import railo.runtime.PageContext;

/**
 * JSP Writer that Remove WhiteSpace from given content
 */
public final class CFMLWriterWS extends CFMLWriterImpl implements WhiteSpaceWriter {

	public static final char CHAR_EMPTY=0;
	public static final char CHAR_NL='\n';
	public static final char CHAR_SPACE=' ';
	public static final char CHAR_TAB='\t';
	public static final char CHAR_BS='\b'; // \x0B\
	public static final char CHAR_FW='\f';
	public static final char CHAR_RETURN='\r';

	char charBuffer=CHAR_EMPTY;
	
	/**
	 * constructor of the class
	 * @param rsp
	 * @param bufferSize 
	 * @param autoFlush 
	 */
	public CFMLWriterWS(PageContext pc,HttpServletRequest req, HttpServletResponse rsp, int bufferSize, boolean autoFlush, boolean closeConn, 
			boolean showVersion, boolean contentLength) {
		super(pc,req,rsp, bufferSize, autoFlush,closeConn,showVersion,contentLength);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(char)
	 */
	@Override
	public void print(char c) throws IOException {
		switch(c) {
		case CHAR_NL:
			if(charBuffer!=CHAR_NL)charBuffer=c;
		break;
		case CHAR_BS:
		case CHAR_FW:
		case CHAR_RETURN:
		case CHAR_SPACE:
		case CHAR_TAB:
			if(charBuffer==CHAR_EMPTY)charBuffer=c;
		break;
		
		default:
			printBuffer();
			super.print(c);
		break;
		}
	}
	
	synchronized void printBuffer() throws IOException {
		if(charBuffer!=CHAR_EMPTY) {
			char b = charBuffer;// muss so bleiben!
			charBuffer=CHAR_EMPTY;
			super.print(b);
		}
	}

	void printBufferEL() {
		if(charBuffer!=CHAR_EMPTY) {
			try {
				char b = charBuffer;
				charBuffer=CHAR_EMPTY;
				super.print(b);
			} 
			catch (IOException e) {}
		}
	}

	/**
	 * @see railo.runtime.writer.CFMLWriter#writeRaw(java.lang.String)
	 */
	@Override
	public void writeRaw(String str) throws IOException {
		printBuffer();
		super.write(str);
	}    
	
	/**
     * just a wrapper function for ACF
     * @throws IOException 
     */
    @Override
	public void initHeaderBuffer() throws IOException{
    	resetHTMLHead();
    }
	
    
    

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#clear()
	 */
	@Override
	public final void clear() throws IOException {
		printBuffer();
		super.clear();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#clearBuffer()
	 */
	@Override
	public final void clearBuffer() {
		printBufferEL();
		super.clearBuffer();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#close()
	 */
	@Override
	public final void close() throws IOException {
		printBuffer();
		super.close();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#flush()
	 */
	@Override
	public final void flush() throws IOException {
		printBuffer();
		super.flush();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#getRemaining()
	 */
	@Override
	public final int getRemaining() {
		printBufferEL();
		return super.getRemaining();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#newLine()
	 */
	@Override
	public final void newLine() throws IOException {
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(boolean)
	 */
	@Override
	public final void print(boolean b) throws IOException {
		printBuffer();
		super.print(b);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(char[])
	 */
	@Override
	public final void print(char[] chars) throws IOException {
		write(chars,0,chars.length);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(double)
	 */
	@Override
	public final void print(double d) throws IOException {
		printBuffer();
		super.print(d);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(float)
	 */
	@Override
	public final void print(float f) throws IOException {
		printBuffer();
		super.print(f);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(int)
	 */
	@Override
	public final void print(int i) throws IOException {
		printBuffer();
		super.print(i);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(long)
	 */
	@Override
	public final void print(long l) throws IOException {
		printBuffer();
		super.print(l);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(java.lang.Object)
	 */
	@Override
	public final void print(Object obj) throws IOException {
		print(obj.toString());
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(java.lang.String)
	 */
	@Override
	public final void print(String str) throws IOException {
		write(str.toCharArray(),0,str.length());
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println()
	 */
	@Override
	public final void println() throws IOException {
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(boolean)
	 */
	@Override
	public final void println(boolean b) throws IOException {
		printBuffer();
		super.print(b);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(char)
	 */
	@Override
	public final void println(char c) throws IOException {
		print(c);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(char[])
	 */
	@Override
	public final void println(char[] chars) throws IOException {
		write(chars,0,chars.length);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(double)
	 */
	@Override
	public final void println(double d) throws IOException {
		printBuffer();
		super.print(d);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(float)
	 */
	@Override
	public final void println(float f) throws IOException {
		printBuffer();
		super.print(f);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(int)
	 */
	@Override
	public final void println(int i) throws IOException {
		printBuffer();
		super.print(i);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(long)
	 */
	@Override
	public final void println(long l) throws IOException {
		printBuffer();
		super.print(l);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(java.lang.Object)
	 */
	@Override
	public final void println(Object obj) throws IOException {
		println(obj.toString());
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(java.lang.String)
	 */
	@Override
	public final void println(String str) throws IOException {
		print(str);
		print(CHAR_NL);
		
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(char[], int, int)
	 */
	@Override
	public final void write(char[] chars, int off, int len) throws IOException {
		for(int i=off;i<len;i++) {
			print(chars[i]);
		}
	}
	
	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(java.lang.String, int, int)
	 */
	@Override
	public final void write(String str, int off, int len) throws IOException {
		write(str.toCharArray(),off,len);
	}
	

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(char[])
	 */
	@Override
	public final void write(char[] chars) throws IOException {
		write(chars,0,chars.length);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(int)
	 */
	@Override
	public final void write(int i) throws IOException {
		print(i);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(java.lang.String)
	 */
	@Override
	public final void write(String str) throws IOException {
        write(str.toCharArray(),0,str.length());
	}
}