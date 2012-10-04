package railo.runtime.debug;

import railo.runtime.PageSource;


/**
 * a single debug entry
 */
public final class DebugEntryTemplateImpl extends DebugEntrySupport implements DebugEntryTemplate {
	
	private static final long serialVersionUID = 809949164432900481L;
	
	private long fileLoadTime;
	private String key;
    private long queryTime;

	/**
	 * constructor of the class
	 * @param source 
	 * @param key 
	 */
    protected DebugEntryTemplateImpl(PageSource source, String key) {
    	super(source);
		this.key=key;
	}

    
    @Override
	public long getFileLoadTime() {
        return positiv(fileLoadTime);
	}

    @Override
	public void updateFileLoadTime(long fileLoadTime) {
		if(fileLoadTime>0)this.fileLoadTime+= fileLoadTime;
	}
    
    @Override
	public void updateQueryTime(long queryTime) {
        if(queryTime>0)this.queryTime+=queryTime;
    }
    
    @Override
	public String getSrc() {
        return getSrc(getPath(),key);
    }
    
    /**
     * @param source 
     * @param key 
     * @return Returns the src.
     */
    static String getSrc(String path, String key) {
        return 
        	path
            +
            (key==null?"":"$"+key);
    }
    
    @Override
	public long getQueryTime() {
        return positiv(queryTime);
    }
    
    @Override
	public void resetQueryTime() {
        this.queryTime=0;
    }
}