package railo.runtime.debug;

/**
 * Debug information just for a part of the templates
 *
 */
public interface DebugEntryTemplatePart extends DebugEntry {
	/**
	 * start position (0 offset) on this Entry
	 * @return
	 */
	public int getStartPosition();
	
	/**
	 * end position (0 offset) on this Entry
	 * @return
	 */
	public int getEndPosition();

    /*/ FUTURE
    public int getStartLine();
    public int getEndLine();
    //*/
}
