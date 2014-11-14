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
package railo.runtime.cache.tag;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWeb;

public class CacheHandlerFactoryCollection {
	
	public final CacheHandlerFactory query=new CacheHandlerFactory(ConfigImpl.CACHE_DEFAULT_QUERY);
	public final CacheHandlerFactory function=new CacheHandlerFactory(ConfigImpl.CACHE_DEFAULT_FUNCTION);
	public final CacheHandlerFactory include=new CacheHandlerFactory(ConfigImpl.CACHE_DEFAULT_INCLUDE);
	
	private ConfigWeb cw;
	

	public CacheHandlerFactoryCollection(ConfigWeb cw) {
		this.cw=cw;
	}

	public void release(PageContext pc){
		query.rch.clear(pc);
		function.rch.clear(pc);
		include.rch.clear(pc);
	}
}
