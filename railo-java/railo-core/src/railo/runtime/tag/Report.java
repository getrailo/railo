package railo.runtime.tag;

import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.tag.BodyTagImpl;

/**
* Runs a predefined Crystal Reports report.
*
*
*
**/
public final class Report extends BodyTagImpl {
	
	private String template;
	private String format;
	private String name;
	private String filename;
	private String query;
	private boolean overwrite;
	private String encryption;
	private String ownerpassword;
	private String userpassword;
	private String permissions;
	private String datasource;
	private String type;
	private double timeout;

	/** The password that corresponds to a username required for database access. Overrides the default 
	** 		settings for the data source in the ColdFusion Administrator. */
	private String password;

	/** Orders results according to your specifications. */
	private String orderby;

	/** Specifies the report path. Store Crystal Reports files in the same directories as ColdFusion 
	** 		page files. */
	private String report;

	/** The username required for entry into the database from which the report is created. Overrides 
	** 		the default settings for the data source in the ColdFusion Administrator. */
	private String username;

	/** Specifies one or more named formulas. Terminate each formula specification with a semicolon. */
	private String formula;


	/**
	* constructor for the tag class
	**/
	public Report() throws ExpressionException {
		// TODO implement tag
		throw new ExpressionException("tag cfreport ( railo.runtime.tag.Report ) is not supported");
	}

	/** set the value password
	*  The password that corresponds to a username required for database access. Overrides the default 
	* 		settings for the data source in the ColdFusion Administrator.
	* @param password value to set
	**/
	public void setPassword(String password)	{
		this.password=password;
	}

	/** set the value orderby
	*  Orders results according to your specifications.
	* @param orderby value to set
	**/
	public void setOrderby(String orderby)	{
		this.orderby=orderby;
	}

	/** set the value report
	*  Specifies the report path. Store Crystal Reports files in the same directories as ColdFusion 
	* 		page files.
	* @param report value to set
	**/
	public void setReport(String report)	{
		this.report=report;
	}

	/** set the value username
	*  The username required for entry into the database from which the report is created. Overrides 
	* 		the default settings for the data source in the ColdFusion Administrator.
	* @param username value to set
	**/
	public void setUsername(String username)	{
		this.username=username;
	}

	/** set the value formula
	*  Specifies one or more named formulas. Terminate each formula specification with a semicolon.
	* @param formula value to set
	**/
	public void setFormula(String formula)	{
		this.formula=formula;
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

	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		password="";
		orderby="";
		report="";
		username="";
		formula="";
		
		template="";
		format="";
		name="";
		filename="";
		query="";
		overwrite=false;
		encryption="";
		ownerpassword="";
		userpassword="";
		permissions="";
		datasource="";
		type="";
		timeout=0;
	}

	public void addReportParam(ReportParamBean param) {
		// TODO Auto-generated method stub
		
	}
}