package railo.commons.io.res.type.ram;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import railo.commons.io.ModeUtil;
import railo.commons.io.res.ContentType;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.util.ResourceSupport;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;


/**
 * a ram resource
 */
public final class RamResource extends ResourceSupport {
	
	private final RamResourceProviderOld provider;
	
	private final String parent;
	private final String name;
	private RamResourceCore _core;

	RamResource(RamResourceProviderOld provider, String path) {
		this.provider=provider;
		if(path.equals("/") || StringUtil.isEmpty(path)) {
		//if(path.equals("/")) {
			this.parent=null;
			this.name="";
		}
		else {
			String[] pn = ResourceUtil.translatePathName(path);
			this.parent=pn[0];
			this.name=pn[1];
		}
		
	}
	
	private RamResource(RamResourceProviderOld provider, String parent,String name) {
		this.provider=provider;
		this.parent=parent ;
		this.name=name;
	}
	
	RamResourceCore getCore() {
		if(_core==null || _core.getType()==0) {
			_core=provider.getCore(getInnerPath());
		}
		return _core;
	}
	

	void removeCore() {
		if(_core==null)return;
		_core.remove();
		_core=null;
	}
	
	private RamResourceCore createCore(int type) throws IOException {
		return _core=provider.createCore(getInnerPath(),type);
	}
	

	/**
	 * @see res.Resource#getPath()
	 */
	public String getPath() {
		return provider.getScheme().concat("://").concat(getInnerPath());
	}
	private String getInnerPath() {
		if(parent==null) return "/";
		return parent.concat(name);
	}

	/**
	 * @see res.Resource#getFullName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see res.Resource#getParent()
	 */
	public String getParent() {
		if(isRoot()) return null;
		return provider.getScheme().concat("://").concat(ResourceUtil.translatePath(parent, true, false));
	}

	/**
	 * @see res.Resource#isReadable()
	 */
	public boolean isReadable() {
		return ModeUtil.isReadable(getMode());
	}

	/**
	 * @see res.Resource#isWriteable()
	 */
	public boolean isWriteable() {
		return ModeUtil.isWritable(getMode());
	}

	/**
	 * @see res.Resource#removeE(boolean)
	 */
	public void remove(boolean force) throws IOException {
		if(isRoot()) 
			throw new IOException("can't remove root resource ["+getPath()+"]");

		provider.read(this);
		RamResourceCore core = getCore();
		if(core==null)
			throw new IOException("can't remove resource ["+getPath()+"],resource does not exist");
		
		Resource[] children = listResources();
		if(children!=null && children.length>0) {
			if(!force) {
				throw new IOException("can't delete directory ["+getPath()+"], directory is not empty");
			}
			for(int i=0;i<children.length;i++) {
				children[i].remove(true);
			}
		}
		removeCore();
	}

	/**
	 * @see res.Resource#exists()
	 */
	public boolean exists() {
		try {
			provider.read(this);
		} catch (IOException e) {
			return true;
		}
		return getCore()!=null;
	}

	/**
	 * @see res.Resource#getParentResource()
	 */
	public Resource getParentResource() {
		return getParentRamResource();
	}


	private RamResource getParentRamResource() {
		if(isRoot()) return null;
		return new RamResource(provider,parent);
	}

	public Resource getRealResource(String realpath) {
		realpath=ResourceUtil.merge(getInnerPath(), realpath);
		if(realpath.startsWith("../"))return null;

		return new RamResource(provider,realpath);
	}

	/**
	 * @see res.Resource#isAbsolute()
	 */
	public boolean isAbsolute() {
		return true;
	}

	/**
	 * @see res.Resource#isDirectory()
	 */
	public boolean isDirectory() {
		return exists() && getCore().getType()==RamResourceCore.TYPE_DIRECTORY;
	}

	/**
	 * @see res.Resource#isFile()
	 */
	public boolean isFile() {
		return exists() && getCore().getType()==RamResourceCore.TYPE_FILE;
	}

	/**
	 * @see res.Resource#lastModified()
	 */
	public long lastModified() {
		if(!exists()) return 0;
		return getCore().getLastModified();
	}

	/**
	 * @see res.Resource#length()
	 */
	public long length() {
		if(!exists()) return 0;
		byte[] data= getCore().getData();
		if(data==null) return 0;
		return data.length;
	}

	/**
	 * @see res.Resource#list()
	 */
	public String[] list() {
		if(!exists()) return null;
		RamResourceCore core = getCore();
		if(core.getType()!=RamResourceCore.TYPE_DIRECTORY)
			return null;
		
		return core.getChildNames();
		/*List list = core.getChildren();
		if(list==null && list.size()==0) return new String[0];
		
		Iterator it = list.iterator();
		String[] children=new String[list.size()];
		RamResourceCore cc;
		int count=0;
		while(it.hasNext()) {
			cc=(RamResourceCore) it.next();
			children[count++]=cc.getName();
		}
		return children;*/
	}

