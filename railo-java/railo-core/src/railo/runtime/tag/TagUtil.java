package railo.runtime.tag;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.jsp.tagext.Tag;

import railo.commons.io.DevNullOutputStream;
import railo.commons.lang.Pair;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.ComponentImpl;
import railo.runtime.ComponentSpecificAccess;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.component.ComponentLoader;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.DynamicAttributes;
import railo.runtime.op.Caster;
import railo.runtime.reflection.Reflector;
import railo.runtime.reflection.pairs.MethodInstance;
import railo.runtime.thread.ThreadUtil;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.KeyConstants;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.library.tag.TagLibTagAttr;

public class TagUtil {
	
	public static final short ORIGINAL_CASE = 0;
	public static final short UPPER_CASE = 1;
	public static final short LOWER_CASE = 2;

	//private static final String "invalid call of the function ["+tlt.getName()+", you can not mix named on regular arguments]" = "invalid argument for function, only named arguments are allowed like struct(name:\"value\",name2:\"value2\")";

	public static void setAttributeCollection(PageContext pc,Tag tag, MissingAttribute[] missingAttrs, Struct _attrs, int attrType) throws PageException {
		// check missing tags
		Map<Key, Object> att=new HashMap<Key, Object>();
		{
			Iterator<Entry<Key, Object>> it = _attrs.entryIterator();
			Entry<Key, Object> e;
			while(it.hasNext()){
				e = it.next();
				att.put(e.getKey(), e.getValue());
			}
		}
		
		if(!ArrayUtil.isEmpty(missingAttrs)){
			Key k;
			Object value;
			MissingAttribute miss;
			for(int i=0;i<missingAttrs.length;i++) {
				miss = missingAttrs[i];
				value=att.get(miss.getName());
				// check alias
				if(value==null && !ArrayUtil.isEmpty(miss.getAlias())) {
					String[] alias = miss.getAlias();
					for(int y=0;y<alias.length;y++){
						value=att.get(k=KeyImpl.init(alias[y]));
						if(value!=null) {
							att.remove(k);
							break;
						}
					}
				}
				
				
				if(value==null)
					throw new ApplicationException("attribute "+missingAttrs[i].getName().getString()+" is required but missing");
					//throw new ApplicationException("attribute "+missingAttrs[i].getName().getString()+" is required for tag "+tag.getFullName());
				att.put(
						missingAttrs[i].getName(), 
						Caster.castTo(pc, missingAttrs[i].getType(), value, false));
			}
		}
		setAttributes(pc,tag,att,attrType);
	}
	

