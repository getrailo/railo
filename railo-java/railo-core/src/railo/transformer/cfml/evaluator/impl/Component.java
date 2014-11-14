/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.transformer.cfml.evaluator.impl;

import java.util.Iterator;
import java.util.List;

import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.cfml.evaluator.EvaluatorSupport;
import railo.transformer.library.tag.TagLibTag;



/**
 * Prueft den Kontext des Tag break.
 * Das Tag <code>break</code> darf nur innerhalb des Tag <code>loop, while, foreach</code> liegen.
 */
public class Component extends EvaluatorSupport {


	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(org.w3c.dom.Element, railo.transformer.library.tag.TagLibTag)
	 */

	public void evaluate(Tag tag,TagLibTag tlt) throws EvaluatorException { 

		Statement pPage = tag.getParent();
		String className=tag.getTagLibTag().getTagClassName();
		
		// is direct in document
		if(!(pPage instanceof Page)){
			
			// is script Component
			Tag p = ASMUtil.getParentTag(tag);
			if(p.getTagLibTag().getName().equals("script") && (pPage = p.getParent()) instanceof Page){
				
				// move imports from script to component body
				List children = p.getBody().getStatements();
				Iterator it = children.iterator();
				Statement stat;
				Tag t;
				while(it.hasNext()){
					stat=(Statement) it.next();
					if(!(stat instanceof Tag)) continue;
					t=(Tag) stat;
					if(t.getTagLibTag().getName().equals("import")){
						tag.getBody().addStatement(t);
					}
				}
				
				// replace script with component	
				ASMUtil.replace(p, tag, false);
			}
			else
				throw new EvaluatorException("Wrong Context, tag "+tlt.getFullName()+" can't be inside other tags, tag is inside tag "+p.getFullname());
		}

		Page page=(Page) pPage;
		
		// is inside a file named cfc
		String src=page.getPageSource().getDisplayPath();
		int pos=src.lastIndexOf(".");
		if(!(pos!=-1 && pos<src.length() && src.substring(pos+1).equals("cfc")))
			throw new EvaluatorException("Wrong Context, "+tlt.getFullName()+" tag must be inside a file with extension cfc");
		
		// check if more than one component in document and remove any other data
		List stats = page.getStatements();
		Iterator it = stats.iterator();
		Statement stat;
		int count=0;
		while(it.hasNext()) {
			stat=(Statement) it.next();
			if(stat instanceof Tag) {
				tag=(Tag) stat;
				if(tag.getTagLibTag().getTagClassName().equals(className)) count++;
			}
		}
		if(count>1)
			throw new EvaluatorException("inside one cfc file only one tag "+tlt.getFullName()+" is allowed, now we have "+count);

		boolean isComponent="railo.runtime.tag.Component".equals(tlt.getTagClassName());
		boolean isInterface="railo.runtime.tag.Interface".equals(tlt.getTagClassName());
		if(isComponent)page.setIsComponent(true);
		if(isInterface)page.setIsInterface(true);
		
// Attributes
		
		// output
		// "output=true" wird in "railo.transformer.cfml.attributes.impl.Function" geh�ndelt
		Attribute attr = tag.getAttribute("output");
		if(attr!=null) {
			Expression expr = CastBoolean.toExprBoolean(attr.getValue());
			if(!(expr instanceof LitBoolean))
				throw new EvaluatorException("Attribute output of the Tag "+tlt.getFullName()+", must contain a static boolean value (true or false, yes or no)");
			//boolean output = ((LitBoolean)expr).getBooleanValue();
			//if(!output) ASMUtil.removeLiterlChildren(tag, true);
		}
		
		// extends
		attr = tag.getAttribute("extends");
		if(attr!=null) {
			Expression expr = CastString.toExprString(attr.getValue());
			if(!(expr instanceof LitString)) throw new EvaluatorException("Attribute extends of the Tag "+tlt.getFullName()+", must contain a literal string value");
		}
		
		// implements
		if(isComponent){
			attr = tag.getAttribute("implements");
			if(attr!=null) {
				Expression expr = CastString.toExprString(attr.getValue());
				if(!(expr instanceof LitString)) throw new EvaluatorException("Attribute implements of the Tag "+tlt.getFullName()+", must contain a literal string value");
			}
		}
	}
}




