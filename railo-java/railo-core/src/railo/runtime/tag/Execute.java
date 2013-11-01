package railo.runtime.tag;

import java.util.Iterator;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.SerializableObject;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.SecurityException;
import railo.runtime.ext.tag.BodyTagImpl;
import railo.runtime.op.Caster;
import railo.runtime.security.SecurityManager;

/**
* Enables CFML developers to execute a process on a server computer.
*
*
*
**/
public final class Execute extends BodyTagImpl {

	/** Command-line arguments passed to the application. */
	private String arguments=null;

	/** Indicates how long, in seconds, the CFML executing thread waits for the spawned process. 
	** 		A timeout of 0 is equivalent to the non-blocking mode of executing. A very high timeout value is 
	** 		equivalent to a blocking mode of execution. The default is 0; therefore, the CFML thread spawns 
	** 		a process and returns without waiting for the process to terminate.If no output file is specified, 
	** 		and the timeout value is 0, the program output is discarded. */
	private long timeout;

	/** The full pathname of the application to execute.
	** 		Note: On Windows, you must specify the extension as part of the application's name. For example, 
	** 		myapp.exe, */
	private String name=null;

	/** The file to which to direct the output of the program. If not specified, the output is 
	** 		displayed on the page from which it was called. */
	private Resource outputfile;
	private Resource errorFile;

    private String variable;
    private String errorVariable;

	private String body;

	private boolean terminateOnTimeout=false;

	@Override
	public void release()	{
		super.release();
		arguments=null;
		timeout=0L;
		name=null;
		outputfile=null;
		errorFile=null;
		variable=null;
		errorVariable=null;
		body=null;
		terminateOnTimeout=false;
	}

	/** set the value arguments
	*  Command-line arguments passed to the application.
	* @param args value to set
	**/
	public void setArguments(Object args)	{
		
	    if(args instanceof railo.runtime.type.Collection) {
		    StringBuffer sb=new StringBuffer();
		    railo.runtime.type.Collection coll=(railo.runtime.type.Collection)args;
		    //railo.runtime.type.Collection.Key[] keys=coll.keys();
		    Iterator<Object> it = coll.valueIterator();
		    while(it.hasNext()) {
		        sb.append(' ');
		        sb.append(it.next());
		    }
		    arguments=args.toString();
		}
	    else if(args instanceof String) {
	        arguments=" "+args.toString();
	    }
	    else this.arguments="";
	}

	/** set the value timeout
	*  Indicates how long, in seconds, the CFML executing thread waits for the spawned process. 
	* 		A timeout of 0 is equivalent to the non-blocking mode of executing. A very high timeout value is 
	* 		equivalent to a blocking mode of execution. The default is 0; therefore, the CFML thread spawns 
	* 		a process and returns without waiting for the process to terminate.If no output file is specified, 
	* 		and the timeout value is 0, the program output is discarded.
	* @param timeout value to set
	 * @throws ApplicationException 
	**/
	public void setTimeout(double timeout) throws ApplicationException	{
		if(timeout<0) 
			throw new ApplicationException("value must be a positive number now ["+Caster.toString(timeout)+"]");
		this.timeout=(long)(timeout*1000L);
	}

	public void setTerminateontimeout(boolean terminateontimeout)	{
		this.terminateOnTimeout=terminateontimeout;
	}
	
	/** set the value name
	*  The full pathname of the application to execute.
	* 		Note: On Windows, you must specify the extension as part of the application's name. For example, 
	* 		myapp.exe,
	* @param name value to set
	**/
	public void setName(String name)	{
		this.name=name;
	}
	
	/**
	 * define name of variable where output is written to
	 * @param variable
	 * @throws PageException
	 */
	public void setVariable(String variable) throws PageException	{
		this.variable=variable;
		pageContext.setVariable(variable,"");
	}

	public void setErrorvariable(String errorVariable) throws PageException	{
		this.errorVariable = errorVariable;
		pageContext.setVariable(errorVariable, "");
	}

	/** set the value outputfile
	*  The file to which to direct the output of the program. If not specified, the output is 
	* 		displayed on the page from which it was called.
	* @param outputfile value to set
	 * @throws SecurityException
	**/
	public void setOutputfile(String outputfile)	{
	    try {
            this.outputfile=ResourceUtil.toResourceExistingParent(pageContext,outputfile);
            pageContext.getConfig().getSecurityManager().checkFileLocation(this.outputfile);
    		
        } 
	    catch (PageException e) {
            this.outputfile=pageContext.getConfig().getTempDirectory().getRealResource(outputfile);
            if(!this.outputfile.getParentResource().exists())
                this.outputfile=null;
            else if(!this.outputfile.isFile())
                this.outputfile=null;
            else if(!this.outputfile.exists()) {
            	ResourceUtil.createFileEL(this.outputfile, false);
                //try {
                    //this.outputfile.createNewFile();
                /*} catch (IOException e1) {
                    this.outputfile=null;
                }*/
            }
        }
	}


	public void setErrorfile(String errorfile)	{

		try {
			this.errorFile = ResourceUtil.toResourceExistingParent(pageContext,errorfile);
			pageContext.getConfig().getSecurityManager().checkFileLocation(this.errorFile);
		}
		catch (PageException e) {

			this.errorFile = pageContext.getConfig().getTempDirectory().getRealResource(errorfile);

			if(!this.errorFile.getParentResource().exists())
				this.errorFile=null;
			else if(!this.errorFile.isFile())
				this.errorFile=null;
			else if(!this.errorFile.exists()) {
				ResourceUtil.createFileEL(this.errorFile, false);
			}
		}
	}


	@Override
	public int doStartTag() throws PageException	{
		return EVAL_BODY_BUFFERED;
	}
	
	private void _execute() throws Exception 	{
	    Object monitor=new SerializableObject();
	    
	    String command="";
	    if(name==null) {
	    	if(StringUtil.isEmpty(body)) {
		    	required("execute", "name", name);
		    	required("execute", "arguments", arguments);
	    	}
	    	else command=body;
	    }
	    else {
	    	if(arguments==null)command=name;
	    	else command=name+arguments;
	    }


	    _Execute execute=new _Execute(pageContext, monitor, command, outputfile, variable, errorFile, errorVariable);
	    
	    //if(timeout<=0)execute._run();
	    //else {
    	execute.start();
    	if(timeout>0){
	    	try {
	            synchronized(monitor) {
	            	monitor.wait(timeout);
	            }
	        } 
		    finally {
		    	execute.abort(terminateOnTimeout);
	        }
		    if(execute.hasException()) {
		    	throw execute.getException();
		    }
		    if(!execute.hasFinished())
		    	throw new ApplicationException("timeout ["+(timeout)+" ms] expired while executing ["+command+"]");
		    //}
    	}
	    
	}

	@Override
	public int doEndTag() throws PageException	{
		if(pageContext.getConfig().getSecurityManager().getAccess(SecurityManager.TYPE_TAG_EXECUTE)==SecurityManager.VALUE_NO) 
			throw new SecurityException("can't access tag [execute]","access is prohibited by security manager");
	    try {
            _execute();
        } 
	    catch (PageException pe) {
           throw pe;
        }
        catch (Exception e) {e.printStackTrace();
           throw new ApplicationException("Error invoking external process",e.getMessage());
        }
	    return EVAL_PAGE;
	}

	@Override
	public void doInitBody()	{
		
	}

	@Override
	public int doAfterBody()	{
		body=bodyContent.getString();
		if(!StringUtil.isEmpty(body))body=body.trim();
		return SKIP_BODY;
	}
}