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
package railo.transformer.cfml.expression;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.cfml.ExprTransformer;
import railo.transformer.cfml.TransfomerSettings;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.cfml.script.AbstrCFMLScriptTransformer;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

public class CFMLExprTransformer extends AbstrCFMLScriptTransformer implements ExprTransformer {

	@Override
	public Expression transformAsString(Page page,EvaluatorPool ep,TagLib[][] tld, FunctionLib[] fld,TagLibTag[] scriptTags, CFMLString cfml, TransfomerSettings settings, boolean allowLowerThan) throws TemplateException {
		return transformAsString(init(page,ep,tld,fld,scriptTags, cfml,settings,allowLowerThan),new String[]{" ", ">", "/>"});
	}
	
	
	@Override
	public Expression transform(Page page,EvaluatorPool ep,TagLib[][] tld, FunctionLib[] fld,TagLibTag[] scriptTags, CFMLString cfml, TransfomerSettings settings) throws TemplateException {
		ExprData data = init(page,ep,tld,fld,scriptTags, cfml,settings,false);
		comments(data);
		return assignOp(data);
	}
	
}
