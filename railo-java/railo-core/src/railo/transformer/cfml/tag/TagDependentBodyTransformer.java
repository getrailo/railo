package railo.transformer.cfml.tag;

import railo.runtime.config.Config;
import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

/**
 * Interface zum implementieren von individullen Parsersn für einezelne Tags (cfscript)
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
	public void transform(Config config,Page page,CFMLTransformer parent, EvaluatorPool ep,FunctionLib[] flibs, Tag tag, TagLibTag tagLibTag, CFMLString cfml)
		throws TemplateException;

}