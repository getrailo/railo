package railo.runtime.writer;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JSP Writer that Remove WhiteSpace from given content
 */
public abstract class AbstrCFMLWriterWS extends CFMLWriterImpl implements WhiteSpaceWriter {
	
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
	public AbstrCFMLWriterWS(HttpServletRequest req, HttpServletResponse rsp, int bufferSize, boolean autoFlush, boolean closeConn, 
			boolean showVersion, boolean contentLength, boolean allowCompression) {
		super(req,rsp, bufferSize, autoFlush,closeConn,showVersion,contentLength,allowCompression);
	}

	
	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#clear()
	 */
	public final void clear() throws IOException {
		printBuffer();
		super.clear();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#clearBuffer()
	 */
	public final void clearBuffer() {
		printBufferEL();
		super.clearBuffer();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#close()
	 */
	public final void close() throws IOException {
		printBuffer();
		super.close();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#flush()
	 */
	public final void flush() throws IOException {
		printBuffer();
		super.flush();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#getRemaining()
	 */
	public final int getRemaining() {
		printBufferEL();
		return super.getRemaining();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#newLine()
	 */
	public final void newLine() throws IOException {
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(boolean)
	 */
	public final void print(boolean b) throws IOException {
		printBuffer();
		super.print(b);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(char[])
	 */
	public final void print(char[] chars) throws IOException {
		write(chars,0,chars.length);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(double)
	 */
	public final void print(double d) throws IOException {
		printBuffer();
		super.print(d);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(float)
	 */
	public final void print(float f) throws IOException {
		printBuffer();
		super.print(f);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(int)
	 */
	public final void print(int i) throws IOException {
		printBuffer();
		super.print(i);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(long)
	 */
	public final void print(long l) throws IOException {
		printBuffer();
		super.print(l);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(java.lang.Object)
	 */
	public final void print(Object obj) throws IOException {
		print(obj.toString());
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(java.lang.String)
	 */
	public final void print(String str) throws IOException {
		write(str.toCharArray(),0,str.length());
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println()
	 */
	public final void println() throws IOException {
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(boolean)
	 */
	public final void println(boolean b) throws IOException {
		printBuffer();
		super.print(b);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(char)
	 */
	public final void println(char c) throws IOException {
		print(c);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(char[])
	 */
	public final void println(char[] chars) throws IOException {
		write(chars,0,chars.length);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(double)
	 */
	public final void println(double d) throws IOException {
		printBuffer();
		super.print(d);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(float)
	 */
	public final void println(float f) throws IOException {
		printBuffer();
		super.print(f);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(int)
	 */
	public final void println(int i) throws IOException {
		printBuffer();
		super.print(i);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(long)
	 */
	public final void println(long l) throws IOException {
		printBuffer();
		super.print(l);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(java.lang.Object)
	 */
	public final void println(Object obj) throws IOException {
		println(obj.toString());
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(java.lang.String)
	 */
	public final void println(String str) throws IOException {
		print(str);
		print(CHAR_NL);
		
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(char[], int, int)
	 */
	public final void write(char[] chars, int off, int len) throws IOException {
		for(int i=off;i<len;i++) {
			print(chars[i]);
		}
	}
	
	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(java.lang.String, int, int)
	 */
	public final void write(String str, int off, int len) throws IOException {
		write(str.toCharArray(),off,len);
	}
	

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(char[])
	 */
	public final void write(char[] chars) throws IOException {
		write(chars,0,chars.length);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(int)
	 */
	public final void write(int i) throws IOException {
		print(i);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(java.lang.String)
	 */
	public final void write(String str) throws IOException {
        write(str.toCharArray(),0,str.length());
	}
    
	abstract void printBuffer() throws IOException;
	abstract void printBufferEL();
}