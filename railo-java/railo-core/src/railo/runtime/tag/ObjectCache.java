package railo.runtime.tag;

import org.apache.oro.text.regex.MalformedPatternException;

import railo.commons.lang.StringUtil;
import railo.runtime.cache.tag.CacheHandlerFactory;
import railo.runtime.cache.tag.CacheHandlerFilter;
import railo.runtime.cache.tag.query.QueryCacheHandlerFilter;
import railo.runtime.cache.tag.query.QueryCacheHandlerFilterUDF;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;
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
	private CacheHandlerFilter filter;
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
	
	
	
	public static CacheHandlerFilter createFilter(Object filter,boolean ignoreCase) throws PageException	{
	   if(filter instanceof UDF)
		   return new QueryCacheHandlerFilterUDF((UDF)filter);
		String sql=Caster.toString(filter,null);
		if(!StringUtil.isEmpty(sql,true)) {
			try {
				return new QueryCacheHandlerFilter(sql,ignoreCase);
			}
			catch (MalformedPatternException e) {
				throw Caster.toPageException(e);
			}
		}
		return null;
	}
	
	/*public static CacheHandlerFilter createFilterx(String sql)	{
	    if(!StringUtil.isEmpty(sql,true)) {
            return new QueryCacheHandlerFilter(sql);
        }
	    return null;
	}*/

	@Override
	public int doStartTag() throws PageException	{
		_doStartTag();
		return SKIP_BODY;
	}
	public void _doStartTag() throws PageException	{
		if(action.equalsIgnoreCase("clear")) {
			if(filter==null)
				ConfigWebUtil.getCacheHandlerFactories(pageContext.getConfig()).query.clear(pageContext);
		    	//qc.clear(pageContext);
		    else
		    	ConfigWebUtil.getCacheHandlerFactories(pageContext.getConfig()).query.clear(pageContext,filter);
	    		//qc.clear(pageContext,filter);
		}
		else if(action.equalsIgnoreCase("size")) {
			pageContext.setVariable(result, Caster.toDouble(ConfigWebUtil.getCacheHandlerFactories(pageContext.getConfig()).query.size(pageContext)));
			//pageContext.setVariable(result, Caster.toDouble(qc.size(pageContext)));
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