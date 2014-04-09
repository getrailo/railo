package railo;



/**
 * Info to this Version
 */
public interface Info {

	public static final int STATE_ALPHA = 2*100000000;
	public static final int STATE_BETA = 1*100000000;
	public static final int STATE_RC = 3*100000000;
	public static final int STATE_FINAL = 0;
	
	/**
	 * @return the level
	 */
	public String getLevel();

    /**
     * @return Returns the releaseTime.
     */
    public long getRealeaseTime();
    
    /**
     * @return Returns the version.
     */
    public String getVersionAsString();

    /**
     * @return Returns the intVersion.
     */
    public int getVersionAsInt();

    /**
     * @return returns the state
     */
    public int getStateAsInt();

    /**
     * @return returns the state
     */
    public String getStateAsString();

    

	public int getFullVersionInfo();

	public String getVersionName();
	
	public int getMajorVersion();
	
	public int getMinorVersion();
	
	public String getVersionNameExplanation();
}