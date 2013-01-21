package mastercontrol.file;

import java.io.IOException;
import java.io.InputStream;

import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;

public class InputStreamResource extends railo.commons.io.res.util.ReadOnlyResourceSupport{

	private static final long serialVersionUID = 1L;
	private String fileName;
	private long size;
	private InputStream stream;
	private long date;
	public InputStreamResource(String fileName,long size,InputStream stream) {
		this.fileName = fileName;
		this.size = size;
		this.stream = stream;
		this.date = System.currentTimeMillis();
	}
	
	@Override
	public boolean isReadable() {
		return true;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public String getName() {
		return this.fileName;
	}

	@Override
	public String getParent() {
		return null;
	}

	@Override
	public Resource getParentResource() {
		return null;
	}

	@Override
	public Resource getRealResource(String realpath) {
		return null;
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public boolean isAbsolute() {
		return false;
	}

	@Override
	public boolean isDirectory() {
		return false;
	}

	@Override
	public boolean isFile() {
		return true;
	}

	@Override
	public long lastModified() {
		return this.date;
	}

	@Override
	public long length() {
		return size;
	}

	@Override
	public Resource[] listResources() {
		return new Resource[0];
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return stream;
	}

	@Override
	public ResourceProvider getResourceProvider() {
		return null;
	}

}
