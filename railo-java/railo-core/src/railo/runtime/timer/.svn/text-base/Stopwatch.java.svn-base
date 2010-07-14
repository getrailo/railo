package railo.runtime.timer;

/**
 * Implementation of a simple Stopwatch
 */
public class Stopwatch {
	
	private long start;
	private int count=0;
	private long total=0;
	boolean isRunning;
	
	/**
	 * start the watch
	 */
	public void start() {
		isRunning=true;
		start=System.currentTimeMillis();
	}
	
	/**
	 * stops the watch
	 * @return returns the current time or 0 if watch not was running
	 */
	public long stop() {
		if(isRunning) {
			long time=System.currentTimeMillis()-start;
			total+=time;
			count++;
			isRunning=false;
			return time;
			
		}
		return 0;
	}
	
	/**
	 * @return returns the current time or 0 if watch is not running
	 */
	public long time() {
		if(isRunning)return System.currentTimeMillis()-start;
		return 0;
	}
	
	/**
	 * @return returns the total elapsed time
	 */
	public long totalTime() {
		return total+time();
	}
	
	/**
	 * @return returns how many start and stop was making
	 */
	public int count() {
		return count;
	}
	/**
	 * resets the stopwatch
	 */
	public void reset() {
		start=0;
		count=0;
		total=0;
		isRunning=false;
	}
}