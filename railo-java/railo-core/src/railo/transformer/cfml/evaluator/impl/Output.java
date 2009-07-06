package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.statement.tag.TagOutput;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLibTag;


/**
 * Prüft den Kontext des Tag output.
 * Das Tag output darf nicht innerhalb eines output Tag verschachtelt sein, 
 * ausser das äussere Tag besitzt ein group Attribute. Das innere Tag darf jedoch kein group Attribute besitzen.

 */
public final class Output extends EvaluatorSupport {

	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag,TagLibTag libTag) throws EvaluatorException { 
		
		TagOutput output=(TagOutput) tag;
		
        // check if inside a query tag
		TagOutput parent = output;
        boolean hasParentWithGroup=false;
        boolean hasParentWithQuery=false;
		boolean hasQuery=tag.containsAttribute("query");
		
		while((parent=getParentTagOutput(parent))!=null) {
            if(!hasParentWithQuery)hasParentWithQuery=parent.hasQuery();
            if(!hasParentWithGroup)hasParentWithGroup=parent.hasGroup();
            if(hasParentWithQuery && hasParentWithGroup)break;
		}
        
        
        
        if(hasQuery && hasParentWithQuery) 
			throw new EvaluatorException("Nesting of tags cfoutput with attribut query is not allowed");

        if(hasQuery) 
        	output.setType(TagOutput.TYPE_QUERY);
        	//ASMUtil.replace(tag, new TagOutputQuery(tag));
        else if(tag.containsAttribute("group") && hasParentWithQuery)
        	output.setType(TagOutput.TYPE_GROUP);
        	//ASMUtil.replace(tag, new TagOutputGroup(tag));
        else if(hasParentWithQuery) {
        	if(hasParentWithGroup) output.setType(TagOutput.TYPE_INNER_GROUP);
        	else output.setType(TagOutput.TYPE_INNER_QUERY);
        	//ASMUtil.replace(tag, new TagOutputInner(tag,hasParentWithGroup));
        }
        else
        	 output.setType(TagOutput.TYPE_NORMAL);
        	//ASMUtil.replace(tag, new TagOutputNormal(tag));
        
        //if(hasParentWithQuery)tag.addAttribute(new Attribute("inner",LitBoolean.toExprBoolean(true, -1),"boolean"));
        //if(hasParentWithGroup)tag.addAttribute(new Attribute("hasGroup",LitBoolean.toExprBoolean(true, -1),"boolean"));
        
	}
	
	public static TagOutput getParentTagOutput(TagOutput stat) {
		Statement parent = stat;
		
		
		while(true)	{
			parent=parent.getParent();
			if(parent==null)return null;
			if(parent instanceof TagOutput)	return (TagOutput) parent;
		}
	}
}




