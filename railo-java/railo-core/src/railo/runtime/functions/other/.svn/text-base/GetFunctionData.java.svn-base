/**
 * Implements the Cold Fusion Function getfunctiondescription
 */
package railo.runtime.functions.other;

import java.util.ArrayList;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.function.FunctionLibFunctionArg;

public final class GetFunctionData implements Function {
	private static final Collection.Key NAME = KeyImpl.getInstance("name");
	private static final Collection.Key TYPE = KeyImpl.getInstance("type");
	private static final Collection.Key DESCRIPTION = KeyImpl.getInstance("description");
	private static final Collection.Key REQUIRED = KeyImpl.getInstance("required");
	private static final Collection.Key ARGUMENTS = KeyImpl.getInstance("arguments");
	private static final Collection.Key RETURN_TYPE = KeyImpl.getInstance("returnType");
	private static final Collection.Key ARGUMENT_TYPE = KeyImpl.getInstance("argumentType");
	private static final Collection.Key ARG_MIN = KeyImpl.getInstance("argMin");
	private static final Collection.Key ARG_MAX = KeyImpl.getInstance("argMax");
	
	public static Struct call(PageContext pc , String strFunctionName) throws PageException {
		FunctionLib[] flds;
		flds = ((ConfigImpl)pc.getConfig()).getFLDs();

		Struct sct=new StructImpl();
		FunctionLibFunction function=null;
		for(int i=0;i<flds.length;i++) {
			function = flds[i].getFunction(strFunctionName.toLowerCase());
			if(function!=null)break;
		}
		if(function == null) throw new ExpressionException("function ["+strFunctionName+"] is not a build in function");

        //sct.set("returnTypeClass",_class(function.getCazz()));
        //sct.set("class",__class(function.getCazz()));
        
        sct.set(NAME,function.getName());
        sct.set(DESCRIPTION,StringUtil.toStringEmptyIfNull(function.getDescription()));
        sct.set(RETURN_TYPE,StringUtil.toStringEmptyIfNull(function.getReturnTypeAsString()));
        sct.set(ARGUMENT_TYPE,StringUtil.toStringEmptyIfNull(function.getArgTypeAsString()));
        sct.set(ARG_MIN,Caster.toDouble(function.getArgMin()));
        sct.set(ARG_MAX,Caster.toDouble(function.getArgMax()));
		
		
		
		Array _args=new ArrayImpl();
		sct.set(ARGUMENTS,_args);
		
		ArrayList args = function.getArg();
		for(int i=0;i<args.size();i++) {
			FunctionLibFunctionArg arg=(FunctionLibFunctionArg) args.get(i);
			Struct _arg=new StructImpl();
			_arg.set(REQUIRED,arg.getRequired()?Boolean.TRUE:Boolean.FALSE);
			_arg.set(TYPE,StringUtil.toStringEmptyIfNull(arg.getTypeAsString()));
			_arg.set(NAME,StringUtil.toStringEmptyIfNull(arg.getName()));
			_arg.set(DESCRIPTION,StringUtil.toStringEmptyIfNull(arg.getDescription()));
			
			
			_args.append(_arg);
		}
		return sct;
		
	}
}