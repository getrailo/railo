package railo.runtime.writer;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JSP Writer that Remove WhiteSpace from given content
 */
public final class CFMLWriterWhiteSpace extends CFMLWriterImpl implements WhiteSpaceWriter {
	

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
	public CFMLWriterWhiteSpace(HttpServletRequest req, HttpServletResponse rsp, int bufferSize, boolean autoFlush, boolean closeConn, 
			boolean showVersion, boolean contentLength, boolean allowCompression) {
		super(req,rsp, bufferSize, autoFlush,closeConn,showVersion,contentLength,allowCompression);
	}
	

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#clear()
	 */
	public void clear() throws IOException {
		printBuffer();
		super.clear();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#clearBuffer()
	 */
	public void clearBuffer() {
		printBufferEL();
		super.clearBuffer();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#close()
	 */
	public void close() throws IOException {
		printBuffer();
		super.close();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#flush()
	 */
	public void flush() throws IOException {
		printBuffer();
		super.flush();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#getRemaining()
	 */
	public int getRemaining() {
		printBufferEL();
		return super.getRemaining();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#newLine()
	 */
	public void newLine() throws IOException {
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(boolean)
	 */
	public void print(boolean b) throws IOException {
		printBuffer();
		super.print(b);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(char)
	 */
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

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(char[])
	 */
	public void print(char[] chars) throws IOException {
		write(chars,0,chars.length);
	}



	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(double)
	 */
	public void print(double d) throws IOException {
		printBuffer();
		super.print(d);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(float)
	 */
	public void print(float f) throws IOException {
		printBuffer();
		super.print(f);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(int)
	 */
	public void print(int i) throws IOException {
		printBuffer();
		super.print(i);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(long)
	 */
	public void print(long l) throws IOException {
		printBuffer();
		super.print(l);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(java.lang.Object)
	 */
	public void print(Object obj) throws IOException {
		print(obj.toString());
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(java.lang.String)
	 */
	public void print(String str) throws IOException {
		write(str.toCharArray(),0,str.length());
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println()
	 */
	public void println() throws IOException {
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(boolean)
	 */
	public void println(boolean b) throws IOException {
		printBuffer();
		super.print(b);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(char)
	 */
	public void println(char c) throws IOException {
		print(c);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(char[])
	 */
	public void println(char[] chars) throws IOException {
		write(chars,0,chars.length);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(double)
	 */
	public void println(double d) throws IOException {
		printBuffer();
		super.print(d);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(float)
	 */
	public void println(float f) throws IOException {
		printBuffer();
		super.print(f);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(int)
	 */
	public void println(int i) throws IOException {
		printBuffer();
		super.print(i);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(long)
	 */
	public void println(long l) throws IOException {
		printBuffer();
		super.print(l);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(java.lang.Object)
	 */
	public void println(Object obj) throws IOException {
		println(obj.toString());
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(java.lang.String)
	 */
	public void println(String str) throws IOException {
		print(str);
		print(CHAR_NL);
		
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(char[], int, int)
	 */
	public void write(char[] chars, int off, int len) throws IOException {
		for(int i=off;i<len;i++) {
			print(chars[i]);
		}
	}
	
	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(java.lang.String, int, int)
	 */
	public void write(String str, int off, int len) throws IOException {
		write(str.toCharArray(),off,len);
	}
	

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(char[])
	 */
	public void write(char[] chars) throws IOException {
		write(chars,0,chars.length);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(int)
	 */
	public void write(int i) throws IOException {
		print(i);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(java.lang.String)
	 */
	public void write(String str) throws IOException {
        write(str.toCharArray(),0,str.length());
	}
	


	private synchronized void printBuffer() throws IOException {
		if(charBuffer!=CHAR_EMPTY) {
			char b = charBuffer;// muss so bleiben!
			charBuffer=CHAR_EMPTY;
			super.print(b);
		}
	}

	private void printBufferEL() {
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
	 * @see railo.runtime.writer.WhiteSpaceWriter#writeRaw(java.lang.String)
	 */
	public void writeRaw(String str) throws IOException {
		printBuffer();
		super.write(str);
	}
	
}