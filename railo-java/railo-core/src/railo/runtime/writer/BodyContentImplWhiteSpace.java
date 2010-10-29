package railo.runtime.writer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.servlet.jsp.JspWriter;

import railo.commons.lang.CharBuffer;

public class BodyContentImplWhiteSpace extends BodyContentImpl implements WhiteSpaceWriter {

	char _charBuffer=CFMLWriterWhiteSpace.CHAR_EMPTY;
	
	public BodyContentImplWhiteSpace(JspWriter jspWriter) {
		super(jspWriter);
	}



	/**
	 * @see railo.runtime.writer.BodyContentImpl#getReader()
	 */
	public Reader getReader() {
		printBuffer();
		return super.getReader();
	}



	/**
	 * @see railo.runtime.writer.BodyContentImpl#getString()
	 */
	public String getString() {
		printBuffer();
		return super.getString();
	}



	/**
	 * @see railo.runtime.writer.BodyContentImpl#writeOut(java.io.Writer)
	 */
	public void writeOut(Writer writer) throws IOException {
		printBuffer();
		super.writeOut(writer);
	}



	/**
	 * @see railo.runtime.writer.BodyContentImpl#print(char)
	 */
	public void print(char c) {
		switch(c) {
		case CFMLWriterWhiteSpace.CHAR_NL:
			if(_charBuffer!=CFMLWriterWhiteSpace.CHAR_NL)_charBuffer=c;
		break;
		case CFMLWriterWhiteSpace.CHAR_BS:
		case CFMLWriterWhiteSpace.CHAR_FW:
		case CFMLWriterWhiteSpace.CHAR_RETURN:
		case CFMLWriterWhiteSpace.CHAR_SPACE:
		case CFMLWriterWhiteSpace.CHAR_TAB:
			if(_charBuffer==CFMLWriterWhiteSpace.CHAR_EMPTY)_charBuffer=c;
		break;
		
		default:
			printBuffer();
			super.print(c);
		break;
		}
		
	}

	private synchronized void printBuffer() {
		if(_charBuffer!=CFMLWriterWhiteSpace.CHAR_EMPTY) {
			char b = _charBuffer;// muss so bleiben!
			_charBuffer=CFMLWriterWhiteSpace.CHAR_EMPTY;
			super.print(b);
		}
	}




	/**
	 * @see railo.runtime.writer.BodyContentImpl#clear()
	 */
	public void clear() throws IOException {
		printBuffer();
		super.clear();
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#clearBuffer()
	 */
	public void clearBuffer() {
		printBuffer();
		super.clearBuffer();
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#flush()
	 */
	public void flush() throws IOException {
		printBuffer();
		super.flush();
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#close()
	 */
	public void close() throws IOException {
		printBuffer();
		super.close();
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#getRemaining()
	 */
	public int getRemaining() {
		printBuffer();
		return super.getRemaining();
	}



	/**
	 * @see railo.runtime.writer.BodyContentImpl#toString()
	 */
	public String toString() {
		printBuffer();
		return super.toString();
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#clearBody()
	 */
	public void clearBody() {
		printBuffer();
		super.clearBody();
	}


	/**
	 * @see railo.runtime.writer.BodyContentImpl#getCharBuffer()
	 */
	public CharBuffer getCharBuffer() {
		printBuffer();
		return super.getCharBuffer();
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#setCharBuffer(railo.commons.lang.CharBuffer)
	 */
	public void setCharBuffer(CharBuffer charBuffer) {
		printBuffer();
		super.setCharBuffer(charBuffer);
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#getBufferSize()
	 */
	public int getBufferSize() {
		printBuffer();
		return super.getBufferSize();
	}


	
	

	/**
	 * @see railo.runtime.writer.BodyContentImpl#print(boolean)
	 */
	public void print(boolean arg) {
		print(arg?"true":"false");
	}

	/***
	 * @see railo.runtime.writer.BodyContentImpl#write(int)
	 */
	public void write(int c) {
		print(c);
	}
	
	/**
	 * @see railo.runtime.writer.BodyContentImpl#print(long)
	 */
	public void print(long arg) {
		print(String.valueOf(arg));
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#print(float)
	 */
	public void print(float arg) {
		print(String.valueOf(arg));
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#print(double)
	 */
	public void print(double arg) {
		print(String.valueOf(arg));
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#print(int)
	 */
	public void print(int arg) {
		print(String.valueOf(arg));
	}
	
	/**
	 * @see railo.runtime.writer.BodyContentImpl#print(java.lang.Object)
	 */
	public void print(Object arg) {
		print(String.valueOf(arg));
	}
	/**
	 * @see railo.runtime.writer.BodyContentImpl#println()
	 */
	public void println() {
		print("\n");
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#println(boolean)
	 */
	public void println(boolean arg) {
		print(arg?"true\n":"false\n");
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#println(char)
	 */
	public void println(char arg) {
		print(arg);
		println();
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#println(int)
	 */
	public void println(int arg) {
		print(arg);
		println();
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#println(long)
	 */
	public void println(long arg) {
		print(arg);
		println();
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#println(float)
	 */
	public void println(float arg) {
		print(arg);
		println();
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#println(double)
	 */
	public void println(double arg) {
		print(arg);
		println();
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#println(char[])
	 */
	public void println(char[] arg) {
		print(arg);
		println();
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#println(java.lang.String)
	 */
	public void println(String arg) {
		print(arg);
		println();
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#println(java.lang.Object)
	 */
	public void println(Object arg) {
		print(arg);
		println();
	}


	/**
	 * @see railo.runtime.writer.BodyContentImpl#newLine()
	 */
	public void newLine() {
		println();
	}
	

	/**
	 * @see railo.runtime.writer.BodyContentImpl#print(java.lang.String)
	 */
	public void print(String str) {
		write(str.toCharArray(),0,str.length());
	}
	

	/**
	 * @see railo.runtime.writer.BodyContentImpl#write(char[], int, int)
	 */
	public void write(char[] chars, int off, int len) {
		for(int i=off;i<len;i++) {
			print(chars[i]);
		}
	}
	/**
	 * @see railo.runtime.writer.BodyContentImpl#print(char[])
	 */
	public void print(char[] chars) {
		write(chars,0,chars.length);
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#write(java.lang.String, int, int)
	 */
	public void write(String str, int off, int len) {
		write(str.toCharArray(), off, len);
	}

	/**
	 * @see railo.runtime.writer.BodyContentImpl#write(java.lang.String)
	 */
	public void write(String str) {
		print(str);
	}
	/**
	 * @see railo.runtime.writer.BodyContentImpl#write(char[])
	 */
	public void write(char[] cbuf) {
		print(cbuf);
	}
	

	/**
	 * @see java.io.Writer#append(java.lang.CharSequence)
	 */
	public Writer append(CharSequence csq) throws IOException {
		write(csq.toString());
		return this;
	}

	/**
	 * @see java.io.Writer#append(java.lang.CharSequence, int, int)
	 */
	public Writer append(CharSequence csq, int start, int end) throws IOException {
		write(csq.subSequence(start, end).toString());
		return this;
	}

	/**
	 * @see java.io.Writer#append(char)
	 */
	public Writer append(char c) throws IOException {
		write(c);
		return this;
	}



	/**
	 * @see railo.runtime.writer.WhiteSpaceWriter#writeRaw(java.lang.String)
	 */
	public void writeRaw(String str) {
		printBuffer();
		super.write(str);
	}
}
