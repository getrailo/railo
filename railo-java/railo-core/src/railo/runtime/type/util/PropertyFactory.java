package railo.runtime.type.util;

import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.ComponentImpl;
import railo.runtime.component.Member;
import railo.runtime.component.Property;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFAddProperty;
import railo.runtime.type.UDFGetterProperty;
import railo.runtime.type.UDFHasProperty;
import railo.runtime.type.UDFRemoveProperty;
import railo.runtime.type.UDFSetterProperty;

public class PropertyFactory {

	public static final Collection.Key SINGULAR_NAME = KeyImpl.intern("singularName");
	public static final Key FIELD_TYPE = KeyConstants._fieldtype;

	
	public static void createPropertyUDFs(ComponentImpl comp, Property property) throws PageException {
		// getter
		if(property.getGetter()){
			PropertyFactory.addGet(comp,property);
		}
		// setter
		if(property.getSetter()){
			PropertyFactory.addSet(comp,property);
		}

		String fieldType = Caster.toString(property.getDynamicAttributes().get(PropertyFactory.FIELD_TYPE,null),null);
		
		// add
		if(fieldType!=null) {
			if("one-to-many".equalsIgnoreCase(fieldType) || "many-to-many".equalsIgnoreCase(fieldType)) {
				PropertyFactory.addHas(comp,property);
				PropertyFactory.addAdd(comp,property);
				PropertyFactory.addRemove(comp,property);
			}
			else if("one-to-one".equalsIgnoreCase(fieldType) || "many-to-one".equalsIgnoreCase(fieldType)) {
				PropertyFactory.addHas(comp,property);
			}
		}
	}
	
	
	public static void addGet(ComponentImpl comp, Property prop) {
		Member m = comp.getMember(Component.ACCESS_PRIVATE,KeyImpl.getInstance("get"+prop.getName()),true,false);
		if(!(m instanceof UDF)){
			UDF udf = new UDFGetterProperty(comp,prop);
			comp.registerUDF(udf.getFunctionName(), udf);
		}
	}

	public static void addSet(ComponentImpl comp, Property prop) throws PageException {
		Member m = comp.getMember(Component.ACCESS_PRIVATE,KeyImpl.getInstance("set"+prop.getName()),true,false);
		if(!(m instanceof UDF)){
			UDF udf = new UDFSetterProperty(comp,prop);
			comp.registerUDF(udf.getFunctionName(), udf);
		}
	}
	
	public static void addHas(ComponentImpl comp, Property prop) {
		Member m = comp.getMember(Component.ACCESS_PRIVATE,KeyImpl.getInstance("has"+getSingularName(prop)),true,false);
		if(!(m instanceof UDF)){
			UDF udf = new UDFHasProperty(comp,prop);
			comp.registerUDF(udf.getFunctionName(), udf);
		}
	}

	public static void addAdd(ComponentImpl comp, Property prop) {
		Member m = comp.getMember(ComponentImpl.ACCESS_PRIVATE,KeyImpl.getInstance("add"+getSingularName(prop)),true,false);
		if(!(m instanceof UDF)){
			UDF udf = new UDFAddProperty(comp,prop);
			comp.registerUDF(udf.getFunctionName(), udf);
		}
	}

	public static void addRemove(ComponentImpl comp, Property prop) {
		Member m = comp.getMember(Component.ACCESS_PRIVATE,KeyImpl.getInstance("remove"+getSingularName(prop)),true,false);
		if(!(m instanceof UDF)){
			UDF udf = new UDFRemoveProperty(comp,prop);
			comp.registerUDF(udf.getFunctionName(), udf);
		}
	}

	public static String getSingularName(Property prop) {
		String singularName=Caster.toString(prop.getDynamicAttributes().get(SINGULAR_NAME,null),null);
		if(!StringUtil.isEmpty(singularName)) return singularName;
		return prop.getName();
	}
	
	public static String getType(Property prop){
		String type = prop.getType();
		if(StringUtil.isEmpty(type) || "any".equalsIgnoreCase(type) || "object".equalsIgnoreCase(type)){
			String fieldType = Caster.toString(prop.getDynamicAttributes().get(FIELD_TYPE,null),null);
			if("one-to-many".equalsIgnoreCase(fieldType) || "many-to-many".equalsIgnoreCase(fieldType)){
				return "array";
			}
			return "any";
		}
        return type;
    }

}
