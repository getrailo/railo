package railo.runtime.thread;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import railo.commons.io.DevNullOutputStream;
import railo.commons.io.log.LogAndSource;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.Pair;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSourceImpl;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.Abort;
import railo.runtime.exp.PageException;
import railo.runtime.net.http.HttpServletResponseDummy;
import railo.runtime.net.http.HttpUtil;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.scope.Argument;
import railo.runtime.type.scope.ArgumentThreadImpl;
import railo.runtime.type.scope.Local;
import railo.runtime.type.scope.LocalImpl;
import railo.runtime.type.scope.Threads;
import railo.runtime.type.scope.Undefined;

public class ChildThreadImpl extends ChildThread implements Serializable {

	private static final long serialVersionUID = -8902836175312356628L;

	private static final Collection.Key KEY_ATTRIBUTES = KeyImpl.intern("attributes");

	//private static final Set EMPTY = new HashSet(); 
	
	private int threadIndex;
	private PageContextImpl parent;
	PageContextImpl pc =null;
	private String tagName;
	private long start;
	private Threads scope;
	
	// accesible from scope
	Struct content=new StructImpl();
	Struct catchBlock;
	boolean terminated;
	boolean completed;
	ByteArrayOutputStream output;
	
	
	// only used for type deamon
	private Page page;
	
	// only used for type task, demon attrs are not Serializable
	private Struct attrs;
	private SerializableCookie[] cookies;
	private String serverName;
	private String queryString;
	private Pair[] parameters;
	private String requestURI;
	private Pair[] headers;
	private Struct attributes;
	private String template;
	private long requestTimeout;

	private boolean serializable;

	String contentType;

	String contentEncoding;
	
	
	public ChildThreadImpl(PageContextImpl parent,Page page, String tagName,int threadIndex, Struct attrs, boolean serializable) {
		this.serializable=serializable;
		this.tagName=tagName;
		this.threadIndex=threadIndex;
		start=System.currentTimeMillis();
		if(attrs==null) this.attrs=new StructImpl();
		else this.attrs=attrs;
		
		if(!serializable){
			this.page=page;
			if(parent!=null){
				output = new ByteArrayOutputStream();
				try{
					this.parent=ThreadUtil.clonePageContext(parent, output,false,false,true);
				}
				catch(ConcurrentModificationException e){// MUST search for:hhlhgiug
					this.parent=ThreadUtil.clonePageContext(parent, output,false,false,true);
				}
				//this.parent=parent;
			}
		}
		else {
			this.template=page.getPageSource().getFullRealpath();
			HttpServletRequest req = parent.getHttpServletRequest();
			serverName=req.getServerName();
			queryString=ReqRspUtil.getQueryString(req);
			cookies=SerializableCookie.toSerializableCookie(ReqRspUtil.getCookies(ThreadLocalPageContext.getConfig(parent),req));
			parameters=HttpUtil.cloneParameters(req);
			requestURI=req.getRequestURI();
			headers=HttpUtil.cloneHeaders(req);
			attributes=HttpUtil.getAttributesAsStruct(req);
			requestTimeout=parent.getRequestTimeout();
			// MUST here ist sill a mutch state values missing
		}
	}

	public PageContext getPageContext(){
		return pc;
	}
	
	
	public void run()  {
		execute(null);
	}
	public PageException execute(Config config)   {
		PageContext oldPc = ThreadLocalPageContext.get();
		
		Page p=page;
		
		if(parent!=null){
			pc=parent;
			ThreadLocalPageContext.register(pc);
		}
		else {
			ConfigWebImpl cwi;
			try {
				cwi = (ConfigWebImpl)config;
				DevNullOutputStream os = DevNullOutputStream.DEV_NULL_OUTPUT_STREAM;
				pc=ThreadUtil.createPageContext(cwi, os, serverName, requestURI, queryString, SerializableCookie.toCookies(cookies), headers, parameters, attributes);
				pc.setRequestTimeout(requestTimeout);
				p=PageSourceImpl.loadPage(pc, cwi.getPageSources(oldPc==null?pc:oldPc,null, template, false,false,true));
				//p=cwi.getPageSources(oldPc,null, template, false,false,true).loadPage(cwi);
			} catch (PageException e) {
				return e;
			}
				pc.addPageSource(p.getPageSource(), true);
		}
		pc.setThreadScope("thread", new ThreadsImpl(this));
		pc.setThread(Thread.currentThread());
		
		//String encodings = pc.getHttpServletRequest().getHeader("Accept-Encoding");
		
		Undefined undefined=pc.us();
		
		Argument newArgs=new ArgumentThreadImpl((Struct) Duplicator.duplicate(attrs,false));
        LocalImpl newLocal=pc.getScopeFactory().getLocalInstance();
        //Key[] keys = attrs.keys();
        Iterator<Entry<Key, Object>> it = attrs.entryIterator();
        Entry<Key, Object> e;
		while(it.hasNext()){
			e = it.next();
			newArgs.setEL(e.getKey(),e.getValue());
		}
		
		newLocal.setEL(KEY_ATTRIBUTES, newArgs);

		Argument oldArgs=pc.argumentsScope();
        Local oldLocal=pc.localScope();
        
        int oldMode=undefined.setMode(Undefined.MODE_LOCAL_OR_ARGUMENTS_ALWAYS);
		pc.setFunctionScopes(newLocal,newArgs);
		
		try {
			p.threadCall(pc, threadIndex); 
		}
		catch (Throwable t) {
			if(!Abort.isSilentAbort(t)) {
				ConfigWeb c = pc.getConfig();
				if(c instanceof ConfigImpl) {
					ConfigImpl ci=(ConfigImpl) c;
					LogAndSource log = ci.getThreadLogger();
					if(log!=null)log.error(this.getName(), ExceptionUtil.getStacktrace(t,true));
				}
				PageException pe = Caster.toPageException(t);
				if(!serializable)catchBlock=pe.getCatchBlock(pc);
				return pe;
			}
		}
		finally {
			completed=true;
			pc.setFunctionScopes(oldLocal,oldArgs);
		    undefined.setMode(oldMode);
		    //pc.getScopeFactory().recycle(newArgs);
            pc.getScopeFactory().recycle(newLocal);
            
            if(pc.getHttpServletResponse() instanceof HttpServletResponseDummy) {
	            HttpServletResponseDummy rsp=(HttpServletResponseDummy) pc.getHttpServletResponse();
	            pc.flush();
	            contentType=rsp.getContentType();
	            Pair<String,Object>[] _headers = rsp.getHeaders();
	            if(_headers!=null)for(int i=0;i<_headers.length;i++){
	            	if(_headers[i].getName().equalsIgnoreCase("Content-Encoding"))
	            		contentEncoding=Caster.toString(_headers[i].getValue(),null);
	            }
            }
            
			((ConfigImpl)pc.getConfig()).getFactory().releasePageContext(pc);
			pc=null;
			if(oldPc!=null)ThreadLocalPageContext.register(oldPc);
		}
		return null;
	}

	@Override
	public String getTagName() {
		return tagName;
	}

	@Override
	public long getStartTime() {
		return start;
	}

	public Threads getThreadScope() {
		if(scope==null) scope=new ThreadsImpl(this);
		return scope;
	}

	public void terminated() {
		terminated=true;
	}

	/**
	 * @return the pageSource
	 */
	public String getTemplate() {
		return template;
	}
	
	
}