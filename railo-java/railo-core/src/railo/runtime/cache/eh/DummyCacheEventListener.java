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
package railo.runtime.cache.eh;

import java.io.Serializable;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import railo.aprint;

public class DummyCacheEventListener implements CacheEventListener, Serializable {

	private static final long serialVersionUID = 5194911259476386528L;



	public void notifyElementExpired(Ehcache cache, Element el) {
		aprint.o("expired:"+el.getKey());
	}

	public void notifyElementRemoved(Ehcache cache, Element el)throws CacheException {

		aprint.o("removed:"+el.getKey());
	}
	

	public void dispose() {
		// TODO Auto-generated method stub

		aprint.o("dispose:");
	}

	public void notifyElementEvicted(Ehcache arg0, Element el) {
		// TODO Auto-generated method stub

		aprint.o("Evicted:"+el.getKey());
	}

	public void notifyElementPut(Ehcache arg0, Element el)
			throws CacheException {
		// TODO Auto-generated method stub
		aprint.o("put:"+el.getKey());
		
	}

	public void notifyElementUpdated(Ehcache arg0, Element el)
			throws CacheException {
		// TODO Auto-generated method stub
		aprint.o("updated:"+el.getKey());
		
	}

	public void notifyRemoveAll(Ehcache arg0) {
		// TODO Auto-generated method stub
		aprint.o("removeAll:");
		
	}
	


	@Override
	public Object clone(){
		return new DummyCacheEventListener(); 
	}

}
