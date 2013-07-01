package railo.loader.osgi.factory;

import java.io.IOException;

public class NameAlreadyExistsException extends IOException {

	public NameAlreadyExistsException(String name) {
		super("a entry with name "+name+" is already assigned to the Zip File");
	}

}
