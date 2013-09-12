package railo.runtime.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceSnippet;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.debug.DebugEntry;
import railo.runtime.debug.DebugEntryTemplatePartImpl;

public class DebugExecutionLog extends ExecutionLogSupport {
	
	private PageContext pc;

    private Map<String, String> sources           = Collections.synchronizedMap(new HashMap());
    private Map<String, ResourceSnippet> snippets = Collections.synchronizedMap(new HashMap());

	protected void _init(PageContext pc, Map<String, String> arguments) {
		this.pc=pc;
	}
	
	@Override
	protected void _log(int startPos, int endPos, long startTime, long endTime) {
		if(!pc.getConfig().debug()) return;

		long diff=endTime-startTime;
		if(unit==UNIT_MICRO)diff/=1000;
		else if(unit==UNIT_MILLI)diff/=1000000;
        PageSource ps = pc.getCurrentPageSource();
        Resource res = ps.getResource();

        String key= ps.getDisplayPath() + ":" + startPos + ":" + endPos;

        DebugEntry e = pc.getDebugger().getEntry(pc, ps, startPos, endPos);
        e.updateExeTime((int)diff);

        ResourceSnippet snippet = snippets.get( key );

        if ( snippet == null ) {

            String src = sources.get( ps.getDisplayPath() );

            if ( src == null ) {
                src = ResourceSnippet.getContents( res, pc.getConfig().getResourceCharset() );
                sources.put( ps.getDisplayPath(), src );
            }

            snippet = ResourceSnippet.createResourceSnippet( src, startPos, endPos );
            snippets.put( key, snippet );

            if ( e instanceof DebugEntryTemplatePartImpl ) {
                ((DebugEntryTemplatePartImpl)e).setStartLine( snippet.getStartLine() );
                ((DebugEntryTemplatePartImpl)e).setEndLine( snippet.getEndLine() );
            }
        }
	}


	@Override
	protected void _release() {}

}
