

package coldfusion.server;

public interface SchedulerService extends Service {

	public abstract void schedule(Runnable arg0, long arg1);

	public abstract void scheduleForShutDown(Runnable arg0);

	public abstract void cancel(Runnable arg0);

}