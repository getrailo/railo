package railo.runtime.exp;

import railo.runtime.config.RemoteClient;

public class RemoteAccessException extends ApplicationException {

	public RemoteAccessException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public RemoteAccessException(RemoteClient client, PageException pe) {
		super(createMessage(client,pe));
	}

	private static String createMessage(RemoteClient client, PageException pe) {
		
		
		return "Recieved the exception [" + pe.getMessage() + "] while accessing the remote client ["+client.getUrl()+"]";
	}

}
