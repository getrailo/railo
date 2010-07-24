package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLibTag;



/**
 *
 * PrÔøºft den Kontext des Tag argument.
 * Das Tag <code>argument</code> darf nur direkt innerhalb des Tag <code>function</code> liegen.
 * Dem Tag <code>argument</code> muss als erstes im tag function vorkommen
 */
public final class Argument extends EvaluatorSupport {
//ç
	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag, TagLibTag libTag) throws EvaluatorException  {
			String ns=libTag.getTagLib().getNameSpaceAndSeparator();
			String functionName=ns+"function";
		    
			ASMUtil.isLiteralAttribute(tag,"type",ASMUtil.TYPE_STRING,false,true);
			ASMUtil.isLiteralAttribute(tag,"name",ASMUtil.TYPE_STRING,false,true);
			//ASMUtil.isLiteralAttribute(tag,"hint",ASMUtil.TYPE_STRING,false,true);
			//ASMUtil.isLiteralAttribute(tag,"displayname",ASMUtil.TYPE_STRING,false,true);
				
			
				
			// check attribute passby
			Attribute attrPassBy = tag.getAttribute("passby");
			if(attrPassBy!=null) {
				ExprString expr = CastString.toExprString(attrPassBy.getValue());
				if(!(expr instanceof LitString))
					throw new EvaluatorException("Attribute passby of the Tag Argument, must be a literal string");
				LitString lit = (LitString)expr;
				String passBy = lit.getString().toLowerCase().trim();
				if(!"value".equals(passBy) && !"ref".equals(passBy) && !"reference".equals(passBy))
					throw new EvaluatorException("Attribute passby of the Tag Argument has a invalid value ["+passBy+"], valid values are [reference,value]");
			}
				
			// check if tag is direct inside function
			if(!ASMUtil.isParentTag(tag,functionName)) {
			    Tag parent=ASMUtil.getParentTag(tag);
			    
			    String addText=(parent!=null)?
			            "but tag "+libTag.getFullName()+" is inside tag "+parent.getFullname()+"":
			            "but tag "+libTag.getFullName()+" has no parent";
			    
				throw new EvaluatorException("Wrong Context, tag "+libTag.getFullName()
				        +" must be direct inside a "+functionName+" tag, "+addText);
			}
			// TODO check if there is a tag other than argument and text before	
			
		}

}