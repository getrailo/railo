package railo.runtime.tag;

import java.io.IOException;

import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpWriter;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagImpl;

/**
* Stops the time from starttag to endtag
*
*
*
**/
public final class Stopwatch extends BodyTagImpl {

	private String label;
	private long time;
    private String variable;

	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		label=null;
		time=0L;
		variable=null;
	}

	/** Label of the Stopwatch
	* @param label sets the Label of the Stopwatch
	**/
	public void setLabel(String label)	{
		this.label=label;
	}

	/**
	 * Variable Name to write result to it
	 * @param variable  variable name
	 */
	public void setVariable(String variable)	{
		this.variable=variable;
	}


	/**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag()	{
		time=System.currentTimeMillis();
		return EVAL_BODY_INCLUDE;
	}

	/**
	* @throws PageException
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag() throws PageException	{
		long exe = (System.currentTimeMillis()-time);
		
		if(variable!=null) {
		    pageContext.setVariable(variable,new Double(exe));
		}
		else {
			DumpTable table = new DumpTable("#ffb200","#ffcc00","#263300");
			table.appendRow(1,new SimpleDumpData(label==null?"Stopwatch":label),new SimpleDumpData(exe));
			DumpWriter writer=pageContext.getConfig().getDefaultDumpWriter();
			try {
				
				pageContext.forceWrite(writer.toString(pageContext,table,true));
			} 
			catch (IOException e) {}
		}
		return EVAL_PAGE;
	}

	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doInitBody()
	*/
	public void doInitBody()	{
		
	}

	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doAfterBody()
	*/
	public int doAfterBody()	{
		return SKIP_BODY;
	}
}