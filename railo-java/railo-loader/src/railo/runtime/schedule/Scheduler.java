package railo.runtime.schedule;

import java.io.IOException;

/**
 * Scheduler interface
 */
public interface Scheduler {

    /** 
     * returns a schedule tasks matching given name or throws a exception
     * @param name Task name of Schedule Task to get
     * @return matching task
     * @throws ScheduleException
     */
    public abstract ScheduleTask getScheduleTask(String name)
            throws ScheduleException;

    /** 
     * returns all schedule tasks valid or not
     * @return all tasks
     */
    public abstract ScheduleTask[] getAllScheduleTasks();

    /** 
     * returns a schedule tasks matching given name or null
     * @param name Task name of Schedule Task to get
     * @param defaultValue 
     * @return matching task
     */
    public abstract ScheduleTask getScheduleTask(String name,
            ScheduleTask defaultValue);

    /**
     * Adds a Task to the scheduler
     * @param task
     * @param allowOverwrite
     * @throws ScheduleException
     * @throws IOException
     */
    public abstract void addScheduleTask(ScheduleTask task,
            boolean allowOverwrite) throws ScheduleException, IOException;
    
    /**
     * pause the scheduler task
     * @param name
     * @param pause
     * @param throwWhenNotExist
     * @throws ScheduleException
     * @throws IOException
     */
	public void pauseScheduleTask(String name, 
			boolean pause, boolean throwWhenNotExist) throws ScheduleException, IOException;

    /**
     * removes a task from scheduler
     * @param name name of the task to remove
     * @param throwWhenNotExist define if method throws a exception if task doesn't exist
     * @throws IOException
     * @throws ScheduleException
     */
    public abstract void removeScheduleTask(String name,
            boolean throwWhenNotExist) throws IOException, ScheduleException;

    /**
     * runs a scheduler task
     * @param name
     * @param throwWhenNotExist
     * @throws IOException
     * @throws ScheduleException
     */
    public abstract void runScheduleTask(String name, boolean throwWhenNotExist)
            throws IOException, ScheduleException;
}