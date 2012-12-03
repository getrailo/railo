package railo.transformer.cfml.script;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.cfml.TransfomerSettings;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.cfml.tag.CFMLTransformer;
import railo.transformer.cfml.tag.TagDependentBodyTransformer;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

public class CFMLOrJavaScriptTransformer implements TagDependentBodyTransformer {

	private JavaScriptTransformer jst=new JavaScriptTransformer();
	private CFMLScriptTransformer cst=new CFMLScriptTransformer();
	
	public void transform(Page page,CFMLTransformer parent, EvaluatorPool ep,FunctionLib[] flibs, Tag tag, TagLibTag tagLibTag,TagLibTag[] scriptTags, CFMLString cfml,TransfomerSettings settings) 
	throws TemplateException {
		Attribute attr = tag.getAttribute("language");
		if(attr!=null) {
			Expression expr = CastString.toExprString(attr.getValue());
			if(!(expr instanceof LitString))
				throw new TemplateException(cfml,"Attribute language of the Tag script, must be a literal string value");
			String str = ((LitString)expr).getString().trim();
			if("java".equalsIgnoreCase(str))		jst.transform(page,parent, ep, flibs, tag, tagLibTag,scriptTags, cfml,settings);
			else if("cfml".equalsIgnoreCase(str))	cst.transform(page,parent, ep, flibs, tag, tagLibTag,scriptTags, cfml,settings);
			else 
				throw new TemplateException(cfml,"invalid value for attribute language from tag script ["+str+"], valid values are [cfml,java]");
		}
	}

}
