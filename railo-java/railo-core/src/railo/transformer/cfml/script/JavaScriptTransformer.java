package railo.transformer.cfml.script;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.ScriptBody;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.cfml.TransfomerSettings;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.cfml.expression.CFMLExprTransformer;
import railo.transformer.cfml.tag.CFMLTransformer;
import railo.transformer.cfml.tag.TagDependentBodyTransformer;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;


/**	
 * Innerhalb des Tag script kann in CFML eine eigene Scriptsprache verwendet werden, 
 * welche sich an Javascript orientiert. 
 * Da der data.cfml Transformer keine Spezialfaelle zulaesst, 
 * also Tags einfach anhand der eingegeben TLD einliest und transformiert, 
 * aus diesem Grund wird der Inhalt des Tag script einfach als Zeichenkette eingelesen.
 * Erst durch den Evaluator (siehe 3.3), der fuer das Tag script definiert ist, 
 * wird der Inhalt des Tag script uebersetzt.
 * 
 */
public final class JavaScriptTransformer extends CFMLExprTransformer implements TagDependentBodyTransformer {
	
	public void transform(Page page,CFMLTransformer parent, EvaluatorPool ep,
			FunctionLib[] flibs, Tag tag, TagLibTag tagLibTag,TagLibTag[] scriptTags, CFMLString cfml,TransfomerSettings settings)
			throws TemplateException {
		
		StringBuilder sb=new StringBuilder();
		//MUST add again int startline=cfml.getLine();
		while(!cfml.isAfterLast() && !cfml.isCurrent("</",tagLibTag.getFullName())){
			sb.append(cfml.getCurrent());
			cfml.next();
		}
		//int endline=cfml.getLine();
		if(cfml.isAfterLast())
			throw new TemplateException(cfml,"missing end tag"); // TODO better error message
		
		
		if(true) throw new RuntimeException("not implemented");
		//MUST add again String dummyStart="public class Susi {public static void code(){"+StringUtil.repeatString("\n", startline-1);
		
		//MUST add again String dummyEnd="}}";
		//MUST add again String src=dummyStart+sb+dummyEnd;
		//MUST add again Label start=new Label();
		//MUST add again Label end=new Label();
		
		//MUST add again ByteArrayInputStream bais = new ByteArrayInputStream(src.getBytes());
		
		try {
			//MUST add again CompilationUnit cu = JavaParser.parse(bais);
			//MUST add again DataBag db = new DataBag();
			ScriptBody body=new ScriptBody();
			tag.setBody(body);
			//MUST add again new JavaParserVisitor(body,start,end).visit(cu, db);
			
		} 
		catch (Exception e) {
			throw new TemplateException(cfml,e);
		}
	}
}