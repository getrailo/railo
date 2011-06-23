package railo.runtime.type;

import java.util.Iterator;
import java.util.Map;

import railo.commons.lang.CFTypes;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.ComponentImpl;
import railo.runtime.PageContext;
import railo.runtime.component.Property;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.orm.ORMUtil;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.util.PropertyFactory;

public class UDFHasProperty extends UDFGSProperty {

	private Property prop;
	//private ComponentScope scope;
	
	private Key propName;
	
	//private static final String NULL="sdsdsdfsfsfjkln fsdfsa";

	public UDFHasProperty(ComponentImpl component,Property prop)  {
		super(component,"has"+StringUtil.ucFirst(PropertyFactory.getSingularName(prop)),getFunctionArgument(prop),CFTypes.TYPE_BOOLEAN,"wddx");
		this.prop=prop;
		this.propName=KeyImpl.getInstance(prop.getName());
	} 

	private static FunctionArgument[] getFunctionArgument(Property prop) {
		String t = PropertyFactory.getType(prop);
		
		if("struct".equalsIgnoreCase(t)){
			FunctionArgumentImpl key = new FunctionArgumentImpl("key","string",false);
			return new FunctionArgument[]{key};
		}
		FunctionArgumentImpl value = new FunctionArgumentImpl(PropertyFactory.getSingularName(prop),"any",false);
		return new FunctionArgument[]{value};
	}
	
	private boolean isStruct() {
		String t = PropertyFactory.getType(prop);
		return "struct".equalsIgnoreCase(t);
	}

	/**
	 * @see railo.runtime.type.UDF#duplicate()
	 */
	public UDF duplicate(ComponentImpl c) {
		return new UDFHasProperty(c,prop);
	}
	
 
	public UDF duplicate() {
		return duplicate(component);
	}
	
	/**
	 * @see railo.runtime.type.UDF#call(railo.runtime.PageContext, java.lang.Object[], boolean)
	 */
	public Object call(PageContext pageContext, Object[] args,boolean doIncludePath) throws PageException {
		if(args.length<1) return has(pageContext);
		return has(pageContext, args[0]);
	}

	/**
	 * @see railo.runtime.type.UDF#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Struct, boolean)
	 */
	public Object callWithNamedValues(PageContext pageContext, Struct values,boolean doIncludePath) throws PageException {
		UDFImpl.argumentCollection(values,getFunctionArguments());
		Key key = arguments[0].getName();
		Object value = values.get(key,null);
		if(value==null){
			Key[] keys = values.keys();
			if(keys.length>0) {
				value=values.get(keys[0]);
			}
			else return has(pageContext);
		}
		
		return has(pageContext, value);
	}
	
	private boolean has(PageContext pageContext) {
		Object propValue = component.getComponentScope().get(propName,null);
		
		// struct
		if(isStruct()) {
			if(propValue instanceof Map) {
				return !((Map)propValue).isEmpty();
			}
			return false;
		}
		else {
			//Object o;
			if(propValue instanceof Array) {
				Array arr = ((Array)propValue);
				return arr.size()>0;
			}
			else if(propValue instanceof java.util.List) {
				
				return ((java.util.List)propValue).size()>0;
			}
			return propValue instanceof Component;
		}
	}
	
	private boolean has(PageContext pageContext, Object value) throws PageException {
		Object propValue = component.getComponentScope().get(propName,null);
		
		// struct
		if(isStruct()) {
			String strKey = Caster.toString(value);
			//if(strKey==NULL) throw new ;
			
			if(propValue instanceof Struct) {
				return ((Struct)propValue).containsKey(KeyImpl.getInstance(strKey));
			}
			else if(propValue instanceof Map) {
				return ((Map)propValue).containsKey(strKey);
			}
			return false;
		}
		else {
			Object o;
			
			if(propValue instanceof Array) {
				Array arr = ((Array)propValue);
				Key[] keys = arr.keys();
				for(int i=0;i<keys.length;i++){
					o=arr.get(keys[i],null);
					if(ORMUtil.equals(value,o))return true;
				}
			}
			else if(propValue instanceof java.util.List) {
				Iterator it=((java.util.List)propValue).iterator();
				while(it.hasNext()){
					o = it.next();
					if(ORMUtil.equals(value,o))return true;
				}
			}
			return false;
		}
	}

	/**
	 * @see railo.runtime.type.UDF#implementation(railo.runtime.PageContext)
	 */
	public Object implementation(PageContext pageContext) throws Throwable {
		return null;
	}
	
	/**
	 * @see railo.runtime.type.UDF#getDefaultValue(railo.runtime.PageContext, int)
	 */
	public Object getDefaultValue(PageContext pc, int index) throws PageException {
		return prop.getDefault();
	}

	/**
	 * @see railo.runtime.type.UDF#getReturnTypeAsString()
	 */
	public String getReturnTypeAsString() {
		return "boolean";
	}
}
