package railo.runtime.spooler;

import railo.commons.lang.ExceptionUtil;
import railo.runtime.config.Config;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;



public abstract class SpoolerTaskSupport implements SpoolerTask {

	private long creation;
	private long lastExecution;
	private int tries=0;
	private long nextExecution;
	private Array exceptions=new ArrayImpl();
	private boolean closed;
	private String id;
	private ExecutionPlan[] plans;
	
	/**
	 * Constructor of the class
	 * @param plans
	 * @param timeOffset offset from the local time to the config time
	 */
	public SpoolerTaskSupport(ExecutionPlan[] plans) {
		this.plans=plans;
		creation=System.currentTimeMillis();
	}

	
	public final String getId() {
		return id;
	}
	public final void setId(String id) {
		this.id= id;
	}

	/**
	 * return last execution of this task
	 * @return last execution
	 */
	public final long lastExecution() {
		return lastExecution;
	}

	public final void setNextExecution(long nextExecution) {
		this.nextExecution=nextExecution;
	}

	public final long nextExecution() {
		return nextExecution;
	}

	/**
	 * returns how many tries to send are already done
	 * @return tries
	 */
	public final int tries() {
		return tries;
	}

	final void _execute(Config config) throws PageException {

		lastExecution=System.currentTimeMillis();
		tries++;
		try {
			execute(config);
		}
		catch(Throwable t) {
			PageException pe = Caster.toPageException(t);
			String st = ExceptionUtil.getStacktrace(t,true);
			//config.getErrWriter().write(st+"\n");
			
			Struct sct=new StructImpl();
			sct.setEL("message", pe.getMessage());
			sct.setEL("detail", pe.getDetail());
			sct.setEL("stacktrace", st);
			sct.setEL("time", Caster.toLong(System.currentTimeMillis()));
			exceptions.appendEL(sct);
			
			throw pe;
		}
		finally {
			lastExecution=System.currentTimeMillis();
		}
	}

	/**
	 * @return the exceptions
	 */
	public final Array getExceptions() {
		return exceptions;
	}

	public final void setClosed(boolean closed) {
		this.closed=closed;
	}
	
	public final boolean closed() {
		return closed;
	}


	/**
	 * @return the plans
	 */
	public ExecutionPlan[] getPlans() {
		return plans;
	}


	/**
	 * @return the creation
	 */
	public long getCreation() {
		return creation;
	}


	public void setLastExecution(long lastExecution) {
		this.lastExecution=lastExecution;
	}

}
