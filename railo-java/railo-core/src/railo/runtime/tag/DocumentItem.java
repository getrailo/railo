package railo.runtime.tag;

import java.io.IOException;
import java.security.InvalidParameterException;

import javax.servlet.jsp.tagext.Tag;

import railo.commons.lang.StringUtil;
import railo.commons.pdf.PDFPageMark;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagImpl;
import railo.runtime.op.Caster;

public final class DocumentItem extends BodyTagImpl {

	private static final int TYPE_PAGE_BREAK = 0;
	private static final int TYPE_HEADER = 1;
	private static final int TYPE_FOOTER = 2;
	private static final int TYPE_BOOKMARK = 3;

	private int type;
	private String name;
	private PDFPageMark body;
	
	/**
	 * @see railo.runtime.ext.tag.BodyTagImpl#release()
	 */
	public void release() {
		super.release();
		this.body=null;
		name=null;
	}

	/**
	 * @param type the type to set
	 * @throws ApplicationException 
	 */
	public void setType(String strType) throws ApplicationException {
		strType=StringUtil.toLowerCase(strType.trim());
		if("pagebreak".equals(strType))		type=TYPE_PAGE_BREAK;
		else if("header".equals(strType))	type=TYPE_HEADER;
		else if("footer".equals(strType))	type=TYPE_FOOTER;
		else if("bookmark".equals(strType))	type=TYPE_BOOKMARK;
		else throw new ApplicationException("invalid type ["+strType+"], valid types are [pagebreak,header,footer,bookmark]");
		//else throw new ApplicationException("invalid type ["+strType+"], valid types are [pagebreak,header,footer]");
		
	}


    /**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag()	{
		return EVAL_BODY_BUFFERED;
	}

	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doInitBody()
	*/
	public void doInitBody()	{}
	
	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doAfterBody()
	*/
	public int doAfterBody()	{
		if(TYPE_HEADER==type || TYPE_FOOTER==type) {
			body=new PDFPageMark(-1,translate(bodyContent.getString()));
		}
		
		return SKIP_BODY;
	}
	
	private String translate(String html) {
		html=StringUtil.replace(html.trim(), "{currentsectionpagenumber}", "${page}", false);
		html=StringUtil.replace(html, "{totalsectionpagecount}", "${total}", false);
		
		html=StringUtil.replace(html.trim(), "{currentpagenumber}", "${page}", false);
		html=StringUtil.replace(html, "{totalpagecount}", "${total}", false);
		

	    //cfdoc.setEL("currentpagenumber", "{currentpagenumber}");
	    //cfdoc.setEL("totalpagecount", "{totalpagecount}");
	    
		
		return html;
	}

	/**
	 *
	 * @throws IOException 
	 * @throws InvalidParameterException 
	 * @see railo.runtime.ext.tag.TagImpl#doEndTag()
	 */
	public int doEndTag() throws PageException {
		try {
			_doEndTag();
		}
		catch (Exception e) {
			throw Caster.toPageException(e);
		}	
		return EVAL_PAGE;
	}
	private void _doEndTag() throws IOException, ApplicationException {
		if(TYPE_PAGE_BREAK==type) {
			pageContext.forceWrite("<pd4ml:page.break>");
			return;
		}
		else if(TYPE_BOOKMARK==type) {
			if(StringUtil.isEmpty(name))
				throw new ApplicationException("attribute [name] is required when type is [bookmark]");
			pageContext.write("<pd4ml:bookmark>"+name+"</pd4ml:bookmark>");
		}
		else if(body!=null) {
			provideDocumentItem();
		}
		
	}

	private void provideDocumentItem() 	{
		// get Document Tag
		Tag parent=getParent();
		while(parent!=null && !(parent instanceof Document) && !(parent instanceof DocumentSection)) {
			parent=parent.getParent();
		}

		if(parent instanceof Document) {
			Document doc = (Document)parent;
			if(TYPE_HEADER==type)doc.setHeader(body);
			else if(TYPE_FOOTER==type)doc.setFooter(body);
			return ;
		}
		else if(parent instanceof DocumentSection) {
			DocumentSection doc = (DocumentSection)parent;
			if(TYPE_HEADER==type)doc.setHeader(body);
			else if(TYPE_FOOTER==type)doc.setFooter(body);
			return ;
		}
	}
	
	/**
	 * sets if has body or not
	 * @param hasBody
	 */
	public void hasBody(boolean hasBody) {
	    
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
