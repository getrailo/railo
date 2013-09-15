package railo.runtime.debug;

import railo.runtime.PageSource;

public class DebugEntryTemplatePartImpl extends DebugEntrySupport implements DebugEntryTemplatePart {

	private int startPos, startLine;
	private int endPos, endLine;
	private String snippet = "";

	protected DebugEntryTemplatePartImpl(PageSource source,int startPos, int endPos) {
		super(source);
		this.startPos=startPos;
		this.endPos=endPos;
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

    public void setStartLine(int value) {
        startLine = value;
    }

    public void setEndLine(int value) {
        endLine = value;
    }

    public void setSnippet(String value) {
        snippet = value;
    }
}
