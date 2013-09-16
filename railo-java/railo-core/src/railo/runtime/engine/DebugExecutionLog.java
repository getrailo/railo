package railo.runtime.engine;

import java.util.Map;

import railo.commons.io.res.util.ResourceSnippet;
import railo.commons.io.res.util.ResourceSnippetsMap;
import railo.runtime.PageContext;
import railo.runtime.debug.DebugEntry;
import railo.runtime.debug.DebugEntryTemplatePartImpl;

public class DebugExecutionLog extends ExecutionLogSupport {
	
	private PageContext pc;
	private ResourceSnippetsMap snippetsMap = new ResourceSnippetsMap( 1024, 128 );

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

		if ( de instanceof DebugEntryTemplatePartImpl ) {

			ResourceSnippet snippet = snippetsMap.getSnippet( pc.getCurrentPageSource(), startPos, endPos, pc.getConfig().getResourceCharset() );

			( (DebugEntryTemplatePartImpl)de).setStartLine( snippet.getStartLine() );
			( (DebugEntryTemplatePartImpl)de).setEndLine( snippet.getEndLine() );
			( (DebugEntryTemplatePartImpl)de).setSnippet( snippet.getContent() );
		}
	}


	@Override
	protected void _release() {
	}

}
