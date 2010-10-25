package railo.runtime.tag;

import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.tag.TagImpl;

/**
* Executes a Java servlet on a JRun engine. This tag is used in conjunction with the 
*   cfserletparam tag, which passes data to the servlet.
*
*
*
**/
public final class Servlet extends TagImpl {

	/** Boolean specifying whether additional information about the JRun connection status and 
	** 		activity is to be written to the JRun error log */
	private boolean debug;

	/** The class name of the Java servlet to execute. */
	private String code;

	/** Boolean specifying whether the text output of the tag should appear as inline text on the 
	** 		generated page or returned inside a ColdFusion variable for further processing . The default value, 
	** 		Yes, means output is returned as text to appear inline on the generated page. Setting it to No means 
	** 		no visible text is returned but, instead, the text is returned as the value of the cfservlet.output 
	** 		variable */
	private boolean writeoutput;

	/** Specifies how many seconds JRun waits for the servlet to complete before timing out. */
	private double timeout;

	/** A remote computer where the JRun engine is executing. By default, the JRun engine is assumed to 
	** 		be on the host running ColdFusion. To indicate the name of a remote host, specify the IP address of 
	** 		the remote host followed by a colon and the port number at which JRun is listening. By default, for 
	** 		the JCP server, JRun 2.3.3 listens at port 8081; JRun 3.0 listens at port 51000. */
	private String jrunproxy;


	/**
	* constructor for the tag class
	 * @throws ExpressionException
	**/
	public Servlet() throws ExpressionException {
		throw new ExpressionException("tag cfservlet is deprecated");
	}

	/** set the value debug
	*  Boolean specifying whether additional information about the JRun connection status and 
	* 		activity is to be written to the JRun error log
	* @param debug value to set
	**/
	public void setDebug(boolean debug)	{
		this.debug=debug;
	}

	/** set the value code
	*  The class name of the Java servlet to execute.
	* @param code value to set
	**/
	public void setCode(String code)	{
		this.code=code;
	}

	/** set the value writeoutput
	*  Boolean specifying whether the text output of the tag should appear as inline text on the 
	* 		generated page or returned inside a ColdFusion variable for further processing . The default value, 
	* 		Yes, means output is returned as text to appear inline on the generated page. Setting it to No means 
	* 		no visible text is returned but, instead, the text is returned as the value of the cfservlet.output 
	* 		variable
	* @param writeoutput value to set
	**/
	public void setWriteoutput(boolean writeoutput)	{
		this.writeoutput=writeoutput;
	}

	/** set the value timeout
	*  Specifies how many seconds JRun waits for the servlet to complete before timing out.
	* @param timeout value to set
	**/
	public void setTimeout(double timeout)	{
		this.timeout=timeout;
	}

	/** set the value jrunproxy
	*  A remote computer where the JRun engine is executing. By default, the JRun engine is assumed to 
	* 		be on the host running ColdFusion. To indicate the name of a remote host, specify the IP address of 
	* 		the remote host followed by a colon and the port number at which JRun is listening. By default, for 
	* 		the JCP server, JRun 2.3.3 listens at port 8081; JRun 3.0 listens at port 51000.
	* @param jrunproxy value to set
	**/
	public void setJrunproxy(String jrunproxy)	{
		this.jrunproxy=jrunproxy;
	}


	/**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag()	{
		return SKIP_BODY;
	}

	/**
	* @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag()	{
		return EVAL_PAGE;
	}

	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		debug=false;
		code="";
		writeoutput=false;
		timeout=0d;
		jrunproxy="";
	}
}