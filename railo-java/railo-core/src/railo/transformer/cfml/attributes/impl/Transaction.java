package railo.transformer.cfml.attributes.impl;

import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.attributes.AttributeEvaluator;
import railo.transformer.cfml.attributes.AttributeEvaluatorException;
import railo.transformer.library.tag.TagLibTag;

public class Transaction implements AttributeEvaluator {

	public TagLibTag evaluate(TagLibTag tagLibTag, Tag tag) throws AttributeEvaluatorException {
		Attribute action = tag.getAttribute("action");
		
		if(action!=null){
			Tag parent = ASMUtil.getAncestorTag(tag, tag.getFullname());
			if(parent!=null) {
				tagLibTag=tagLibTag.duplicate(false);
				tagLibTag.setBodyContent("empty");
			}
		}
		
		return tagLibTag;
	}

}
