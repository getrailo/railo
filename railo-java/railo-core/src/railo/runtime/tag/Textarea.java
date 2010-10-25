package railo.runtime.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;

// TODO tag textarea
// attribute html macht irgendwie keinen sinn, aber auch unter neo nicht



public final class Textarea extends Input  implements BodyTag {
	private static final String BASE_PATH = null; // TODO
	private static final String STYLE_XML = null;
	private static final String TEMPLATE_XML = null;
	private static final String SKIN = "default";
	private static final String TOOLBAR = "default";
	
	private static final int WRAP_OFF = 0;
	private static final int WRAP_HARD = 1;
	private static final int WRAP_SOFT = 2;
	private static final int WRAP_PHYSICAL = 3;
	private static final int WRAP_VIRTUAL = 4;
	private static final Collection.Key NAME = KeyImpl.getInstance("name");
	private static final Collection.Key ID = KeyImpl.getInstance("id");
	
	private BodyContent bodyContent=null;

	private String basepath=BASE_PATH;
	private String fontFormats=null;
	private String fontNames=null;
	private String fontSizes=null;
	
	private boolean html=false;
	private boolean richText=false;
	private String skin=SKIN;
	private String stylesXML=STYLE_XML;
	private String templatesXML=TEMPLATE_XML;
	private String toolbar=TOOLBAR;
	private boolean toolbarOnFocus=false;
	private int wrap=WRAP_OFF;
	
	/**
	 * @see javax.servlet.jsp.tagext.Tag#release()
	 */
	public void release() {
		super.release();
		bodyContent=null;
		

		basepath=BASE_PATH;
		fontFormats=null;
		fontNames=null;
		fontSizes=null;
		
		html=false;
		richText=false;
		skin=SKIN;
		stylesXML=STYLE_XML;
		templatesXML=TEMPLATE_XML;
		toolbar=TOOLBAR;
		toolbarOnFocus=false;
		wrap=WRAP_OFF;
	}

	public void setCols(double cols) throws PageException {
		attributes.set("cols", Caster.toString(cols));
	}
	public void setRows(double rows) throws PageException {
		attributes.set("rows", Caster.toString(rows));
	}
	public void setBasepath(String basepath) {
		this.basepath=basepath;
	}
	public void setFontFormats(String fontFormats) {
		this.fontFormats=fontFormats;
	}
	public void setFontNames(String fontNames) {
		this.fontNames=fontNames;
	}
	public void setFontSizes(String fontSizes) {
		this.fontSizes=fontSizes;
	}
	public void setHtml(boolean html) {
		this.html=html;
	}
	public void setRichtext(boolean richText) {
		this.richText = richText;
	}
	public void setSkin(String skin) {
		this.skin = skin;
	}
	public void setStylesxml(String stylesXML) {
		this.stylesXML = stylesXML;
	}
	public void setTemplatesxml(String templatesXML) {
		this.templatesXML = templatesXML;
	}
	public void setToolbar(String toolbar) {
		this.toolbar = toolbar;
	}
	public void setToolbaronfocus(boolean toolbarOnFocus) {
		this.toolbarOnFocus = toolbarOnFocus;
	}
	public void setWrap(String strWrap) throws ExpressionException {
		strWrap=strWrap.trim().toLowerCase();
		if("hard".equals(strWrap))			wrap=WRAP_HARD;
		else if("soft".equals(strWrap))		wrap=WRAP_SOFT;
		else if("off".equals(strWrap))		wrap=WRAP_OFF;
		else if("physical".equals(strWrap))	wrap=WRAP_PHYSICAL;
		else if("virtual".equals(strWrap))	wrap=WRAP_VIRTUAL;
		else throw new ExpressionException("invalid value ["+strWrap+"] for attribute wrap, valid values are [hard,soft,off,physical,virtual]");		
	}

	/**
	 *
	 * @see railo.runtime.tag.Input#draw()
	 */
	void draw() throws IOException, PageException {
		
		// value
		String attrValue=null;
		String bodyValue=null;
		String value="";
		if(bodyContent!=null)bodyValue=bodyContent.getString();
		if(attributes.containsKey("value"))attrValue=Caster.toString(attributes.get("value",null));
		
		// check values
        if(!StringUtil.isEmpty(bodyValue) && !StringUtil.isEmpty(attrValue)) {
        	throw new ApplicationException("the value of tag can't be set twice (tag body and attribute value)");
        }
        else if(!StringUtil.isEmpty(bodyValue)){
        	value=enc(bodyValue);
        }
        else if(!StringUtil.isEmpty(attrValue)){
        	value=enc(attrValue);
        }
        // id
		if(StringUtil.isEmpty(attributes.get(ID,null)))
			attributes.set(ID,StringUtil.toVariableName((String)attributes.get(NAME)));
		
		// start output
        pageContext.write("<textarea");
        
        railo.runtime.type.Collection.Key[] keys = attributes.keys();
        railo.runtime.type.Collection.Key key;
        for(int i=0;i<keys.length;i++) {
            key = keys[i];
            pageContext.write(" ");
            pageContext.write(key.getString());
            pageContext.write("=\"");
            pageContext.write(enc(Caster.toString(attributes.get(key,null))));
            pageContext.write("\"");
        }
        
        if(passthrough!=null) {
            pageContext.write(" ");
            pageContext.write(passthrough);
        }
        pageContext.write(">");
        pageContext.write(value);
        pageContext.write("</textarea>");
	}

	/**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag()	{
		return EVAL_BODY_BUFFERED;
	}
	
	/**
	 * @see javax.servlet.jsp.tagext.BodyTag#setBodyContent(javax.servlet.jsp.tagext.BodyContent)
	 */
	public void setBodyContent(BodyContent bodyContent) {
		this.bodyContent=bodyContent;
	}

	/**
	 * @see javax.servlet.jsp.tagext.BodyTag#doInitBody()
	 */
	public void doInitBody() throws JspException {}

	/**
	 * @see javax.servlet.jsp.tagext.IterationTag#doAfterBody()
	 */
	public int doAfterBody() throws JspException {
		return SKIP_BODY;
	}
	public void hasBody(boolean hasBody) {
	    
	}
}
