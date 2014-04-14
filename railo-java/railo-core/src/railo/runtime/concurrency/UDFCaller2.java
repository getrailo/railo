package railo.runtime.concurrency;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

import railo.commons.io.IOUtil;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.net.http.HttpServletResponseDummy;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.thread.ThreadUtil;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;

public class UDFCaller2<P> implements Callable<Data<P>> { 

	private PageContext parent;
	private PageContextImpl pc;
	private ByteArrayOutputStream baos;
	
	private UDF udf;
	private boolean doIncludePath;
	private Object[] arguments;
	private Struct namedArguments;
	private P passed;


	private UDFCaller2(PageContext parent) {
		this.parent = parent;
		this.baos = new ByteArrayOutputStream();
		this.pc=ThreadUtil.clonePageContext(parent, baos, false, false, true);
	}
	
	public UDFCaller2(PageContext parent, UDF udf, Object[] arguments,P passed, boolean doIncludePath) {
		this(parent);
		this.udf=udf;
		this.arguments=arguments;
		this.doIncludePath=doIncludePath;
		this.passed=passed;
	}
	public UDFCaller2(PageContext parent, UDF udf,Struct namedArguments, P passed,boolean doIncludePath) {
		this(parent);
		this.udf=udf;
		this.namedArguments=namedArguments;
		this.doIncludePath=doIncludePath;
		this.passed=passed;
	}
	
	
	
	public final Data<P> call() throws PageException {
		ThreadLocalPageContext.register(pc);
		pc.getRootOut().setAllowCompression(false); // make sure content is not compressed
		String str=null;
		Object result=null;
		try{
			if(namedArguments!=null) result=udf.callWithNamedValues(pc, namedArguments, doIncludePath);
			else result=udf.call(pc, arguments, doIncludePath);
			
		} 
		finally{
			try {
			HttpServletResponseDummy rsp=(HttpServletResponseDummy) pc.getHttpServletResponse();
			
			Charset cs = ReqRspUtil.getCharacterEncoding(pc,rsp);
			//if(enc==null) enc="ISO-8859-1";
			
			pc.getOut().flush(); //make sure content is flushed
			
			pc.getConfig().getFactory().releasePageContext(pc);
				str=IOUtil.toString((new ByteArrayInputStream(baos.toByteArray())), cs); // TODO add support for none string content
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new Data<P>(str,result,passed);
	}
}
