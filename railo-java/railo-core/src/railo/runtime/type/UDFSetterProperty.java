 package railo.runtime.type;

import railo.commons.lang.CFTypes;
import railo.commons.lang.StringUtil;
import railo.runtime.ComponentImpl;
import railo.runtime.PageContext;
import railo.runtime.component.Property;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection.Key;

public class UDFSetterProperty extends UDFGSProperty {

	private Property prop;
	private Key propName;

	public UDFSetterProperty(ComponentImpl component,Property prop) {
		super(component,"set"+StringUtil.ucFirst(prop.getName()),new FunctionArgument[]{
			new FunctionArgumentImpl(prop.getName(),prop.getType(),true)
		},CFTypes.TYPE_ANY,"wddx");
		
		
		this.prop=prop; 
		this.propName=KeyImpl.init(prop.getName());
		
	} 

	/**
	 * @see railo.runtime.type.UDF#duplicate()
	 */
	public UDF duplicate(ComponentImpl c) {
		return new UDFSetterProperty(c,prop);
	}

	

	public UDF duplicate() {
		return duplicate(component);
	}
	/**
	 * @see railo.runtime.type.UDF#call(railo.runtime.PageContext, java.lang.Object[], boolean)
	 */
	public Object call(PageContext pageContext, Object[] args,boolean doIncludePath) throws PageException {
		if(args.length<1)
			throw new ExpressionException("The parameter "+prop.getName()+" to function "+getFunctionName()+" is required but was not passed in.");
		component.getComponentScope().set(propName, args[0]);
		
		return component;
	}

	/**
	 * @see railo.runtime.type.UDF#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Struct, boolean)
	 */
	public Object callWithNamedValues(PageContext pageContext, Struct values,boolean doIncludePath) throws PageException {
		UDFImpl.argumentCollection(values,getFunctionArguments());
		Object value = values.get(propName,null);
		
		if(value==null){
			Key[] keys = values.keys();
			if(keys.length==1) {
				value=values.get(keys[0]);
			}
			else throw new ExpressionException("The parameter "+prop.getName()+" to function "+getFunctionName()+" is required but was not passed in.");
		}
		component.getComponentScope().set(propName, value);
		return component;
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

	/**
	 * @see railo.runtime.type.UDF#implementation(railo.runtime.PageContext)
	 */
	public Object implementation(PageContext pageContext) throws Throwable {
		return null;
	}

}
