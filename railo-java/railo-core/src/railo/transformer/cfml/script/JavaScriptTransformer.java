package railo.transformer.cfml.script;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.ByteArrayInputStream;

import org.objectweb.asm.Label;

import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.ScriptBody;
import railo.transformer.bytecode.statement.java.DataBag;
import railo.transformer.bytecode.statement.java.JavaParserVisitor;
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
 * Innerhalb des Tag script kann in Cold Fusion eine eigene Scriptsprache verwendet werden, 
 * welche sich an Javascript orientiert. 
 * Da der data.cfml Transformer keine Spezialfälle zulässt, 
 * also Tags einfach anhand der eingegeben TLD einliest und transformiert, 
 * aus diesem Grund wird der Inhalt des Tag script einfach als Zeichenkette eingelesen.
 * Erst durch den Evaluator (siehe 3.3), der für das Tag script definiert ist, 
 * wird der Inhalt des Tag script übersetzt.
 * 
 */
public final class JavaScriptTransformer extends CFMLExprTransformer implements TagDependentBodyTransformer {
	
	public void transform(Config config,Page page,CFMLTransformer parent, EvaluatorPool ep,
			FunctionLib[] flibs, Tag tag, TagLibTag tagLibTag, CFMLString cfml,TransfomerSettings settings)
			throws TemplateException {
		
		StringBuilder sb=new StringBuilder();
		int startline=cfml.getLine();
		while(!cfml.isAfterLast() && !cfml.isCurrent("</",tagLibTag.getFullName())){
			sb.append(cfml.getCurrent());
			cfml.next();
		}
		//int endline=cfml.getLine();
		if(cfml.isAfterLast())
			throw new TemplateException(cfml,"missing end tag"); // TODO better error message
		
		
		
		String dummyStart="public class Susi {public static void code(){"+StringUtil.repeatString("\n", startline-1);
		
		String dummyEnd="}}";
		String src=dummyStart+sb+dummyEnd;
		Label start=new Label();
		Label end=new Label();
		
		ByteArrayInputStream bais = new ByteArrayInputStream(src.getBytes());
		
		try {
			CompilationUnit cu = JavaParser.parse(bais);
			DataBag db = new DataBag();
			ScriptBody body=new ScriptBody();
			tag.setBody(body);
			new JavaParserVisitor(body,start,end).visit(cu, db);
			
		} 
		catch (Exception e) {
			throw new TemplateException(cfml,e);
		}
	}
}