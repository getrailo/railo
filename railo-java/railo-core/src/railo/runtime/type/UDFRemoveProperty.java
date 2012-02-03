package railo.runtime.type;

import java.util.Iterator;
import java.util.Map;

import railo.commons.lang.CFTypes;
import railo.commons.lang.StringUtil;
import railo.runtime.ComponentImpl;
import railo.runtime.PageContext;
import railo.runtime.component.Property;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.orm.ORMUtil;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.util.PropertyFactory;

public final class UDFRemoveProperty extends UDFGSProperty {

	private final Property prop;
	//private ComponentScope scope;
	
	private final Key propName;
	
	private static final Object NULL=new Object();

	public UDFRemoveProperty(ComponentImpl component,Property prop)  {
		super(component,"remove"+StringUtil.ucFirst(PropertyFactory.getSingularName(prop)),getFunctionArgument(prop),CFTypes.TYPE_BOOLEAN,"wddx");
		this.prop=prop;
		this.propName=KeyImpl.getInstance(prop.getName());
	} 

	private static FunctionArgument[] getFunctionArgument(Property prop) {
		String t = PropertyFactory.getType(prop);
		
		if("struct".equalsIgnoreCase(t)){
			FunctionArgumentImpl key = new FunctionArgumentImpl(KeyImpl.KEY,"string",CFTypes.TYPE_STRING,true);
			return new FunctionArgument[]{key};
		}
		FunctionArgumentImpl value = new FunctionArgumentImpl(KeyImpl.init(PropertyFactory.getSingularName(prop)),"any",CFTypes.TYPE_ANY,true);
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
		return new UDFRemoveProperty(c,prop);
	}
	
 
	public UDF duplicate() {
		return duplicate(component);
	}
	
	/**
	 * @see railo.runtime.type.UDF#call(railo.runtime.PageContext, java.lang.Object[], boolean)
	 */
	public Object call(PageContext pageContext, Object[] args,boolean doIncludePath) throws PageException {
		if(args.length<1)
			throw new ExpressionException("The parameter "+this.arguments[0].getName()+" to function "+getFunctionName()+" is required but was not passed in.");
		
		return remove(pageContext, args[0]);
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
			if(keys.length==1) {
				value=values.get(keys[0]);
			}
			else throw new ExpressionException("The parameter "+key+" to function "+getFunctionName()+" is required but was not passed in.");
		}
		
		return remove(pageContext, value);
	}
	
	
	private boolean remove(PageContext pageContext, Object value) throws PageException {
		Object propValue = component.getComponentScope().get(propName,null);
		value=cast(arguments[0],value,1);
		
		// struct
		if(isStruct()) {
			String strKey = Caster.toString(value,null);
			if(strKey==null) return false;
			
			if(propValue instanceof Struct) {
				return ((Struct)propValue).removeEL(KeyImpl.getInstance(strKey))!=null;
			}
			else if(propValue instanceof Map) {
				return ((Map)propValue).remove(strKey)!=null;
			}
			return false;
		}
		else {
			Object o;
			boolean has=false;
			if(propValue instanceof Array) {
				Array arr = ((Array)propValue);
				Key[] keys = arr.keys();
				for(int i=0;i<keys.length;i++){
					o=arr.get(keys[i],null);
					if(ORMUtil.equals(value,o)){
						arr.removeEL(keys[i]);
						has=true;
					}
				}
			}
			else if(propValue instanceof java.util.List) {
				Iterator it=((java.util.List)propValue).iterator();
				while(it.hasNext()){
					o = it.next();
					if(ORMUtil.equals(value,o)){
						it.remove();
						has=true;
					}
				}
			}
			return has;
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
