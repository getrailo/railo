package railo.runtime.tag;

import java.io.StringReader;

import org.xml.sax.InputSource;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagImpl;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.text.xml.XMLUtil;

/**
* Creates a XML document object that contains the markup in the tag body. This tag can include XML and CFML tags. Railo processes the CFML code in the tag body, then assigns the resulting text to an XML document object variable.
*
*
*
**/
public final class Xml extends BodyTagImpl {

	/** name of an xml variable */
	private String variable;
	private String validator;

	/** yes: maintains the case of document elements and attributes */
	private boolean casesensitive;

	private String strXML;


	@Override
	public void release()	{
		super.release();
		variable=null;
		casesensitive=false;
		strXML=null;
		validator=null;
	}

	/** set the value variable
	*  name of an xml variable
	* @param variable value to set
	**/
	public void setVariable(String variable)	{
		this.variable=variable;
	}

	/** set the value casesensitive
	*  yes: maintains the case of document elements and attributes
	* @param casesensitive value to set
	**/
	public void setCasesensitive(boolean casesensitive)	{
		this.casesensitive=casesensitive;
	}


	@Override
	public int doStartTag()	{
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public int doEndTag() throws PageException	{
		try {
			InputSource vis = StringUtil.isEmpty(validator)?null:XMLUtil.toInputSource(pageContext,validator);
			pageContext.setVariable(variable,XMLCaster.toXMLStruct(XMLUtil.parse(new InputSource(new StringReader(strXML)),vis,false),casesensitive));
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
		
		return EVAL_PAGE;
	}

	@Override
	public void doInitBody()	{
		
	}

	@Override
	public int doAfterBody()	{
		strXML=bodyContent.getString().trim();
		return SKIP_BODY;
	}

	/**
	 * @param validator the validator to set
	 */
	public void setValidator(String validator) {
		this.validator = validator;
	}
}