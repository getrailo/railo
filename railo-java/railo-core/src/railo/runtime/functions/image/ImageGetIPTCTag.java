package railo.runtime.functions.image;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class ImageGetIPTCTag {

	public static Object call(PageContext pc, Object name, String tagName) throws PageException {
		if(name instanceof String) name=pc.getVariable(Caster.toString(name));
		//Image img = Image.toImage(name);
		
		throw new ExpressionException("method ImageGetIPTCTag not implemented yet");
	}
	
}
