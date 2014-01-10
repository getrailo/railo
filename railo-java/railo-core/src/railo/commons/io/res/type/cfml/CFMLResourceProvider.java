package railo.commons.io.res.type.cfml;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.Resources;
import railo.commons.io.res.util.ResourceLockImpl;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;

public class CFMLResourceProvider implements ResourceProvider {

	private static final Object[] ZERO_ARGS = new Object[0];
	
	private int lockTimeout=20000;
	private final ResourceLockImpl lock=new ResourceLockImpl(lockTimeout,false);
	private String scheme;
	private Map args;
	//private ResourceProvider provider;
	private Resources resources;

	private String cfcPath;

	private Component component;

	private boolean useStreams=false;
 
	

	@Override
	public ResourceProvider init(String scheme, Map args) {
		this.scheme=scheme;
		this.args=args;
		
		// CFC Path
		cfcPath=Caster.toString(args.get("cfc"),null);
		if(StringUtil.isEmpty(cfcPath,true))
			cfcPath=Caster.toString(args.get("component"),null);
		
		// use Streams for data
		Boolean _useStreams = Caster.toBoolean(args.get("use-streams"),null);
		if(_useStreams==null)_useStreams = Caster.toBoolean(args.get("usestreams"),null);
		
		if(_useStreams!=null)useStreams=_useStreams.booleanValue();
		
		return this;
	}

	@Override
	public Resource getResource(String path) {
		path=ResourceUtil.removeScheme(scheme,path);
		path=ResourceUtil.prettifyPath(path);
		if(!StringUtil.startsWith(path,'/'))path="/"+path;
		return callResourceRTE(null, null, "getResource", new Object[]{path},false);
	}

	@Override
	public String getScheme() {
		return scheme;
	}

	@Override
	public Map getArguments() {
		return args;
	}

	@Override
	public void setResources(Resources resources) {
		this.resources=resources;
	}


	@Override
	public boolean isCaseSensitive() {
		return callbooleanRTE(null,null, "isCaseSensitive", ZERO_ARGS);
	}

	@Override
	public boolean isModeSupported() {
		return callbooleanRTE(null,null, "isModeSupported", ZERO_ARGS);
	}

	@Override
	public boolean isAttributesSupported() {
		return callbooleanRTE(null,null, "isAttributesSupported", ZERO_ARGS);
	}
	
	public int getLockTimeout() {
		return lockTimeout;
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

	public boolean isUseStreams() {
		return useStreams;
	}
	
	
	Resource callResourceRTE(PageContext pc,Component cfc,String methodName, Object[] args, boolean allowNull) {
		pc = ThreadLocalPageContext.get(pc);
		try {
			Object res = call(pc,getCFC(pc,cfc), methodName, args);
			if(allowNull && res==null) return null;
			return new CFMLResource(this,Caster.toComponent(res));
		} 
		catch (PageException pe) {pe.printStackTrace();
			throw new PageRuntimeException(pe);
		} 
	}

	Resource[] callResourceArrayRTE(PageContext pc,Component cfc,String methodName, Object[] args) {
		pc = ThreadLocalPageContext.get(pc);
		try {
			Array arr = Caster.toArray(call(pc,getCFC(pc,cfc), methodName, args));
			Iterator<Object> it = arr.valueIterator();
			CFMLResource[] resources=new CFMLResource[arr.size()];
			int index=0;
			while(it.hasNext()){
				resources[index++]=new CFMLResource(this,Caster.toComponent(it.next()));
			}
			return resources;
		} 
		catch (PageException pe) {
			throw new PageRuntimeException(pe);
		}
	}
	

	int callintRTE(PageContext pc,Component cfc,String methodName, Object[] args) {
		try {
			return callint(pc,cfc, methodName, args);
		} 
		catch (PageException pe) {
			throw new PageRuntimeException(pe);
		}
	}
	int callint(PageContext pc,Component cfc,String methodName, Object[] args) throws PageException {
		return Caster.toIntValue(call(pc,cfc,methodName, args));
	}

	long calllongRTE(PageContext pc,Component cfc,String methodName, Object[] args) {
		try {
			return calllong(pc,cfc, methodName, args);
		} 
		catch (PageException pe) {
			throw new PageRuntimeException(pe);
		}
	}
	long calllong(PageContext pc,Component cfc,String methodName, Object[] args) throws PageException {
		return Caster.toLongValue(call(pc,cfc,methodName, args));
	}
	
	boolean callbooleanRTE(PageContext pc,Component cfc,String methodName, Object[] args) {
		try {
			return callboolean(pc,cfc, methodName, args);
		} 
		catch (PageException pe) {
			throw new PageRuntimeException(pe);
		}
	}
	boolean callboolean(PageContext pc,Component cfc,String methodName, Object[] args) throws PageException {
		return Caster.toBooleanValue(call(pc,cfc,methodName, args));
	}

	String callStringRTE(PageContext pc,Component cfc,String methodName, Object[] args) {
		try {
			return Caster.toString(call(pc,cfc,methodName, args));
		} 
		catch (PageException pe) {
			throw new PageRuntimeException(pe);
		}
	}

	String callString(PageContext pc,Component cfc,String methodName, Object[] args) throws PageException {
		return Caster.toString(call(pc,cfc,methodName, args));
	}

	Object callRTE(PageContext pc,Component cfc,String methodName, Object[] args) {
		try {
			return call(pc,cfc,methodName, args);
		} 
		catch (PageException pe) {
			throw new PageRuntimeException(pe);
		}
	}

	Object call(PageContext pc,Component cfc,String methodName, Object[] args) throws PageException {
		pc = ThreadLocalPageContext.get(pc);
		return getCFC(pc, cfc).call(pc, methodName, args);
	}

	private Component getCFC(PageContext pc,Component cfc) throws PageException {
		if(cfc!=null) return cfc;
		
		if(component!=null) return component;
		
		if(StringUtil.isEmpty(cfcPath,true))throw new ApplicationException("you need to define the argument [component] for the [CFMLResourceProvider]");
		cfcPath=cfcPath.trim();
		component=pc.loadComponent(cfcPath);
		call(pc, component, "init", new Object[]{scheme,Caster.toStruct(args)});
		
		return component;
	}

}
