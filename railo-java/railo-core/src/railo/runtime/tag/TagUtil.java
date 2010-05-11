package railo.runtime.tag;

import javax.servlet.jsp.tagext.Tag;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.DynamicAttributes;
import railo.runtime.op.Caster;
import railo.runtime.reflection.Reflector;
import railo.runtime.reflection.pairs.MethodInstance;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.util.ArrayUtil;
import railo.transformer.library.tag.TagLibTag;

public class TagUtil {
	
	//private static final String "invalid call of the function ["+tlt.getName()+", you can not mix named on regular arguments]" = "invalid argument for function, only named arguments are allowed like struct(name:\"value\",name2:\"value2\")";

	public static void setAttributeCollection(PageContext pc,Tag tag, MissingAttribute[] missingAttrs, Struct attrs, int attrType) throws PageException {
		// check missing tags
		if(!ArrayUtil.isEmpty(missingAttrs)){
			Object value;
			for(int i=0;i<missingAttrs.length;i++) {
				value=attrs.get(
						missingAttrs[i].getName()
						,null);
				if(value==null)
					throw new ApplicationException("attribute "+missingAttrs[i].getName().getString()+" is required but missing");
					//throw new ApplicationException("attribute "+missingAttrs[i].getName().getString()+" is required for tag "+tag.getFullName());
				attrs.put(missingAttrs[i].getName(), Caster.castTo(pc, missingAttrs[i].getType(), value, false));
		}
			
		}
		
		
		Key[] keys = attrs.keys();
		if(TagLibTag.ATTRIBUTE_TYPE_DYNAMIC==attrType) {
			DynamicAttributes da=(DynamicAttributes) tag;
			for(int i=0;i<keys.length;i++) {
				da.setDynamicAttribute(null, keys[i].getString(),attrs.get(keys[i],null));
			}
		}
		else if(TagLibTag.ATTRIBUTE_TYPE_FIXED==attrType) {
			for(int i=0;i<keys.length;i++) {
				try{
				Reflector.callSetter(tag, keys[i].getString(),attrs.get(keys[i],null));
				}
				catch(PageException pe){}
			}	
		}
		else if(TagLibTag.ATTRIBUTE_TYPE_MIXED==attrType) {
			MethodInstance setter;
			for(int i=0;i<keys.length;i++) {
				setter = Reflector.getSetterEL(tag, keys[i].getString(),attrs.get(keys[i],null));
				if(setter!=null) {
					try {
						setter.invoke(tag);
					} 
					catch (Exception e) {
						throw Caster.toPageException(e);
					}
				}
				else {
					DynamicAttributes da=(DynamicAttributes) tag;
					da.setDynamicAttribute(null, keys[i].getString(),attrs.get(keys[i],null));
				}
			}
		}
	}

	/**
	 * sets dynamic attributes
	 * @param attributes
	 * @param name
	 * @param value
	 */
	public static void setDynamicAttribute(StructImpl attributes,String name, Object value) {
        name=StringUtil.toLowerCase(name);
        if(name.equals("attributecollection") && value instanceof railo.runtime.type.Collection) {
            railo.runtime.type.Collection coll=(railo.runtime.type.Collection)value;
            railo.runtime.type.Collection.Key[] keys=coll.keys();
            railo.runtime.type.Collection.Key key;
            for(int i=0;i<keys.length;i++) {
                key=keys[i]; 
                if(attributes.get(key,null)==null)
                    attributes.setEL(key,coll.get(key,null));
            }
        }
        else attributes.setEL(KeyImpl.init(name), value);
	}

	
}
