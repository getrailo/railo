package coldfusion.server;

import railo.runtime.exp.ApplicationException;

public class ServiceException extends ApplicationException {

	public ServiceException(String message) {
		super(message);
	}
}
