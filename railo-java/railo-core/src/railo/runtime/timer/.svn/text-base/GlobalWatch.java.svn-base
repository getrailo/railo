package railo.runtime.timer;


/**
 * a global stop watch, only one for process
 */
public final class GlobalWatch extends Stopwatch {
	
	private static GlobalWatch gw;
	
	private GlobalWatch(){}
	
	/**
	 * @return returns allways the same Instance of the GlobalWatch (Singelton)
	 */
	public static GlobalWatch newInstance() {
		if(gw==null)gw=new GlobalWatch();
		return gw;
	}

}