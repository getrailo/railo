package railo.commons.cli;

import java.io.IOException;
import java.io.InputStream;

import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.lang.StringUtil;

public class Command {
    
    public static String execute(String cmdline) throws IOException, InterruptedException {
        return _execute(Runtime.getRuntime().exec(cmdline));
    }

    public static String execute(String[] cmdline) throws IOException, InterruptedException {
        return _execute(Runtime.getRuntime().exec(cmdline));
    }
    public static String execute(String cmd,String[] args) throws IOException, InterruptedException {
    	return execute(StringUtil.merge(cmd,args));
    }

    private static String _execute(Process p) throws IOException, InterruptedException {
    	InputStream is=null;
    	InputStream es=null;
    	try {
    		
    		String err = IOUtil.toString(es=p.getErrorStream(),SystemUtil.getCharset());
    		String in = IOUtil.toString(is=p.getInputStream(),SystemUtil.getCharset());
	        if(p.waitFor()!=0 && !StringUtil.isEmpty(err))throw new CommandException(err); // DIFF 23
        	return in;
    	}
    	finally {
    		IOUtil.closeEL(is);
    		IOUtil.closeEL(es);
    	}
    }
}