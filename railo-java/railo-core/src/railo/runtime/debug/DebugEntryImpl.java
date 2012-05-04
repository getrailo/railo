package railo.runtime.debug;

import railo.runtime.PageSource;
import railo.runtime.exp.DeprecatedException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;


/**
 * a single debug entry
 */
public final class DebugEntryImpl implements DebugEntry {
	
	private static final long serialVersionUID = 809949164432900481L;
	
	private String path;
	//private long start;
	private int fileLoadTime;
	private int exeTime;
	
	//boolean isRunning;
    private String key;
    private int count=1;
    private int queryTime;
    private static int _id=1;
    private String id;
    
    private int max;
    private int min=0;

	/**
	 * constructor of the class
	 * @param source 
	 * @param key 
	 */
    protected DebugEntryImpl(PageSource source, String key) {
		this.path=source==null?"":source.getDisplayPath();
		this.key=key;
		id=Caster.toString(++_id);
	}
    
    @Override
	public int getExeTime() {
		return positiv(exeTime);
	}
    
    @Override
	public void updateExeTime(int exeTime) {
		if(exeTime>=0) {
            if(count==1 || min>exeTime)min=exeTime;
            if(max<exeTime)max=exeTime;
            
            this.exeTime += exeTime;
        }
	}
    
    @Override
	public int getFileLoadTime() {
        return positiv(fileLoadTime);
	}
	
    private int positiv(int time) {
        if(time<0)return 0;
        return time;
    }

    @Override
	public void updateFileLoadTime(int fileLoadTime) {
		if(fileLoadTime>0)this.fileLoadTime+= fileLoadTime;
	}
    
    @Override
	public void updateQueryTime(int queryTime) {
        if(queryTime>0)this.queryTime+=queryTime;
    }
    
    @Override
	public String getSrc() {
        return getSrc(path,key);//source.getDisplayPath()+(key==null?"":"$"+key);
    }
    
    @Override
	public String getPath() {
        return path;
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
    
    /**
     * increment the inner counter
     */
    protected void countPP() {
        count++;
        
    }
    
    @Override
	public int getCount() {
        return count;
    }
    
    @Override
	public int getQueryTime() {
        return positiv(queryTime);
    }
    
    @Override
	public int getMax() {
        return positiv(max);
    }
    
    @Override
	public int getMin() {
        return positiv(min);
    }
    
    @Override
	public void resetQueryTime() {
        this.queryTime=0;
    }

    @Override
	public String getId() {
		return id;
	}
}