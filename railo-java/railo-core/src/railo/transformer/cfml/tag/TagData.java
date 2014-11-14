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
package railo.transformer.cfml.tag;

import railo.transformer.bytecode.Page;
import railo.transformer.cfml.Data;
import railo.transformer.cfml.TransfomerSettings;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.cfml.expression.SimpleExprTransformer;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

public class TagData extends Data {
	
	private SimpleExprTransformer set;
	
	public TagData(TagLib[][] tlibs, FunctionLib[] flibs,TagLibTag[] scriptTags, CFMLString cfml,TransfomerSettings settings,Page page) {
		super(page,cfml,new EvaluatorPool(),settings,tlibs,flibs,scriptTags);
	}
	public TagData(TagLib[][] tlibs, FunctionLib[] flibs,TagLibTag[] scriptTags, CFMLString cfml,Boolean dotNotationUpperCase,Page page) {
		super(page,cfml,new EvaluatorPool(),TransfomerSettings.toSetting(page.getPageSource().getMapping(),dotNotationUpperCase),tlibs,flibs,scriptTags);
	}
	
	public SimpleExprTransformer getSimpleExprTransformer() {
		return set;
	}

	public void setSimpleExprTransformer(SimpleExprTransformer set) {
		this.set = set;
	}
}