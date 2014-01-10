/**
 * Implements the CFML Function getfunctiondescription
 */
package railo.runtime.functions.other;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.ComponentWrap;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.component.ComponentLoader;
import railo.runtime.config.ConfigImpl;
import railo.runtime.customtag.InitFile;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.tag.CFTagCore;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibFactory;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.library.tag.TagLibTagAttr;
import railo.transformer.library.tag.TagLibTagScript;

public final class GetTagData implements Function {
	
	public static Struct call(PageContext pc , String nameSpace, String strTagName) throws PageException {
		TagLib[] tlds;
		tlds = ((ConfigImpl)pc.getConfig()).getTLDs();

		
		TagLib tld=null;
		TagLibTag tag=null;
		for(int i=0;i<tlds.length;i++) {
		    tld=tlds[i];
			if(tld.getNameSpaceAndSeparator().equalsIgnoreCase(nameSpace)) {
			    tag = tld.getTag(strTagName.toLowerCase());
			    if(tag!=null)break;
			}
			
		}
		if(tag == null) throw new ExpressionException("tag ["+nameSpace+strTagName+"] is not a built in tag");

		// CFML Based Function
		Class clazz=null;
		try{
			clazz=tag.getClazz();
		}
		catch(Throwable t){}
		
		if(clazz==CFTagCore.class){
			PageContextImpl pci=(PageContextImpl) pc;
			boolean prior = pci.useSpecialMappings(true);
			try{
				return cfmlBasedTag(pc,tld,tag);
			}
			finally {
				pci.useSpecialMappings(prior);
			}
			
		}
		return javaBasedTag(tld,tag);
		
		
		
		
		
	}

	private static Struct cfmlBasedTag(PageContext pc, TagLib tld, TagLibTag tag) throws PageException {
		
		//Map attrs = tag.getAttributes();

		TagLibTagAttr attrFilename = tag.getAttribute("__filename");
		//TagLibTagAttr attrName = tag.getAttribute("__name");
		TagLibTagAttr attrIsWeb = tag.getAttribute("__isweb");
		
		String filename = Caster.toString(attrFilename.getDefaultValue());
		String name = Caster.toString(attrFilename.getDefaultValue());
		boolean isWeb = Caster.toBooleanValue(attrIsWeb.getDefaultValue());
		InitFile source = CFTagCore.createInitFile(pc, isWeb, filename);
		
		Component cfc = ComponentLoader.loadComponent(pc,null,source.getPageSource(), source.getFilename().substring(0,source.getFilename().length()-(pc.getConfig().getCFCExtension().length()+1)), false,true);
        ComponentWrap cw=ComponentWrap.toComponentWrap(Component.ACCESS_PRIVATE, cfc);
		Struct metadata=Caster.toStruct(cw.get("metadata",null),null,false);
		
		
		Struct sct=new StructImpl();
		sct.set("nameSpaceSeperator",tld.getNameSpaceSeparator());
		sct.set("nameSpace",tld.getNameSpace());
		sct.set(KeyConstants._name,name.substring(0,name.lastIndexOf('.')));
		sct.set("hasNameAppendix",Boolean.FALSE);
		sct.set(KeyConstants._status,"implemeted");
		sct.set(KeyConstants._type,"cfml");
		
		sct.set("bodyType",getBodyType(tag));
		sct.set("attrMin",Caster.toDouble(0));
		sct.set("attrMax",Caster.toDouble(0));
		sct.set("attributeCollection",getSupportAttributeCollection(tag));
		
		// TODO add support for script for cfml tags
		Struct scp=new StructImpl();
		sct.set(KeyConstants._script,scp);
		scp.set("rtexpr", Boolean.FALSE);
		scp.set(KeyConstants._type, "none");
		
		
		
		
		if(metadata!=null) {
			sct.set(KeyConstants._description,metadata.get("hint",""));
			sct.set("attributeType",metadata.get("attributeType",""));
			sct.set("parseBody",Caster.toBoolean(metadata.get("parseBody",Boolean.FALSE),Boolean.FALSE));
			
			Struct _attrs=new StructImpl();
			sct.set(KeyConstants._attributes,_attrs);
			
			Struct srcAttrs = Caster.toStruct(metadata.get(KeyConstants._attributes,null),null,false);
			Struct src;
			if(srcAttrs!=null){
				//Key[] keys = srcAttrs.keys();
				Iterator<Entry<Key, Object>> it = srcAttrs.entryIterator();
				Entry<Key, Object> e;
				while(it.hasNext()){
					e = it.next();
					src = Caster.toStruct(e.getValue(),null,false);
					if(Caster.toBooleanValue(src.get(KeyConstants._hidden,null),false))continue;
					Struct _attr=new StructImpl();
					_attr.set(KeyConstants._status,"implemeted");
					_attr.set(KeyConstants._description,src.get(KeyConstants._hint,""));
					_attr.set(KeyConstants._type,src.get(KeyConstants._type,"any"));
					_attr.set(KeyConstants._required,Caster.toBoolean(src.get(KeyConstants._required,""),null));
					_attr.set("scriptSupport","none");
					_attrs.setEL(e.getKey().getLowerString(),_attr);
					
				}
			}
			
		}
		
		
		/* /////////////////////
		
		
		Map atts = tag.getAttributes();
		Iterator it = atts.keySet().iterator();
		
		while(it.hasNext()) {
		    Object key = it.next();
		    TagLibTagAttr attr=(TagLibTagAttr) atts.get(key);
		    if(attr.getHidden()) continue;
		//for(int i=0;i<args.size();i++) {
			Struct _arg=new StructImpl();
			_arg.set("status",TagLibFactory.toStatus(attr.getStatus()));
			_arg.set("description",attr.getDescription());
			_arg.set("type",attr.getType());
			_arg.set("required",attr.isRequired()?Boolean.TRUE:Boolean.FALSE);
			_args.setEL(attr.getName(),_arg);
		}
		*/
		
		
		
		return sct;
	}

