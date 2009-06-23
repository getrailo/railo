package railo.commons.io.res.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.filter.ResourceFilter;
import railo.commons.io.res.filter.ResourceNameFilter;

public final class FileWrapper extends File implements Resource {

	private final Resource res;
	
	/**
	 * Constructor of the class
	 * @param res
	 */
	private FileWrapper(Resource res) {
		super(res.getPath());
		this.res=res;
	}
	
	/**
	 *
	 * @see java.io.File#canRead()
	 */
	public boolean canRead() {
		return res.canRead();
	}


	/**
	 *
	 * @see java.io.File#canWrite()
	 */
	public boolean canWrite() {
		return res.canWrite();
	}


	/**
	 *
	 * @see java.io.File#compareTo(java.io.File)
	 */
	public int compareTo(File pathname) {
		if(res instanceof File) ((File)res).compareTo(pathname);
		return res.getPath().compareTo(pathname.getPath());
	}


	/**
	 *
	 * @see java.io.File#compareTo(java.lang.Object)
	 */
//	public int compareTo(Object o) {
//		if(o instanceof File) return compareTo((File)o);
//		//if(res instanceof File) ((File)res).compareTo(o);
//		return res.getPath().compareTo(o.toString());
//	}


	/**
	 *
	 * @see java.io.File#createNewFile()
	 */
	public boolean createNewFile() {
		return res.createNewFile();
	}


	/**
	 *
	 * @see java.io.File#delete()
	 */
	public boolean delete() {
		return res.delete();
	}


	/**
	 *
	 * @see java.io.File#deleteOnExit()
	 */
	public void deleteOnExit() {
		if(res instanceof File) ((File)res).deleteOnExit();
	}


	/**
	 *
	 * @see java.io.File#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return res.equals(obj);
	}


	/**
	 *
	 * @see java.io.File#exists()
	 */
	public boolean exists() {
		return res.exists();
	}


	/**
	 *
	 * @see java.io.File#getAbsoluteFile()
	 */
	public File getAbsoluteFile() {
		if(res.isAbsolute()) return this;
		return new FileWrapper(res.getAbsoluteResource());
	}


	/**
	 *
	 * @see java.io.File#getAbsolutePath()
	 */
	public String getAbsolutePath() {
		return res.getAbsolutePath();
	}


	/**
	 *
	 * @see java.io.File#getCanonicalFile()
	 */
	public File getCanonicalFile() throws IOException {
		return new FileWrapper(res.getCanonicalResource());
	}


	/**
	 *
	 * @see java.io.File#getCanonicalPath()
	 */
	public String getCanonicalPath() throws IOException {
		return res.getCanonicalPath();
	}


	/**
	 *
	 * @see java.io.File#getName()
	 */
	public String getName() {
		return res.getName();
	}


	/**
	 *
	 * @see java.io.File#getParent()
	 */
	public String getParent() {
		return res.getParent();
	}


	/**
	 *
	 * @see java.io.File#getParentFile()
	 */
	public File getParentFile() {
		return new FileWrapper(this.getParentResource());
	}


	/**
	 *
	 * @see java.io.File#getPath()
	 */
	public String getPath() {
		return res.getPath();
	}


	/**
	 *
	 * @see java.io.File#hashCode()
	 */
	public int hashCode() {
		return res.hashCode();
	}


	/**
	 *
	 * @see java.io.File#isAbsolute()
	 */
	public boolean isAbsolute() {
		return res.isAbsolute();
	}


	/**
	 *
	 * @see java.io.File#isDirectory()
	 */
	public boolean isDirectory() {
		return res.isDirectory();
	}


	/**
	 *
	 * @see java.io.File#isFile()
	 */
	public boolean isFile() {
		return res.isFile();
	}


	/**
	 *
	 * @see java.io.File#isHidden()
	 */
	public boolean isHidden() {
		return res.isHidden();
	}


	/**
	 *
	 * @see java.io.File#lastModified()
	 */
	public long lastModified() {
		return res.lastModified();
	}


	/**
	 *
	 * @see java.io.File#length()
	 */
	public long length() {
		return res.length();
	}


	/**
	 *
	 * @see java.io.File#list()
	 */
	public String[] list() {
		return res.list();
	}


