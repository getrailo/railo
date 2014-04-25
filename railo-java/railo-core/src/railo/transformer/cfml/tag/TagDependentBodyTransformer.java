package railo.transformer.cfml.tag;

import railo.runtime.exp.TemplateException;
import railo.transformer.Factory;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.cfml.TransfomerSettings;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.SourceCode;

/**
 * Interface zum implementieren von individullen Parsersn fuer einezelne Tags (cfscript)
 */
public interface TagDependentBodyTransformer {
	
	/**
	 * @param parent
	 * @param flibs
	 * @param cfxdTag
	 * @param tagLibTag
	 * @param cfml
	 * @throws TemplateException
	 */
	public void transform(Factory factory,Page page,CFMLTransformer parent, EvaluatorPool ep,TagLib[][] tlibs, FunctionLib[] flibs, Tag tag, TagLibTag tagLibTag,TagLibTag[] scriptTags, SourceCode cfml,TransfomerSettings setting)
		throws TemplateException;

}