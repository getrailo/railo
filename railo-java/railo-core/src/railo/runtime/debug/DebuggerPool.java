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
	}

	public Array getData(PageContext pc) {
		
		Iterator<Struct> it = queue.iterator();
		Array arr=new ArrayImpl();
		while(it.hasNext()){
			arr.appendEL(it.next());
		}
		return arr;
	}

}