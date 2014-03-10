package railo.commons.lang;

import static railo.commons.io.SystemUtil.ERR;
import static railo.commons.io.SystemUtil.OUT;

import java.io.PrintWriter;
import java.util.Date;

import railo.commons.io.SystemUtil;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;

public final class SystemOut {

    /**
     * logs a value 
     * @param value
     */
    public static void printDate(PrintWriter pw,String value) {
    	long millis=System.currentTimeMillis();
    	pw.write(
    			new Date(millis)
    			+"-"
    			+(millis-(millis/1000*1000))
    			+" "+value+"\n");
    	pw.flush();
    }
    /**
     * logs a value 
     * @param value
     */
    public static void print(PrintWriter pw,String value) {
    	pw.write(value+"\n");
    	pw.flush();
    } 


	public static void printStack(PrintWriter pw) {
		new Throwable().printStackTrace(pw);
	}

	public static void printStack(int type) {
		Config config=ThreadLocalPageContext.getConfig();
    	if(config!=null) {
    		if(type==ERR)
    			printStack(config.getErrWriter());
    		else 
    			printStack(config.getOutWriter());
    	}
    	else {
    		printStack(new PrintWriter((type==ERR)?System.err:System.out));
    	}
	}
    
    /**
     * logs a value 
     * @param value
     */
    public static void printDate(String value) {
    	printDate(value,OUT);
    }
    
    public static void printDate(String value,int type) {
    	printDate(getPrinWriter(type),value);
    }
    

    public static PrintWriter getPrinWriter(int type) {
    	Config config=ThreadLocalPageContext.getConfig();
    	if(config!=null) {
    		if(type==ERR) return config.getErrWriter();
    		return config.getOutWriter();
    	}
    	return SystemUtil.getPrintWriter(type);
    }
    
    
    
    /**
     * logs a value 
     * @param value
     */
    
    public static void print(String value) {
    	print(value, OUT);
    }
    
    public static void print(String value,int type) {
    	PageContext pc=ThreadLocalPageContext.get();
    	if(pc!=null) {
    		if(type==ERR)
    			print(pc.getConfig().getErrWriter(),value);
    		else 
    			print(pc.getConfig().getOutWriter(),value);
    	}
    	else {
    		print(new PrintWriter((type==ERR)?System.err:System.out),value);
    	}
    }

}
