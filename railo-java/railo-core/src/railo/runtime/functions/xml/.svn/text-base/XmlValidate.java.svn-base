package railo.runtime.functions.xml;

import org.xml.sax.InputSource;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.Struct;

/**
 * 
 */
public final class XmlValidate implements Function {

	public static Struct call(PageContext pc, String strXml) throws PageException {
		return call(pc,strXml,null);
	}
	public static Struct call(PageContext pc, String strXml, String strValidator) throws PageException {
		strXml=strXml.trim();
		try {
			InputSource xml = XMLUtil.toInputSource(pc,strXml);
			InputSource validator = StringUtil.isEmpty(strValidator)?null:XMLUtil.toInputSource(pc,strValidator);
			return XMLUtil.validate(xml, validator,strValidator);
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
		
	}
}