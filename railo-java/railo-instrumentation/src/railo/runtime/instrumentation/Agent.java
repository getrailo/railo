package railo.runtime.instrumentation;

import java.lang.instrument.Instrumentation;


public class Agent {
	  private static Instrumentation instrumentation;
	  
	  public static void premain(String agentArgs, Instrumentation inst) {
		  //System.out.println("Agent-premain:"+agentArgs);
		  if(inst!=null)instrumentation = inst;
	  }
	  
	  public static void agentmain(String agentArgs, Instrumentation inst) {
		  //System.out.println("Agent-agentmain:"+agentArgs);
		  if(inst!=null)instrumentation = inst;
	  }

	  public static Instrumentation getInstrumentation() {
		
		  return instrumentation;
	  }
	}
