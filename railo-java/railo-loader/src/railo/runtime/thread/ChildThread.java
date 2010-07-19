package railo.runtime.thread;


public abstract class ChildThread extends Thread {
	
	private static ThreadGroup group=new ThreadGroup("cfthread");
	private static int count=0;
	
	public abstract String getTagName();

	//public PageContext getParent();

	public abstract long getStartTime();

	/**
	 * this method is invoked when thread is terminated by user interaction
	 */
	public abstract void terminated();
	
	public ChildThread(){
		super(group,null,"cfthread-"+(count<0?count=0:count++));
	}
}
