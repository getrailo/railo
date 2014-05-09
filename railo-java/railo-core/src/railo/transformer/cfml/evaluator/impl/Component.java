package railo.transformer.cfml.evaluator.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import railo.runtime.config.Constants;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.statement.tag.TagCIObject;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.expression.Expression;
import railo.transformer.expression.literal.LitBoolean;
import railo.transformer.expression.literal.LitString;
import railo.transformer.library.tag.TagLibTag;



/**
 * Prueft den Kontext des Tag break.
 * Das Tag <code>break</code> darf nur innerhalb des Tag <code>loop, while, foreach</code> liegen.
 */
public class Component extends EvaluatorSupport {

	@Override
	public void evaluate(Tag tag,TagLibTag tlt) throws EvaluatorException { 
		/*if(tag instanceof TagOther) {
			print.e(((TagOther)tag).getFullname());
		}*/
		TagCIObject tc=(TagCIObject) tag;
		
		
		Statement pPage = tag.getParent();
		String className=tag.getTagLibTag().getTagClassName();
		Page page;
		
		
		// move components inside script to root
		if(pPage instanceof Page){
			page=(Page) pPage;
		}
		else {
			// is in script 
			Tag p = ASMUtil.getParentTag(tag);
			if(p.getTagLibTag().getName().equals(Constants.SCRIPT_TAG_NAME) && (pPage = p.getParent()) instanceof Page){
				page=(Page) pPage;
				
				// move imports from script to component body
				List<Statement> children = p.getBody().getStatements();
				Iterator<Statement> it = children.iterator();
				Statement stat;
				Tag t;
				while(it.hasNext()){
					stat=it.next();
					if(!(stat instanceof Tag)) continue;
					t=(Tag) stat;
					if(t.getTagLibTag().getName().equals("import")){
						tag.getBody().addStatement(t);
					}
				}
				
				//move to page
				ASMUtil.move(tag, page);
				
				
				
				
				//if(!inline)ASMUtil.replace(p, tag, false);
			}
			else
				throw new EvaluatorException("Wrong Context, tag "+tlt.getFullName()+" can't be inside other tags, tag is inside tag "+p.getFullname());
		}
		
		//Page page=(Page) pPage;
		boolean insideCompFile=isInsideComponentFile(page);
		boolean main=isMainComponent(page,tc);
		
		
		


		// is a full grown component or a inline component
		
		
		// check if more than one component in document and remove any other data
		if(insideCompFile) {
			/*List<Statement> stats = page.getStatements();
			Iterator<Statement> it = stats.iterator();
			Statement stat;
			int count=0;
			while(it.hasNext()) {
				stat=it.next();
				if(stat instanceof Tag) {
					tag=(Tag) stat;
					if(tag.getTagLibTag().getTagClassName().equals(className)) count++;
				}
			}
			if(count>1)
				throw new EvaluatorException("inside one cfc file only one tag "+tlt.getFullName()+" is allowed, now we have "+count);
		*/
		}
		else {
			throw new EvaluatorException("Wrong Context, "+tlt.getFullName()+" tag must be inside a file with extension "+Constants.COMPONENT_EXTENSION);
		}
		
		
		
		boolean isComponent="railo.runtime.tag.Component".equals(tlt.getTagClassName());
		/*boolean isInterface="railo.runtime.tag.Interface".equals(tlt.getTagClassName());
		if(main) {
			if(isComponent)			page.setIsComponent(true);
			else if(isInterface)	page.setIsInterface(true);
		}*/
		tc.setMain(main);
		
		
		
// Attributes
		
		// Name
		String name=null;
		if(!main) {
			Map<String, Attribute> attrs = tag.getAttributes();
			if(attrs.size()>0) {
				Attribute first = attrs.values().iterator().next();
				if(first.isDefaultValue()) {
					name=first.getName();
				}
			}
		
			if(name==null) {
				Attribute attr = tag.getAttribute("name");
				if(attr!=null) {
					Expression expr = tag.getFactory().toExprString(attr.getValue());
					if(!(expr instanceof LitString)) throw new EvaluatorException("Name of the component "+tlt.getFullName()+", must be a literal string value");
					name=((LitString)expr).getString();
				}
				else
					throw new EvaluatorException("Missing name of the component "+tlt.getFullName()+"");
			}
			tc.setName(name);
		}
		
		
		// output
		// "output=true" wird in "railo.transformer.cfml.attributes.impl.Function" gehändelt
		Attribute attr = tag.getAttribute("output");
		if(attr!=null) {
			Expression expr = tag.getFactory().toExprBoolean(attr.getValue());
			if(!(expr instanceof LitBoolean))
				throw new EvaluatorException("Attribute output of the Tag "+tlt.getFullName()+", must contain a static boolean value (true or false, yes or no)");
			//boolean output = ((LitBoolean)expr).getBooleanValue();
			//if(!output) ASMUtil.removeLiterlChildren(tag, true);
		}
		
		// extends
		attr = tag.getAttribute("extends");
		if(attr!=null) {
			Expression expr = tag.getFactory().toExprString(attr.getValue());
			if(!(expr instanceof LitString)) throw new EvaluatorException("Attribute extends of the Tag "+tlt.getFullName()+", must contain a literal string value");
		}
		
		// implements
		if(isComponent){
			attr = tag.getAttribute("implements");
			if(attr!=null) {
				Expression expr = tag.getFactory().toExprString(attr.getValue());
				if(!(expr instanceof LitString)) throw new EvaluatorException("Attribute implements of the Tag "+tlt.getFullName()+", must contain a literal string value");
			}
		}
	}

	private boolean isMainComponent(Page page, TagCIObject comp) {
		// first is main
		Iterator<Statement> it = page.getStatements().iterator();
		while(it.hasNext()){
			Statement s = it.next();
			if(s instanceof TagCIObject) return s == comp;
		}
		return false;
	}

	private boolean isInsideComponentFile(Page page) {
		String src=page.getPageSource().getDisplayPath();
		int pos=src.lastIndexOf(".");
		return pos!=-1 && pos<src.length() && src.substring(pos+1).equals(Constants.COMPONENT_EXTENSION);
	}
}




