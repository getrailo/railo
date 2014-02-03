package railo.runtime.spooler;

import railo.runtime.config.Config;
import railo.runtime.exp.PageException;

public class TaskWrap implements Task {
	
	private SpoolerTask st;

	public TaskWrap(SpoolerTask st){
		this.st=st;
	}

	@Override
	public Object execute(Config config) throws PageException {
		return st.execute(config);
	}

}
