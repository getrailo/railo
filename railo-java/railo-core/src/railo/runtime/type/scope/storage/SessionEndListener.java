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

import java.io.Serializable;

import railo.runtime.CFMLFactoryImpl;
import railo.runtime.exp.ExceptionHandler;
import railo.runtime.listener.ApplicationListener;
import railo.runtime.op.Caster;

public class SessionEndListener implements StorageScopeListener,Serializable {

	private static final long serialVersionUID = -3868545140988347285L;

	@Override
	public void doEnd(StorageScopeEngine engine,StorageScopeCleaner cleaner,String appName, String cfid) {
		CFMLFactoryImpl factory = engine.getFactory();
		ApplicationListener listener = factory.getConfig().getApplicationListener();
		try {
			cleaner.info("call onSessionEnd for "+appName+"/"+cfid);
			listener.onSessionEnd(factory, appName, cfid);
		} 
		catch (Throwable t) {
			ExceptionHandler.log(factory.getConfig(),Caster.toPageException(t));
		}
	}

}
