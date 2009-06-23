

package railo.commons.io.res.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ResourceFilter;
import railo.commons.io.res.filter.ResourceNameFilter;

/**
 * Helper class to build resources
 */
public abstract class ResourceSupport implements Resource {

	/**
	 * @see railo.commons.io.res.Resource#copyFrom(railo.commons.io.res.Resource,boolean)
	 */
	public void copyFrom(Resource res,boolean append) throws IOException {
		IOUtil.copy(res, this.getOutputStream(append),true);
	}

	/**
	 * @see railo.commons.io.res.Resource#copyTo(railo.commons.io.res.Resource,boolean)
	 */
	public void copyTo(Resource res,boolean append) throws IOException {
		IOUtil.copy(this, res.getOutputStream(append),true);
	}

	/**
	 * @see res.Resource#getAbsoluteResource()
	 */
	public Resource getAbsoluteResource() {
		return this;
	}

	/**
	 * @see res.Resource#getAbsolutePath()
	 */
	public String getAbsolutePath() {
		return getPath();
	}

	/**
	 * @see res.Resource#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		return getOutputStream(false);
	}

	/**
	 * @see res.Resource#getCanonicalResource()
	 */
	public Resource getCanonicalResource() throws IOException {
		return this;
	}

	/**
	 * @see res.Resource#getCanonicalPath()
	 */
	public String getCanonicalPath() throws IOException {
		return getPath();
	}

	/**
	 * @see res.Resource#moveTo(res.Resource)
	 */
	public void moveTo(Resource dest) throws IOException {
		ResourceUtil.moveTo(this,dest);
	}
	
	/**
	 * @see res.Resource#list(res.filter.ResourceFilter)
	 */
	public String[] list(ResourceFilter filter) {
		String[] files = list();
		if(files==null) return null;
		List list=new ArrayList();
		Resource res;
		for(int i=0;i<files.length;i++) {
			res=getRealResource(files[i]);
			if(filter.accept(res))list.add(files[i]);
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * @see res.Resource#list(res.filter.ResourceNameFilter)
	 */
	public String[] list(ResourceNameFilter filter) {
		String[] lst=list();
		if(lst==null) return null;
		
		List list=new ArrayList();
		for(int i=0;i<lst.length;i++) {
			if(filter.accept(getParentResource(),lst[i]))list.add(lst[i]);
		}
		if(list.size()==0) return new String[0];
		if(list.size()==lst.length) return lst;
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * @see res.Resource#listResources(res.filter.ResourceNameFilter)
	 */
	public Resource[] listResources(ResourceNameFilter filter) {
		String[] files = list();
		if(files==null) return null;
		
		List list=new ArrayList();
		for(int i=0;i<files.length;i++) {
			if(filter.accept(this,files[i]))list.add(getRealResource(files[i]));
		}
		return (Resource[]) list.toArray(new Resource[list.size()]);
	}

	/**
	 * @see res.Resource#listResources(res.filter.ResourceFilter)
	 */
	public Resource[] listResources(ResourceFilter filter) {
		String[] files = list();
		if(files==null) return null;
		
		List list=new ArrayList();
		Resource res;
		for(int i=0;i<files.length;i++) {
			res=this.getRealResource(files[i]);
			if(filter.accept(res))list.add(res);
		}
		return (Resource[]) list.toArray(new Resource[list.size()]);
	}

	/**
	 * @see res.Resource#getReal(java.lang.String)
	 */
	public String getReal(String realpath) {
		return getRealResource(realpath).getPath();
	}
	

	/**
	 * @see res.Resource#list()
	 */
	public String[] list() {
		Resource[] children = listResources();
		if(children==null) return null;
		String[] rtn=new String[children.length];
		for(int i=0;i<children.length;i++) {
			rtn[i]=children[i].getName();
		}
		return rtn;
	}
	

	/**
	 * @see res.Resource#canRead()
	 */
	public boolean canRead() {
		return isReadable();
	}

	/**
	 * @see res.Resource#canWrite()
	 */
	public boolean canWrite() {
		return isWriteable();
	}

	/**
	 * @see res.Resource#renameTo(res.Resource)
	 */
	public boolean renameTo(Resource dest) {
		try {
			moveTo(dest);
			return true;
		}
		catch (IOException e) {
			return false;
		}
		
	}

	/**
	 * @see res.Resource#createNewFile()
	 */
	public boolean createNewFile() {
		try {
			createFile(false);
			return true;
		} 
		catch (IOException e) {}
		return false;
	}

	/**
	 * @see res.Resource#mkdir()
	 */
	public boolean mkdir() {
		try {
			createDirectory(false);
			return true;
		}
		catch (IOException e) {}
		return false;
	}

	/**
	 * @see res.Resource#mkdirs()
	 */
	public boolean mkdirs() {
		try {
			createDirectory(true);
			return true;
		}
		catch (IOException e) {
			return false;
		}
	}
	

	/**
	 *
	 * @see railo.commons.io.res.Resource#delete()
	 */
	public boolean delete() {
		try {
			remove(false);
			return true;
		} 
		catch (IOException e) {}
		return false;
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#isArchive()
	 */
	public boolean isArchive() {
		return getAttribute(Resource.ATTRIBUTE_ARCHIVE);
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#isSystem()
	 */
	public boolean isSystem() {
		return getAttribute(Resource.ATTRIBUTE_SYSTEM);
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#isHidden()
	 */
	public boolean isHidden() {
		return getAttribute(Resource.ATTRIBUTE_HIDDEN);
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#setArchive(boolean)
	 */
	public void setArchive(boolean value) throws IOException {
		setAttribute(ATTRIBUTE_ARCHIVE, value);
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#setHidden(boolean)
	 */
	public void setHidden(boolean value) throws IOException {
		setAttribute(ATTRIBUTE_HIDDEN, value);
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#setReadOnly()
	 */
	public boolean setReadOnly() {
		return setWritable(false);
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#setSystem(boolean)
	 */
	public void setSystem(boolean value) throws IOException {
		setAttribute(ATTRIBUTE_SYSTEM, value);
	}
	
	/**
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(this==obj) return true;
		if(!(obj instanceof Resource)) return false;
		Resource other=(Resource) obj;
		
		if(getPath().equals(other.getPath())) return true;
		return ResourceUtil.getCanonicalPathEL(this).equals(ResourceUtil.getCanonicalPathEL(other));
	}
	
	/**
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getPath();
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#getAttribute(short)
	 */
	public boolean getAttribute(short attribute) {
		return false;
	}

	/**
	 *
	 * @see railo.commons.io.res.Resource#setAttribute(short, boolean)
	 */
	public void setAttribute(short attribute, boolean value) throws IOException {
		throw new IOException("the resource ["+getPath()+"] does not support attributes");
	}
}