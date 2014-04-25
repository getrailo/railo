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
import railo.transformer.util.SourceCode;

public class TagData extends Data {
	
	private SimpleExprTransformer set;
	
    public TagData(Factory factory,TagLib[][] tlibs, FunctionLib[] flibs,TagLibTag[] scriptTags, SourceCode cfml,TransfomerSettings settings,Page page) {
		super(factory,page,cfml,new EvaluatorPool(),settings,tlibs,flibs,scriptTags);
	}
    public TagData(Factory factory,TagLib[][] tlibs, FunctionLib[] flibs,TagLibTag[] scriptTags, SourceCode cfml,Boolean dotNotationUpperCase,Page page) {
		super(factory,page,cfml,new EvaluatorPool(),TransfomerSettings.toSetting(page.getPageSource().getMapping(),dotNotationUpperCase),tlibs,flibs,scriptTags);

	}
	
	public SimpleExprTransformer getSimpleExprTransformer() {
		return set;
	}

	public void setSimpleExprTransformer(SimpleExprTransformer set) {
		this.set = set;
	}
}