package railo.transformer.cfml.evaluator.impl;

import java.util.Iterator;
import java.util.List;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.PrintOut;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.statement.tag.TagFunction;
import railo.transformer.bytecode.statement.tag.TagImport;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.library.tag.TagLibTag;

public class Interface extends Component {
	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag,TagLibTag libTag) throws EvaluatorException { 
		super.evaluate(tag,libTag);
		Body body = tag.getBody();
		List statments = body.getStatements();
		Statement stat;
		Iterator it = statments.iterator();
		Tag t;
		while(it.hasNext()) {
			stat=(Statement) it.next();
			
			if(stat instanceof PrintOut) {
				//body.remove(stat);
			}
			else if(stat instanceof Tag) {
				t=(Tag) stat;
				if(stat instanceof TagImport) {
					// ignore
				}
				else if(stat instanceof TagFunction) {
					
					Function.throwIfNotEmpty(t);
					Attribute attr = t.getAttribute("access");
					
					if(attr!=null) {
						ExprString expr = CastString.toExprString(attr.getValue());
						
						if(!(expr instanceof LitString))
							throw new EvaluatorException(
						"the attribute access of the Tag function inside an interface must contain a constant value");
						String access = ((LitString)expr).getString().trim();
						if(!"public".equalsIgnoreCase(access))
							throw new EvaluatorException(
						"the attribute access of the tag function inside an interface definition can only have the value [public] not ["+access+"]");
					}
					else t.addAttribute(new Attribute(false,"access",LitString.toExprString("public"),"string"));
					
				}
				else throw new EvaluatorException("tag "+libTag.getFullName()+" can only contain function definitions.");
			}
		}
		
		
	}

}
