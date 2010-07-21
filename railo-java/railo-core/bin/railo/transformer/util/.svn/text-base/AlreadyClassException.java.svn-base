package railo.transformer.util;

import java.io.IOException;
import java.io.InputStream;

import railo.commons.io.res.Resource;

public class AlreadyClassException extends IOException {

	private Resource res;

	public AlreadyClassException(Resource resource) {
		this.res = resource;
	}

	public InputStream getInputStream() throws IOException {
		return res.getInputStream();
	}

}
