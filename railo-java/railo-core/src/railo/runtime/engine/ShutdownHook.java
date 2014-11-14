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
package railo.runtime.engine;

import railo.runtime.config.ConfigServer;

public class ShutdownHook extends Thread {
	
	private ConfigServer cs;

	public ShutdownHook(ConfigServer cs) {
		this.cs=cs;
	}
	
	public void run() {
		
		// TODO Server.cfc->onServerEnd
		
		// try to update jars, doing this here because on windows the files could be locked
		/*try {
			JarLoader.download(cs, Admin.UPDATE_JARS);
		}
		catch (Throwable t) {
			SystemOut.printDate(cs.getErrWriter(),ExceptionUtil.getStacktrace(t, true));
		}*/
		
		
	}
}
