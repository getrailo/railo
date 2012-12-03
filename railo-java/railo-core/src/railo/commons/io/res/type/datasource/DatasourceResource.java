package railo.commons.io.res.type.datasource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import railo.commons.io.IOUtil;
import railo.commons.io.ModeUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.type.datasource.DatasourceResourceProvider.ConnectionData;
import railo.commons.io.res.util.ResourceSupport;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.type.util.ArrayUtil;

public final class DatasourceResource extends ResourceSupport {

	

	private DatasourceResourceProvider provider;
	private String parent;
	private String name;
	private ConnectionData data;
	private int fullPathHash;
	private int pathHash;
	
	/**
	 * Constructor of the class
	 * @param provider
	 * @param data
	 * @param path
	 */
	DatasourceResource(DatasourceResourceProvider provider, ConnectionData data,String path) {
		this.provider=provider;
		this.data=data;
		if("/".equals(path)) {
			this.parent=null;
			this.name="";
		}
		else {
			String[] pn = ResourceUtil.translatePathName(path);
			this.parent=pn[0];
			this.name=pn[1];
		}
	}


	private int fullPathHash() {
		if(fullPathHash==0) fullPathHash=getInnerPath().hashCode();
		return fullPathHash;
	}
	
	private int pathHash() {
		if(pathHash==0 && parent!=null) pathHash=parent.hashCode();
		return pathHash;
	}

	private Attr attr() {
		return provider.getAttr(data,fullPathHash(),parent,name);
	}


	private boolean isRoot() {
		return parent==null;
	}
	
	/**
	 * @see railo.commons.io.res.Resource#createDirectory(boolean)
	 */
	public void createDirectory(boolean createParentWhenNotExists) throws IOException {
		ResourceUtil.checkCreateDirectoryOK(this,createParentWhenNotExists);
		provider.create(data,fullPathHash(),pathHash(),parent,name,Attr.TYPE_DIRECTORY);
		
	}

	/**
	 * @see railo.commons.io.res.Resource#createFile(boolean)
	 */
	public void createFile(boolean createParentWhenNotExists) throws IOException {
		ResourceUtil.checkCreateFileOK(this,createParentWhenNotExists);
		provider.create(data,fullPathHash(),pathHash(),parent,name,Attr.TYPE_FILE);
	}
	
	/**
	 * @see railo.commons.io.res.Resource#remove(boolean)
	 */
	public void remove(boolean force) throws IOException {
		ResourceUtil.checkRemoveOK(this);
		if(isRoot()) 
			throw new IOException("can't remove root resource ["+getPath()+"]");

		
		Resource[] children = listResources();
		if(children!=null && children.length>0) {
			if(!force) {
				throw new IOException("can't delete directory ["+getPath()+"], directory is not empty");
			}
			for(int i=0;i<children.length;i++) {
				children[i].remove(true);
			}
		}
		provider.delete(data,fullPathHash(),parent,name);
	}

	/**
	 * @see railo.commons.io.res.Resource#exists()
	 */
	public boolean exists() {
		return attr().exists();
	}

	/**
	 * @see railo.commons.io.res.Resource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		ResourceUtil.checkGetInputStreamOK(this);
		return provider.getInputStream(data,fullPathHash(),parent,name);
	}

	/**
	 * @see railo.commons.io.res.Resource#getMode()
	 */
	public int getMode() {
		return attr().getMode();
	}

	/**
	 * @see res.Resource#getFullName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see railo.commons.io.res.Resource#getOutputStream(boolean)
	 */
	public OutputStream getOutputStream(boolean append) throws IOException {
		ResourceUtil.checkGetOutputStreamOK(this);
		byte[] barr=null;
		
		if(append && !provider.concatSupported(data) && isFile()){
			try{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				IOUtil.copy(getInputStream(), baos,true,true);
				barr = baos.toByteArray();
			}
			catch(Throwable t){
				
			}
		}
		
		OutputStream os = provider.getOutputStream(data,fullPathHash(),pathHash(),parent,name,append);
		if(!ArrayUtil.isEmpty(barr))IOUtil.copy(new ByteArrayInputStream(barr), os,true,false);
		return os;
	}

