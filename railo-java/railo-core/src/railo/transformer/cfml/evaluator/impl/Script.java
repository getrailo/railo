package railo.transformer.cfml.evaluator.impl;

import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.statement.tag.TagJavaScript;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.cfml.script.JavaScriptTransformer;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLibTag;



public final class Script extends EvaluatorSupport {
	
	private static TagLibTag javaVersion;

	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(railo.transformer.bytecode.statement.tag.Tag, railo.transformer.library.tag.TagLibTag, railo.transformer.library.function.FunctionLib[])
	 */
	public void evaluate(Tag tag,TagLibTag tagLibTag,FunctionLib[] flibs) throws EvaluatorException {
        
		Attribute attr = tag.getAttribute("language");
		if(attr!=null) {
			Expression expr = CastString.toExprString(attr.getValue());
			if(!(expr instanceof LitString))
				throw new EvaluatorException("Attribute language of the Tag script, must be a literal string value");
			String str = ((LitString)expr).getString().trim();
			if("java".equalsIgnoreCase(str)){
				if(javaVersion==null){
					javaVersion=tag.getTagLibTag().duplicate(false);
					javaVersion.setTdbtClass(JavaScriptTransformer.class.getName());
					javaVersion.setTttClass(TagJavaScript.class.getName());
					
				}
			}
			else if(!"cfml".equalsIgnoreCase(str))
				throw new EvaluatorException("invalid value for attribute language from tag script ["+str+"], valid values are [cfml,java]");
		}
	}
}