	/**
	 * @see res.Resource#listResources()
	 */
	public Resource[] listResources() {
		String[] list = list();
		if(list==null)return null;
		
		Resource[] children=new Resource[list.length];
		String p=getInnerPath();
		if(!isRoot())p=p.concat("/");
		for(int i=0;i<children.length;i++) {
			children[i]=new RamResource(provider,p,list[i]);
		}
		return children;
	}

	/**
	 * @see res.Resource#setLastModified(long)
	 */
	public boolean setLastModified(long time) {
		if(!exists()) return false;
		getCore().setLastModified(time);
		return true;
	}

	/**
	 * @see res.Resource#setReadOnly()
	 */
	public boolean setReadOnly() {
		return setWritable(false);
	}

	/**
	 * @throws IOException 
	 * @see res.Resource#createFile(boolean)
	 */
	public void createFile(boolean createParentWhenNotExists) throws IOException {
		ResourceUtil.checkCreateFileOK(this,createParentWhenNotExists);
		provider.lock(this);
		try {
			createCore(RamResourceCore.TYPE_FILE);
		}
		finally {
			provider.unlock(this);
		}
	}


	/**
	 * @see res.Resource#createDirectory(boolean)
	 */
	public void createDirectory(boolean createParentWhenNotExists) throws IOException {
		ResourceUtil.checkCreateDirectoryOK(this,createParentWhenNotExists);
		provider.lock(this);
		try {
			createCore(RamResourceCore.TYPE_DIRECTORY);
		}
		finally {
			provider.unlock(this);
		}
		
	}

	/**
	 * @see res.Resource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		ResourceUtil.checkGetInputStreamOK(this);

		provider.lock(this);
		RamResourceCore core = getCore();
		
		byte[] data = core.getData();
		if(data==null)data=new byte[0];
		provider.unlock(this);
		return new ByteArrayInputStream(data);
	}

	public OutputStream getOutputStream(boolean append) throws IOException {
		ResourceUtil.checkGetOutputStreamOK(this);
		provider.lock(this);
		return new RamOutputStream(this,append);
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#getContentType()
	 */
	public ContentType getContentType() {
		return ResourceUtil.getContentType(this);
	}

	/**
	 * @see res.Resource#getResourceProvider()
	 */
	public ResourceProvider getResourceProvider() {
		return provider;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getPath();
	}
	

	/**
	 * This is useed by the MemoryResource too write back data to, that are written to outputstream
	 */
	class RamOutputStream extends ByteArrayOutputStream {

		private RamResource res;
		private boolean append;

		/**
		 * Constructor of the class
		 * @param res
		 */
		public RamOutputStream(RamResource res, boolean append) {
			this.append=append;
			this.res=res;
		}

		/**
		 * @see java.io.ByteArrayOutputStream#close()
		 */
		public void close() throws IOException {
			try {
				super.close();
				RamResourceCore core = res.getCore();
				if(core==null)core=res.createCore(RamResourceCore.TYPE_FILE);
				
				core.setData(this.toByteArray(),append);
			}
			finally {
				res.getResourceProvider().unlock(res);
			}
		}
	}

	/**
	 * @see railo.commons.io.res.Resource#setReadable(boolean)
	 */
	public boolean setReadable(boolean value) {
		if(!exists())return false;
		try {
			setMode(ModeUtil.setReadable(getMode(), value));
			return true;
		} catch (IOException e) {
			return false;
		}
		
	}
	
	/**
	 * @see railo.commons.io.res.Resource#setWritable(boolean)
	 */
	public boolean setWritable(boolean value) {
		if(!exists())return false;
		try {
			setMode(ModeUtil.setWritable(getMode(), value));
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private boolean isRoot() {
		return parent==null;
	}
	
	public int getMode() {
		if(!exists())return 0;
		return getCore().getMode();
	}
	
	public void setMode(int mode) throws IOException {
		if(!exists())throw new IOException("can't set mode on resource ["+this+"], resource does not exist");
		getCore().setMode(mode);
	}
	/**
	 *
	 * @see railo.commons.io.res.util.ResourceSupport#getAttribute(short)
	 */
	public boolean getAttribute(short attribute) {
		if(!exists())return false;
		return (getCore().getAttributes()&attribute)>0;
	}
	/**
	 *
	 * @see railo.commons.io.res.util.ResourceSupport#setAttribute(short, boolean)
	 */
	public void setAttribute(short attribute, boolean value) throws IOException {
		if(!exists())throw new IOException("can't get attributes on resource ["+this+"], resource does not exist");
		int attr = getCore().getAttributes();
		if(value) {
			if((attr&attribute)==0) attr+=attribute;
		}
		else {
			if((attr&attribute)>0) attr-=attribute;
		}
		getCore().setAttributes(attr);
	}
	
}
