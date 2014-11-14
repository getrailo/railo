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
package railo.cli;

import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Closer extends Thread {

	private String name;
	private Registry reg;
	private long idleTime;
	private CLIInvokerImpl invoker;

	public Closer(Registry reg, CLIInvokerImpl invoker, String name, long idleTime) {
		this.reg=reg;
		this.name=name;
		this.idleTime=idleTime;
		this.invoker=invoker;
	}

	public void run() {
		// idle
		do{
			sleepEL(idleTime);
		}
		while(invoker.lastAccess()+idleTime>System.currentTimeMillis());
		
		
		try {
			reg.unbind(name);
			UnicastRemoteObject.unexportObject(invoker,true);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
	}

	private void sleepEL(long millis) {
		try {
			sleep(millis);
		} catch (Throwable t) {t.printStackTrace();}
	}

}
