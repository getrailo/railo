/**
 * Implements the Cold Fusion Function getfunctiondescription
 */
package railo.runtime.functions.other;

import java.util.ArrayList;

import railo.print;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.system.CFFunction;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFImpl;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.function.FunctionLibFunctionArg;

public final class GetFunctionData implements Function {
	private static final Collection.Key NAME = KeyImpl.getInstance("name");
	private static final Collection.Key TYPE = KeyImpl.getInstance("type");
	private static final Collection.Key SOURCE = KeyImpl.getInstance("source");
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

		
		FunctionLibFunction function=null;
		for(int i=0;i<flds.length;i++) {
			function = flds[i].getFunction(strFunctionName.toLowerCase());
			if(function!=null)break;
		}
		if(function == null) throw new ExpressionException("function ["+strFunctionName+"] is not a build in function");

        //sct.set("returnTypeClass",_class(function.getCazz()));
        //sct.set("class",__class(function.getCazz()));
        
		
		
		// CFML Based Function
		if(function.getCazz()==railo.runtime.functions.system.CFFunction.class){
			return cfmlBasedFunction(pc,function);
		}
		return javaBasedFunction(function);
		
		
		
	}

	private static Struct javaBasedFunction(FunctionLibFunction function) throws PageException {
		Struct sct=new StructImpl();
        sct.set(NAME,function.getName());
        sct.set(DESCRIPTION,StringUtil.toStringEmptyIfNull(function.getDescription()));
        sct.set(RETURN_TYPE,StringUtil.toStringEmptyIfNull(function.getReturnTypeAsString()));
        sct.set(ARGUMENT_TYPE,StringUtil.toStringEmptyIfNull(function.getArgTypeAsString()));
        sct.set(ARG_MIN,Caster.toDouble(function.getArgMin()));
        sct.set(ARG_MAX,Caster.toDouble(function.getArgMax()));
        sct.set(TYPE,"java");
		
		
		Array _args=new ArrayImpl();
		sct.set(ARGUMENTS,_args);
		if(function.getArgType()!=FunctionLibFunction.ARG_DYNAMIC){
			ArrayList<FunctionLibFunctionArg> args = function.getArg();
			for(int i=0;i<args.size();i++) {
				FunctionLibFunctionArg arg=args.get(i);
				Struct _arg=new StructImpl();
				_arg.set(REQUIRED,arg.getRequired()?Boolean.TRUE:Boolean.FALSE);
				_arg.set(TYPE,StringUtil.toStringEmptyIfNull(arg.getTypeAsString()));
				_arg.set(NAME,StringUtil.toStringEmptyIfNull(arg.getName()));
				_arg.set(DESCRIPTION,StringUtil.toStringEmptyIfNull(arg.getDescription()));
				
				
				_args.append(_arg);
			}
		}
		return sct;
	}

	private static Struct cfmlBasedFunction(PageContext pc, FunctionLibFunction function) throws PageException {
		Struct sct=new StructImpl();
		ArrayList<FunctionLibFunctionArg> args = function.getArg();
		
		String filename = Caster.toString(args.get(0).getDefaultValue());
		Key name = KeyImpl.toKey(args.get(1).getDefaultValue());
		boolean isWeb = Caster.toBooleanValue(args.get(2).getDefaultValue());
		UDFImpl udf = (UDFImpl) CFFunction.loadUDF(pc, filename, name, isWeb);
		
		sct.set(NAME,function.getName());
        sct.set(ARGUMENT_TYPE,"fixed");
        
        sct.set(DESCRIPTION,StringUtil.toStringEmptyIfNull(udf.getHint()));
        sct.set(RETURN_TYPE,StringUtil.toStringEmptyIfNull(udf.getReturnTypeAsString()));
        sct.set(TYPE,"cfml");
        sct.set(SOURCE,udf.getPageSource().getDisplayPath());
		
        
        FunctionArgument[] fas = udf.getFunctionArguments();
        Array _args=new ArrayImpl();
		sct.set(ARGUMENTS,_args);
        int min=0,max=0;
		for(int i=0;i<fas.length;i++) {
        	FunctionArgument fa=fas[i];
			Struct _arg=new StructImpl();
			if(fa.isRequired()) min++;
			max++;
			_arg.set(REQUIRED,fa.isRequired()?Boolean.TRUE:Boolean.FALSE);
			_arg.set(TYPE,StringUtil.toStringEmptyIfNull(fa.getTypeAsString()));
			_arg.set(NAME,StringUtil.toStringEmptyIfNull(fa.getName()));
			_arg.set(DESCRIPTION,StringUtil.toStringEmptyIfNull(fa.getHint()));
			
			
			_args.append(_arg);
		}
        sct.set(ARG_MIN,Caster.toDouble(min));
        sct.set(ARG_MAX,Caster.toDouble(max));
        
        
		return sct;
	}
}