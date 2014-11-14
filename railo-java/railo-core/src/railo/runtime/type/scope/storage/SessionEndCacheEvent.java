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
package railo.runtime.type.scope.storage;

import railo.commons.io.cache.CacheEntry;
import railo.commons.io.cache.CacheEventListener;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExceptionHandler;
import railo.runtime.listener.ApplicationListener;
import railo.runtime.op.Caster;

public class SessionEndCacheEvent implements CacheEventListener {

	public void onExpires(CacheEntry entry) {
		//return new StringBuilder(cache instanceof CacheEvent?"":"railo-storage:").append(type).append(":").append(cfid).append(":").append(appName).toString();
		
		String key=entry.getKey();
		
		// type
		int index=key.indexOf(':'),last;
		//String type=key.substring(0,index);
		
		// cfid
		last=index+1;
		index=key.indexOf(':',last);
		String cfid=key.substring(last,index);
		
		// appName
		last=index+1;
		index=key.indexOf(':',last);
		String appName=key.substring(last);
				
		Config config = ThreadLocalPageContext.getConfig();
		
		_doEnd((CFMLFactoryImpl) config.getFactory(), appName, cfid);
	}
	
	private void _doEnd(CFMLFactoryImpl factory,String appName, String cfid) {
		ApplicationListener listener = factory.getConfig().getApplicationListener();
		try {
			factory.getScopeContext().info("call onSessionEnd for "+appName+"/"+cfid);
			listener.onSessionEnd(factory, appName, cfid);
		} 
		catch (Throwable t) {
			ExceptionHandler.log(factory.getConfig(),Caster.toPageException(t));
		}
	}
	
	@Override
	public void onRemove(CacheEntry entry) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPut(CacheEntry entry) {
		// TODO Auto-generated method stub

	}


	@Override
	public CacheEventListener duplicate() {
		// TODO Auto-generated method stub
		return null;
	}

}
