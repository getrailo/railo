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
package railo.runtime.cache;

import railo.commons.io.cache.CacheEntry;
import railo.commons.io.cache.CacheEventListener;
import railo.runtime.Component;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;

public class ComponentCacheEventListener implements CacheEventListener {

	private static final long serialVersionUID = 6271280246677734153L;
	private static final Collection.Key ON_EXPIRES = KeyImpl.intern("onExpires");
	private static final Collection.Key ON_PUT = KeyImpl.intern("onPut");
	private static final Collection.Key ON_REMOVE = KeyImpl.intern("onRemove");
	private Component cfc;

	public ComponentCacheEventListener(Component cfc) {
		this.cfc=cfc;
	}
	
	@Override
	public void onRemove(CacheEntry entry) {
		call(ON_REMOVE,entry);
	}

	@Override
	public void onPut(CacheEntry entry) {
		call(ON_PUT,entry);
	}

	@Override
	public void onExpires(CacheEntry entry) {
		call(ON_EXPIRES,entry);
	}

	private void call(Key methodName, CacheEntry entry) {
		//Struct data = entry.getCustomInfo();
		//cfc.callWithNamedValues(pc, methodName, data);
	}

	@Override
	public CacheEventListener duplicate() {
		return new ComponentCacheEventListener((Component)cfc.duplicate(false));
	}

}