	/**
	 * @see railo.commons.io.res.Resource#getParent()
	 */
	public String getParent() {
		if(isRoot()) return null;
		String p = (StringUtil.isEmpty(parent))?"/":parent;
		return provider.getScheme().concat("://").concat(data.key()).concat(ResourceUtil.translatePath(p, true, false));
		
	}

	/**
	 * @see railo.commons.io.res.Resource#getParentResource()
	 */
	public Resource getParentResource() {
		return getParentDatasourceResource();
	}
	private DatasourceResource getParentDatasourceResource() {
		if(isRoot()) return null;
		return new DatasourceResource(provider,data,parent);
	}

	/**
	 * @see res.Resource#getPath()
	 */
	public String getPath() {
		return provider.getScheme().concat("://").concat(data.key()).concat(getInnerPath());
	}
	private String getInnerPath() {
		if(parent==null) return "/";
		return parent.concat(name);
	}

	/**
	 * @see railo.commons.io.res.Resource#getRealResource(java.lang.String)
	 */
	public Resource getRealResource(String realpath) {
		realpath=ResourceUtil.merge(getInnerPath(), realpath);
		if(realpath.startsWith("../"))return null;

		return new DatasourceResource(provider,data,realpath);
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
		return true;
	}

	/**
	 * @see railo.commons.io.res.Resource#isDirectory()
	 */
	public boolean isDirectory() {
		return attr().isDirectory();
	}
	
	/**
	 * @see railo.commons.io.res.Resource#isFile()
	 */
	public boolean isFile() {
		return attr().isFile();
	}

	/**
	 * @see railo.commons.io.res.Resource#isReadable()
	 */
	public boolean isReadable() {
		return ModeUtil.isReadable(getMode());
	}

	/**
	 * @see railo.commons.io.res.Resource#isWriteable()
	 */
	public boolean isWriteable() {
		return ModeUtil.isWritable(getMode());
	}

	/**
	 * @see railo.commons.io.res.Resource#lastModified()
	 */
	public long lastModified() {
		return attr().getLastModified();
	}

	/**
	 * @see railo.commons.io.res.Resource#length()
	 */
	public long length() {
		return attr().size();
	}

	/**
	 * @see railo.commons.io.res.Resource#listResources()
	 */
	public Resource[] listResources() {
		if(!attr().isDirectory())return null;
		
		String path;
		if(parent==null) path= "/";
		else path=parent.concat(name).concat("/");
		
		
		Attr[] children=null;
		try {
			children = provider.getAttrs(data,path.hashCode(),path);
		} catch (PageException e) {
			throw new PageRuntimeException(e);
		}
		if(children==null) return new Resource[0];
		Resource[] attrs = new Resource[children.length];
		for(int i=0;i<children.length;i++) {
			// TODO optimieren, alle attr mitgeben
			attrs[i]=new DatasourceResource(provider,data,path+children[i].getName());
		}
		return attrs;
	}

	/**
	 * @see railo.commons.io.res.Resource#setLastModified(long)
	 */
	public boolean setLastModified(long time) {
		if(!exists()) return false;
		return provider.setLastModified(data,fullPathHash(),parent,name,time);
	}

	/**
	 * @see railo.commons.io.res.Resource#setMode(int)
	 */
	public void setMode(int mode) throws IOException {
		if(!exists())throw new IOException("can't set mode on resource ["+this+"], resource does not exist");
		provider.setMode(data,fullPathHash(),parent,name,mode);
	}


	/**
	 * @see railo.commons.io.res.util.ResourceSupport#moveTo(railo.commons.io.res.Resource)
	 */
	public void moveTo(Resource dest) throws IOException {
		super.moveTo(dest);// TODO
	}
	
	
	/**
	 * @see railo.commons.io.res.Resource#setReadable(boolean)
	 */
	public boolean setReadable(boolean readable) {
		if(!exists())return false;
		try {
			setMode(ModeUtil.setReadable(getMode(), readable));
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * @see railo.commons.io.res.Resource#setWritable(boolean)
	 */
	public boolean setWritable(boolean writable) {
		if(!exists())return false;
		try {
			setMode(ModeUtil.setWritable(getMode(), writable));
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getPath();
	}

}
