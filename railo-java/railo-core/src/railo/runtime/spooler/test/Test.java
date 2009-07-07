package railo.runtime.spooler.test;

import java.io.IOException;

import railo.commons.io.SystemUtil;
import railo.commons.io.log.LogAndSource;
import railo.commons.io.log.LogConsole;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.ResourcesImpl;
import railo.runtime.spooler.ExecutionPlan;
import railo.runtime.spooler.ExecutionPlanImpl;
import railo.runtime.spooler.SpoolerEngine;
import railo.runtime.spooler.SpoolerEngineImpl;

public class Test extends Thread {
	
	private SpoolerEngine engine;

	public Test(SpoolerEngine engine) {
		this.engine=engine;
	}
	
	private static ExecutionPlan[] plans=new ExecutionPlan[]{
			new ExecutionPlanImpl(2,5),
			new ExecutionPlanImpl(2,10)
	};

	public static void main(String[] args) throws IOException {
		
		
		ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
		SpoolerEngine engine=new SpoolerEngineImpl(null,frp.getResource("/Users/mic/temp/spooler/"),"test",LogConsole.getInstance(LogAndSource.LEVEL_INFO));
		
		TestTask task1=new TestTask(plans,"Task "+System.currentTimeMillis(),1);

		engine.add(task1);
		
		
		Test test=new Test(engine);
		test.start();
		//SpoolerTask[] tasks=engine.getOpenTasks();
		//SpoolerEngine.list(tasks);
	}
	
	public void run() {
		while(true) {
			SystemUtil.sleep(1000);
			TestTask task=new TestTask(plans,"Task "+(System.currentTimeMillis()+1),3);

			engine.add(task);
			break;
		}
	}
}
