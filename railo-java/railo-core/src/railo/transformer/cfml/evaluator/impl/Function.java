package railo.transformer.cfml.evaluator.impl;


import java.util.Iterator;
import java.util.List;
import java.util.Map;

import railo.runtime.functions.system.CFFunction;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.PrintOut;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.tag.TagLibTag;

/**
 * Pr¸ft den Kontext des Tag function.
 * Das Attribute <code>argument</code> darf nur direkt innerhalb des Tag <code>function</code> liegen.
 * Dem Tag <code>argument</code> muss als erstes im tag function vorkommen
 */
public final class Function extends EvaluatorSupport {

	//ç
	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */
	public void evaluate(Tag tag, TagLibTag libTag, FunctionLib[] flibs) throws EvaluatorException {
		Body p=(Body) tag.getParent();
		Statement pp = p.getParent();
		
		boolean isCFC=true;
        try {
			isCFC = ASMUtil.getAncestorPage(tag).isComponent();
		} catch (BytecodeException e) {}

		Attribute attrName = tag.getAttribute("name");
		if(attrName!=null) {
			Expression expr = attrName.getValue();
			if(expr instanceof LitString && !isCFC){
				checkFunctionName(((LitString)expr).getString(),flibs);
			}
				
		}
		// attribute abstract
		Attribute attrAbstract = tag.getAttribute("abstract");
		if(attrAbstract!=null) {
			Expression expr = CastBoolean.toExprBoolean(attrAbstract.getValue());
			if(!(expr instanceof LitBoolean))
				throw new EvaluatorException("Attribute abstract of the Tag Function, must be a literal boolean value (true or false, yes or no)");
			boolean abstr = ((LitBoolean)expr).getBooleanValue();
			if(abstr)throwIfNotEmpty(tag);
		}
		
		// Attribute Output
		// "output=true" wird in "railo.transformer.cfml.attributes.impl.Function" gehändelt
		Attribute attrOutput = tag.getAttribute("output");
		if(attrOutput!=null) {
			Expression expr = CastBoolean.toExprBoolean(attrOutput.getValue());
			if(!(expr instanceof LitBoolean))
				throw new EvaluatorException("Attribute output of the Tag Function, must be a literal boolean value (true or false, yes or no)");
			boolean output = ((LitBoolean)expr).getBooleanValue();
			//if(!output) ASMUtil.removeLiterlChildren(tag, true);
		}
		
		
        //if(ASMUtil.isRoot(pp)) {
        	Map attrs = tag.getAttributes();
        	Iterator it = attrs.keySet().iterator();
        	Attribute attr;
        	while(it.hasNext()) {
        		attr=(Attribute) attrs.get(it.next());
        		checkAttributeValue(tag,attr);
        	}
        //}
        
	}


	public static void checkFunctionName(String name, FunctionLib[] flibs) throws EvaluatorException {
		FunctionLibFunction flf;
		for (int i = 0; i < flibs.length; i++) {
			flf = flibs[i].getFunction(name);
			if(flf!=null && flf.getCazz()!=CFFunction.class) {
				throw new EvaluatorException("The name ["+name+"] is already used by a built in Function");
			}
		}
	}


	public static void throwIfNotEmpty(Tag tag) throws EvaluatorException {
		Body body = tag.getBody();
		List statments = body.getStatements();
		Statement stat;
		Iterator it = statments.iterator();
		TagLibTag tlt;
		
		while(it.hasNext()) {
			stat=(Statement) it.next();
			if(stat instanceof PrintOut) {
				//body.remove(stat);
			}
			else if(stat instanceof Tag) {
				tlt = ((Tag)stat).getTagLibTag();
				if(!tlt.getTagClassName().equals("railo.runtime.tag.Argument"))
					throw new EvaluatorException("tag "+tlt.getFullName()+" is not allowed inside a function declaration");
			}
		}
	}

	private void checkAttributeValue(Tag tag, Attribute attr) throws EvaluatorException {
		if(!(attr.getValue() instanceof Literal))
			throw new EvaluatorException("Attribute ["+attr.getName()+"] of the Tag ["+tag.getFullname()+"] must be a literal/constant value");
        
    }
}