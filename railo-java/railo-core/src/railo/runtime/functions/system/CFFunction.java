package railo.runtime.functions.system;

import java.io.File;
import java.util.Map;

import org.apache.commons.collections.ReferenceMap;

import railo.runtime.Mapping;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.FunctionValue;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.scope.Variables;
import railo.runtime.type.scope.VariablesImpl;

public class CFFunction {
	
	
	private static final Variables VAR = new VariablesImpl();
	private static Map udfs=new ReferenceMap();
	
	
	
	public static Object call(PageContext pc , Object[] objArr) throws PageException {
		if(objArr.length<3)
			throw new ExpressionException("invalid call of a CFML Based Build in Function");
		
		// translate arguments
		String filename=Caster.toString((((FunctionValue) objArr[0]).getValue()));
		Collection.Key name=KeyImpl.toKey((((FunctionValue) objArr[1]).getValue()));
		boolean isweb=Caster.toBooleanValue((((FunctionValue) objArr[2]).getValue()));
		
		Struct namedArguments=null;
		Object[] arguments=null;
		if(objArr.length<=3)arguments=new Object[]{};
		else if(objArr[3] instanceof FunctionValue){
			FunctionValue fv;
			namedArguments=new StructImpl();
			for(int i=3;i<objArr.length;i++){
				fv=toFunctionValue(name,objArr[i]);
				namedArguments.set(fv.getName(), fv.getValue());
			}
		}
		else {
			arguments=new Object[objArr.length-3];
			for(int i=3;i<objArr.length;i++){
				arguments[i-3]=toObject(name,objArr[i]);
			}
		}
		
		
		// load UDF
		UDF udf=loadUDF(pc, filename, name, isweb);
		
		// execute UDF
		if(namedArguments==null)return udf.call(pc, arguments, false);
		return udf.callWithNamedValues(pc, namedArguments, false);
	}

	private static synchronized UDF loadUDF(PageContext pc, String filename,Collection.Key name,boolean isweb) throws PageException {
		ConfigWebImpl config = (ConfigWebImpl) pc.getConfig();
		String key=isweb?name.getString()+config.getId():name.getString();
    	UDF udf=(UDF) udfs.get(key);
		if(udf!=null) return udf;
		
		
		Mapping mapping=isweb?config.getFunctionMapping():config.getServerFunctionMapping();
    	PageSource ps = mapping.getPageSource(filename);
    	Page p = ps.loadPage(pc.getConfig());	
		
    	
    	// execute page
    	Variables old = pc.variablesScope();
    	pc.setVariablesScope(VAR);
    	boolean wasSilent = pc.setSilent();
    	try {
			p.call(pc);
			Object o= pc.variablesScope().get(name,null);
			if(o instanceof UDF) {
				udf= (UDF) o;
				udfs.put(key, udf);
				return udf;
			}
			throw new ExpressionException("there is no Function defined with name ["+name+"] in template ["+mapping.getStrPhysical()+File.separator+filename+"]");
		} 
    	catch (Throwable t) {
			throw Caster.toPageException(t);
		}
		finally{
			pc.setVariablesScope(old);
			if(!wasSilent)pc.unsetSilent();
		}
		
		
	}

	private static FunctionValue toFunctionValue(Collection.Key name,Object obj) throws ExpressionException {
		if(obj instanceof FunctionValue)
			return (FunctionValue) obj;
		throw new ExpressionException("invalid argument for function "+name+", you can not mix named and unnamed arguments");
	}

	private static Object toObject(Collection.Key name,Object obj) throws ExpressionException {
		if(obj instanceof FunctionValue)
			throw new ExpressionException("invalid argument for function "+name+", you can not mix named and unnamed arguments");
		return obj;
	}
}
