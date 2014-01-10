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
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.PropertyFactory;
import railo.runtime.type.util.UDFUtil;

public final class UDFHasProperty extends UDFGSProperty {

	private final Property prop;
	//private ComponentScope scope;
	
	private final Key propName;
	
	//private static final String NULL="sdsdsdfsfsfjkln fsdfsa";

	public UDFHasProperty(ComponentImpl component,Property prop)  {
		super(component,"has"+StringUtil.ucFirst(PropertyFactory.getSingularName(prop)),getFunctionArgument(prop),CFTypes.TYPE_BOOLEAN,"wddx");
		this.prop=prop;
		this.propName=KeyImpl.getInstance(prop.getName());
	} 

	private static FunctionArgument[] getFunctionArgument(Property prop) {
		String t = PropertyFactory.getType(prop);
		
		if("struct".equalsIgnoreCase(t)){
			FunctionArgument key = new FunctionArgumentImpl(KeyConstants._key,"string",CFTypes.TYPE_STRING,false);
			return new FunctionArgument[]{key};
		}
		FunctionArgument value = new FunctionArgumentImpl(KeyImpl.init(PropertyFactory.getSingularName(prop)),"any",CFTypes.TYPE_ANY,false);
		return new FunctionArgument[]{value};
	}
	
	private boolean isStruct() {
		String t = PropertyFactory.getType(prop);
		return "struct".equalsIgnoreCase(t);
	}
 
	@Override
	public UDF duplicate() {
		return new UDFHasProperty(component,prop);
	}
	
	@Override
	public Object call(PageContext pageContext, Object[] args,boolean doIncludePath) throws PageException {
		if(args.length<1) return has(pageContext);
		return has(pageContext, args[0]);
	}

	@Override
	public Object callWithNamedValues(PageContext pageContext, Struct values,boolean doIncludePath) throws PageException {
		UDFUtil.argumentCollection(values,getFunctionArguments());
		Key key = arguments[0].getName();
		Object value = values.get(key,null);
		if(value==null){
			Key[] keys = CollectionUtil.keys(values);
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
		
			Object o;
			
			if(propValue instanceof Array) {
				Array arr = ((Array)propValue);
				Iterator<Object> it = arr.valueIterator();
				while(it.hasNext()){
					if(ORMUtil.equals(value,it.next()))return true;
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

	@Override
	public Object implementation(PageContext pageContext) throws Throwable {
		return null;
	}
	
	@Override
	public Object getDefaultValue(PageContext pc, int index) throws PageException {
		return prop.getDefault();
	}
	
	@Override
	public Object getDefaultValue(PageContext pc, int index, Object defaultValue) throws PageException {
		return prop.getDefault();
	}

	@Override
	public String getReturnTypeAsString() {
		return "boolean";
	}
}
