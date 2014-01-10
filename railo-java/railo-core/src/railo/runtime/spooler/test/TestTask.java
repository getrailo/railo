package railo.runtime.spooler.test;

import railo.runtime.config.Config;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.spooler.ExecutionPlan;
import railo.runtime.spooler.SpoolerTaskSupport;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class TestTask extends SpoolerTaskSupport {

	private int fail;
	private String label;

	public TestTask(ExecutionPlan[] plans,String label, int fail) {
		super(plans);
		this.label=label;
		this.fail=fail;
	}

	@Override
	public String getType() {
		return "test";
	}

	public Struct detail() {
		return new StructImpl();
	}

	public Object execute(Config config) throws PageException {
		//print.out("execute:"+label+":"+fail+":"+new Date());
		if(fail-->0)throw new ExpressionException("no idea");

		return null;
	}

	public String subject() {
		return label;
	}

}