	/**
	 *
	 * @see java.io.File#list(java.io.FilenameFilter)
	 */
	public String[] list(FilenameFilter filter) {
		if(res instanceof File) ((File)res).list(filter);
		return list((ResourceNameFilter)new FileNameFilterWrapper(filter));
	}


	/**
	 *
	 * @see java.io.File#listFiles()
	 */
	public File[] listFiles() {
		//if(res instanceof File) return ((File)res).listFiles();
		return toFiles(listResources());
	}
	
	private File[] toFiles(Resource[] resources) {
		File[] files = new File[resources.length];
		for(int i=0;i<resources.length;i++) {
			files[i]=new FileWrapper(resources[i]);
		}
		return files;
	}


	/**
	 *
	 * @see java.io.File#listFiles(java.io.FileFilter)
	 */
	public File[] listFiles(FileFilter filter) {
		//if(res instanceof File) return ((File)res).listFiles(filter);
		return toFiles(listResources(new FileFilterWrapper(filter)));
	}


	/**
	 *
	 * @see java.io.File#listFiles(java.io.FilenameFilter)
	 */
	public File[] listFiles(FilenameFilter filter) {
		//if(res instanceof File) return ((File)res).listFiles(filter);
		return toFiles(listResources(new FileNameFilterWrapper(filter)));
	}


	/**
	 *
	 * @see java.io.File#mkdir()
	 */
	public boolean mkdir() {
		return res.mkdir();
	}


	/**
	 *
	 * @see java.io.File#mkdirs()
	 */
	public boolean mkdirs() {
		return res.mkdirs();
	}


	/**
	 *
	 * @throws IOException 
	 * @see java.io.File#renameTo(java.io.File)
	 */
	public boolean renameTo(File dest) {
		try {
			if(res instanceof File) return ((File)res).renameTo(dest);
			if(dest instanceof Resource) return res.renameTo((Resource)dest);
			ResourceUtil.moveTo(this, ResourceUtil.toResource(dest));
			return true;
		}
		catch(IOException ioe) {
			return false;
		}
	}


	/**
	 *
	 * @see java.io.File#setLastModified(long)
	 */
	public boolean setLastModified(long time) {
		return res.setLastModified(time);
	}


	/**
	 *
	 * @see java.io.File#setReadOnly()
	 */
	public boolean setReadOnly() {
		return res.setReadOnly();
	}


	/**
	 *
	 * @see java.io.File#toString()
	 */
	public String toString() {
		return res.toString();
	}


	/**
	 *
	 * @see java.io.File#toURI()
	 */
	public URI toURI() {
		if(res instanceof File) return ((File)res).toURI();
		return null;
	}


	/**
	 *
	 * @see java.io.File#toURL()
	 */
	public URL toURL() throws MalformedURLException {
		if(res instanceof File) return ((File)res).toURL();
		return null;
	}

	/**
	 * @see railo.commons.io.res.Resource#createDirectory(boolean)
	 */
	public void createDirectory(boolean createParentWhenNotExists) throws IOException {
		res.createDirectory(createParentWhenNotExists);
	}

	/**
	 * @see railo.commons.io.res.Resource#createFile(boolean)
	 */
	public void createFile(boolean createParentWhenNotExists) throws IOException {
		res.createFile(createParentWhenNotExists);
	}

	/**
	 * @see railo.commons.io.res.Resource#getAbsoluteResource()
	 */
	public Resource getAbsoluteResource() {
		return res.getAbsoluteResource();
	}

	/**
	 * @see railo.commons.io.res.Resource#getCanonicalResource()
	 */
	public Resource getCanonicalResource() throws IOException {
		return res.getCanonicalResource();
	}

	/**
	 * @see railo.commons.io.res.Resource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return res.getInputStream();
	}

	/**
	 * @see railo.commons.io.res.Resource#getMode()
	 */
	public int getMode() {
		return res.getMode();
	}

	/**
	 * @see railo.commons.io.res.Resource#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		return res.getOutputStream();
	}

	/**
	 * @see railo.commons.io.res.Resource#getOutputStream(boolean)
	 */
	public OutputStream getOutputStream(boolean append) throws IOException {
		return res.getOutputStream(append);
	}

	/**
	 * @see railo.commons.io.res.Resource#getParentResource()
	 */
	public Resource getParentResource() {
		return res.getParentResource();
	}

	/**
	 * @see railo.commons.io.res.Resource#getReal(java.lang.String)
	 */
	public String getReal(String realpath) {
		return res.getReal(realpath);
	}

