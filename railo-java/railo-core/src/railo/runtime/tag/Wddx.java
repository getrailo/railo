package railo.runtime.tag;

import java.io.IOException;

import javax.xml.parsers.FactoryConfigurationError;

import railo.runtime.converter.ConverterException;
import railo.runtime.converter.JSConverter;
import railo.runtime.converter.WDDXConverter;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;

/**
* Serializes and de-serializes CFML data structures to the XML-based WDDX format. 
*   Generates JavaScript statements to instantiate JavaScript objects equivalent to the contents of a 
*   WDDX packet or some CFML data structures.
*
*
*
**/
public final class Wddx extends TagImpl {

	/** The value to be processed. */
	private Object input;

	/** Specifies the action taken by the cfwddx tag. */
	private String action;

	/** The name of the variable to hold the output of the operation. This attribute is required for 
	** 		action = 'WDDX2CFML'. For all other actions, if this attribute is not provided, the result of the 
	** 		WDDX processing is outputted in the HTML stream. */
	private String output;

	private boolean validate;

	/** The name of the top-level JavaScript object created by the deserialization process. The object 
	** 		created is an instance of the WddxRecordset object, explained in WddxRecordset Object. */
	private String toplevelvariable;

	/** Indicates whether to output time-zone information when serializing CFML to WDDX. If time-zone 
	** 		information is taken into account, the hour-minute offset, as represented in the ISO8601 format, is 
	** 		calculated in the date-time output. If time-zone information is not taken into account, the local 
	** 		time is output. The default is Yes. */
	private boolean usetimezoneinfo;

	private boolean xmlConform;

	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		input=null;
		action=null;
		output=null;
		validate=false;
		toplevelvariable=null;
		usetimezoneinfo=false;
		xmlConform=false;
	}
	

	/** set the value input
	*  The value to be processed.
	* @param input value to set
	**/
	public void setInput(Object input)	{
		this.input=input;
	}

	/** set the value action
	*  Specifies the action taken by the cfwddx tag.
	* @param action value to set
	**/
	public void setAction(String action)	{
		this.action=action.toLowerCase();
	}

	/** set the value output
	*  The name of the variable to hold the output of the operation. This attribute is required for 
	* 		action = 'WDDX2CFML'. For all other actions, if this attribute is not provided, the result of the 
	* 		WDDX processing is outputted in the HTML stream.
	* @param output value to set
	**/
	public void setOutput(String output)	{
		this.output=output;
	}

	/** set the value validate
	*  
	* @param validate value to set
	**/
	public void setValidate(boolean validate)	{
		this.validate=validate;
	}

	/** set the value toplevelvariable
	*  The name of the top-level JavaScript object created by the deserialization process. The object 
	* 		created is an instance of the WddxRecordset object, explained in WddxRecordset Object.
	* @param toplevelvariable value to set
	**/
	public void setToplevelvariable(String toplevelvariable)	{
		this.toplevelvariable=toplevelvariable;
	}

	/** set the value usetimezoneinfo
	*  Indicates whether to output time-zone information when serializing CFML to WDDX. If time-zone 
	* 		information is taken into account, the hour-minute offset, as represented in the ISO8601 format, is 
	* 		calculated in the date-time output. If time-zone information is not taken into account, the local 
	* 		time is output. The default is Yes.
	* @param usetimezoneinfo value to set
	**/
	public void setUsetimezoneinfo(boolean usetimezoneinfo)	{
		this.usetimezoneinfo=usetimezoneinfo;
	}
	
	/**
	 * sets if generated code is xml or wddx conform
	 * @param xmlConform
	 */
	public void setXmlconform(boolean xmlConform)	{
		this.xmlConform=xmlConform;
	}


	/**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws PageException	{
		try {
			doIt();
			
		} catch (Exception e) {
			throw Caster.toPageException(e);
		}
		return SKIP_BODY;
	}
	private void doIt() throws ExpressionException, PageException, ConverterException, IOException, FactoryConfigurationError	{
	// cfml > wddx
		if(action.equals("cfml2wddx")) {
			if(output!=null) pageContext.setVariable(output,cfml2wddx(input));
			else pageContext.write(cfml2wddx(input));
		}
		
	// wddx > cfml
		else if(action.equals("wddx2cfml")) {
			if(output==null) throw new ApplicationException("at tag cfwddx the attribute output is required if you set action==wddx2cfml");
			pageContext.setVariable(output,wddx2cfml(Caster.toString(input)));
		}
		
	// cfml > js
		else if(action.equals("cfml2js")) {
			if(output!=null) pageContext.setVariable(output,cfml2js(input));
			else pageContext.write(cfml2js(input));
		}
		
	// wddx > js
		else if(action.equals("wddx2js")) {
			if(output!=null) pageContext.setVariable(output,wddx2js(Caster.toString(input)));
			else pageContext.write(wddx2js(Caster.toString(input)));
		}
		
		
		else throw new ExpressionException("invalid attribute action for tag cfwddx, attributes are [cfml2wddx, wddx2cfml,cfml2js, wddx2js].");

	}
	
	private String cfml2wddx(Object input) throws ConverterException {
		WDDXConverter converter =new WDDXConverter(pageContext.getTimeZone(),xmlConform);
		if(!usetimezoneinfo)converter.setTimeZone(null);
		return converter.serialize(input);
	}
	private Object wddx2cfml(String input) throws ConverterException, IOException, FactoryConfigurationError {
		WDDXConverter converter =new WDDXConverter(pageContext.getTimeZone(),xmlConform);
		converter.setTimeZone(pageContext.getTimeZone());
		return converter.deserialize(input,validate);
	}
	private String cfml2js(Object input) throws ConverterException {
		if(toplevelvariable==null)missingTopLevelVariable();
		JSConverter converter =new JSConverter();
		return converter.serialize(input,toplevelvariable);
	}
	private String wddx2js(String input) throws ConverterException, IOException, FactoryConfigurationError {
		if(toplevelvariable==null)missingTopLevelVariable();
		JSConverter converter =new JSConverter();
		return converter.serialize(wddx2cfml(input),toplevelvariable);
	}
	
	
	private ApplicationException missingTopLevelVariable() {
		return new ApplicationException("at tag cfwddx the attribute topLevelVariable is required if you set action equal wddx2js or cfml2js");
	}


	/**
	* @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}