package railo.commons.io.res.type.ram;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.Resources;
import railo.commons.io.res.util.ResourceLockImpl;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.runtime.op.Caster;
import railo.runtime.type.List;
import railo.runtime.type.Sizeable;


/**
 * Resource Provider for ram resource
 */
public final class RamResourceProvider implements ResourceProvider,Sizeable {

	private String scheme="ram";
	private RamResourceCore root;
	
	boolean caseSensitive=true;
	//private Resources resources;
	private long lockTimeout=1000;
	private ResourceLockImpl lock=new ResourceLockImpl(lockTimeout,caseSensitive);
	private Map arguments;

	
	/**
	 * initalize ram resource
	 * @param scheme
	 * @param arguments
	 * @return RamResource
	 */
	public ResourceProvider init(String scheme,Map arguments) {
		if(!StringUtil.isEmpty(scheme))this.scheme=scheme;
		
		if(arguments!=null) {
			this.arguments=arguments;
			Object oCaseSensitive= arguments.get("case-sensitive");
			if(oCaseSensitive!=null) {
				caseSensitive=Caster.toBooleanValue(oCaseSensitive,true);
			}
			
			// lock-timeout
			Object oTimeout = arguments.get("lock-timeout");
			if(oTimeout!=null) {
				lockTimeout=Caster.toLongValue(oTimeout,lockTimeout);
			}
		}
		lock.setLockTimeout(lockTimeout);
		lock.setCaseSensitive(caseSensitive);
		
		root=new RamResourceCore(null,RamResourceCore.TYPE_DIRECTORY,"");
		return this;
	}
	
	
	
	/**
	 * @see res.ResourceProvider#getResource(java.lang.String)
	 */
	public Resource getResource(String path) {
		path=ResourceUtil.removeScheme(scheme,path);
		return new RamResource(this,path);
	}
	
	/**
	 * returns core for this path if exists, otherwise return null
	 * @param path
	 * @return core or null
	 */
	RamResourceCore getCore(String path) {
		String[] names = List.listToStringArray(path,'/');
		
		
		RamResourceCore rrc=root;
		for(int i=0;i<names.length;i++) {
			rrc=rrc.getChild(names[i],caseSensitive);
			if(rrc==null) return null;
		}
		return rrc;
	}

	/**
	 * create a new core 
	 * @param path
	 * @param type
	 * @return created core
	 * @throws IOException
	 */
	RamResourceCore createCore(String path, int type) throws IOException {
		String[] names = List.listToStringArray(path,'/');
		RamResourceCore rrc=root;
		for(int i=0;i<names.length-1;i++) {
			rrc=rrc.getChild(names[i],caseSensitive);
			if(rrc==null) throw new IOException("can't create resource "+path+", missing parent resource");
		}
		rrc = new RamResourceCore(rrc,type,names[names.length-1]);
		return rrc;
	}


	/**
	 * @see res.ResourceProvider#getScheme()
	 */
	public String getScheme() {
		return scheme;
	}
	/**
	 * @see railo.commons.io.res.ResourceProvider#setResources(railo.commons.io.res.Resources)
	 */
	public void setResources(Resources resources) {
		//this.resources=resources;
	}

	/**
	 * @throws IOException 
	 * @see railo.commons.io.res.ResourceProvider#lock(railo.commons.io.res.Resource)
	 */
	public void lock(Resource res) throws IOException {
		lock.lock(res);
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#unlock(railo.commons.io.res.Resource)
	 */
	public void unlock(Resource res) {
		lock.unlock(res);
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#read(railo.commons.io.res.Resource)
	 */
	public void read(Resource res) throws IOException {
		lock.read(res);
	}

	/**
	 *
	 * @see railo.commons.io.res.ResourceProvider#isAttributesSupported()
	 */
	public boolean isAttributesSupported() {
		return true;
	}

	/**
	 *
	 * @see railo.commons.io.res.ResourceProvider#isCaseSensitive()
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 *
	 * @see railo.commons.io.res.ResourceProvider#isModeSupported()
	 */
	public boolean isModeSupported() {
		return true;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ResourceProvider p = new RamResourceProvider().init("ram",new HashMap());
		Resource r = p.getResource("/susi/test.cfm");
		r.getParentResource().createDirectory(false);
		OutputStream os = r.getOutputStream();
		IOUtil.copy(new ByteArrayInputStream("hallo Welt".getBytes()),os,true,true);
		
		print(r);
		r = r.getParentResource();
		print(r);
	}

	private static void print(Resource r) throws IOException {
		System.out.println("****************************************");
		System.out.println("path:"+r.getPath());
		System.out.println("name:"+r.getName());
		System.out.println("parent:"+r.getParent());
		System.out.println("parent-res:"+r.getParentResource());
		System.out.println("exists:"+r.exists());
		System.out.println("isDirectory:"+r.isDirectory());
		System.out.println("isFile:"+r.isFile());
		System.out.println("lastModified:"+r.lastModified());
		if(r.isFile()) {
			System.out.println("->"+IOUtil.toString(r,null)+"<-");
		}
		if(r.isDirectory()) {
			System.out.println(" - children");
			String[] children = r.list();
			for(int i=0;i<children.length;i++) {
				System.out.println("   - "+children[i]);
			}
			Resource[] ch2 = r.listResources();
			for(int i=0;i<children.length;i++) {
				System.out.println("   - "+ch2[i]);
			}
			
		}
		
	}

	/**
	 * @see railo.runtime.type.Sizeable#sizeOf()
	 */
	public long sizeOf() {
		return SizeOf.size(root)+SizeOf.size(lock);
	}

	/**
	 * @see railo.commons.io.res.ResourceProvider#getArguments()
	 */
	public Map getArguments() {
		return arguments;
	}
}
