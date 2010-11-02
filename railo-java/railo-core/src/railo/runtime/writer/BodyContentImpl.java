package railo.runtime.writer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;

import railo.commons.lang.CharBuffer;


/**
 * implementation of the BodyContent
 */
public class BodyContentImpl extends BodyContent {
	
	CharBuffer charBuffer=new CharBuffer(128);
	JspWriter enclosingWriter;

	/**
	 * default constructor
	 * @param jspWriter
	 */
	public BodyContentImpl(JspWriter jspWriter) {
		super(jspWriter);
		enclosingWriter=jspWriter;
		
	}

	/**
	 * initialize the BodyContent with the enclosing jsp writer
	 * @param jspWriter
	 */
	public void init(JspWriter jspWriter) {
		enclosingWriter=jspWriter;
		clearBuffer();
		
	}

	/**
	 * @see javax.servlet.jsp.tagext.BodyContent#getReader()
	 */
	public Reader getReader() {
		return new StringReader(charBuffer.toString());
	}

	/**
	 * @see javax.servlet.jsp.tagext.BodyContent#getString()
	 */
	public String getString() {
		return charBuffer.toString();
	}

	/**
	 * @see javax.servlet.jsp.tagext.BodyContent#writeOut(java.io.Writer)
	 */
	public void writeOut(Writer writer) throws IOException {
		charBuffer.writeOut(writer);
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#newLine()
	 */
	public void newLine() {
		println();
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#print(boolean)
	 */
	public void print(boolean arg) {
		print(arg?"true":"false");
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#print(char)
	 */
	public void print(char arg) {
		charBuffer.append(String.valueOf(arg));
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#print(int)
	 */
	public void print(int arg) {
		charBuffer.append(String.valueOf(arg));
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#print(long)
	 */
	public void print(long arg) {
		charBuffer.append(String.valueOf(arg));
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#print(float)
	 */
	public void print(float arg) {
		charBuffer.append(String.valueOf(arg));
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#print(double)
	 */
	public void print(double arg) {
		charBuffer.append(String.valueOf(arg));
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#print(char[])
	 */
	public void print(char[] arg) {
		charBuffer.append(arg);
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#print(java.lang.String)
	 */
	public void print(String arg) {
		charBuffer.append(arg);
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#print(java.lang.Object)
	 */
	public void print(Object arg) {		
		charBuffer.append(String.valueOf(arg));
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#println()
	 */
	public void println() {
		charBuffer.append("\n");
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#println(boolean)
	 */
	public void println(boolean arg) {
		print(arg);
		println();
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#println(char)
	 */
	public void println(char arg) {
		print(arg);
		println();
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#println(int)
	 */
	public void println(int arg) {
		print(arg);
		println();
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#println(long)
	 */
	public void println(long arg) {
		print(arg);
		println();
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#println(float)
	 */
	public void println(float arg) {
		print(arg);
		println();
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#println(double)
	 */
	public void println(double arg) {
		print(arg);
		println();
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#println(char[])
	 */
	public void println(char[] arg) {
		print(arg);
		println();
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#println(java.lang.String)
	 */
	public void println(String arg) {
		print(arg);
		println();
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#println(java.lang.Object)
	 */
	public void println(Object arg) {
		print(arg);
		println();
	}

	/**
	 * @throws IOException 
	 * @see javax.servlet.jsp.JspWriter#clear()
	 */
	public void clear() throws IOException {
		charBuffer.clear();
        enclosingWriter.clear();
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#clearBuffer()
	 */
	public void clearBuffer() {
		charBuffer.clear();
	}
	
	/**
	 * @see java.io.Writer#flush()
	 */
	public void flush() throws IOException {
		enclosingWriter.write(charBuffer.toCharArray());
		charBuffer.clear();
	}

	/**
	 * @see java.io.Writer#close()
	 */
	public void close() throws IOException {
		flush();
		enclosingWriter.close();
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#getRemaining()
	 */
	public int getRemaining() {
		return bufferSize-charBuffer.size();
	}

	/**
	 * @see java.io.Writer#write(char[], int, int)
	 */
	public void write(char[] cbuf, int off, int len) {
		charBuffer.append(cbuf,off,len);
	}

	/**
	 * @see java.io.Writer#write(char[])
	 */
	public void write(char[] cbuf) {
		charBuffer.append(cbuf);
	}

	/**
	 * @see java.io.Writer#write(int)
	 */
	public void write(int c) {
		print(c);
	}

	/**
	 * @see java.io.Writer#write(java.lang.String, int, int)
	 */
	public void write(String str, int off, int len) {
		charBuffer.append(str,off,len);
	}

	/**
	 * @see java.io.Writer#write(java.lang.String)
	 */
	public void write(String str) {
		charBuffer.append(str);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return charBuffer.toString();
	}

	/**
	 * @see javax.servlet.jsp.tagext.BodyContent#clearBody()
	 */
	public void clearBody() {
		charBuffer.clear();
	}

	/**
	 * @see javax.servlet.jsp.tagext.BodyContent#getEnclosingWriter()
	 */
	public JspWriter getEnclosingWriter() {
		return enclosingWriter;
	}

	/**
	 * returns the inner char buffer
	 * @return intern CharBuffer
	 */
	public CharBuffer getCharBuffer() {
		return charBuffer;
	}

	/**
	 * sets the inner Charbuffer
	 * @param charBuffer
	 */
	public void setCharBuffer(CharBuffer charBuffer) {
		this.charBuffer=charBuffer;
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#getBufferSize()
	 */
	public int getBufferSize() {
		return charBuffer.size();
	}

	/**
	 * @see javax.servlet.jsp.JspWriter#isAutoFlush()
	 */
	public boolean isAutoFlush() {
		return super.isAutoFlush();
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
	


	
}