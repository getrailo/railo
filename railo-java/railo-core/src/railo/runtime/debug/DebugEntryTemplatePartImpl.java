package railo.runtime.debug;

import railo.runtime.PageSource;

public class DebugEntryTemplatePartImpl extends DebugEntrySupport implements DebugEntryTemplatePart {

	private int startPos, startLine;
	private int endPos, endLine;
	private String snippet = "";

	protected DebugEntryTemplatePartImpl(PageSource source, int startPos, int endPos) {
		super(source);
		this.startPos=startPos;
		this.endPos=endPos;
	}

	protected DebugEntryTemplatePartImpl(PageSource source, int startPos, int endPos, int startLine, int endLine, String snippet) {
		super(source);
		this.startPos=startPos;
		this.endPos=endPos;
		this.startLine = startLine;
		this.endLine = endLine;
		this.snippet = snippet;
	}

	@Override
	public String getSrc() {
		return getSrc(getPath(),startPos,endPos);
	}

	@Override
	public int getStartPosition() {
		return startPos;
	}

	@Override
	public int getEndPosition() {
		return endPos;
	}
	
	static String getSrc(String path, int startPos, int endPos) {
        return path+":"+startPos+" - "+endPos;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public String getSnippet() {
        return snippet;
    }
}
