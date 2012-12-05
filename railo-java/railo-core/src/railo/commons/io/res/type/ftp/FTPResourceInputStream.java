package railo.commons.io.res.type.ftp;

import java.io.IOException;
import java.io.InputStream;

import railo.commons.io.res.util.ResourceInputStream;

public final class FTPResourceInputStream extends ResourceInputStream {

	private final FTPResourceClient client;

	/**
	 * Constructor of the class
	 * @param res
	 * @param is
	 */
	public FTPResourceInputStream(FTPResourceClient client,FTPResource res, InputStream is) {
		super(res, is);
		//print.ln("is:"+is);
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
