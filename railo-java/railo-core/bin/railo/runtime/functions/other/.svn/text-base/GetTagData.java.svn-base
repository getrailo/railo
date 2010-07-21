/**
 * Implements the Cold Fusion Function getfunctiondescription
 */
package railo.runtime.functions.other;

import java.util.Iterator;
import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.library.tag.TagLibTagAttr;

public final class GetTagData implements Function {
	
	public static Struct call(PageContext pc , String nameSpace, String strTagName) throws PageException {
		TagLib[] tlds;
		tlds = ((ConfigImpl)pc.getConfig()).getTLDs();

		Struct sct=new StructImpl();
		TagLib tld=null;
		TagLibTag tag=null;
		for(int i=0;i<tlds.length;i++) {
		    tld=tlds[i];
			if(tld.getNameSpaceAndSeparator().equalsIgnoreCase(nameSpace)) {
			    tag = tld.getTag(strTagName.toLowerCase());
			    if(tag!=null)break;
			}
			
		}
		if(tag == null) throw new ExpressionException("tag ["+nameSpace+strTagName+"] is not a build in tag");

		sct.set("nameSpace",tld.getNameSpace());
		sct.set("nameSpaceSeperator",tld.getNameSpaceSeparator());
		
		sct.set("name",tag.getName());
		sct.set("description",tag.getDescription());
		sct.set("attributeType",getAttributeType(tag));
		sct.set("parseBody",Caster.toBoolean(tag.getParseBody()));
		sct.set("bodyType",getBodyType(tag));
		sct.set("attrMin",Caster.toDouble(tag.getMin()));
		sct.set("attrMax",Caster.toDouble(tag.getMax()));
		sct.set("hasNameAppendix",Caster.toBoolean(tag.hasAppendix()));
		
		
		
		
		
		
		
		Struct _args=new StructImpl();
		sct.set("attributes",_args);
		
		Map atts = tag.getAttributes();
		Iterator it = atts.keySet().iterator();
		
		while(it.hasNext()) {
		    Object key = it.next();
		    TagLibTagAttr attr=(TagLibTagAttr) atts.get(key);
		    if(attr.getHidden()) continue;
		//for(int i=0;i<args.size();i++) {
			Struct _arg=new StructImpl();
			_arg.set("description",attr.getDescription());
			_arg.set("type",attr.getType());
			_arg.set("required",attr.isRequired()?Boolean.TRUE:Boolean.FALSE);
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
}
