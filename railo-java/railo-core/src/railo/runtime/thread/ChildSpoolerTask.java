package railo.runtime.thread;

import railo.runtime.config.Config;
import railo.runtime.exp.PageException;
import railo.runtime.spooler.ExecutionPlan;
import railo.runtime.spooler.SpoolerTaskSupport;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class ChildSpoolerTask extends SpoolerTaskSupport {

	private ChildThreadImpl ct;

	public ChildSpoolerTask(ChildThreadImpl ct,ExecutionPlan[] plans) {
		super(plans);
		this.ct=ct;
	}

	@Override
	public Struct detail() {
		StructImpl detail = new StructImpl();
		detail.setEL("template", ct.getTemplate());
		return detail;
	}

	public Object execute(Config config) throws PageException {
		PageException pe = ct.execute(config);
		if(pe!=null) throw pe;
		return null;
	}

	public String getType() {
		return "cfthread";
	}

	public String subject() {
		return ct.getTagName();
	}

}