	/**
	 * @see railo.commons.io.res.Resource#getRealResource(java.lang.String)
	 */
	public Resource getRealResource(String realpath) {
		return res.getRealResource(realpath);
	}

	/**
	 * @see railo.commons.io.res.Resource#getResourceProvider()
	 */
	public ResourceProvider getResourceProvider() {
		return res.getResourceProvider();
	}

	/**
	 * @see railo.commons.io.res.Resource#isArchive()
	 */
	public boolean isArchive() {
		return res.isArchive();
	}

	/**
	 * @see railo.commons.io.res.Resource#isReadable()
	 */
	public boolean isReadable() {
		return res.isReadable();
	}

	/**
	 * @see railo.commons.io.res.Resource#isSystem()
	 */
	public boolean isSystem() {
		return res.isSystem();
	}

	/**
	 * @see railo.commons.io.res.Resource#isWriteable()
	 */
	public boolean isWriteable() {
		return res.isWriteable();
	}

	/**
	 * @see railo.commons.io.res.Resource#list(railo.commons.io.res.filter.ResourceNameFilter)
	 */
	public String[] list(ResourceNameFilter filter) {
		return res.list(filter);
	}

	/**
	 * @see railo.commons.io.res.Resource#list(railo.commons.io.res.filter.ResourceFilter)
	 */
	public String[] list(ResourceFilter filter) {
		return res.list(filter);
	}

	/**
	 * @see railo.commons.io.res.Resource#listResources()
	 */
	public Resource[] listResources() {
		return res.listResources();
	}

	/**
	 * @see railo.commons.io.res.Resource#listResources(railo.commons.io.res.filter.ResourceFilter)
	 */
	public Resource[] listResources(ResourceFilter filter) {
		return res.listResources(filter);
	}

	/**
	 * @see railo.commons.io.res.Resource#listResources(railo.commons.io.res.filter.ResourceNameFilter)
	 */
	public Resource[] listResources(ResourceNameFilter filter) {
		return res.listResources(filter);
	}

	/**
	 * @see railo.commons.io.res.Resource#moveTo(railo.commons.io.res.Resource)
	 */
	public void moveTo(Resource dest) throws IOException {
		res.moveTo(dest);
	}

	/**
	 * @see railo.commons.io.res.Resource#remove(boolean)
	 */
	public void remove(boolean force) throws IOException {
		res.remove(force);
	}

	/**
	 * @see railo.commons.io.res.Resource#renameTo(railo.commons.io.res.Resource)
	 */
	public boolean renameTo(Resource dest) {
		return res.renameTo(dest);
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#setMode(int)
	 */
	public void setMode(int mode) throws IOException {
		res.setMode(mode);
	}


	/**
	 * @param res
	 * @return
	 */
	public static File toFile(Resource res) {
		if(res instanceof File) return (File)res;
		return new FileWrapper(res);
	}


	/**
	 *
	 * @see railo.commons.io.res.Resource#setArchive(boolean)
	 */
	public void setArchive(boolean value) throws IOException {
		res.setArchive(value);
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#setHidden(boolean)
	 */
	public void setHidden(boolean value) throws IOException {
		res.setHidden(value);
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#setSystem(boolean)
	 */
	public void setSystem(boolean value) throws IOException {
		res.setSystem(value);
	}


	/**
	 * @see railo.commons.io.res.Resource#getAttribute(short)
	 */
	public boolean getAttribute(short attribute) {
		return res.getAttribute(attribute);
	}


	/**
	 * @see railo.commons.io.res.Resource#setAttribute(short, boolean)
	 */
	public void setAttribute(short attribute, boolean value) throws IOException {
		res.setAttribute(attribute, value);
	}


	/**
	 *
	 * @see railo.commons.io.res.Resource#setReadable(boolean)
	 */
	public boolean setReadable(boolean value) {
		return res.setReadable(value);
	}


	/**
	 *
	 * @see railo.commons.io.res.Resource#setWritable(boolean)
	 */
	public boolean setWritable(boolean value) {
		return res.setWritable(value);
	}

	public void copyFrom(Resource res, boolean append) throws IOException {
		res.copyFrom(res, append);
	}

	public void copyTo(Resource res, boolean append) throws IOException {
		res.copyTo(res, append);
	}

}