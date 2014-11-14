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
/**
 * Implements the CFML Function getfunctionlist
 */
package railo.runtime.functions.other;

import java.util.Iterator;
import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;

public final class GetTagList implements Function {
	
	private static Struct sct;
	

	public synchronized static railo.runtime.type.Struct call(PageContext pc) throws PageException {
		if(sct==null) {
		    sct=new StructImpl();
			//synchronized(sct) {
				//hasSet=true;
				TagLib[] tlds;
				TagLibTag tag;
				tlds = ((ConfigImpl)pc.getConfig()).getTLDs();
				
				for(int i=0;i<tlds.length;i++) {
				    String ns = tlds[i].getNameSpaceAndSeparator();
				    
				    
					Map tags = tlds[i].getTags();
					Iterator it = tags.keySet().iterator();
					Struct inner=new StructImpl();
                    sct.set(ns,inner);
					while(it.hasNext()){
						Object n=it.next();
						tag = tlds[i].getTag(n.toString());
						if(tag.getStatus()!=TagLib.STATUS_HIDDEN && tag.getStatus()!=TagLib.STATUS_UNIMPLEMENTED)
							inner.set(n.toString(),"");
					}
					
				}
			//}
		}
		return sct;
	}
}