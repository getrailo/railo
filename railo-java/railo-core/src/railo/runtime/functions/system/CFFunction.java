package railo.runtime.functions.system;

import java.io.File;

import railo.runtime.Mapping;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageSourceImpl;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection;
import railo.runtime.type.FunctionValue;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFPlus;
import railo.runtime.type.scope.Variables;
import railo.runtime.type.scope.VariablesImpl;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.KeyConstants;

public class CFFunction {
	
	
	private static final Variables VAR = new VariablesImpl();
	//private static Map udfs=new ReferenceMap();
	
	public static Object call(PageContext pc , Object[] objArr) throws PageException {
		if(objArr.length<3)
			throw new ExpressionException("invalid call of a CFML Based built in function");
		
		// translate arguments
		String filename=Caster.toString((((FunctionValue) objArr[0]).getValue()));
		Collection.Key name=KeyImpl.toKey((((FunctionValue) objArr[1]).getValue()));
		boolean isweb=Caster.toBooleanValue((((FunctionValue) objArr[2]).getValue()));
		
		
		UDF udf=loadUDF(pc, filename, name, isweb);
		Struct meta = udf.getMetaData(pc);
		boolean caller=meta==null?false:Caster.toBooleanValue(meta.get(KeyConstants._caller,Boolean.FALSE),false);
		
		Struct namedArguments=null;
		Object[] arguments=null;
		if(objArr.length<=3)arguments=ArrayUtil.OBJECT_EMPTY;
		else if(objArr[3] instanceof FunctionValue){
			FunctionValue fv;
			namedArguments=new StructImpl();
			if(caller)namedArguments.setEL(KeyConstants._caller, Duplicator.duplicate(pc.undefinedScope(),false));
			for(int i=3;i<objArr.length;i++){
				fv=toFunctionValue(name,objArr[i]);
				namedArguments.set(fv.getName(), fv.getValue());
			}
		}
		else {
			int offset=(caller?2:3);
			arguments=new Object[objArr.length-offset];
			if(caller)arguments[0]=Duplicator.duplicate(pc.undefinedScope(),false);
			for(int i=3;i<objArr.length;i++){
				arguments[i-offset]=toObject(name,objArr[i]);
			}
		}
		
		
		// load UDF
		
		
		// execute UDF
		if(namedArguments==null){
			return ((UDFPlus)udf).call(pc,name, arguments, false);
		}
		
		
		return ((UDFPlus)udf).callWithNamedValues(pc,name, namedArguments, false);
	}

	public static synchronized UDF loadUDF(PageContext pc, String filename,Collection.Key name,boolean isweb) throws PageException {
		ConfigWebImpl config = (ConfigWebImpl) pc.getConfig();
		String key=isweb?name.getString()+config.getId():name.getString();
    	UDF udf=config.getFromFunctionCache(key);
		if(udf!=null) return udf;
		
		Mapping mapping=isweb?config.getFunctionMapping():config.getServerFunctionMapping();
    	PageSourceImpl ps = (PageSourceImpl) mapping.getPageSource(filename);
    	Page p = ps.loadPage(pc);	
		
    	
    	// execute page
    	Variables old = pc.variablesScope();
    	pc.setVariablesScope(VAR);
    	boolean wasSilent = pc.setSilent();
    	try {
			p.call(pc);
			Object o= pc.variablesScope().get(name,null);
			if(o instanceof UDF) {
				udf= (UDF) o;
				config.putToFunctionCache(key, udf);
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
