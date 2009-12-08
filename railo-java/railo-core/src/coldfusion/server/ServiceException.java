package coldfusion.server;

import railo.runtime.exp.ApplicationException;

 class ServiceException extends ApplicationException {

	public ServiceException(String message) {
		super(message);
	}
}
