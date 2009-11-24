package org.opencfml.eventgateway;

import java.io.IOException;

public class GatewayException extends IOException {

	/**
	 * Constructor of the class
	 * @param message
	 */
	public GatewayException(String message){
		super(message);
	}
}
