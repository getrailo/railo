package railo.runtime.tag;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.tagext.Tag;

import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.DynamicAttributes;
import railo.runtime.op.Caster;
import railo.runtime.reflection.Reflector;
import railo.runtime.reflection.pairs.MethodInstance;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDFImpl;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.KeyConstants;
import railo.transformer.library.tag.TagLibTag;

public class TagUtil {
	
	public static final short ORIGINAL_CASE = 0;
	public static final short UPPER_CASE = 1;
	public static final short LOWER_CASE = 2;

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
		
		
		//Key[] keys = attrs.keys();
		Iterator<Entry<Key, Object>> it;
		Entry<Key, Object> e;
		if(TagLibTag.ATTRIBUTE_TYPE_DYNAMIC==attrType) {
			DynamicAttributes da=(DynamicAttributes) tag;
			it = attrs.entryIterator();
			while(it.hasNext()) {
				e = it.next();
				da.setDynamicAttribute(null, e.getKey(),e.getValue());
			}
		}
		else if(TagLibTag.ATTRIBUTE_TYPE_FIXED==attrType) {
			Object value;
			it = attrs.entryIterator();
			while(it.hasNext()) {
				e = it.next();
				value=e.getValue();
				if(value!=null)Reflector.callSetterEL(tag, e.getKey().getString(),value);
				//}catch(PageException pe){}
			}	
		}
		else if(TagLibTag.ATTRIBUTE_TYPE_MIXED==attrType) {
			MethodInstance setter;
			it = attrs.entryIterator();
			while(it.hasNext()) {
				e = it.next();
				setter = Reflector.getSetterEL(tag, e.getKey().getString(),e.getValue());
				if(setter!=null) {
					try {
						setter.invoke(tag);
					} 
					catch (Exception _e) {
						throw Caster.toPageException(_e);
					}
				}
				else {
					DynamicAttributes da=(DynamicAttributes) tag;
					da.setDynamicAttribute(null, e.getKey(),e.getValue());
				}
			}
		}
	}

	/* *
	 * sets dynamic attributes
	 * @param attributes
	 * @param name
	 * @param value
	 * /
	public static void setDynamicAttribute(StructImpl attributes,String name, Object value, short caseType) {
		if(LOWER_CASE==caseType)name=StringUtil.toLowerCase(name);
		else if(UPPER_CASE==caseType)name=StringUtil.toUpperCase(name);
        if(name.equals("attributecollection")) {
            if(value instanceof railo.runtime.type.Collection) {
            	railo.runtime.type.Collection coll=(railo.runtime.type.Collection)value;
                Iterator<Entry<Key, Object>> it = coll.entryIterator();
            	Entry<Key, Object> e;
                while(it.hasNext()) {
                	e = it.next();
                    if(attributes.get(e.getKey(),null)==null)
                        attributes.setEL(e.getKey(),e.getValue());
                }
                return;
            }
            else if(value instanceof Map) {
            	
            	Map map=(Map) value;
			    Iterator it = map.entrySet().iterator();
			    Map.Entry entry;
			    Key key;
			    while(it.hasNext()) {
			    	entry=(Entry) it.next();
			    	key = UDFImpl.toKey(entry.getKey());
			    	if(!attributes.containsKey(key)){
			    		attributes.setEL(key,entry.getValue());
	            	}
	            }
                return;
            }
        }
        attributes.setEL(KeyImpl.getInstance(name), value);
	}*/
	

	public static void setDynamicAttribute(StructImpl attributes,Collection.Key name, Object value, short caseType) {
		if(name.equalsIgnoreCase(KeyConstants._attributecollection)) {
            if(value instanceof railo.runtime.type.Collection) {
            	railo.runtime.type.Collection coll=(railo.runtime.type.Collection)value;
                Iterator<Entry<Key, Object>> it = coll.entryIterator();
            	Entry<Key, Object> e;
                while(it.hasNext()) {
                	e = it.next();
                    if(attributes.get(e.getKey(),null)==null)
                        attributes.setEL(e.getKey(),e.getValue());
                }
                return;
            }
            else if(value instanceof Map) {
            	
            	Map map=(Map) value;
			    Iterator it = map.entrySet().iterator();
			    Map.Entry entry;
			    Key key;
			    while(it.hasNext()) {
			    	entry=(Entry) it.next();
			    	key = UDFImpl.toKey(entry.getKey());
			    	if(!attributes.containsKey(key)){
			    		attributes.setEL(key,entry.getValue());
	            	}
	            }
                return;
            }
        }
		if(LOWER_CASE==caseType)name=KeyImpl.init(name.getLowerString());
		else if(UPPER_CASE==caseType)name=KeyImpl.init(name.getUpperString());
        attributes.setEL(name, value);
	}

	
}
