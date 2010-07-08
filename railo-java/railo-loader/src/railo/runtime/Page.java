package railo.runtime;

import java.io.Serializable;

import railo.runtime.type.UDF;

/**
 * abstract Method for all generated Page Object
 */
public abstract class Page implements Serializable{
	
    /**
     * Field <code>FALSE</code>
     */
    public static boolean FALSE=false;
    
    /**
     * Field <code>TRUE</code>
     */
    public static boolean TRUE=true;
	private PageSource pageSource;
    private byte loadType;
	
    
    
    
	/**
	 * return version definition of the page
	 * @return version
	 */
	public int getVersion() {return -1;}
	
	/**
	 * method to invoke a page
	 * @param pc PageContext 
	 * @throws Throwable
	 */
	public abstract void call(PageContext pc) throws Throwable;

	
	/**
     * return when the source file last time was modified
	 * @return last modification of source file
	 */
	public long getSourceLastModified() {return 0;}
	
	/**
	 * return the time when the file was compiled
	 */
	public long getCompileTime() {return 0;}

	/**
	 * @param pageSource
	 */
	public void setPageSource(PageSource pageSource) {
		this.pageSource=pageSource;
	}
	/**
	 * @return Returns the pageResource.
	 */
	public PageSource getPageSource() {
		return pageSource;
	}

    /**
     * @return gets the load type
     */
    public byte getLoadType() {
        return loadType;
    }
    
    /**
     * @param loadType sets the load type
     */
    public void setLoadType(byte loadType) {
        this.loadType = loadType;
    }

    public Object udfCall(PageContext pageContext, UDF udf,int functionIndex) throws Throwable {
    	return null;
    }
    
    public void threadCall(PageContext pageContext, int threadIndex) throws Throwable {
    }

	public Object udfDefaultValue(PageContext pc, int functionIndex, int argumentIndex) {
		return null;
	}

}