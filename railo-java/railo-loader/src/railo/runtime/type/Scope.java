package railo.runtime.type;

import railo.runtime.PageContext;

/**
 * abstract class for all cold fusion scopes 
 */
public interface Scope extends Struct {
	
	/**
	 * Scope Undefined
	 */
	public static final int SCOPE_UNDEFINED=0;
	/**
	 * Scope Variables
	 */
	public static final int SCOPE_VARIABLES=1;
	/**
	 * Scope Request
	 */
	public static final int SCOPE_REQUEST=2;
	/**
	 * Scope URL
	 */
	public static final int SCOPE_URL=3;
	/**
	 * Scope Form
	 */
	public static final int SCOPE_FORM=4;
	/**
	 * Scope Client
	 */
	public static final int SCOPE_CLIENT=5;
	/**
	 * Scope Cookie
	 */
	public static final int SCOPE_COOKIE=6;
	/**
	 * Scope Session
	 */
	public static final int SCOPE_SESSION=7;
	/**
	 * Scope Application
	 */
	public static final int SCOPE_APPLICATION=8;
	/**
	 * Scope Arguments
	 */
	public static final int SCOPE_ARGUMENTS=9;
	/**
	 * Scope CGI
	 */
	public static final int SCOPE_CGI=10;	
	/**
	 * Scope Server
	 */
	public static final int SCOPE_SERVER=11;
    
    /**
     * Scope Local
     */
    public static final int SCOPE_LOCAL=12;
    
    /**
     * Scope Caller
     */
    public static final int SCOPE_CALLER=13;

    public static final int SCOPE_CLUSTER=14;

    public static final int SCOPE_VAR=15;
    
    public static final int SCOPE_COUNT=16;
    
    
    
    
	/**
	 * return if the scope is Initialiesd
	 * @return scope is init
	 */
	public boolean isInitalized(); 
	
	/**
	 * Initalize Scope
	 * @param pc Page Context
	 */
	public void initialize(PageContext pc);
	
	/**
	 * release scope for reuse
	 * @deprecated use instead <code>release(PageContext pc);</code>
	 */
	public void release();
	
	/**
	 * release scope for reuse
	 */
	public void release(PageContext pc);
	
    /** 
     * @return return the scope type (SCOPE_SERVER, SCOPE_SESSION usw.) 
     */ 
    public int getType(); 
    
    /** 
     * @return return the scope type as a String (server,session usw.) 
     */ 
    public String getTypeAsString(); 

}