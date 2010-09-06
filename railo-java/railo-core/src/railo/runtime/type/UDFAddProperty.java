package railo.runtime.type;

import java.util.HashMap;
import java.util.Map;

import railo.commons.lang.CFTypes;
import railo.commons.lang.StringUtil;
import railo.runtime.ComponentImpl;
import railo.runtime.PageContext;
import railo.runtime.component.Property;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.util.PropertyFactory;

public class UDFAddProperty extends UDFGSProperty {

	private Property prop;
	//private ComponentScope scope;
	
	private Key propName;
	
	private static final Object NULL=new Object();

	public UDFAddProperty(ComponentImpl component,Property prop)  {
		super(component,"add"+StringUtil.ucFirst(PropertyFactory.getSingularName(prop)),getFunctionArgument(prop),CFTypes.TYPE_ANY,"wddx");
		this.prop=prop;
		this.propName=KeyImpl.init(prop.getName());
	} 

	private static FunctionArgument[] getFunctionArgument(Property prop) {
		String t = PropertyFactory.getType(prop);
		FunctionArgumentImpl value = new FunctionArgumentImpl(PropertyFactory.getSingularName(prop),"any",true);
		if("struct".equalsIgnoreCase(t)){
			FunctionArgumentImpl key = new FunctionArgumentImpl("key","string",true);
			return new FunctionArgument[]{key,value};
		}
		return new FunctionArgument[]{value};
	}

	/**
	 * @see railo.runtime.type.UDF#duplicate()
	 */
	public UDF duplicate(ComponentImpl c) {
		return new UDFAddProperty(c,prop);
	}
	
 
	public UDF duplicate() {
		return duplicate(component);
	}
	
	/**
	 * @see railo.runtime.type.UDF#call(railo.runtime.PageContext, java.lang.Object[], boolean)
	 */
	public Object call(PageContext pageContext, Object[] args,boolean doIncludePath) throws PageException {
		// struct
		if(this.arguments.length==2) {
			if(args.length<2)
				throw new ExpressionException("The function "+getFunctionName()+" need 2 arguments, only "+args.length+" argment"+(args.length==1?" is":"s are")+" passed in.");
			return _call(pageContext, args[0], args[1]);
		}
		// array
		else if(this.arguments.length==1) {
			if(args.length<1)
				throw new ExpressionException("The parameter "+this.arguments[0].getName()+" to function "+getFunctionName()+" is required but was not passed in.");
			return _call(pageContext, null, args[0]);
		}
		
		// never reached
		return component;
		
	}

	/**
	 * @see railo.runtime.type.UDF#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Struct, boolean)
	 */
	public Object callWithNamedValues(PageContext pageContext, Struct values,boolean doIncludePath) throws PageException {
		UDFImpl.argumentCollection(values,getFunctionArguments());
		
		
		// struct
		if(this.arguments.length==2) {
			Key keyName = arguments[0].getName();
			Key valueName = arguments[1].getName();
			Object key = values.get(keyName,null);
			Object value = values.get(valueName,null);
			if(key==null)
				throw new ExpressionException("The parameter "+keyName+" to function "+getFunctionName()+" is required but was not passed in.");
			if(value==null)
				throw new ExpressionException("The parameter "+valueName+" to function "+getFunctionName()+" is required but was not passed in.");
			
			return _call(pageContext, key, value);
		}
		// array
		else if(this.arguments.length==1) {
			Key valueName = arguments[0].getName();
			Object value = values.get(valueName,null);
			if(value==null){
				Key[] keys = values.keys();
				if(keys.length==1) {
					value=values.get(keys[0]);
				}
				else throw new ExpressionException("The parameter "+valueName+" to function "+getFunctionName()+" is required but was not passed in.");
			}
			return _call(pageContext, null, value);
		}

		// never reached
		return component;
	}
	
	
	private Object _call(PageContext pageContext, Object key, Object value) throws PageException {
		
		
		Object propValue = component.getComponentScope().get(propName,null);
		
		// struct
		if(this.arguments.length==2) {
			key=cast(arguments[0],key,1);
			value=cast(arguments[1],value,2);
			if(propValue==null){
				HashMap map=new HashMap();
				component.getComponentScope().setEL(propName,map);
				propValue=map;
			}	
			if(propValue instanceof Struct) {
				((Struct)propValue).set(KeyImpl.toKey(key), value);
			}
			else if(propValue instanceof Map) {
				((Map)propValue).put(key, value);
			}
		}
		else {
			value=cast(arguments[0],value,1);
			if(propValue==null){
				Array arr=new ArrayImpl();
				component.getComponentScope().setEL(propName,arr);
				propValue=arr;
			}	
			if(propValue instanceof Array) {
				((Array)propValue).appendEL(value);
			}
			else if(propValue instanceof java.util.List) {
				((java.util.List)propValue).add(value);
			}
		}
		return component;
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
		return "any";
	}
}
