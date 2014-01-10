package railo.runtime.tag;

import org.apache.oro.text.regex.MalformedPatternException;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;
import railo.runtime.query.QueryCacheFilter;
import railo.runtime.query.QueryCacheFilterImpl;
import railo.runtime.query.QueryCacheFilterUDF;
import railo.runtime.query.QueryCacheSupport;
import railo.runtime.type.UDF;

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

	public void setFilter(Object filter) throws PageException	{
		this.filter=createFilter(filter, false);
	}

	public void setFilter(String filter) throws PageException	{
		this.filter=createFilter(filter, false);
	}
	
	public void setFilterignorecase(String filter) throws PageException	{
		this.filter=createFilter(filter, true);
	}
	
	
	
	public static QueryCacheFilter createFilter(Object filter,boolean ignoreCase) throws PageException	{
	   if(filter instanceof UDF)
		   return createFilter((UDF)filter);
	   return createFilter(Caster.toString(filter),ignoreCase);
	}

	
	public static QueryCacheFilter createFilter(UDF filter) throws PageException	{
		return new QueryCacheFilterUDF(filter);
	}
	
	public static QueryCacheFilter createFilter(String pattern,boolean ignoreCase) throws PageException	{
	    if(!StringUtil.isEmpty(pattern,true)) {
            try {
            	return new QueryCacheFilterImpl(pattern,ignoreCase);
            } catch (MalformedPatternException e) {
                throw Caster.toPageException(e);
            }
        }
	    return null;
	}
	
	
	
	
	


	@Override
	public int doStartTag() throws PageException	{
		_doStartTag();
		return SKIP_BODY;
	}
	public void _doStartTag() throws PageException	{
		QueryCacheSupport qc = ((QueryCacheSupport)pageContext.getQueryCache());
		if(action.equalsIgnoreCase("clear")) {
			if(filter==null)
		    	qc.clear(pageContext);
		    else
		    	qc.clear(pageContext,filter);
		}
		else if(action.equalsIgnoreCase("size")) {
			pageContext.setVariable(result, Caster.toDouble(qc.size(pageContext)));
		}
		else throw new ApplicationException("attribute action has an invalid value ["+action+"], valid is only [clear,size]");
		
	    
		
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}

	@Override
	public void release()	{
		super.release();
		action="clear";
		result="cfObjectCache";
		filter=null;
	}
}