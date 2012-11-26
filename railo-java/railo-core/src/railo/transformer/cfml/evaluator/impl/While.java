package railo.transformer.cfml.evaluator.impl;

import railo.commons.lang.StringUtil;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.statement.tag.TagWhile;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLibTag;



public final class While extends EvaluatorSupport {
	@Override
	public void evaluate(Tag tag,TagLibTag tagLibTag,FunctionLib[] flibs) throws EvaluatorException {
		TagWhile whil=(TagWhile) tag;
		
		// label
		if(ASMUtil.isLiteralAttribute(tag, "label", ASMUtil.TYPE_STRING, false, true)) {
			LitString ls=(LitString) CastString.toExprString(tag.getAttribute("label").getValue());
			String l = ls.getString();
			if(!StringUtil.isEmpty(l,true)) {
				whil.setLabel(l.trim());
				tag.removeAttribute("label");
			}
		}
	}
}
