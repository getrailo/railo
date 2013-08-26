package railo.runtime.type;

import railo.commons.lang.CFTypes;
import railo.commons.lang.StringUtil;
import railo.runtime.ComponentImpl;
import railo.runtime.PageContext;
import railo.runtime.component.Property;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection.Key;

public final class UDFGetterProperty extends UDFGSProperty {

	private final Property prop;
	//private ComponentScope scope;
	private final Key propName;

	public UDFGetterProperty(ComponentImpl component,Property prop)  {
		super(component,"get"+StringUtil.ucFirst(prop.getName()),new FunctionArgument[0],CFTypes.TYPE_STRING,"wddx");
		this.prop=prop;
		this.propName=KeyImpl.getInstance(prop.getName());
		
	} 

	public UDF duplicate() {
		return new UDFGetterProperty(component,prop);
	}
	
	@Override
	public Object call(PageContext pageContext, Object[] args,boolean doIncludePath) throws PageException {
		return component.getComponentScope().get(pageContext, propName,null);
	}

	@Override
	public Object callWithNamedValues(PageContext pageContext, Struct values,boolean doIncludePath) throws PageException {
		return component.getComponentScope().get(pageContext,propName,null);
	}

	@Override
	public Object implementation(PageContext pageContext) throws Throwable {
		return component.getComponentScope().get(pageContext,propName,null);
	}
	
	@Override
	public Object getDefaultValue(PageContext pc, int index) throws PageException {
		return null;
	}
	
	@Override
	public Object getDefaultValue(PageContext pc, int index, Object defaultValue) throws PageException {
		return defaultValue;
	}

	@Override
	public String getReturnTypeAsString() {
		return prop.getType();
	}


}
