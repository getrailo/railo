package railo.runtime.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.debug.DebugEntry;
import railo.runtime.debug.Debugger;

public class DebugExecutionLog extends ExecutionLogSupport {
	
	private PageContext pc;
	private ArrayList<Resource> pathes=new ArrayList<Resource>();
	
	private Map<String,Entry> entries=Collections.synchronizedMap(new HashMap<String,Entry>());
	
	
	protected void _init(PageContext pc, Map<String, String> arguments) {
		this.pc=pc;
	}
	
	@Override
	protected void _log(int startPos, int endPos, long startTime, long endTime) {
		if(!pc.getConfig().debug()) return;
		long diff=endTime-startTime;
		if(unit==UNIT_MICRO)diff/=1000;
		else if(unit==UNIT_MILLI)diff/=1000000;
		Resource res = pc.getCurrentPageSource().getResource();
		int index = path(res);
		String key=index+":"+startPos+":"+endPos;
		Entry entry=entries.get(key);
		if(entry==null) entries.put(key, new Entry(res,startPos,endPos,diff));
		else entry.add(diff);
		
		PageSource ps = pc.getCurrentPageSource();
		
		Debugger debugger = pc.getDebugger();
		DebugEntry e = debugger.getEntry(pc, ps, startPos,endPos);
		e.updateExeTime((int)diff);
	}
	

	private int path(Resource res) {
		int index= pathes.indexOf(res);
		if(index==-1){
			pathes.add(res);
			return pathes.size()-1;
		}
		return index;
	}

	@Override
	protected void _release() {
		
	}

	
	
	
	class Entry{

		private final Resource res;
		private final int startPos;
		private final int endPos;
		private long time;
		private int count=1;

		public Entry(Resource res, int startPos, int endPos, long time) { 
			this.res=res;
			this.startPos=startPos;
			this.endPos=endPos;
			this.time=time;
		}

		public void add(long time) { 
			this.time+=time;
			this.count++;
		}
		
		public String toString(){
			return "res:"+res+";time:"+time+";count:"+count+";start-pos:"+startPos+";end-pos:"+endPos;
		}
		
	}
}
