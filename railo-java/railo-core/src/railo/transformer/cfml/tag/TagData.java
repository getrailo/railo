package railo.transformer.cfml.tag;

import railo.transformer.Factory;
import railo.transformer.bytecode.Page;
import railo.transformer.cfml.Data;
import railo.transformer.cfml.TransfomerSettings;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.cfml.expression.SimpleExprTransformer;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

public class TagData extends Data {
	
	private SimpleExprTransformer set;
	
    public TagData(Factory factory,TagLib[][] tlibs, FunctionLib[] flibs,TagLibTag[] scriptTags, CFMLString cfml,TransfomerSettings settings,Page page) {
		super(factory,page,cfml,new EvaluatorPool(),settings,flibs,scriptTags);
		this.tlibs = tlibs;
	}
    public TagData(Factory factory,TagLib[][] tlibs, FunctionLib[] flibs,TagLibTag[] scriptTags, CFMLString cfml,Boolean dotNotationUpperCase,Page page) {
		super(factory,page,cfml,new EvaluatorPool(),TransfomerSettings.toSetting(page.getPageSource().getMapping(),dotNotationUpperCase),flibs,scriptTags);
		this.tlibs = tlibs;

	}
	
	public SimpleExprTransformer getSimpleExprTransformer() {
		return set;
	}

	public void setSimpleExprTransformer(SimpleExprTransformer set) {
		this.set = set;
	}
}