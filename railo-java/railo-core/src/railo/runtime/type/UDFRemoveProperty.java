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
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.PropertyFactory;
import railo.runtime.type.util.UDFUtil;

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
			FunctionArgumentImpl key = new FunctionArgumentImpl(KeyConstants._key,"string",CFTypes.TYPE_STRING,true);
			return new FunctionArgument[]{key};
		}
		FunctionArgumentImpl value = new FunctionArgumentImpl(KeyImpl.init(PropertyFactory.getSingularName(prop)),"any",CFTypes.TYPE_ANY,true);
		return new FunctionArgument[]{value};
	}
	
	private boolean isStruct() {
		String t = PropertyFactory.getType(prop);
		return "struct".equalsIgnoreCase(t);
	}

	@Override
	public UDF duplicate() {
		return new UDFRemoveProperty(component,prop);
	}
	
	@Override
	public Object call(PageContext pageContext, Object[] args,boolean doIncludePath) throws PageException {
		if(args.length<1)
			throw new ExpressionException("The parameter "+this.arguments[0].getName()+" to function "+getFunctionName()+" is required but was not passed in.");
		
		return remove(pageContext, args[0]);
	}

	@Override
	public Object callWithNamedValues(PageContext pageContext, Struct values,boolean doIncludePath) throws PageException {
		UDFUtil.argumentCollection(values,getFunctionArguments());
		Key key = arguments[0].getName();
		Object value = values.get(key,null);
		if(value==null){
			Key[] keys = CollectionUtil.keys(values);
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
		
			Object o;
			boolean has=false;
			if(propValue instanceof Array) {
				Array arr = ((Array)propValue);
				Key[] keys = CollectionUtil.keys(arr);
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
