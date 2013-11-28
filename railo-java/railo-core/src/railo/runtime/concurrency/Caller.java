package railo.runtime.concurrency;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.Callable;

import railo.commons.io.IOUtil;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.config.ConfigImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.net.http.HttpServletResponseDummy;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.thread.ThreadUtil;

public abstract class Caller implements Callable<String> { 

	private PageContext parent;
	private PageContextImpl pc;
	private ByteArrayOutputStream baos;

	public Caller(PageContext parent) {
		this.parent = parent;
		this.baos = new ByteArrayOutputStream();
		this.pc=ThreadUtil.clonePageContext(parent, baos, false, false, true);
	}
	
	public final String call() throws PageException {
		ThreadLocalPageContext.register(pc);
		pc.getRootOut().setAllowCompression(false); // make sure content is not compressed
		String str=null;
		try{
			_call(parent,pc);
		} 
		finally{
			try {
			HttpServletResponseDummy rsp=(HttpServletResponseDummy) pc.getHttpServletResponse();
			
			String enc = ReqRspUtil.getCharacterEncoding(pc,rsp);
			//if(enc==null) enc="ISO-8859-1";
			
			pc.getOut().flush(); //make sure content is flushed
			
			((ConfigImpl)pc.getConfig()).getFactory().releasePageContext(pc);
				str=IOUtil.toString((new ByteArrayInputStream(baos.toByteArray())), enc); // TODO add support for none string content
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return str;
	}

	public abstract void _call(PageContext parent, PageContext pc) throws PageException;
	//public abstract void afterCleanup(PageContext parent, ByteArrayOutputStream baos);
}
