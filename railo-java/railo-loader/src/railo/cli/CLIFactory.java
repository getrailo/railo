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

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

import javax.servlet.ServletException;


//import railo.cli.servlet.ServletConfigImpl;
//import railo.cli.servlet.ServletContextImpl;

public class CLIFactory extends Thread {
	
	private static final int PORT=8893;
	private File root;
	private String servletName;
	private Map<String, String> config;
	private long idleTime;
	
	public CLIFactory(File root, String servletName, Map<String, String> config) {
		this.root=root;
		this.servletName=servletName;
		this.config=config;
		
		this.idleTime=60000;
		String strIdle = config.get("idle");
		if(strIdle!=null) {
			try{
				idleTime=Long.parseLong(strIdle);
			}
			catch(Throwable t){}
		}
	}
	
	@Override
	public void run() {
		
		String name=root.getAbsolutePath();
		InetAddress current=null;
		try {
			current = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			return;
		}
		try {
			try {
				// first try to call existing service
				invoke(current,name);
				
			} 
			catch (ConnectException e) {
				startInvoker(name);
				invoke(current,name);
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private void invoke(InetAddress current, String name) throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(current.getHostAddress(),PORT);
		CLIInvoker invoker = (CLIInvoker) registry.lookup( name );
		invoker.invoke(config);
	}

	private void startInvoker(String name) throws ServletException, RemoteException {
		Registry myReg = getRegistry(PORT);
		CLIInvokerImpl invoker=new CLIInvokerImpl(root, servletName);
		CLIInvoker stub = (CLIInvoker) UnicastRemoteObject.exportObject( invoker, 0 );
		myReg.rebind( name, stub );
		if(idleTime>0){
			Closer closer = new Closer(myReg,invoker,name,idleTime);
			closer.setDaemon(false);
			closer.start();
		}
	}

	public static Registry getRegistry(int port) {
		Registry registry=null;
		try {
	    	
			 registry= LocateRegistry.createRegistry( port );
		} 
		catch (RemoteException e) {}
		
		try {
	    	
			 if(registry==null)registry= LocateRegistry.getRegistry(port);
		}
		catch (RemoteException e) {
		}
		
		
		RemoteServer.setLog(System.out );
		
		return registry;
	}
}
