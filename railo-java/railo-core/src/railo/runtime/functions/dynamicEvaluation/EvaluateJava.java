/**
 * Implements the CFML Function UnserializeJava
 */
package railo.runtime.functions.dynamicEvaluation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import railo.commons.io.IOUtil;
import railo.runtime.PageContext;
import railo.runtime.converter.JavaConverter;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;

public final class EvaluateJava implements Function {

	private static final long serialVersionUID = 2665025287805145492L;

	public static Object call(PageContext pc , Object stringOrBinary) throws PageException {
	   // Binary
		if(Decision.isBinary(stringOrBinary)){
		   InputStream is=null;
		   try {
	           is=new ByteArrayInputStream(Caster.toBinary(stringOrBinary));
			   return JavaConverter.deserialize(is);
	        }
		    catch (Exception e) {
	            throw Caster.toPageException(e);
	        }
	        finally {
	        	IOUtil.closeEL(is);
	        }
	   }
		
		// STring
		try {
            return JavaConverter.deserialize(Caster.toString(stringOrBinary));
        } catch (Exception e) {
            throw Caster.toPageException(e);
        }
	}
}