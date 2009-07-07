package railo.runtime.tag;

import org.apache.oro.text.regex.MalformedPatternException;

import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;
import railo.runtime.query.QueryCacheFilter;
import railo.runtime.query.QueryCacheFilterImpl;
import railo.runtime.query.QueryCacheImpl;

/**
* Flushes the query cache
*
*
*
**/
public final class ObjectCache extends TagImpl {

	/** Clears queries from the cache in the Application scope. */
	private String action="";
	private QueryCacheFilter filter;

	/** set the value action
	*  Clears queries from the cache in the Application scope.
	* @param action value to set
	**/
	public void setAction(String action)	{
		this.action=action;
	}

	private void setFilter(String filter,boolean ignoreCase) throws PageException	{
		try {
			this.filter=new QueryCacheFilterImpl(filter,ignoreCase);
		} catch (MalformedPatternException e) {
			throw Caster.toPageException(e);
		}
	}
	public void setFilter(String filter) throws PageException	{
		setFilter(filter, false);
	}
	public void setFilterignorecase(String filter) throws PageException	{
		setFilter(filter, true);
	}


	/**
	* @throws ApplicationException
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws ApplicationException	{
	    if(!action.equalsIgnoreCase("clear")) throw new ApplicationException("attribute action has a invalid value ["+action+"], valid is only [clear]");
	    
	    
	    if(filter==null)
	    	pageContext.getQueryCache().clear();
	    else
	    	((QueryCacheImpl)pageContext.getQueryCache()).clear(filter);
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
		action="";
		filter=null;
	}
}