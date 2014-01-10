package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.statement.tag.TagOutput;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLibTag;


/**
 * Prueft den Kontext des Tag output.
 * Das Tag output darf nicht innerhalb eines output Tag verschachtelt sein, 
 * ausser das aeussere Tag besitzt ein group Attribute. Das innere Tag darf jedoch kein group Attribute besitzen.

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
			throw new EvaluatorException("Nesting of tags cfoutput with attribute query is not allowed");

        if(hasQuery) 
        	output.setType(TagOutput.TYPE_QUERY);
        
        else if(tag.containsAttribute("group") && hasParentWithQuery)
        	output.setType(TagOutput.TYPE_GROUP);
        
        else if(hasParentWithQuery) {
        	if(hasParentWithGroup) output.setType(TagOutput.TYPE_INNER_GROUP);
        	else output.setType(TagOutput.TYPE_INNER_QUERY);
        }
        else
        	 output.setType(TagOutput.TYPE_NORMAL);
        
        
        
        // attribute maxrows and endrow not allowd at the same time
        if(tag.containsAttribute("maxrows") && tag.containsAttribute("endrow"))
        	throw new EvaluatorException("Wrong Context, you cannot use attribute maxrows and endrow at the same time.");
        
        
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




