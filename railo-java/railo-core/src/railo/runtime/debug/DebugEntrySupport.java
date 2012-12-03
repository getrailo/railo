package railo.runtime.debug;

import railo.runtime.PageSource;
import railo.runtime.op.Caster;

public abstract class DebugEntrySupport implements DebugEntry {

	private static final long serialVersionUID = -2495816599745340388L;

	private static int _id=1;
    private String id;
    
	private long exeTime;
	private String path;
    private int count=1;
    private long min=0;
    private long max=0;
	

	/**
	 * constructor of the class
	 * @param source 
	 * @param key 
	 */
	protected DebugEntrySupport(PageSource source) {
		this.path=source==null?"":source.getDisplayPath();
		id=Caster.toString(++_id);
	}
	
    
    @Override
	public long getExeTime() {
		return positiv(exeTime);
	}
    
    @Override
	public void updateExeTime(long exeTime) {
		if(exeTime>=0) {
            if(count==1 || min>exeTime)min=exeTime;
            if(max<exeTime)max=exeTime;
            
            this.exeTime += exeTime;
        }
	}
    
    @Override
	public String getPath() {
        return path;
    }

    @Override
	public String getId() {
		return id;
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
	public long getMax() {
        return positiv(max);
    }
    
    @Override
	public long getMin() {
        return positiv(min);
    }
    
    protected long positiv(long time) {
        if(time<0)return 0;
        return time;
    }

}
