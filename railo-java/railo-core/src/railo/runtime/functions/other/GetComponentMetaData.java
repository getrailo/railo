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
 * Implements the CFML Function getmetadata
 */
package railo.runtime.functions.other;

import java.util.HashMap;

import railo.runtime.Component;
import railo.runtime.InterfaceImpl;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.component.ComponentLoader;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;

public final class GetComponentMetaData implements Function {

	public static Struct call(PageContext pc , Object obj) throws PageException {
		if(obj instanceof Component){
			return ((Component)obj).getMetaData(pc);
		}
		// load existing meta without loading the cfc
		/*try{
			Page page = ComponentLoader.loadPage(pc,((PageContextImpl)pc).getCurrentPageSource(null), Caster.toString(obj), null,null);
			if(page.metaData!=null && page.metaData.get()!=null) return page.metaData.get();
		}catch(Throwable t){}*/

		// load the cfc when metadata was not defined before
		try{
			Component cfc = CreateObject.doComponent(pc, Caster.toString(obj));
			return cfc.getMetaData(pc); 
		}
		// TODO better solution
		catch(ApplicationException ae){
			try{
				InterfaceImpl inter = ComponentLoader.loadInterface(pc,((PageContextImpl)pc).getCurrentPageSource(null), Caster.toString(obj), new HashMap());
				return inter.getMetaData(pc);
			}
			catch(PageException pe){
				throw ae;
			}
		}
	}
}