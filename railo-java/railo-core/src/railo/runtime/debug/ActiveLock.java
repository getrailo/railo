package railo.runtime.debug;

public class ActiveLock {

	public final long startTime;
	public final short type;
	public final String name;
	public final int timeoutInMillis;

	public ActiveLock(short type, String name, int timeoutInMillis) {
		this.startTime=System.currentTimeMillis();
		this.type=type;
		this.name=name;
		this.timeoutInMillis=timeoutInMillis;
	}

}
