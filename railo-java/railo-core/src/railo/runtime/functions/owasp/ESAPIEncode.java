package railo.runtime.functions.owasp;

import java.io.PrintStream;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.errors.EncodingException;

import railo.commons.io.DevNullOutputStream;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public class ESAPIEncode implements Function {
	
	private static final long serialVersionUID = -6432679747287827759L;
	
	public static final short ENC_BASE64=1;
	public static final short ENC_CSS=2;
	public static final short ENC_DN=3;
	public static final short ENC_HTML=4;
	public static final short ENC_HTML_ATTR=5;
	public static final short ENC_JAVA_SCRIPT=6;
	public static final short ENC_LDAP=7;
	public static final short ENC_OS=8;
	public static final short ENC_SQl=9;
	public static final short ENC_URL=10;
	public static final short ENC_VB_SCRIPT=11;
	public static final short ENC_XML=12;
	public static final short ENC_XML_ATTR=13;
	public static final short ENC_XPATH=14;
	
	
	public static String encode(String item, short encFor) throws PageException  {
		
		PrintStream out = System.out;
		try {
			 System.setOut(new PrintStream(DevNullOutputStream.DEV_NULL_OUTPUT_STREAM));
			 Encoder encoder = ESAPI.encoder();
			 switch(encFor){
			 //case ENC_CSS:return encoder.encodeForBase64(item);
			 case ENC_CSS:return encoder.encodeForCSS(item);
			 case ENC_DN:return encoder.encodeForDN(item);
			 case ENC_HTML:return encoder.encodeForHTML(item);
			 case ENC_HTML_ATTR:return encoder.encodeForHTMLAttribute(item);
			 case ENC_JAVA_SCRIPT:return encoder.encodeForJavaScript(item);
			 case ENC_LDAP:return encoder.encodeForLDAP(item);
			 //case ENC_CSS:return encoder.encodeForOS(arg0, arg1)(item);
			 //case ENC_CSS:return encoder.encodeForSQL(arg0, arg1)CSS(item);
			 case ENC_URL:return encoder.encodeForURL(item);
			 case ENC_VB_SCRIPT:return encoder.encodeForVBScript(item);
			 case ENC_XML:return encoder.encodeForXML(item);
			 case ENC_XML_ATTR:return encoder.encodeForXMLAttribute(item);
			 case ENC_XPATH:return encoder.encodeForXPath(item);
			 }
			 throw new ApplicationException("invalid target encoding defintion");
		}
		catch(EncodingException ee){
			throw Caster.toPageException(ee);
		}
		finally {
			 System.setOut(out);
		}
	}
	
	public static String call(PageContext pc , String strEncodeFor, String value) throws PageException{
		short encFor;
		strEncodeFor=StringUtil.emptyIfNull(strEncodeFor).trim().toLowerCase();
		//if("base64".equals(strEncodeFor)) encFor=ENC_BASE64;
		if("css".equals(strEncodeFor)) encFor=ENC_CSS;
		else if("dn".equals(strEncodeFor)) encFor=ENC_DN;
		else if("html".equals(strEncodeFor)) encFor=ENC_HTML;
		else if("html_attr".equals(strEncodeFor)) encFor=ENC_HTML_ATTR;
		else if("htmlattr".equals(strEncodeFor)) encFor=ENC_HTML_ATTR;
		else if("html-attr".equals(strEncodeFor)) encFor=ENC_HTML_ATTR;
		else if("html attr".equals(strEncodeFor)) encFor=ENC_HTML_ATTR;
		else if("html_attributes".equals(strEncodeFor)) encFor=ENC_HTML_ATTR;
		else if("htmlattributes".equals(strEncodeFor)) encFor=ENC_HTML_ATTR;
		else if("html-attributes".equals(strEncodeFor)) encFor=ENC_HTML_ATTR;
		else if("html attributes".equals(strEncodeFor)) encFor=ENC_HTML_ATTR;
		else if("js".equals(strEncodeFor)) encFor=ENC_JAVA_SCRIPT;
		else if("javascript".equals(strEncodeFor)) encFor=ENC_JAVA_SCRIPT;
		else if("java_script".equals(strEncodeFor)) encFor=ENC_JAVA_SCRIPT;
		else if("java script".equals(strEncodeFor)) encFor=ENC_JAVA_SCRIPT;
		else if("java-script".equals(strEncodeFor)) encFor=ENC_JAVA_SCRIPT;
		else if("ldap".equals(strEncodeFor)) encFor=ENC_LDAP;
		//else if("".equals(strEncodeFor)) encFor=ENC_OS;
		//else if("".equals(strEncodeFor)) encFor=ENC_SQl;
		else if("url".equals(strEncodeFor)) encFor=ENC_URL;
		else if("vbs".equals(strEncodeFor)) encFor=ENC_VB_SCRIPT;
		else if("vbscript".equals(strEncodeFor)) encFor=ENC_VB_SCRIPT;
		else if("vb-script".equals(strEncodeFor)) encFor=ENC_VB_SCRIPT;
		else if("vb_script".equals(strEncodeFor)) encFor=ENC_VB_SCRIPT;
		else if("vb script".equals(strEncodeFor)) encFor=ENC_VB_SCRIPT;
		else if("xml".equals(strEncodeFor)) encFor=ENC_XML;
		else if("xmlattr".equals(strEncodeFor)) encFor=ENC_XML_ATTR;
		else if("xml attr".equals(strEncodeFor)) encFor=ENC_XML_ATTR;
		else if("xml-attr".equals(strEncodeFor)) encFor=ENC_XML_ATTR;
		else if("xml_attr".equals(strEncodeFor)) encFor=ENC_XML_ATTR;
		else if("xmlattributes".equals(strEncodeFor)) encFor=ENC_XML_ATTR;
		else if("xml attributes".equals(strEncodeFor)) encFor=ENC_XML_ATTR;
		else if("xml-attributes".equals(strEncodeFor)) encFor=ENC_XML_ATTR;
		else if("xml_attributes".equals(strEncodeFor)) encFor=ENC_XML_ATTR;
		else if("xpath".equals(strEncodeFor)) encFor=ENC_XPATH;
		else 
			throw new FunctionException(pc, "ESAPIEncode", 1, "encodeFor", "value ["+strEncodeFor+"] is invalid, valid values are " +
					"[css,dn,html,html_attr,javascript,ldap,vbscript,xml,xml_attr,xpath]");
		return encode(value, encFor);
	}

	public static String canonicalize(String input, boolean restrictMultiple, boolean restrictMixed) {
		if(StringUtil.isEmpty(input)) return null;
		PrintStream out = System.out;
		try {
			 System.setOut(new PrintStream(DevNullOutputStream.DEV_NULL_OUTPUT_STREAM));
			 return ESAPI.encoder().canonicalize(input, restrictMultiple, restrictMixed);
		}
		finally {
			 System.setOut(out);
		}	
	}
	
}