	private static Struct javaBasedTag(TagLib tld, TagLibTag tag) throws PageException {
		Struct sct=new StructImpl();
		sct.set("nameSpaceSeperator",tld.getNameSpaceSeparator());
		sct.set("nameSpace",tld.getNameSpace());
		sct.set(KeyConstants._name,tag.getName());
		sct.set(KeyConstants._description,tag.getDescription());
		sct.set(KeyConstants._status,TagLibFactory.toStatus(tag.getStatus()));
		
		sct.set("attributeType",getAttributeType(tag));
		sct.set("parseBody",Caster.toBoolean(tag.getParseBody()));
		sct.set("bodyType",getBodyType(tag));
		sct.set("attrMin",Caster.toDouble(tag.getMin()));
		sct.set("attrMax",Caster.toDouble(tag.getMax()));
		sct.set("hasNameAppendix",Caster.toBoolean(tag.hasAppendix()));
		sct.set("attributeCollection",getSupportAttributeCollection(tag));
		
		// script
		TagLibTagScript script = tag.getScript();
		if(script!=null) {
			Struct scp=new StructImpl();
			sct.set(KeyConstants._script,scp);
			scp.set("rtexpr", Caster.toBoolean(script.getRtexpr()));
			scp.set(KeyConstants._type, script.getTypeAsString());
			if(script.getType()==TagLibTagScript.TYPE_SINGLE) {
				TagLibTagAttr attr = script.getSingleAttr();
				if(attr!=null)scp.set("singletype", attr.getScriptSupportAsString());
				else scp.set("singletype", "none");
			}
		}
		
		
		sct.set(KeyConstants._type,"java");
		
		Struct _args=new StructImpl();
		sct.set(KeyConstants._attributes,_args);
		
		Map atts = tag.getAttributes();
		Iterator it = atts.keySet().iterator();
		
		while(it.hasNext()) {
		    Object key = it.next();
		    TagLibTagAttr attr=(TagLibTagAttr) atts.get(key);
		    if(attr.getHidden()) continue;
		//for(int i=0;i<args.size();i++) {
			Struct _arg=new StructImpl();
			_arg.set(KeyConstants._status,TagLibFactory.toStatus(attr.getStatus()));
			_arg.set(KeyConstants._description,attr.getDescription());
			_arg.set(KeyConstants._type,attr.getType());
			if(attr.getValues()!=null)_arg.set(KeyConstants._values,Caster.toArray(attr.getValues()));
			if(attr.getDefaultValue()!=null)_arg.set("defaultValue",attr.getDefaultValue());
			_arg.set(KeyConstants._required,attr.isRequired()?Boolean.TRUE:Boolean.FALSE);
			_arg.set("scriptSupport",attr.getScriptSupportAsString());
			_args.setEL(attr.getName(),_arg);
		}
		return sct;
	}

	private static String getBodyType(TagLibTag tag) {
		if(!tag.getHasBody()) return "prohibited";
		if(tag.isBodyFree()) return "free";
		return "required";
	}

	private static String getAttributeType(TagLibTag tag) {
		int type = tag.getAttributeType();
		if(TagLibTag.ATTRIBUTE_TYPE_DYNAMIC==type) return "dynamic";
		if(TagLibTag.ATTRIBUTE_TYPE_FIXED==type) return "fixed";
		if(TagLibTag.ATTRIBUTE_TYPE_MIXED==type) return "mixed";
		if(TagLibTag.ATTRIBUTE_TYPE_NONAME==type) return "noname";
		
		return "fixed";
	}


	private static Boolean getSupportAttributeCollection(TagLibTag tag) {	
		return StringUtil.isEmpty(tag.getTttClassName())?Boolean.TRUE:Boolean.FALSE;
	}
}
