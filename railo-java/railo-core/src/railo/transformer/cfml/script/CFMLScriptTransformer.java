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
package railo.transformer.cfml.script;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.cfml.TransfomerSettings;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.cfml.tag.CFMLTransformer;
import railo.transformer.cfml.tag.TagDependentBodyTransformer;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

public class CFMLScriptTransformer extends AbstrCFMLScriptTransformer implements TagDependentBodyTransformer {
	
	
	@Override
	public void transform(Page page,CFMLTransformer parentTransformer,EvaluatorPool ep,TagLib[][] tlibs, FunctionLib[] fld, Tag tag,TagLibTag libTag,TagLibTag[] scriptTags, CFMLString cfml,TransfomerSettings settings) throws TemplateException	{
		//Page page = ASMUtil.getAncestorPage(tag);
		boolean isCFC= page.isComponent();
		boolean isInterface= page.isInterface();
		
		ExprData data = init(page,ep,tlibs,fld,scriptTags,cfml,settings,true);
		data.insideFunction=false; 
		data.tagName=libTag.getFullName();
		data.isCFC=isCFC;
		data.isInterface=isInterface;
		//data.scriptTags=((ConfigImpl) config).getCoreTagLib().getScriptTags();
		
		tag.setBody(statements(data));
	}

	/**
	 * @see railo.transformer.data.cfml.expression.data.cfmlExprTransformer#expression()
	 */
	public final Expression expression(ExprData data) throws TemplateException {
		Expression expr;
		expr = super.expression(data);
		comments(data);
		return expr;
	}
}
