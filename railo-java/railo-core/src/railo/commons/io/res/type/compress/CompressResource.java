package railo.commons.io.res.type.compress;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.util.ResourceSupport;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;

public final class CompressResource extends ResourceSupport {

	private final CompressResourceProvider provider;
	private final Compress zip;
	private final String path;
	private final String name;
	private final String parent;
	private final boolean caseSensitive; 

	/**
	 * Constructor of the class
	 * @param provider
	 * @param zip
	 * @param path
	 * @param caseSensitive
	 */
	CompressResource(CompressResourceProvider provider, Compress zip, String path, boolean caseSensitive) {
		if(StringUtil.isEmpty(path)) path="/";
		this.provider=provider;
		this.zip=zip; 
		this.path=path;
		String[] tmp = ResourceUtil.translatePathName(path);
		this.name=tmp[1];
		this.parent=tmp[0];
		this.caseSensitive=caseSensitive;
	}

	/**
	 * @return return ram resource that contain the data
	 */
	private Resource getRamResource() {
		return zip.getRamProviderResource(path);
	}

	/**
	 * @see railo.commons.io.res.Resource#exists()
	 */
	public boolean exists() {
		try {
			provider.read(this);
		} 
		catch (IOException e) {
			return false;
		}
		return getRamResource().exists();
	}

	/**
	 * @see railo.commons.io.res.Resource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		ResourceUtil.checkGetInputStreamOK(this);
		return getRamResource().getInputStream();
	}

	/**
	 * @see railo.commons.io.res.Resource#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see railo.commons.io.res.Resource#getParent()
	 */
	public String getParent() {
		if(StringUtil.isEmpty(parent))return null;
		return provider.getScheme().concat("://").concat(zip.getCompressFile().getPath()).concat("!").concat(parent);
	}

	/**
	 * @see railo.commons.io.res.Resource#getParentResource()
	 */
	public Resource getParentResource() {
		if(StringUtil.isEmpty(parent))return null;
		return new CompressResource(provider,zip,parent,caseSensitive);
	}

	/**
	 * @see railo.commons.io.res.Resource#getPath()
	 */
	public String getPath() {
		return provider.getScheme().concat("://").concat(zip.getCompressFile().getPath()).concat("!").concat(path);
	}

	/**
	 * @see railo.commons.io.res.Resource#getRealResource(java.lang.String)
	 */
	public Resource getRealResource(String realpath) {
		realpath=ResourceUtil.merge(path, realpath);
		if(realpath.startsWith("../"))return null;
		return new CompressResource(provider,zip,realpath,caseSensitive);
	}

	/**
	 * @see railo.commons.io.res.Resource#getResourceProvider()
	 */
	public ResourceProvider getResourceProvider() {
		return provider;
	}

	/**
	 * @see railo.commons.io.res.Resource#isAbsolute()
	 */
	public boolean isAbsolute() {
		return getRamResource().isAbsolute();
	}

	/**
	 * @see railo.commons.io.res.Resource#isDirectory()
	 */
	public boolean isDirectory() {
		return getRamResource().isDirectory();
	}

	/**
	 * @see railo.commons.io.res.Resource#isFile()
	 */
	public boolean isFile() {
		return getRamResource().isFile();
	}

	/**
	 * @see railo.commons.io.res.Resource#isReadable()
	 */
	public boolean isReadable() {
		return getRamResource().isReadable();
	}

	/**
	 * @see railo.commons.io.res.Resource#isWriteable()
	 */
	public boolean isWriteable() {
		return getRamResource().isWriteable();
	}

	/**
	 * @see railo.commons.io.res.Resource#lastModified()
	 */
	public long lastModified() {
		return getRamResource().lastModified();
	}

	/**
	 * @see railo.commons.io.res.Resource#length()
	 */
	public long length() {
		return getRamResource().length();
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#listResources()
	 */
	public Resource[] listResources() {
		String[] names = list();
		if(names==null) return null;
		Resource[] children = new Resource[names.length];
		for(int i=0;i<children.length;i++) {
			children[i]=new CompressResource(provider,zip,path.concat("/").concat(names[i]),caseSensitive);
		}
		return children;
	}
	
	/**
	 * @see railo.commons.io.res.util.ResourceSupport#list()
	 */
	public String[] list() {
		return getRamResource().list();
	}

	/**
	 * @see railo.commons.io.res.Resource#remove(boolean)
	 */
	public void remove(boolean force) throws IOException {
		Resource rr = getRamResource();
		if(rr.getParent()==null) 
			throw new IOException("can't remove root resource ["+getPath()+"]");
		
		if(!rr.exists())
			throw new IOException("can't remove resource ["+getPath()+"],resource does not exists");
		
		Resource[] children = listResources();
		if(children!=null && children.length>0) {
			if(!force) {
				throw new IOException("can't delete directory ["+getPath()+"], directory is not empty");
			}
			for(int i=0;i<children.length;i++) {
				children[i].remove(true);
			}
		}
		rr.remove(force);
	}

	/**
	 * @see railo.commons.io.res.Resource#setLastModified(long)
	 */
	public boolean setLastModified(long time) {
		boolean lm = getRamResource().setLastModified(time);
		zip.synchronize(provider.async);
		return lm;
	}
	
	/**
	 * @see railo.commons.io.res.Resource#createDirectory(boolean)
	 */
	public void createDirectory(boolean createParentWhenNotExists) throws IOException {
		ResourceUtil.checkCreateDirectoryOK(this,createParentWhenNotExists);
		getRamResource().createDirectory(createParentWhenNotExists);
		zip.synchronize(provider.async);
	}

	/**
	 * @see railo.commons.io.res.Resource#createFile(boolean)
	 */
	public void createFile(boolean createParentWhenNotExists) throws IOException {
		ResourceUtil.checkCreateFileOK(this,createParentWhenNotExists);
		getRamResource().createFile(createParentWhenNotExists);
		zip.synchronize(provider.async);
	}

	/**
	 * @see railo.commons.io.res.Resource#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		ResourceUtil.checkGetOutputStreamOK(this);
		//Resource res = getRamResource();
		//Resource p = res.getParentResource();
		//if(p!=null && !p.exists())p.mkdirs();
		return new CompressOutputStreamSynchronizer(getRamResource().getOutputStream(),zip,provider.async);
	}

	/**
	 * @see railo.commons.io.res.Resource#getOutputStream(boolean)
	 */
	public OutputStream getOutputStream(boolean append) throws IOException {
		return new CompressOutputStreamSynchronizer(getRamResource().getOutputStream(append),zip,provider.async);
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#getMode()
	 */
	public int getMode() {
		return getRamResource().getMode();
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#setMode(int)
	 */
	public void setMode(int mode) throws IOException {
		getRamResource().setMode(mode);
		zip.synchronize(provider.async);
		
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#setReadable(boolean)
	 */
	public boolean setReadable(boolean value) {
		if(!isFile())return false;
		getRamResource().setReadable(value);
		zip.synchronize(provider.async);
		return true;
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#setWritable(boolean)
	 */
	public boolean setWritable(boolean value) {
		if(!isFile())return false;
		getRamResource().setWritable(value);
		zip.synchronize(provider.async);
		return true;
	}

}
