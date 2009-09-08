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
	private String action="clear";
	private QueryCacheFilter filter;
	private String result="cfObjectCache";

	/** set the value action
	*  Clears queries from the cache in the Application scope.
	* @param action value to set
	**/
	public void setAction(String action)	{
		this.action=action;
	}
	public void setResult(String result)	{
		this.result=result;
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
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws PageException	{
		_doStartTag();
		return SKIP_BODY;
	}
	public void _doStartTag() throws PageException	{
		QueryCacheImpl qc = ((QueryCacheImpl)pageContext.getQueryCache());
		if(action.equalsIgnoreCase("clear")) {
			if(filter==null)
		    	qc.clear();
		    else
		    	qc.clear(filter);
		}
		else if(action.equalsIgnoreCase("size")) {
			pageContext.setVariable(result, Caster.toDouble(qc.size()));
		}
		else throw new ApplicationException("attribute action has a invalid value ["+action+"], valid is only [clear,size]");
		
	    
		
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
		action="clear";
		result="cfObjectCache";
		filter=null;
	}
}