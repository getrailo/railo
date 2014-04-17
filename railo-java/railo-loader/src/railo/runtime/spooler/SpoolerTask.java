package railo.runtime.spooler;

import java.io.Serializable;

import railo.runtime.config.Config;
import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.Struct;


// FUTURE extend interface task
public interface SpoolerTask extends Serializable {
	
	public String getId();
	
	public void setId(String id);
	
	/**
	 * execute Task
	 * @param config 
	 * @throws SpoolerException
	 */
	public Object execute(Config config) throws PageException; 

	/**
	 * returns a short info to the task
	 * @return Task subject
	 */
	public String subject();
	
	/**
	 * returns task type as String
	 * @return Task subject
	 */
	public String getType();
	
	/**
	 * returns advanced info to the task
	 * @return Task detail
	 */
	public Struct detail();

	/**
	 * return last execution of this task
	 * @return last execution
	 */
	public long lastExecution();

	public void setNextExecution(long nextExecution);

	public long nextExecution();
	
	/**
	 * returns how many tries to send are already done
	 * @return tries
	 */
	public int tries();

	/**
	 * @return the exceptions
	 */
	public Array getExceptions();

	public void setClosed(boolean closed);
	
	public boolean closed();


	/**
	 * @return the plans
	 */
	public ExecutionPlan[] getPlans();


	/**
	 * @return the creation
	 */
	public long getCreation();


	public void setLastExecution(long lastExecution);

}
