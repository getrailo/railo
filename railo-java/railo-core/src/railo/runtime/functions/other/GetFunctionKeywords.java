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
package railo.runtime.functions.other;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.functions.arrays.ArraySort;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.util.ArrayUtil;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.tag.TagLib;

public class GetFunctionKeywords {
	private static Array keywords;
	

	public synchronized static Array call(PageContext pc) throws PageException {

		if(keywords==null) {
			Set<String> set=new HashSet<String>();
			FunctionLib[] flds;
			flds = ((ConfigImpl)pc.getConfig()).getFLDs();
			Map<String, FunctionLibFunction> functions;
			Iterator<FunctionLibFunction> it;
			FunctionLibFunction flf;
			String[] arr;
			for(int i=0;i<flds.length;i++) {
				functions = flds[i].getFunctions();
				it = functions.values().iterator();
				
				while(it.hasNext()){
					flf = it.next();
					if(flf.getStatus()!=TagLib.STATUS_HIDDEN && flf.getStatus()!=TagLib.STATUS_UNIMPLEMENTED && !ArrayUtil.isEmpty(flf.getKeywords())){ 
						arr = flf.getKeywords();
						if(arr!=null)for(int y=0;y<arr.length;y++) {
							set.add(arr[y].toLowerCase());
						}
						
					}
				}
			}
			keywords=Caster.toArray(set);
			ArraySort.call(pc, keywords, "textnocase");
			//}
		}
		return keywords;
	}

}
