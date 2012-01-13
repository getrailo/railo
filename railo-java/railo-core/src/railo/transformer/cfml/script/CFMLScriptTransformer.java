package railo.transformer.cfml.script;

import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.cfml.expression.AbstrCFMLExprTransformer.Data;
import railo.transformer.cfml.tag.CFMLTransformer;
import railo.transformer.cfml.tag.TagDependentBodyTransformer;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

public class CFMLScriptTransformer extends AbstrCFMLScriptTransformer implements TagDependentBodyTransformer {
	
	/**
	 * Einstiegsmethode für den CFScript Transformer, 
	 * die Methode erbt sich von der Transform Methode der data.cfmlExprTransformer Klasse.
	 * Der einzige Unterschied liegt darin, das der data.cfmlString der eingegeben wird als vererbte Klasse CFScriptString vorliegen muss.
	 * Der Parameter ist als data.cfmlString definiert, so dass er die transform Methode überschreibt.
	 * @param fld Array von Function Libraries, 
	 * Mithilfe dieser Function Libraries kann der Transfomer buil-in Funktionen innerhalb des data.cfml Codes erkennen 
	 * und validieren.
	 * <br />
	 * EBNF:<br />
	 * <code>statements;</code>
	 * @param cfxdTag XML Document des aktuellen zu erstellenden CFXD
	 * @param libTag Definition des aktuellen Tag.
	 * @param data.cfml data.cfml Code 
	 * @param parentTransformer
	 * @throws TemplateException
	 */
	public void transform(Config config,CFMLTransformer parentTransformer,EvaluatorPool ep,FunctionLib[] fld, Tag tag,TagLibTag libTag, CFMLString cfml) throws TemplateException	{
		Page page = ASMUtil.getAncestorPage(tag);
		boolean isCFC= page.isComponent();
		boolean isInterface= page.isInterface();
		
		Data data = init(ep,fld,cfml,true);
		data.insideFunction=false; 
		data.tagName=libTag.getFullName();
		data.isCFC=isCFC;
		data.isInterface=isInterface;
		data.scriptTags=((ConfigImpl) config).getCoreTagLib().getScriptTags();
		
		tag.setBody(statements(data));
	}

	/**
	 * @see railo.transformer.data.cfml.expression.data.cfmlExprTransformer#expression()
	 */
	public final Expression expression(Data data) throws TemplateException {
		Expression expr;
		expr = super.expression(data);
		comments(data);
		return expr;
	}
}
