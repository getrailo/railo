package railo.transformer.cfml.evaluator.impl;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.statement.tag.TagLoop;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.ExprTransformer;
import railo.transformer.cfml.TransfomerSettings;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;



/**
 * Pr¸ft den Kontext des Tag loop.
 * Die Anforderungen an das Tag unterscheiden sich je nach Definition der Attribute.
 * Falls das Attribute list vorhanden ist, muss auch das Attribute index vorhanden sein.
 * Falls das Attribute list nicht vorhanden ist, aber das Attribute index, m¸ssen auch die Attribute from und to vorhanden sein.
 * Wenn das Attribute condition vorhanden ist, muss dieses mithilfe des ExprTransformer noch transformiert werden. 
 * Falls das Attribute collection verwendet wird, muss auch das Attribute item verwendet werden.
 **/
public final class Loop extends EvaluatorSupport {
	//ç
	/**
	 *
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(railo.transformer.bytecode.statement.tag.Tag, railo.transformer.library.tag.TagLibTag, railo.transformer.library.function.FunctionLib[])
	 */
	public void evaluate(Tag tag,TagLibTag tagLibTag,FunctionLib[] flibs) throws EvaluatorException {
        TagLoop loop=(TagLoop) tag;
		
		// file loop      
        if(tag.containsAttribute("file")) {
            if(!tag.containsAttribute("index"))
                throw new EvaluatorException("Wrong Context, when you use attribute file you must also use attribute index");
            loop.setType(TagLoop.TYPE_FILE);
        }
        // list loop
        else if(tag.containsAttribute("list")){
			if(!tag.containsAttribute("index"))
				throw new EvaluatorException("Wrong Context, when you use attribute list you must also use attribute index");
			loop.setType(TagLoop.TYPE_LIST);
		}
        // array loop
        else if(tag.containsAttribute("array")){
			if(!tag.containsAttribute("index"))
				throw new EvaluatorException("Wrong Context, when you use attribute array you must also use attribute index");
			loop.setType(TagLoop.TYPE_ARRAY);
		}
		// index loop	
		else if(tag.containsAttribute("index")) {
			if(!tag.containsAttribute("from") || !tag.containsAttribute("to"))
				throw new EvaluatorException("Wrong Context, when you use attribute index you must also use attribute from and to or list or file");
			loop.setType(TagLoop.TYPE_INDEX);
		}
		// condition loop
		else if(tag.containsAttribute("condition")){
			TagLib tagLib=tagLibTag.getTagLib();
			ExprTransformer transformer;
			String text=ASMUtil.getAttributeString(tag, "condition");

			try {
				transformer = tagLib.getExprTransfomer();
				Expression expr=transformer.transform(ASMUtil.getAncestorPage(tag),null,flibs,new CFMLString(text,"UTF-8"),TransfomerSettings.toSetting(ThreadLocalPageContext.getConfig()));
				tag.addAttribute(new Attribute(false,"condition",CastBoolean.toExprBoolean(expr),"boolean"));
			}
			catch (Exception e) {
				throw new EvaluatorException(e.getMessage());
			}
			loop.setType(TagLoop.TYPE_CONDITION);
		}
		// query loop
		else if(tag.containsAttribute("query")){
			loop.setType(TagLoop.TYPE_QUERY);
		}
        // collection loop      
        else if(tag.containsAttribute("collection")) {
            if(!tag.containsAttribute("item"))
                throw new EvaluatorException("Wrong Context, when you use attribute collection you must also use attribute item");
            loop.setType(TagLoop.TYPE_COLLECTION);
        }
		else {
			throw new EvaluatorException("Wrong Context, invalid attributes in tag cfloop");
		}
	}
}