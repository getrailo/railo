package railo.transformer.cfml.evaluator.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import railo.commons.lang.IDGenerator;
import railo.commons.lang.Md5;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.op.OpString;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLibTag;


public final class Sprite extends EvaluatorSupport {
	
	private static final Expression DELIMITER = LitString.toExprString(",");
	private static Map<String,Previous> sprites=new HashMap<String,Previous>(); 
	
	
	
	
	/**
	 *
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(railo.transformer.bytecode.statement.tag.Tag, railo.transformer.library.tag.TagLibTag, railo.transformer.library.function.FunctionLib[])
	 */
	public void evaluate(Tag tag,TagLibTag tagLibTag,FunctionLib[] flibs) throws EvaluatorException {
        String id="sprite_"+IDGenerator.intId();
        try {
			Page page = ASMUtil.getAncestorPage(tag);
			String key=Md5.getDigestAsString(Thread.currentThread().getId()+":"+page.getSource());
			Expression src = tag.getAttribute("src").getValue();
			
			
			// get data from previous sprites
			Previous previous = sprites.get(key);
			if(previous!=null) {
				previous.tag.removeAttribute("_ids");
				previous.tag.removeAttribute("_srcs");
				previous.tag=tag;
			}
			else {
				sprites.put(key, previous = new Previous(tag));
			}
			
			previous.ids.add(id);
			if(previous.src==null)previous.src=src;
			else {
				previous.src=OpString.toExprString(previous.src,DELIMITER);
				previous.src=OpString.toExprString(previous.src,src);
			}
			
			
			
			tag.addAttribute(
					new Attribute(
							false,
							"_id",
							LitString.toExprString(id),
							"string"
					));
			tag.addAttribute(
					new Attribute(
							false,
							"_ids",
							LitString.toExprString(railo.runtime.type.util.ListUtil.listToList(previous.ids, ",")),
							"string"
					));

			tag.addAttribute(
					new Attribute(
							false,
							"_srcs",
							previous.src,
							"string"
					));
			
		} 
        catch (Throwable e) {// TODO handle Excpetion much more precise
			throw new PageRuntimeException(Caster.toPageException(e));
		}
		   
	}
	
	private static class Previous {
		public Previous(Tag tag) {
			this.tag=tag;
		}
		private List<String> ids=new ArrayList<String>();
		private Expression src=null;
		private Tag tag;
		
	}
	
}