	public static void setAttributes(PageContext pc,Tag tag, Map<Key, Object> att, int attrType) throws PageException {
		Iterator<Entry<Key, Object>> it;
		Entry<Key, Object> e;
		//TagLibTag tlt=null;
		if(TagLibTag.ATTRIBUTE_TYPE_DYNAMIC==attrType) {
			DynamicAttributes da=(DynamicAttributes) tag;
			it = att.entrySet().iterator();
			while(it.hasNext()) {
				e = it.next();
				da.setDynamicAttribute(null, e.getKey(),e.getValue());
			}
		}
		else if(TagLibTag.ATTRIBUTE_TYPE_FIXED==attrType) {
			Object value;
			it = att.entrySet().iterator();
			MethodInstance setter;
			while(it.hasNext()) {
				e = it.next();
				value=e.getValue();
				if(value!=null){
					setter = Reflector.getSetter(tag, e.getKey().getLowerString(),value,null);
					//if(tlt==null) tlt=getTLT(pc.getConfig(), tag);
					//setter=getSetter(pc,tlt,tag,e.getKey().getLowerString(),value);
					if(setter!=null) {
						try {
							setter.invoke(tag);
						} 
						catch (Exception _e) {
							throw Caster.toPageException(_e);
						}
					}
				}
				//}catch(PageException pe){}
			}	
		}
		else if(TagLibTag.ATTRIBUTE_TYPE_MIXED==attrType) {
			MethodInstance setter;
			it = att.entrySet().iterator();
			while(it.hasNext()) {
				e = it.next();
				setter = Reflector.getSetter(tag, e.getKey().getLowerString(),e.getValue(),null);
				//if(tlt==null) tlt=getTLT(pc.getConfig(), tag);
				//setter=getSetter(pc,tlt,tag,e.getKey().getLowerString(),e.getValue());
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

	/*private static MethodInstance getSetter(PageContext pc,TagLibTag tlt, Tag tag, String name, Object value) throws PageException { 
		MethodInstance setter = Reflector.getSetter(tag, name,value,null);
		if(setter==null && tlt!=null) {
			TagLibTagAttr attr = tlt.getAttribute(name);
			if(attr==null)
				throw new TemplateException(
					"Attribute "+name+" is not allowed for tag "+tlt.getFullName(),
					"valid attribute names are ["+tlt.getAttributeNames()+"]");
			value=Caster.castTo(pc, attr.getType(), value, false);
			setter = Reflector.getSetter(tag, name,value,null);
		}
		return setter;
	}*/

	private static TagLibTag getTLT(ConfigWeb config, Tag tag) {
		TagLib[] tlds = ((ConfigWebImpl)config).getTLDs();
		TagLibTag tlt;
		for(int i=0;i<tlds.length;i++){
			tlt = tlds[i].getTag(tag.getClass());
			if(tlt!=null) return tlt;
		}
		return null;
	}

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
			    	key = Caster.toKey(entry.getKey(),null);
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

	/**
     * load metadata from cfc based custom tags and add the info to the tag
     * @param cs
     * @param config
     */
    public static void addTagMetaData(ConfigWebImpl cw) {
    	if(true) return;
    	
    	PageContextImpl pc=null;
    	try{
    		pc = ThreadUtil.createPageContext(cw, DevNullOutputStream.DEV_NULL_OUTPUT_STREAM, 
				"localhost", "/","", new Cookie[0], new Pair[0], new Pair[0], new StructImpl());
    		
    	}
    	catch(Throwable t){
    		return;
    	}
    	PageContext orgPC = ThreadLocalPageContext.get();
    	ThreadLocalPageContext.register(pc);
        try{
    		TagLibTagAttr attrFileName,attrIsWeb;
        	String filename;
    		Boolean isWeb;
    		TagLibTag tlt;
    		
    		TagLib[] tlds = cw.getTLDs();
    		for(int i=0;i<tlds.length;i++){
    			Map<String, TagLibTag> tags = tlds[i].getTags();
    			Iterator<TagLibTag> it = tags.values().iterator();
		    	while(it.hasNext()){
		    		tlt = it.next();
		    		if("railo.runtime.tag.CFTagCore".equals(tlt.getTagClassName())) {
		    			attrFileName = tlt.getAttribute("__filename");
		    			attrIsWeb = tlt.getAttribute("__isweb");
		    			if(attrFileName!=null && attrIsWeb!=null) {
		    				filename = Caster.toString(attrFileName.getDefaultValue(),null);
		    				isWeb=Caster.toBoolean(attrIsWeb.getDefaultValue(),null);
		    				if(filename!=null && isWeb!=null) {
		    					addTagMetaData(pc, tlds[i], tlt, filename,isWeb.booleanValue());
		    				}
		    			}
		    		}
		    	}
    		}
    	}
    	catch(Throwable t){
    		//t.printStackTrace();
    	}
    	finally{
    		pc.release();
    		ThreadLocalPageContext.register(orgPC);
    	}
	}

	private static void addTagMetaData(PageContext pc,TagLib tl, TagLibTag tlt, String filename, boolean isWeb) {
    	if(pc==null) return;
		try{
			ConfigWebImpl config=(ConfigWebImpl) pc.getConfig();
			PageSource ps = isWeb?
					config.getTagMapping().getPageSource(filename):
					config.getServerTagMapping().getPageSource(filename);
			
			Page p = ps.loadPage(pc);
			ComponentImpl c = ComponentLoader.loadComponent(pc, p, ps, filename, true,true);
			ComponentSpecificAccess cw = ComponentSpecificAccess.toComponentSpecificAccess(Component.ACCESS_PRIVATE,c);
			Struct meta = Caster.toStruct( cw.get(KeyConstants._metadata,null),null);

			// TODO handle all metadata here and make checking at runtime useless
			if(meta!=null) {
				
				// parse body
				boolean rtexprvalue=Caster.toBooleanValue(meta.get(KeyConstants._parsebody,Boolean.FALSE),false);
	    		tlt.setParseBody(rtexprvalue);
	    		
	    		// hint
	    		String hint=Caster.toString(meta.get(KeyConstants._hint,null),null);
	    		if(!StringUtil.isEmpty(hint))tlt.setDescription(hint);
	    		
			}
			
		} 
		catch (Throwable t) {
			//t.printStackTrace();
		}
    }
	
}
