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

		String keyRes = res.getAbsolutePath() + "@" + res.lastModified();
        String keySnp = ps.getDisplayPath()   + "@" + ps.getLastAccessTime() + ":" + startPos + "-" + endPos;

        DebugEntry de = pc.getDebugger().getEntry(pc, ps, startPos, endPos);
        de.updateExeTime((int) diff);

        ResourceSnippet snippet = snippets.get( keySnp );

        if ( snippet == null ) {

            String src = sources.get( keyRes );

            if ( src == null ) {
                src = ResourceSnippet.getContents( res, pc.getConfig().getResourceCharset() );
                sources.put( keyRes, src );
            }

            snippet = ResourceSnippet.createResourceSnippet( src, startPos, endPos );
            snippets.put( keySnp, snippet );

            if ( de instanceof DebugEntryTemplatePartImpl ) {
                ( (DebugEntryTemplatePartImpl)de).setStartLine( snippet.getStartLine() );
                ( (DebugEntryTemplatePartImpl)de).setEndLine( snippet.getEndLine() );
                ( (DebugEntryTemplatePartImpl)de).setSnippet( snippet.getContent() );
            }
        }
	}


	@Override
	protected void _release() {

	}

}
