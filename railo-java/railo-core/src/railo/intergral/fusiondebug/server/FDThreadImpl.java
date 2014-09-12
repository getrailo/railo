package railo.intergral.fusiondebug.server;

import java.util.ArrayList;
import java.util.List;

import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.PageSourceImpl;
import railo.transformer.bytecode.util.ASMUtil;

import com.intergral.fusiondebug.server.IFDController;
import com.intergral.fusiondebug.server.IFDStackFrame;
import com.intergral.fusiondebug.server.IFDThread;

public class FDThreadImpl implements IFDThread {
	
	
	private PageContextImpl pc;
	private String name;
	private FDControllerImpl engine;
	//private CFMLFactoryImpl factory;

	
	public FDThreadImpl(FDControllerImpl engine,CFMLFactoryImpl factory, String name, PageContextImpl pc) {
		this.engine=engine;
		//this.factory=factory;
		this.name=name;
		this.pc=pc;
	}

	@Override
	public String getName() {
		return name+":"+pc.getCFID();
	}

	@Override
	public int id() {
		return pc.getId();
	}
	
	public static int id(PageContext pc) {
		return pc.getId();
	}

	@Override
	public void stop() {
		SystemUtil.stop(pc.getThread());
	}
	
	@Override
	public Thread getThread() {
		return pc.getThread();
	}

	@Override
	public String getOutputBuffer() {
		return pc.getRootOut().toString();
	}


	public List getStackFrames() {
		return getStack();
	}
	public List getStack() {
		List stack = pc.getPageSourceList();
		
		StackTraceElement[] traces = pc.getThread().getStackTrace();
		String template="";
		StackTraceElement trace=null;
		ArrayList list=new ArrayList();
		Resource res;
		PageSource ps;
		int index=stack.size();
		for(int i=traces.length-1;i>=0;i--) {
			trace=traces[i];
			ps=null;
			if(trace.getLineNumber()<=0) continue;
			template=trace.getFileName();
			if(template==null || ResourceUtil.getExtension(template,"").equals("java")) continue;
			
			if(index>0)ps=(PageSource) stack.get(--index);
			if(ps==null || !isEqual(ps,trace)){
				ps=toPageSource(pc,template);
			}
			FDStackFrameImpl frame = new FDStackFrameImpl(this,pc,trace,ps);
			if(ASMUtil.isOverfowMethod(trace.getMethodName())) list.set(0,frame);
			else list.add(0,frame);
		}
		return list;
	}
	
	public IFDStackFrame getTopStack(){
		return getTopStackFrame();
	}
	
	@Override
    public IFDStackFrame getTopStackFrame(){
		PageSource ps = pc.getCurrentPageSource();
		
		StackTraceElement[] traces = pc.getThread().getStackTrace();
		String template="";
		StackTraceElement trace=null;
		
		for(int i=0;i<traces.length;i++) {
			trace=traces[i];
			if(trace.getLineNumber()<=0) continue;
			template=trace.getFileName();
			if(template==null || ResourceUtil.getExtension(template,"").equals("java")) continue;
			
			if(ps==null || !isEqual(ps,trace)){
				ps=toPageSource(pc,template);
			}
			break;
		}
		return new FDStackFrameImpl(this,pc,trace,ps);	
	}
	
	private PageSource toPageSource(PageContextImpl pc2, String template) {
		Resource res = ResourceUtil.toResourceNotExisting(pc, template);
		return pc.toPageSource(res, null);
	}

	private boolean isEqual(PageSource ps, StackTraceElement trace) {
		// class name do not match
		if(!ps.getFullClassName().equals(trace.getClassName())) return false;
		// filename to not match
		if(!ps.getResource().getAbsolutePath().equals(trace.getFileName())) return false;
		
		return true;
	}

	/**
	 * @return the engine
	 */
	public IFDController getController() {
		return engine;
	}


}
