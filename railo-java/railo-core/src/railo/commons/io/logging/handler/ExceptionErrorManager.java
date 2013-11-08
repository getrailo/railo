package railo.commons.io.logging.handler;

import java.util.logging.ErrorManager;

public class ExceptionErrorManager extends ErrorManager {

	@Override
	public synchronized void error(String msg, Exception ex, int code) {
		throw new RuntimeException(ex);
	}

}
