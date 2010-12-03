package railo.intergral.fusiondebug.server;

import java.util.ArrayList;
import java.util.List;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.NativeException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.TemplateException;
import railo.runtime.op.Caster;
import railo.transformer.bytecode.util.ASMUtil;

import com.intergral.fusiondebug.server.FDSignalException;

public class FDSignal {
	private static ThreadLocal hash=new ThreadLocal();

	public static void signal(PageException pe, boolean caught) {
		try {
			String id = pe.hashCode()+":"+caught;
			if(Caster.toString(hash.get(),"").equals(id)) return;
			
			List stack = createExceptionStack(pe);
			if(stack.size()>0){
				FDSignalException se = new FDSignalException(); 
				se.setExceptionStack(stack);
				se.setRuntimeExceptionCaughtStatus(caught);
				se.setRuntimeExceptionExpression(createRuntimeExceptionExpression(pe));
				if(pe instanceof NativeException) se.setRuntimeExceptionType("native");
				else se.setRuntimeExceptionType(pe.getTypeAsString());
				se.setStackTrace(pe.getStackTrace());
				hash.set(id);
				throw se;
			}
			
		}
		catch( FDSignalException fdse){
			// do nothing - will be processed by JDI and handled by FD
		}
	}
	
	public static String createRuntimeExceptionExpression(PageException pe){
		if(!StringUtil.isEmpty(pe.getDetail()))
			return pe.getMessage()+" "+pe.getDetail();
		return pe.getMessage();
	}
	
	public static List createExceptionStack(PageException pe) {
		StackTraceElement[] traces = pe.getStackTrace();
		PageContextImpl pc = (PageContextImpl) ThreadLocalPageContext.get();
		String template="";
		StackTraceElement trace=null;
		List list=new ArrayList();
		Resource res;
		PageSource ps;
		FDStackFrameImpl frame;
		
		
		for(int i=traces.length-1;i>=0;i--) {
			trace=traces[i];
			ps=null;
			if(trace.getLineNumber()<=0) continue;
			template=trace.getFileName();
			if(template==null || ResourceUtil.getExtension(template,"").equals("java")) continue;
			
			res = ResourceUtil.toResourceNotExisting(pc, template);
			ps = pc.toPageSource(res, null);
			
			frame = new FDStackFrameImpl(null,pc,trace,ps);
			if(ASMUtil.isOverfowMethod(trace.getMethodName())) list.set(0,frame);
			else list.add(0,frame);
			
		}
		if(pe instanceof TemplateException){
			TemplateException te = (TemplateException) pe;
			if(te.getPageSource()!=null)
				list.add(0,new FDStackFrameImpl(null,pc,te.getPageSource(),te.getLine()));
		}
		
		return list;
	}
}
