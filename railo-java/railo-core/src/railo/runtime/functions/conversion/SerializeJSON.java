package railo.runtime.functions.conversion;

import java.nio.charset.Charset;

import railo.commons.io.CharsetUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.JSONConverter;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * Decodes Binary Data that are encoded as String
 */
public final class SerializeJSON implements Function {

	private static final long serialVersionUID = -4632952919389635891L;

	public static String call(PageContext pc, Object var) throws PageException {
		return _call(pc, var, false, ((PageContextImpl)pc).getWebCharset());
	}
	public static String call(PageContext pc, Object var,boolean serializeQueryByColumns) throws PageException {
		return _call(pc, var, serializeQueryByColumns, ((PageContextImpl)pc).getWebCharset());
	}
	public static String call(PageContext pc, Object var,boolean serializeQueryByColumns, String strCharset) throws PageException {
		Charset cs=StringUtil.isEmpty(strCharset)?((PageContextImpl)pc).getWebCharset():CharsetUtil.toCharset(strCharset);
		return _call(pc, var, serializeQueryByColumns, cs);
	}
	private static String _call(PageContext pc, Object var,boolean serializeQueryByColumns, Charset charset) throws PageException {
		try {
            return new JSONConverter(true,charset).serialize(pc,var,serializeQueryByColumns);
        } catch (ConverterException e) {
            throw Caster.toPageException(e);
        }
	}
}