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
package railo.transformer.cfml;

import railo.runtime.config.Config;
import railo.transformer.bytecode.Page;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

public abstract class Data {
	
		public final CFMLString cfml;
		public final TransfomerSettings settings; 
		public final TagLib[][] tlibs;
		public final FunctionLib[] flibs;
		public final Config config;
		public final Page page;
		public final TagLibTag[] scriptTags;
		public final EvaluatorPool ep;
		
		
	    public Data(Page page,CFMLString cfml,EvaluatorPool ep,TransfomerSettings settings,TagLib[][] tlibs,FunctionLib[] flibs,TagLibTag[] scriptTags) {
	    	this.config = page.getPageSource().getMapping().getConfig();
	    	this.page = page;
	    	this.cfml = cfml;
	    	this.settings = settings;
	    	this.tlibs = tlibs;
	    	this.flibs = flibs;
			this.scriptTags = scriptTags;
			this.ep = ep;
		}
	}