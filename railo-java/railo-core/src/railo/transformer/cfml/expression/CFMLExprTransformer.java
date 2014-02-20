package railo.transformer.cfml.expression;

import railo.runtime.exp.TemplateException;
import railo.transformer.Factory;
import railo.transformer.bytecode.Page;
import railo.transformer.cfml.ExprTransformer;
import railo.transformer.cfml.TransfomerSettings;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.cfml.script.AbstrCFMLScriptTransformer;
import railo.transformer.expression.Expression;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

public class CFMLExprTransformer extends AbstrCFMLScriptTransformer implements ExprTransformer {

	@Override
	public Expression transformAsString(Factory factory,Page page,EvaluatorPool ep,FunctionLib[] fld,TagLibTag[] scriptTags, CFMLString cfml, TransfomerSettings settings, boolean allowLowerThan) throws TemplateException {
		return transformAsString(init(factory,page,ep,fld,scriptTags, cfml,settings,allowLowerThan),new String[]{" ", ">", "/>"});
	}
	
	@Override
	public Expression transform(Factory factory,Page page,EvaluatorPool ep,FunctionLib[] fld,TagLibTag[] scriptTags, CFMLString cfml, TransfomerSettings settings) throws TemplateException {
		ExprData data = init(factory,page,ep,fld,scriptTags, cfml,settings,false);
		comments(data);
		return assignOp(data);
	}
	
}
