package railo.runtime.tag;

import railo.runtime.exp.TagNotSupported;
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
	private String password;
	private String orderby;
	private String report;
	private String username;
	private String formula;


	/**
	* constructor for the tag class
	 * @throws TagNotSupported 
	**/
	public Report() throws TagNotSupported {
		// TODO implement tag
		throw new TagNotSupported("report");
	}

	/** set the value password
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
	* @param report value to set
	**/
	public void setReport(String report)	{
		this.report=report;
	}

	/** set the value username
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


	@Override
	public int doStartTag()	{
		return SKIP_BODY;
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}

	@Override
	public void doInitBody()	{
		
	}

	@Override
	public int doAfterBody()	{
		return SKIP_BODY;
	}

	@Override
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