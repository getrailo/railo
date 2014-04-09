package railo.runtime.type.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import railo.commons.lang.StringUtil;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PagePlus;
import railo.runtime.PageSource;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpRow;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageExceptionImpl;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFGSProperty;
import railo.runtime.type.UDFPlus;
import railo.runtime.type.scope.Argument;
import railo.runtime.type.scope.ArgumentIntKey;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.function.FunctionLibFunctionArg;

public class UDFUtil {

	public static final short TYPE_UDF=1;
	public static final short TYPE_BIF=2;
	public static final short TYPE_CLOSURE=4;

	private static final char CACHE_DEL = ';';
	private static final char CACHE_DEL2 = ':';

	private static final FunctionArgument[] EMPTY = new FunctionArgument[0];
	
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

	public static Object getDefaultValue(PageContext pc, PageSource ps, int udfIndex, int index, Object defaultValue) throws PageException {
		Page p=ComponentUtil.getPage(pc,ps);
    	if(p instanceof PagePlus) return ((PagePlus)p).udfDefaultValue(pc,udfIndex,index,defaultValue);
    	Object rtn = p.udfDefaultValue(pc,udfIndex,index);
    	if(rtn==null) return defaultValue;// in that case it can make no diff between null and not existing, but this only happens with data from old ra files
    	return rtn;
	}
	

	public static Object getDefaultValue(PageContext pc, UDF udf, int index, Object defaultValue) throws PageException {
		Page p=ComponentUtil.getPage(pc,udf.getPageSource());
		return p.udfDefaultValue(pc,udf.getIndex(),index,defaultValue);
	}

	public static void argumentCollection(Struct values) {
		argumentCollection(values,EMPTY);
	}

	public static void argumentCollection(Struct values, FunctionArgument[] funcArgs) {
		Object value=values.removeEL(KeyConstants._argumentCollection);
		if(value !=null) {
			value=Caster.unwrap(value,value);
			
			if(value instanceof Argument) {
				Argument argColl=(Argument) value;
				Iterator<Key> it = argColl.keyIterator();
				Key k;
				int i=-1;
			    while(it.hasNext()) {
			    	i++;
			    	k = it.next();
			    	if(funcArgs.length>i && k instanceof ArgumentIntKey) {
	            		if(!values.containsKey(funcArgs[i].getName()))
	            			values.setEL(funcArgs[i].getName(),argColl.get(k,Argument.NULL));
	            		else 
	            			values.setEL(k,argColl.get(k,Argument.NULL));
			    	}
	            	else if(!values.containsKey(k)){
	            		values.setEL(k,argColl.get(k,Argument.NULL));
	            	}
	            }
		    }
			else if(value instanceof Collection) {
		        Collection argColl=(Collection) value;
			    //Collection.Key[] keys = argColl.keys();
				Iterator<Key> it = argColl.keyIterator();
				Key k;
				while(it.hasNext()) {
			    	k = it.next();
			    	if(!values.containsKey(k)){
	            		values.setEL(k,argColl.get(k,Argument.NULL));
	            	}
	            }
		    }
			else if(value instanceof Map) {
				Map map=(Map) value;
			    Iterator it = map.entrySet().iterator();
			    Map.Entry entry;
			    Key key;
			    while(it.hasNext()) {
			    	entry=(Entry) it.next();
			    	key = Caster.toKey(entry.getKey(),null);
			    	if(!values.containsKey(key)){
	            		values.setEL(key,entry.getValue());
	            	}
	            }
		    }
			else if(value instanceof java.util.List) {
				java.util.List list=(java.util.List) value;
			    Iterator it = list.iterator();
			    Object v;
			    int index=0;
			    Key k;
			    while(it.hasNext()) {
			    	v= it.next();
			    	k=ArgumentIntKey.init(++index);
			    	if(!values.containsKey(k)){
	            		values.setEL(k,v);
	            	}
	            }
		    }
		    else {
		        values.setEL(KeyConstants._argumentCollection,value);
		    }
		} 
	}
	
	public static String toReturnFormat(int returnFormat,String defaultValue) {
		if(UDF.RETURN_FORMAT_WDDX==returnFormat)		return "wddx";
		else if(UDF.RETURN_FORMAT_JSON==returnFormat)	return "json";
		else if(UDF.RETURN_FORMAT_PLAIN==returnFormat)	return "plain";
		else if(UDF.RETURN_FORMAT_SERIALIZE==returnFormat)	return "cfml";
		else if(UDFPlus.RETURN_FORMAT_JAVA==returnFormat)	return "java";
		// NO XML else if(UDFPlus.RETURN_FORMAT_XML==returnFormat)	return "xml";
		else return defaultValue;
	}
	

	public static boolean isValidReturnFormat(int returnFormat) {
		return toReturnFormat(returnFormat,null)!=null;
	}
	

	public static int toReturnFormat(String[] returnFormats, int defaultValue) {
		if(ArrayUtil.isEmpty(returnFormats)) return defaultValue;
		int rf;
		for(int i=0;i<returnFormats.length;i++){
			rf=toReturnFormat(returnFormats[i].trim(), -1);
			if(rf!=-1) return rf;
		}
		return defaultValue;
	}
	public static int toReturnFormat(String returnFormat, int defaultValue) {
		if(StringUtil.isEmpty(returnFormat,true)) return defaultValue;
		
		returnFormat=returnFormat.trim().toLowerCase();
		if("wddx".equals(returnFormat))				return UDF.RETURN_FORMAT_WDDX;
		else if("json".equals(returnFormat))		return UDF.RETURN_FORMAT_JSON;
		else if("plain".equals(returnFormat))		return UDF.RETURN_FORMAT_PLAIN;
		else if("text".equals(returnFormat))		return UDF.RETURN_FORMAT_PLAIN;
		else if("serialize".equals(returnFormat))	return UDF.RETURN_FORMAT_SERIALIZE;
		else if("cfml".equals(returnFormat))	return UDF.RETURN_FORMAT_SERIALIZE;
		else if("cfm".equals(returnFormat))	return UDF.RETURN_FORMAT_SERIALIZE;
		else if("xml".equals(returnFormat))	return UDF.RETURN_FORMAT_XML;
		else if("java".equals(returnFormat))	return UDFPlus.RETURN_FORMAT_JAVA;
		return defaultValue;
	}
	

