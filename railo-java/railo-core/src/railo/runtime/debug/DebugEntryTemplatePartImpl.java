package railo.runtime.debug;

import railo.runtime.PageSource;

public class DebugEntryTemplatePartImpl extends DebugEntrySupport implements DebugEntryTemplatePart {

	private int startPos;
	private int endPos;

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

}
