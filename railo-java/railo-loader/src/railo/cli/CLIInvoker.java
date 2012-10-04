package railo.cli;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface CLIInvoker extends Remote {

	void invoke(Map<String, String> config) throws RemoteException;

}
