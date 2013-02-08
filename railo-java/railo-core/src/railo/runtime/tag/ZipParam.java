package railo.runtime.tag;

import javax.servlet.jsp.tagext.Tag;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.io.res.util.WildcardPatternFilter;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;

public final class ZipParam extends TagImpl {
	
	private String charset;
	private Object content;
	private String entryPath;
	private String filter;
	private String prefix;
	private railo.commons.io.res.Resource source;
	private Boolean recurse=null;
	private Zip zip;
	


	@Override
	public void release()	{
		super.release();
		charset=null;
		content=null;
		entryPath=null;
		filter=null;
		prefix=null;
		source=null;
		recurse=null;
		zip=null;
	}
	
	
	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset=charset;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(Object content) {
		this.content=content;
	}

	/**
	 * @param entryPath the entryPath to set
	 */
	public void setEntrypath(String entryPath) {
		this.entryPath=entryPath;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(String filter) {
		this.filter=filter;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix=prefix;
	}

	/**
	 * @param strSource the source to set
	 * @throws PageException 
	 */
	public void setSource(String strSource) throws PageException {
		Resource zipSrc = getZip().getSource();
		if(zipSrc!=null)source=zipSrc.getRealResource(strSource);
		if(source==null || !source.exists())
			source=ResourceUtil.toResourceExisting(pageContext, strSource);
	}

	/**
	 * @param recurse the recurse to set
	 */
	public void setRecurse(boolean recurse) {
		this.recurse=Caster.toBoolean(recurse);
	}

	@Override
	public int doStartTag() throws PageException	{
		
		if(source!=null) {
			notAllowed("source","charset", charset);
			notAllowed("source","content", content);
		
			WildcardPatternFilter f = ( filter == null ? null : new WildcardPatternFilter( filter ) );
			getZip().setParam( new ZipParamSource( source, entryPath, f, prefix, recurse() ) );		
		}
		else if(content!=null) {
			required("content","entrypath",entryPath);
			notAllowed("content,entrypath","filter", filter);
			notAllowed("content,entrypath","prefix", prefix);
			notAllowed("content,entrypath","source", source);
			notAllowed("content,entrypath","recurse", recurse);
			
			getZip().setParam(new ZipParamContent(content,entryPath,charset));
		}
		/*else if(filter!=null) {
			notAllowed("filter","charset", charset);
			notAllowed("filter","content", content);
			notAllowed("filter","prefix", prefix);
			notAllowed("filter","source", source);
			getZip().setParam(new ZipParamFilter(filter,entryPath,recurse()));
		}
		else if(entryPath!=null) {
			notAllowed("entryPath","charset", charset);
			notAllowed("entryPath","content", content);
			notAllowed("entryPath","prefix", prefix);
			notAllowed("entryPath","source", source);
			getZip().setParam(new ZipParamFilter(filter,entryPath,recurse()));
		}*/
		else 
			throw new ApplicationException("invalid attribute combination");
			

		
		
		return SKIP_BODY;
	}

	private boolean recurse() {
		return recurse==null?true:recurse.booleanValue();
	}


	private Zip getZip() throws ApplicationException {
		if(zip!=null) return zip;
		Tag parent=getParent();
		while(parent!=null && !(parent instanceof Zip)) {
			parent=parent.getParent();
		}
		
		if(parent instanceof Zip) {
			return zip=(Zip)parent;
		}
		throw new ApplicationException("Wrong Context, tag ZipParam must be inside a Zip tag");	
	}


	private void notAllowed(String combi, String name, Object value) throws ApplicationException {
		if(value!=null)
			throw new ApplicationException("attribute ["+name+"] is not allowed in combination with attribute(s) ["+combi+"]");	
	}
	public void required(String combi, String name, Object value) throws ApplicationException {
		if(value==null)
			throw new ApplicationException("attribute ["+name+"] is required in combination with attribute(s) ["+combi+"]");	
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}