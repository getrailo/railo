package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BodyBase;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.statement.tag.TagOutput;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;


/**
 * Prueft den Kontext des Tag Mail.

 */
public final class Mail extends EvaluatorSupport {

	
	//ç
	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag,TagLibTag libTag) throws EvaluatorException { 
		if(tag.containsAttribute("query")) {
		    
			
		    TagLib lib = libTag.getTagLib();
		    TagLibTag outputTag = lib.getTag("output");
		    
		    TagOutput output=new TagOutput(tag.getStart(),null);
		    output.setFullname(outputTag.getFullName());
		    output.setTagLibTag(outputTag);
		    output.addAttribute(new Attribute(false,"output",LitBoolean.TRUE,"boolean"));
		    output.addAttribute(new Attribute(false,"formail",LitBoolean.TRUE,"boolean"));
		    
		    Body body=new BodyBase();//output.getBody();
		    output.setBody(body);
		    
		    ASMUtil.replace(tag,output,false);
		    body.addStatement(tag);

		    output.addAttribute(tag.removeAttribute("query"));
		    if(tag.containsAttribute("group"))output.addAttribute(tag.removeAttribute("group"));
		    if(tag.containsAttribute("groupcasesensitive"))output.addAttribute(tag.removeAttribute("groupcasesensitive"));
		    if(tag.containsAttribute("startrow"))output.addAttribute(tag.removeAttribute("startrow"));
		    if(tag.containsAttribute("maxrows"))output.addAttribute(tag.removeAttribute("maxrows"));
		    
		    new Output().evaluate(output,outputTag);
		}
	}
}




