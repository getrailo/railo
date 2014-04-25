package railo.commons.activation;

// Imports
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;

/**
 * File Data Source.
 */
public final class ResourceDataSource implements DataSource {

	/**
	 * File source.
	 */
	private	final Resource _file;

	/**
	 * Constructor of the class
	 * @param res source
	 */
	public ResourceDataSource(Resource res) {
		_file = res;
	}

	/**
	 * Get name.
	 * @returns Name
	 */
	@Override
	public String getName() {
		return _file.getName();
	} 

	/**
	 * Get Resource.
	 * @returns Resource
	 */
	public Resource getResource() {
		return _file;
	}

	/**
	 * Get input stream.
	 * @returns Input stream
	 * @throws IOException IO exception occurred
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return IOUtil.toBufferedInputStream(_file.getInputStream());
	} 

	/**
	 * Get content type.
	 * @returns Content type
	 */
	@Override
	public String getContentType() {
		InputStream is=null;
		try {
			return IOUtil.getMimeType(is=_file.getInputStream(), "application/unknow");
		} catch (IOException e) {
			return "application/unknow";
		}
		finally {
			IOUtil.closeEL(is);
		}
		
	}

	/**
	 * Get output stream.
	 * @returns Output stream
	 * @throws IOException IO exception occurred
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		if (!_file.isWriteable()) {
			throw new IOException("Cannot write");
		}
		return IOUtil.toBufferedOutputStream(_file.getOutputStream());
	}
}
