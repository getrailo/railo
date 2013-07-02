package railo.runtime.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import railo.commons.lang.StringUtil;
import railo.commons.net.HTTPUtil;
import railo.runtime.exp.Abort;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.NativeException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.op.Caster;


public final class Location extends TagImpl {

	/** Yes or No. clientManagement must be enabled. Yes appends client variable 
	** 		information to the URL you specify in the url. */
	private boolean addtoken=true;

	/** The URL of the HTML file or CFML page to open. */
	private String url="";
	
	private int statuscode=302;
	

	@Override
	public void release()	{
		super.release();
		addtoken=true;
		url="";
		statuscode=302;
	}

	/**
	 * @param statuscode the statuscode to set
	 * @throws ApplicationException 
	 */
	public void setStatuscode(double statuscode) throws ApplicationException {
		int sc=(int) statuscode;
		if(sc<300 || sc>307) 
			throw new ApplicationException("invalid value for attribute statuscode ["+Caster.toString(statuscode)+"]",
					"attribute must have one of the folloing values [300|301|302|303|304|305|307]");
			
		this.statuscode = sc;
	}


	/** set the value addtoken
	*  Yes or No. clientManagement must be enabled. Yes appends client variable 
	* 		information to the URL you specify in the url.
	* @param addtoken value to set
	**/
	public void setAddtoken(boolean addtoken)	{
		this.addtoken=addtoken;
	}

	/** set the value url
	*  The URL of the HTML file or CFML page to open.
	* @param url value to set
	 * @throws ApplicationException
	**/
	public void setUrl(String url) throws ApplicationException	{
		this.url=url.trim();
		if(this.url.length()==0) throw new ApplicationException("invalid url [empty string] for attribute url");
		if(StringUtil.hasLineFeed(url)) throw new ApplicationException("invalid url ["+url+"] for attribute url, carriage-return or line-feed inside the url are not allowed");
	}


	@Override
	public int doStartTag() throws PageException	{
		try {
			pageContext.getOut().clear();
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
		HttpServletResponse rsp = pageContext. getHttpServletResponse();
		
		url=HTTPUtil.encode(url);
		
		// add token
		if(addtoken && needId()) {
			String[] arr=url.split("\\?");
			
			// only string_name
			if(arr.length==1) {
				url+="?"+pageContext.getURLToken();
			}
			// script_name and query_string
			else if(arr.length>1) {
				url=arr[0]+"?"+pageContext.getURLToken();
				for(int i=1;i<arr.length;i++)url+="&"+arr[i]; 
			}
			url=rsp.encodeRedirectURL(url);	
		}
		
		rsp.setHeader("Connection", "close"); // IE unter IIS6, Win2K3 und Resin
		rsp.setStatus(statuscode);
		rsp.setHeader("location", url);
		
		
		try {
			pageContext.forceWrite("<html>\n<head>\n\t<title>Document Moved</title>\n");
			//pageContext.forceWrite("\t<script>window.location='"+JSStringFormat.invoke(url)+"';</script>\n");
			pageContext.forceWrite("</head>\n<body>\n\t<h1>Object Moved</h1>\n\t		 <a HREF=\""+url+"\">here</a>\n");
			pageContext.forceWrite("</body>\n</html>");
		} catch (IOException e) {
			throw new NativeException(e);
		}
        if(pageContext.getConfig().debug())pageContext.getDebugger().setOutput(false);
		throw new Abort(Abort.SCOPE_REQUEST);
	}
	
	


	private boolean needId() {
		ApplicationContext ac = pageContext.getApplicationContext();
		return ac.isSetClientManagement() || ac.isSetSessionManagement();
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}