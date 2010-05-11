package railo.transformer.cfml.evaluator.impl;

import java.util.Iterator;
import java.util.List;

import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLibTag;



/**
 * Pr¸ft den Kontext des Tag break.
 * Das Tag <code>break</code> darf nur innerhalb des Tag <code>loop, while, foreach</code> liegen.
 */
public class Component extends EvaluatorSupport {
//ç

	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */

	public void evaluate(Tag tag,TagLibTag tlt) throws EvaluatorException { 
		
		
		
		
		Statement pPage = tag.getParent();
		String className=tag.getTagLibTag().getTagClassName();
		
		// is direct in document
		if(!(pPage instanceof Page)){
			
			// is script Component
			Tag p = ASMUtil.getParentTag(tag);
			if(p.getTagLibTag().getName().equals("script") && (pPage = p.getParent()) instanceof Page){
				ASMUtil.replace(p, tag, false);
			}
			else
				throw new EvaluatorException("Wrong Context, tag "+tlt.getFullName()+" can't be inside other tags, tag is inside tag "+p.getFullname());
		}

		Page page=(Page) pPage;
		
		// is inside a file named cfc
		String src=page.getSource();
		int pos=src.lastIndexOf(".");
		if(!(pos!=-1 && pos<src.length() && src.substring(pos+1).equals("cfc")))
			throw new EvaluatorException("Wrong Context, "+tlt.getFullName()+" tag must be inside a file with extension cfc");
		
		// check if more than one component in document and remove any other data
		List stats = page.getStatements();
		Iterator it = stats.iterator();
		Statement stat;
		int count=0;
		while(it.hasNext()) {
			stat=(Statement) it.next();
			if(stat instanceof Tag) {
				tag=(Tag) stat;
				if(tag.getTagLibTag().getTagClassName().equals(className)) count++;
			}
		}
		if(count>1)
			throw new EvaluatorException("inside one cfc file only one tag "+tlt.getFullName()+" is allowed, now we have "+count);

		if("railo.runtime.tag.Component".equals(tlt.getTagClassName()))page.setIsComponent(true);
		if("railo.runtime.tag.Interface".equals(tlt.getTagClassName()))page.setIsInterface(true);
		
		// Attribute Output
		// "output=true" wird in "railo.transformer.cfml.attributes.impl.Function" gehändelt
		Attribute attrOutput = tag.getAttribute("output");
		if(attrOutput!=null) {
			Expression expr = CastBoolean.toExprBoolean(attrOutput.getValue());
			if(!(expr instanceof LitBoolean))
				throw new EvaluatorException("Attribute output of the Tag "+tlt.getFullName()+", must be a static boolean value (true or false, yes or no)");
			boolean output = ((LitBoolean)expr).getBooleanValue();
			//if(!output) ASMUtil.removeLiterlChildren(tag, true);
		}	
	}
}




