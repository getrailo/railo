package railo.runtime.gateway;

import java.io.IOException;

public class GatewayException extends IOException {
	
	private static final long serialVersionUID = -4271501962148246058L;

	/**
	 * Constructor of the class
	 * @param message
	 */
	public GatewayException(String message){
		super(message);
	}
}
