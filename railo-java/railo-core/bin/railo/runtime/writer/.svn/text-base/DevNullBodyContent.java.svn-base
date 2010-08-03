package railo.runtime.writer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;


/**
 * BodyContent implementation that dont store input
 */
public final class DevNullBodyContent extends BodyContent {
	
	private JspWriter enclosingWriter;
	
	/**
	 * default constructor
	 */
	public DevNullBodyContent() {
		super(null);
	}
	
	/**
	 * @see javax.servlet.jsp.tagext.BodyContent#getReader()
	 */
	public Reader getReader() {
		return new StringReader("");
	}
	
	/**
	 * @see javax.servlet.jsp.tagext.BodyContent#getString()
	 */
	public String getString() {
		return "";
	}
	
	/**
	 * 
	 * @see javax.servlet.jsp.tagext.BodyContent#writeOut(java.io.Writer)
	 */
	public void writeOut(Writer writer) {
		
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#newLine()
	 */
	public void newLine() {
		
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#print(boolean)
	 */
	public void print(boolean b) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#print(char)
	 */
	public void print(char c) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#print(int)
	 */
	public void print(int i) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#print(long)
	 */
	public void print(long l) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#print(float)
	 */
	public void print(float f) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#print(double)
	 */
	public void print(double d) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#print(char[])
	 */
	public void print(char[] c) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#print(java.lang.String)
	 */
	public void print(String str) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#print(java.lang.Object)
	 */
	public void print(Object o) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#println()
	 */
	public void println() {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#println(boolean)
	 */
	public void println(boolean b) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#println(char)
	 */
	public void println(char c) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#println(int)
	 */
	public void println(int i) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#println(long)
	 */
	public void println(long l) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#println(float)
	 */
	public void println(float f) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#println(double)
	 */
	public void println(double d) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#println(char[])
	 */
	public void println(char[] c) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#println(java.lang.String)
	 */
	public void println(String str) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#println(java.lang.Object)
	 */
	public void println(Object o) {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#clear()
	 */
	public void clear() {
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#clearBuffer()
	 */
	public void clearBuffer() {
	}
	
	/**
	 * @see java.io.Writer#close()
	 */
	public void close() throws IOException {
		enclosingWriter.close();
	}
	/**
	 * @see javax.servlet.jsp.JspWriter#getRemaining()
	 */
	public int getRemaining() {
		return 0;
	}
	
	/**
	 * @see java.io.Writer#write(char[], int, int)
	 */
	public void write(char[] cbuf, int off, int len) {
	}
	
	/**
	 * @see javax.servlet.jsp.tagext.BodyContent#clearBody()
	 */
	public void clearBody() {
		
	}
	/**
	 * @see java.io.Writer#flush()
	 */
	public void flush() throws IOException {
		enclosingWriter.flush();
	}
	
	/**
	 * @see javax.servlet.jsp.tagext.BodyContent#getEnclosingWriter()
	 */
	public JspWriter getEnclosingWriter() {
		return enclosingWriter;
	}
	
	/**
	 * @see javax.servlet.jsp.JspWriter#getBufferSize()
	 */
	public int getBufferSize() {
		return 0;
	}
	/**
	 * @see javax.servlet.jsp.JspWriter#isAutoFlush()
	 */
	public boolean isAutoFlush() {
		return false;
	}
}