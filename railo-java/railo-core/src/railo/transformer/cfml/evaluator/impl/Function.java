package railo.transformer.cfml.evaluator.impl;


import java.util.Iterator;
import java.util.List;
import java.util.Map;

import railo.commons.lang.StringUtil;
import railo.runtime.functions.system.CFFunction;
import railo.runtime.listener.AppListenerUtil;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.expression.ExprString;
import railo.transformer.expression.Expression;
import railo.transformer.expression.literal.LitBoolean;
import railo.transformer.expression.literal.LitString;
import railo.transformer.expression.literal.Literal;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.tag.TagLibTag;

/**
 * Prueft den Kontext des Tag function.
 * Das Attribute <code>argument</code> darf nur direkt innerhalb des Tag <code>function</code> liegen.
 * Dem Tag <code>argument</code> muss als erstes im tag function vorkommen
 */
public final class Function extends EvaluatorSupport {

	@Override
	public void evaluate(Tag tag, TagLibTag libTag, FunctionLib[] flibs) throws EvaluatorException {
		//Body p=(Body) tag.getParent();
		//Statement pp = p.getParent();
		
		boolean isCFC=true;
        try {
			isCFC = ASMUtil.getAncestorPage(tag).isComponent();
		} catch (TransformerException e) {}

		Attribute attrName = tag.getAttribute("name");
		if(attrName!=null) {
			Expression expr = attrName.getValue();
			if(expr instanceof LitString && !isCFC){
				checkFunctionName(((LitString)expr).getString(),flibs);
			}
				
		}
		// attribute modifier
		Attribute attrModifier = tag.getAttribute("modifier");
		if(attrModifier!=null) {
			ExprString expr = tag.getFactory().toExprString(attrModifier.getValue());
			if(!(expr instanceof Literal))
				throw new EvaluatorException("Attribute modifier of the Tag Function, must be one of the following literal string values: [abstract] or [final]");
			String modifier=StringUtil.emptyIfNull(((Literal)expr).getString()).trim();
			if(!StringUtil.isEmpty(modifier) && !"abstract".equalsIgnoreCase(modifier) && !"final".equalsIgnoreCase(modifier))
				throw new EvaluatorException("Attribute modifier of the Tag Function, must be one of the following literal string values: [abstract] or [final]");
			
			
			boolean abstr = "abstract".equalsIgnoreCase(modifier);
			if(abstr)throwIfNotEmpty(tag);
		}
		
		// cachedWithin
		Attribute attrCachedWithin = tag.getAttribute("cachedwithin");
		if(attrCachedWithin!=null) {
			Expression val = attrCachedWithin.getValue();
			tag.addAttribute(new Attribute(
					attrCachedWithin.isDynamicType(), 
					attrCachedWithin.getName(), 
					ASMUtil.cachedWithinValue(val),
					attrCachedWithin.getType()));
		}
		
		// Attribute localMode
		Attribute attrLocalMode = tag.getAttribute("localmode");
		if(attrLocalMode!=null) {
			Expression expr = attrLocalMode.getValue();
			String str = ASMUtil.toString(expr,null);
			if(!StringUtil.isEmpty(str) && AppListenerUtil.toLocalMode(str, -1)==-1)
				throw new EvaluatorException("Attribute localMode of the Tag Function, must be a literal value (modern, classic, true or false)");
			//boolean output = ((LitBoolean)expr).getBooleanValue();
			//if(!output) ASMUtil.removeLiterlChildren(tag, true);
		}
		
		
		// Attribute Output
		Attribute attrOutput = tag.getAttribute("output");
		if(attrOutput!=null) {
			Expression expr = tag.getFactory().toExprBoolean(attrOutput.getValue());
			if(!(expr instanceof LitBoolean))
				throw new EvaluatorException("Attribute output of the Tag Function, must be a literal boolean value (true or false, yes or no)");
		}
		
		Attribute attrBufferOutput = tag.getAttribute("bufferoutput");
		if(attrBufferOutput!=null) {
			Expression expr = tag.getFactory().toExprBoolean(attrBufferOutput.getValue());
			if(!(expr instanceof LitBoolean))
				throw new EvaluatorException("Attribute bufferOutput of the Tag Function, must be a literal boolean value (true or false, yes or no)");
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
			if(flf!=null && flf.getClazz(null)!=CFFunction.class) {
				throw new EvaluatorException("The name ["+name+"] is already used by a built in Function");
			}
		}
	}

	public static void throwIfNotEmpty(Tag tag) throws EvaluatorException {
		Body body = tag.getBody();
		List<Statement> statments = body.getStatements();
		Statement stat;
		Iterator<Statement> it = statments.iterator();
		TagLibTag tlt;
		
		while(it.hasNext()) {
			stat=it.next();
			if(stat instanceof Tag) {
				tlt = ((Tag)stat).getTagLibTag();
				if(!tlt.getTagClassName().equals("railo.runtime.tag.Argument"))
					throw new EvaluatorException("tag "+tlt.getFullName()+" is not allowed inside a function declaration");
			}
			/*else if(stat instanceof PrintOut) {
				//body.remove(stat);
			}*/
		}
	}

	private void checkAttributeValue(Tag tag, Attribute attr) throws EvaluatorException {
		if(!(attr.getValue() instanceof Literal))
			throw new EvaluatorException("Attribute ["+attr.getName()+"] of the Tag ["+tag.getFullname()+"] must be a literal/constant value");
        
    }
}