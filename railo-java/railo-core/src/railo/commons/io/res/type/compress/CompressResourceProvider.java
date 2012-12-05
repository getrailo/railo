package railo.commons.io.res.type.compress;

import java.io.IOException;
import java.util.Map;

import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.Resources;
import railo.commons.io.res.util.ResourceLockImpl;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.op.Caster;
import railo.runtime.type.Sizeable;

public abstract class CompressResourceProvider implements ResourceProvider,Sizeable {
	
	private static final long serialVersionUID = 5930090603192203086L;
	
	private Resources resources;
	protected String scheme=null;
	protected boolean caseSensitive=true;
	boolean async=true; 
	private long lockTimeout=10000;
	private final ResourceLockImpl lock=new ResourceLockImpl(lockTimeout,caseSensitive);
	private Map arguments;

	@Override
	public ResourceProvider init(String scheme, Map arguments) {
		if(!StringUtil.isEmpty(scheme))this.scheme=scheme;
		if(arguments!=null) {
			this.arguments=arguments;
			// case-sensitive
			String strCaseSensitive=(String) arguments.get("case-sensitive");
			if(strCaseSensitive!=null) {
				caseSensitive=Caster.toBooleanValue(strCaseSensitive,true);
			}
			
			// sync
			String strASync=(String) arguments.get("asynchronus");
			if(strASync==null)strASync=(String) arguments.get("async");
			if(strASync!=null) {
				async=Caster.toBooleanValue(strASync,true);
			}
			
			// lock-timeout
			String strTimeout = (String) arguments.get("lock-timeout");
			if(strTimeout!=null) {
				lockTimeout=Caster.toLongValue(arguments.get("lock-timeout"),lockTimeout);
			}
		}
		lock.setLockTimeout(lockTimeout);
		lock.setCaseSensitive(caseSensitive);
		
		return this;
	}
	
	public ResourceProvider init(String scheme, boolean caseSensitive, boolean async) {
		if(!StringUtil.isEmpty(scheme))this.scheme=scheme;
		this.caseSensitive=caseSensitive;
		this.async=async;
		return this;
	}

	@Override
	public Resource getResource(String path) { 
		path=ResourceUtil.removeScheme(scheme,path);
		int index=path.lastIndexOf('!');
		if(index!=-1) {
			
			Resource file = toResource(path.substring(0,index));//resources.getResource(path.substring(0,index));
			return new CompressResource(this,getCompress(file),path.substring(index+1),caseSensitive);
		}
		Resource file = toResource(path);//resources.getResource(path);
		return new CompressResource(this,getCompress(file),"/",caseSensitive);
	}
	
	private Resource toResource(String path) {
		PageContext pc = ThreadLocalPageContext.get();
		if(pc!=null) {
			return ResourceUtil.toResourceNotExisting(ThreadLocalPageContext.get(), path,true);
		}
		return resources.getResource(path);
	}

	public abstract Compress getCompress(Resource file);

	@Override
	public String getScheme() {
		return scheme;
	}



	public void setResources(Resources resources) {
		this.resources=resources;
	}

	@Override
	public void lock(Resource res) throws IOException {
		lock.lock(res);
	}

	@Override
	public void unlock(Resource res) {
		lock.unlock(res);
	}

	@Override
	public void read(Resource res) throws IOException {
		lock.read(res);
	}

	@Override
	public Map getArguments() {
		return arguments;
	}
	
	/*public static void main(String[] args) throws IOException {
		Resources rs=ResourcesImpl.getGlobal();
		rs.registerResourceProvider(new ZipResourceProvider().init("zip", null));
		rs.registerResourceProvider(new RamResourceProvider().init("ram", null));
		
		
		Resource ra = rs.getResource("zip:///Users/mic/temp/test/ras111.zip!/dd/");
		print.ln(ra);
		print.ln(ra.getParent());
		
		ra = rs.getResource("ram:///dd/");
		print.ln(ra);
		print.ln(ra.getParent());
		
		
		Resource org = rs.getResource("/Users/mic/temp/test/org.zip"); 
		Resource trg = rs.getResource("/Users/mic/temp/test/trg.zip"); 
		Resource ras = rs.getResource("/Users/mic/temp/test/ras.zip"); 
		
		ResourceUtil.copy(org, ras);
		
		
		
		Resource res1 = rs.getResource("zip:///Users/mic/temp/test/rasx.zip!/dd"); 
		Resource res2 = rs.getResource("zip:///Users/mic/temp/test/ras.zip!/ddd"+Math.random()+".txt"); 

		res1.mkdirs();
		res2.createNewFile();
		ResourceUtil.copy(ras, trg);
		print.ln("copy");
		
		//Resource org2 = rs.getResource("/Users/mic/temp/test/org.zip"); 
		Resource railotmp = rs.getResource("/Users/mic/temp/railotmp/");
		Resource trg2 = rs.getResource("zip:///Users/mic/temp/railotmp.zip!");
		trg2.delete();
		long start=System.currentTimeMillis();
		ResourceUtil.copyRecursive(railotmp, trg2);
		
		print.ln("ende "+(System.currentTimeMillis()-start));
		
		//print(res3);
		
		
	}

	private static void print(Resource r) {
		
		print.ln("****************************************");
		print.ln(r);
		if(r==null) return;
		print.ln("path:"+r.getPath());
		print.ln("name:"+r.getName());
		print.ln("parent:"+r.getParent());
		print.ln("parent-res:"+r.getParentResource());
		print.ln("exists:"+r.exists());
		print.ln("isDirectory:"+r.isDirectory());
		print.ln("isFile:"+r.isFile());
		print.ln("lastModified:"+r.lastModified());
		if(r.isFile()) {
			//print.ln("->"+IOUtil.toString(r.getI nputStream(),null)+"<-");
		}
		if(r.isDirectory()) {
			print.ln(" - children");
			String[] children = r.list();
			Resource[] ch2 = r.listResources();
			for(int i=0;i<children.length;i++) {
				print.ln("   - "+children[i]);
				print.ln("   - "+ch2[i]);
			}
		}
	}*/

	
	@Override
	public long sizeOf() {
		return SizeOf.size(lock);
	}
}
