package railo.runtime.writer;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.jsp.JspWriter;

public abstract class CFMLWriter extends JspWriter {

	protected CFMLWriter(int bufferSize, boolean autoFlush) {
		super(bufferSize, autoFlush);
	}

	public abstract ServletOutputStream getServletOutputStream() throws IOException;

	public abstract void setClosed(boolean b) ;

	public abstract void setBufferConfig(int interval, boolean b) throws IOException ;

	public abstract void setToHTMLHead(String text) throws IOException;

}
