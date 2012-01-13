package railo.runtime.type.util;

import java.util.ArrayList;
import java.util.Iterator;

import railo.runtime.exp.PageExceptionImpl;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.function.FunctionLibFunctionArg;

public class UDFUtil {

	/**
	 * add detailed function documentation to the exception
	 * @param pe
	 * @param flf
	 */
	public static void addFunctionDoc(PageExceptionImpl pe,FunctionLibFunction flf) {
		ArrayList<FunctionLibFunctionArg> args=flf.getArg();
		Iterator<FunctionLibFunctionArg> it = args.iterator();
		
		// Pattern
		StringBuilder pattern=new StringBuilder(flf.getName());
		StringBuilder end=new StringBuilder();
		pattern.append("(");
		FunctionLibFunctionArg arg;
		int c=0;
		while(it.hasNext()){
			arg = it.next();
			if(!arg.isRequired()) {
				pattern.append(" [");
				end.append("]");
			}
			if(c++>0)pattern.append(", ");
			pattern.append(arg.getName());
			pattern.append(":");
			pattern.append(arg.getType());
			
		}
		pattern.append(end);
		pattern.append("):");
		pattern.append(flf.getReturnTypeAsString());
		
		pe.setAdditional("Pattern", pattern);
		
		// Documentation
		StringBuilder doc=new StringBuilder(flf.getDescription());
		StringBuilder req=new StringBuilder();
		StringBuilder opt=new StringBuilder();
		StringBuilder tmp;
		doc.append("\n");
		
		it = args.iterator();
		while(it.hasNext()){
			arg = it.next();
			tmp=arg.isRequired()?req:opt;
			
			tmp.append("- ");
			tmp.append(arg.getName());
			tmp.append(" (");
			tmp.append(arg.getType());
			tmp.append("): ");
			tmp.append(arg.getDescription());
			tmp.append("\n");
		}

		if(req.length()>0)doc.append("\nRequired:\n").append(req);
		if(opt.length()>0)doc.append("\nOptional:\n").append(opt);
		
		
		pe.setAdditional("Documentation", doc);
		
	}

}
