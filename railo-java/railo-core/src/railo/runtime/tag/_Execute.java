
package railo.runtime.tag;

import railo.commons.cli.Command;
import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.runtime.PageContext;

/**
 * 
 */
public final class _Execute extends Thread {

    private PageContext pc;
    private Resource outputfile;
    private String variable;
    private boolean aborted;
    private String command;
    //private static final int BLOCK_SIZE=4096;
    private Object monitor;
	private Exception exception;
	private String body;

    /**
     * @param pageContext
     * @param monitor
     * @param process
     * @param outputfile
     * @param variable
     * @param body 
     */
    public _Execute(PageContext pageContext, Object monitor, String command, Resource outputfile, String variable, String body) {
         this.pc=pageContext; 
         this.monitor=monitor;
         this.command=command;
         this.outputfile=outputfile;
         this.variable=variable;
         this.body=body;
    }
    
    /**
     * @see java.lang.Thread#run()
     */
    public void run() {
        try {
            _run();
        } catch (Exception e) {}
    }
     void _run() {
    	//synchronized(monitor){
			try {
				String rst=null;
				rst=Command.execute(command);
				if(!aborted) {
					if(outputfile==null && variable==null) pc.write(rst);
					else {
						if(outputfile!=null)	IOUtil.write(outputfile, rst, SystemUtil.getCharset(), false);
						if(variable!=null)	pc.setVariable(variable,rst);
					}
				}
			}
			catch(Exception ioe){
				//print.printST(ioe);
				exception=ioe;
			}
			finally {
				synchronized(monitor){
					monitor.notify();
				}
			}
		//}
    }

    /**
     * define that execution is aborted
     */
    public void abort() {
        aborted=true;
    }

	public boolean hasException() {
		return exception!=null;
	}

	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}

}