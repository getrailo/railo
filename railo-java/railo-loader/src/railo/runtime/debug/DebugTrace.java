package railo.runtime.debug;

import java.io.Serializable;

public interface DebugTrace extends Serializable {
	
    public static final int TYPE_INFO=0;
    public static final int TYPE_DEBUG=1;
    public static final int TYPE_WARN=2;
    public static final int TYPE_ERROR=3;
    public static final int TYPE_FATAL=4;
    public static final int TYPE_TRACE=5;
    
	/**
	 * @return the category
	 */
	public String getCategory();

	/**
	 * @return the line
	 */
	public int getLine();

	/**
	 * @return the template
	 */
	public String getTemplate();

	/**
	 * @return the text
	 */
	public String getText();

	/**
	 * @return the time
	 */
	public long getTime();

	/**
	 * @return the type
	 */
	public int getType();

	/**
	 * @return the var
	 */
	public String getVarName();
	/**
	 * @return the var
	 */
	public String getVarValue();
	
	public String getAction();
	
}
