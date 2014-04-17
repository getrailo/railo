package railo.runtime.instrumentation;

import java.lang.instrument.Instrumentation;

import sun.instrument.InstrumentationImpl;

public class ExternalAgent {
	  
	  public static void premain(String agentArgs, Instrumentation inst) {
		  if(inst!=null)Agent.setInstrumentation(inst);
		  //System.out.println("Agent-premain:"+agentArgs);
	  }
	  
	  public static void agentmain(String agentArgs, Instrumentation inst) {
		  if(inst!=null)Agent.setInstrumentation(inst);
		  //System.out.println("Agent-agentmain:"+agentArgs);
	  }
}