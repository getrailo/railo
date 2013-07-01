package railo.runtime.type.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageExceptionImpl;
import railo.runtime.op.Decision;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
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
			pattern.append(arg.getTypeAsString());
			
		}
		pattern.append(end);
		pattern.append("):");
		pattern.append(flf.getReturnTypeAsString());
		
		pe.setAdditional(KeyConstants._Pattern, pattern);
		
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
			tmp.append(arg.getTypeAsString());
			tmp.append("): ");
			tmp.append(arg.getDescription());
			tmp.append("\n");
		}

		if(req.length()>0)doc.append("\nRequired:\n").append(req);
		if(opt.length()>0)doc.append("\nOptional:\n").append(opt);
		
		
		pe.setAdditional(KeyImpl.init("Documentation"), doc);
		
	}

	public static String callerHash(UDF udf, Object[] args, Struct values) throws ApplicationException {
		StringBuilder sb=new StringBuilder(udf.getPageSource().getDisplayPath())
			.append(';')
			.append(udf.getFunctionName())
			.append(';');
		
		if(values!=null) {
			Iterator<Entry<Key, Object>> it = values.entryIterator();
			Entry<Key, Object> e;
			while(it.hasNext()){
				e = it.next();
				if(!Decision.isSimpleValue(e.getValue())) throw new ApplicationException("only simple values are allowed as parameter for a function with cachedWithin");
				sb.append(e.getKey().getString()).append(':').append(e.getValue()).append(';');
				
			}
		}
		else if(args!=null){
			for(int i=0;i<args.length;i++){
				if(!Decision.isSimpleValue(args[i])) throw new ApplicationException("only simple values are allowed as parameter for a function with cachedWithin");
				sb.append(args[i]).append(';');
				
			}
		}
		return sb.toString();
	}

	public static Object getDefaultValue(PageContext pc, PageSource ps, int udfIndex, int index, Object defaultValue) throws PageException {
		return ComponentUtil.getPage(pc,ps).udfDefaultValue(pc,udfIndex,index,defaultValue);
	}
	

	public static Object getDefaultValue(PageContext pc, UDF udf, int index, Object defaultValue) throws PageException {
		Page p=ComponentUtil.getPage(pc,udf.getPageSource());
		return p.udfDefaultValue(pc,udf.getIndex(),index,defaultValue);
	}

}