	public static int toReturnFormat(String returnFormat) throws ExpressionException {
		int rf = toReturnFormat(returnFormat,-1);
		if(rf!=-1) return rf;
		throw new ExpressionException("invalid returnFormat definition ["+returnFormat+"], valid values are [wddx,plain,json,cfml]");
	}

	public static String toReturnFormat(int returnFormat) throws ExpressionException {
		if(UDF.RETURN_FORMAT_WDDX==returnFormat)		return "wddx";
		else if(UDF.RETURN_FORMAT_JSON==returnFormat)	return "json";
		else if(UDF.RETURN_FORMAT_PLAIN==returnFormat)	return "plain";
		else if(UDF.RETURN_FORMAT_SERIALIZE==returnFormat)	return "cfml";
		else if(UDFPlus.RETURN_FORMAT_JAVA==returnFormat)	return "java";
		else throw new ExpressionException("invalid returnFormat definition, valid values are [wddx,plain,json,cfml]");
	}
	

	public static DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp,UDF udf, short type) {
	
		if(!dp.getShowUDFs()) {
			if(TYPE_UDF==type) return new SimpleDumpData("<UDF>");
			if(TYPE_BIF==type) return new SimpleDumpData("<BIF>");
			if(TYPE_CLOSURE==type) return new SimpleDumpData("<Closure>");
		}
		// arguments
		FunctionArgument[] args = udf.getFunctionArguments();
        
        DumpTable atts;
        if(TYPE_UDF==type) 			atts= new DumpTable("udf","#cc66ff","#ffccff","#000000");
        else if(TYPE_CLOSURE==type) atts= new DumpTable("udf","#ff00ff","#ffccff","#000000");
        else						atts= new DumpTable("udf","#ffff66","#ffffcc","#000000");
        
        
		atts.appendRow(new DumpRow(63,new DumpData[]{new SimpleDumpData("label"),new SimpleDumpData("name"),new SimpleDumpData("required"),new SimpleDumpData("type"),new SimpleDumpData("default"),new SimpleDumpData("hint")}));
		for(int i=0;i<args.length;i++) {
			FunctionArgument arg=args[i];
			DumpData def;
			try {
				Object oa=null;
                try {
                    oa = UDFUtil.getDefaultValue(pageContext, udf, i, null);//udf.getDefaultValue(pageContext,i,null);
                } catch (Throwable t) {

                }
                if(oa==null)oa="null";
				def=new SimpleDumpData(Caster.toString(oa));
			} catch (PageException e) {
				def=new SimpleDumpData("");
			}
			atts.appendRow(new DumpRow(0,new DumpData[]{
					new SimpleDumpData(arg.getDisplayName()),
					new SimpleDumpData(arg.getName().getString()),
					new SimpleDumpData(arg.isRequired()),
					new SimpleDumpData(arg.getTypeAsString()),
					def,
					new SimpleDumpData(arg.getHint())}));
			//atts.setRow(0,arg.getHint());
			
		}
		DumpTable func;
		if(TYPE_CLOSURE==type) {
			func=new DumpTable("#ff00ff","#ffccff","#000000");
			func.setTitle("Closure");
		}
		else if(TYPE_UDF==type) {
			func=new DumpTable("#cc66ff","#ffccff","#000000");
			String f="Function ";
			try {
				f=StringUtil.ucFirst(ComponentUtil.toStringAccess(udf.getAccess()).toLowerCase())+" "+f;
			} 
			catch (ExpressionException e) {}
			f+=udf.getFunctionName();
			if(udf instanceof UDFGSProperty) f+=" (generated)";
			func.setTitle(f);
		}
		else {
			String f="Build in Function "+udf.getFunctionName();
			
			
			func=new DumpTable("#ffff66","#ffffcc","#000000");
			func.setTitle(f);
		}

		if(udf instanceof UDFPlus) {
			PageSource ps = ((UDFPlus)udf).getPageSource();
			if(ps!=null)
				func.setComment("source:"+ps.getDisplayPath());
		}

		if(!StringUtil.isEmpty(udf.getDescription()))func.setComment(udf.getDescription());
		
		func.appendRow(1,new SimpleDumpData("arguments"),atts);
		func.appendRow(1,new SimpleDumpData("return type"),new SimpleDumpData(udf.getReturnTypeAsString()));
		
		boolean hasLabel=!StringUtil.isEmpty(udf.getDisplayName());//displayName!=null && !displayName.equals("");
		boolean hasHint=!StringUtil.isEmpty(udf.getHint());//hint!=null && !hint.equals("");
		
		if(hasLabel || hasHint) {
			DumpTable box = new DumpTable("#ffffff","#cccccc","#000000");
			box.setTitle(hasLabel?udf.getDisplayName():udf.getFunctionName());
			if(hasHint)box.appendRow(0,new SimpleDumpData(udf.getHint()));
			box.appendRow(0,func);
			return box;
		}
		return func;
	}


}
