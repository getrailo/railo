package railo.commons.io.res.type.ftp;

import java.io.IOException;
import java.io.OutputStream;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceOutputStream;

public final class FTPResourceOutputStream extends ResourceOutputStream {
	

	private final FTPResourceClient client;

	/**
	 * Constructor of the class
	 * @param client
	 * @param res
	 * @param os
	 * @throws IOException 
	 */
	public FTPResourceOutputStream(FTPResourceClient client,Resource res, OutputStream os) {
		super(res, os);
		this.client=client;
	}
	
	@Override
	public void close() throws IOException {
		try {
			super.close();
		}
		finally {
			client.completePendingCommand();
			((FTPResourceProvider)getResource().getResourceProvider()).returnClient(client);
		}
	}
}
