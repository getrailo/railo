package railo.runtime.spooler;

import railo.runtime.exp.PageException;

public interface SpoolerEngine {
	
	/**
	 * return the label of the engine
	 * @return the label
	 */
	public String getLabel();
	
	/**
	 * adds a task to the engine
	 * @param task
	 */
	public void add(SpoolerTask task);

	/**
	 * remove that task from Spooler
	 * @param task
	 */
	public void remove(SpoolerTask task);

	/**
	 * remove a task that match given id
	 * @param id
	 */
	public void remove(String id);

	/**
	 * execute task by id and return error throwed by task
	 * @param id
	 * @throws SpoolerException
	 */
	public PageException execute(String id);
	
	/**
	 * execute task and return error throwed by task
	 * @param task
	 * @throws SpoolerException
	 */
	public PageException execute(SpoolerTask task);


	//public void setLabel(String label);
	//public void setPersisDirectory(Resource persisDirectory);
	//public void setLog(Log log);
	//public void setConfig(Config config);
}
