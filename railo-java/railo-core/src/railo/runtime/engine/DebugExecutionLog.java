package railo.runtime.engine;

import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.debug.DebugEntry;

public class DebugExecutionLog extends ExecutionLogSupport {
	
	private PageContext pc;


	protected void _init(PageContext pc, Map<String, String> arguments) {
		this.pc=pc;
	}
	
	@Override
	protected void _log(int startPos, int endPos, long startTime, long endTime) {

		if(!pc.getConfig().debug()) return;

		long diff=endTime-startTime;
		if(unit==UNIT_MICRO)diff/=1000;
		else if(unit==UNIT_MILLI)diff/=1000000;

		DebugEntry de = pc.getDebugger().getEntry(pc, pc.getCurrentPageSource(), startPos, endPos);
		de.updateExeTime((int) diff);
	}


	@Override
	protected void _release() {
	}

}
