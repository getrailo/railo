package railo.runtime.debug;

import java.util.Iterator;
import java.util.LinkedList;

import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.PageException;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Struct;

public class DebuggerPool {

	//private Resource storage;
	private LinkedList<Struct> queue=new LinkedList<Struct>();
	//private List<Debugger> list=new ArrayList<Debugger>();
	
	public DebuggerPool(Resource storage) {
		//this.storage=storage;
	}
	
	public synchronized void store(PageContext pc,Debugger debugger) {
		if(ReqRspUtil.getScriptName(pc.getHttpServletRequest()).indexOf("/railo-context/")==0)return;
		try {
			queue.add((Struct) Duplicator.duplicate(debugger.getDebuggingData(pc, true),true));
		} catch (PageException e) {}
		
		while(queue.size()>((ConfigWebImpl)pc.getConfig()).getDebugMaxRecordsLogged())
			queue.poll();
		
		/*
		 store to file
		 
		OutputStream os = null;
	   	try {
	   		String str = pc.serialize(debugger.getDebuggingData(pc,true));
	   		Resource res = storage.getRealResource(IDGenerator.stringId()+".cfs");
	   		IOUtil.write(res, str.getBytes("UTF-8"));
			}
	   	catch (Exception e) {
				e.printStackTrace();
			}
	   	finally {
	   		IOUtil.closeEL(os);
	   	}*/
	}

	public Array getData(PageContext pc) {
		
		Iterator<Struct> it = queue.iterator();
		Array arr=new ArrayImpl();
		while(it.hasNext()){
			arr.appendEL(it.next());
		}
		return arr;
		
    	/*Resource[] children = storage.listResources(new ExtensionResourceFilter(".cfs"));
    	Array arr=new ArrayImpl();
    	String str;
		for(int i=0;i<children.length && i<10;i++){print.o(children[i].toString());
    		try {
				str=IOUtil.toString(children[i], "UTF-8");
				arr.appendEL(pc.evaluate(str));
			} 
    		catch (Exception e) {
				print.e(e);
			}
    	}
    	return arr;*/
	}

}