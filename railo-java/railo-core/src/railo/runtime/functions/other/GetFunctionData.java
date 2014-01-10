/**
 * Implements the CFML Function getfunctiondescription
 */
package railo.runtime.functions.other;

import java.util.ArrayList;

import railo.commons.lang.CFTypes;
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
import railo.runtime.type.util.KeyConstants;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.function.FunctionLibFunctionArg;
import railo.transformer.library.tag.TagLibFactory;

public final class GetFunctionData implements Function {
	private static final Collection.Key SOURCE = KeyConstants._source;
	private static final Collection.Key RETURN_TYPE = KeyImpl.intern("returnType");
	private static final Collection.Key ARGUMENT_TYPE = KeyImpl.intern("argumentType");
	private static final Collection.Key ARG_MIN = KeyImpl.intern("argMin");
	private static final Collection.Key ARG_MAX = KeyImpl.intern("argMax");
	
	public static Struct call(PageContext pc , String strFunctionName) throws PageException {
		FunctionLib[] flds;
		flds = ((ConfigImpl)pc.getConfig()).getFLDs();

		
		FunctionLibFunction function=null;
		for(int i=0;i<flds.length;i++) {
			function = flds[i].getFunction(strFunctionName.toLowerCase());
			if(function!=null)break;
		}
		if(function == null) throw new ExpressionException("function ["+strFunctionName+"] is not a built in function");

        //sct.set("returnTypeClass",_class(function.getCazz()));
        //sct.set("class",__class(function.getCazz()));
        
		
		
		// CFML Based Function
		Class clazz=null;
		try{
			clazz=function.getClazz();
		}
		catch(Throwable t){}
		if(clazz==railo.runtime.functions.system.CFFunction.class){
			return cfmlBasedFunction(pc,function);
		}
		return javaBasedFunction(function);
		
		
		
	}

	private static Struct javaBasedFunction(FunctionLibFunction function) throws PageException {
		Struct sct=new StructImpl();
		sct.set(KeyConstants._name,function.getName());
        sct.set(KeyConstants._status,TagLibFactory.toStatus(function.getStatus()));
		sct.set(KeyConstants._description,StringUtil.emptyIfNull(function.getDescription()));
        sct.set(RETURN_TYPE,StringUtil.emptyIfNull(function.getReturnTypeAsString()));
        sct.set(ARGUMENT_TYPE,StringUtil.emptyIfNull(function.getArgTypeAsString()));
        sct.set(ARG_MIN,Caster.toDouble(function.getArgMin()));
        sct.set(ARG_MAX,Caster.toDouble(function.getArgMax()));
        sct.set(KeyConstants._type,"java");
		String mm = function.getMemberName();
        if(mm!=null && function.getMemberType()!=CFTypes.TYPE_UNKNOW) {
        	StructImpl mem = new StructImpl();
        	sct.set(KeyConstants._member, mem);
        	mem.set(KeyConstants._name,mm);
        	mem.set(KeyConstants._chaining,Caster.toBoolean(function.getMemberChaining()));
            mem.set(KeyConstants._type, function.getMemberTypeAsString());
        }
		
		Array _args=new ArrayImpl();
		sct.set(KeyConstants._arguments,_args);
		if(function.getArgType()!=FunctionLibFunction.ARG_DYNAMIC){
			ArrayList<FunctionLibFunctionArg> args = function.getArg();
			for(int i=0;i<args.size();i++) {
				FunctionLibFunctionArg arg=args.get(i);
				Struct _arg=new StructImpl();
				_arg.set(KeyConstants._required,arg.getRequired()?Boolean.TRUE:Boolean.FALSE);
				_arg.set(KeyConstants._type,StringUtil.emptyIfNull(arg.getTypeAsString()));
				_arg.set(KeyConstants._name,StringUtil.emptyIfNull(arg.getName()));
				_arg.set(KeyConstants._status,TagLibFactory.toStatus(arg.getStatus()));
				_arg.set(KeyConstants._description,StringUtil.toStringEmptyIfNull(arg.getDescription()));
				
				
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
		UDF udf = CFFunction.loadUDF(pc, filename, name, isWeb);
		
		sct.set(KeyConstants._name,function.getName());
        sct.set(ARGUMENT_TYPE,"fixed");
        sct.set(KeyConstants._description,StringUtil.emptyIfNull(udf.getHint()));
        sct.set(RETURN_TYPE,StringUtil.emptyIfNull(udf.getReturnTypeAsString()));
        sct.set(KeyConstants._type,"cfml");
        sct.set(SOURCE,udf.getPageSource().getDisplayPath());
		sct.set(KeyConstants._status,"implemeted");
		
		
        FunctionArgument[] fas = udf.getFunctionArguments();
        Array _args=new ArrayImpl();
		sct.set(KeyConstants._arguments,_args);
        int min=0,max=0;
		for(int i=0;i<fas.length;i++) {
        	FunctionArgument fa=fas[i];
        	Struct meta = fa.getMetaData();
        	
			Struct _arg=new StructImpl();
			if(fa.isRequired()) min++;
			max++;
			_arg.set(KeyConstants._required,fa.isRequired()?Boolean.TRUE:Boolean.FALSE);
			_arg.set(KeyConstants._type,StringUtil.emptyIfNull(fa.getTypeAsString()));
			_arg.set(KeyConstants._name,StringUtil.emptyIfNull(fa.getName()));
			_arg.set(KeyConstants._description,StringUtil.emptyIfNull(fa.getHint()));
			
			String status;
			if(meta==null)status="implemeted";
			else status=TagLibFactory.toStatus(TagLibFactory.toStatus(Caster.toString(meta.get(KeyConstants._status,"implemeted"))));
			
			_arg.set(KeyConstants._status,status);
			
			_args.append(_arg);
		}
        sct.set(ARG_MIN,Caster.toDouble(min));
        sct.set(ARG_MAX,Caster.toDouble(max));
        
        
		return sct;
	}
}