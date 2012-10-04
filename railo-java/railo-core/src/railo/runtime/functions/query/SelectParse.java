/**
 * Implements the CFML Function isquery
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.sql.SQLParserException;
import railo.runtime.sql.SelectParser;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public final class SelectParse implements Function {
	public static Struct call(PageContext pc , String sql) throws PageException {
		
		try {
			//Selects selects = 
			new SelectParser().parse(sql);
			Struct sct=new StructImpl();
			
			
			
			return sct;
		} 
		catch (SQLParserException e) {
			throw Caster.toPageException(e);
		}
	}
}