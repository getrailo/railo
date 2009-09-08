package railo.runtime.tag;

import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.LockException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagTryCatchFinallyImpl;
import railo.runtime.lock.LockData;
import railo.runtime.lock.LockManager;
import railo.runtime.lock.LockTimeoutException;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.scope.ApplicationImpl;
import railo.runtime.type.scope.RequestImpl;
import railo.runtime.type.scope.ScopeSupport;
import railo.runtime.type.scope.ServerImpl;

/**
* Provides two types of locks to ensure the integrity of shared data: Exclusive lock and Read-only 
*   lock. An exclusive lock single-threads access to the CFML constructs in its body. Single-threaded access 
*   implies that the body of the tag can be executed by at most one request at a time. A request executing 
*   inside a cflock tag has an "exclusive lock" on the tag. No other requests can start executing inside the 
*   tag while a request has an exclusive lock. ColdFusion issues exclusive locks on a first-come, first-served 
*   basis. A read-only lock allows multiple requests to access the CFML constructs inside its body concurrently. 
*   Therefore, read-only locks should be used only when the shared data is read only and not modified. If another 
*   request already has an exclusive lock on the shared data, the request waits for the exclusive lock to be 
*   released.
*
*
*
**/
public final class Lock extends BodyTagTryCatchFinallyImpl {



	private static final short SCOPE_NONE=0;
	private static final short SCOPE_SERVER=1;
	private static final short SCOPE_APPLICATION=2;
	private static final short SCOPE_SESSION=3;
	private static final short SCOPE_REQUEST=4;
	
	private String id="anonymous";
	
	/** Specifies the maximum amount of time, in seconds, to wait to obtain a lock. If a lock can be 
	** 		obtained within the specified period, execution continues inside the body of the tag. Otherwise, the 
	** 		behavior depends on the value of the throwOnTimeout attribute. */
	private int timeoutInMillis;
	
	/** readOnly or Exclusive. Specifies the type of lock: read-only or exclusive. Default is Exclusive. 
	** 	A read-only lock allows more than one request to read shared data. An exclusive lock allows only one 
	** 	request to read or write to shared data. */
	private short type=LockManager.TYPE_EXCLUSIVE;
	
	/** Specifies the scope as one of the following: Application, Server, or Session. This attribute is mutually 
	** 	exclusive with the name attribute. */
	private short scope=SCOPE_NONE;
	
	/** Yes or No. Specifies how timeout conditions are handled. If the value is Yes, an exception is 
	** 	generated to provide notification of the timeout. If the value is No, execution continues past the 
	**    cfclock tag. Default is Yes. */
	private boolean throwontimeout = true;
	
	/** Specifies the name of the lock. Only one request can execute inside a cflock tag with a given 
	** 	name. Therefore, providing the name attribute allows for synchronizing access to resources from 
	** 	different parts of an application. Lock names are global to a ColdFusion server. They are shared 
	** 	between applications and user sessions, but not across clustered servers. This attribute is mutually 
	** 	exclusive with the scope attribute. Therefore, do not specify the scope attribute and the name attribute 
	** 	in a tag. The value of name cannot be an empty string. */
	private String name;
	
	private LockManager manager;
    private LockData data=null;

	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release() {
		super.release();
		type = LockManager.TYPE_EXCLUSIVE;
		scope = SCOPE_NONE;
		throwontimeout = true;
		name = null;
        manager=null;
        this.data=null;
        id="anonymous";
        timeoutInMillis=0;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/** set the value timeout
	*  Specifies the maximum amount of time, in seconds, to wait to obtain a lock. If a lock can be 
	* 		obtained within the specified period, execution continues inside the body of the tag. Otherwise, the 
	* 		behavior depends on the value of the throwOnTimeout attribute.
	* @param timeout value to set
	**/
	public void setTimeout(Object oTimeout) throws PageException {
		if(oTimeout instanceof TimeSpan)
			this.timeoutInMillis=(int)((TimeSpan)oTimeout).getMillis();
		else
			this.timeoutInMillis = (int)(Caster.toDoubleValue(oTimeout)*1000D);
		//print.out(Caster.toString(timeoutInMillis));
	}
	public void setTimeout(double timeout) throws PageException {
		this.timeoutInMillis = (int)(timeout*1000D);
	}

	/** set the value type
	*  readOnly or Exclusive. Specifies the type of lock: read-only or exclusive. Default is Exclusive. 
	* 	A read-only lock allows more than one request to read shared data. An exclusive lock allows only one 
	* 	request to read or write to shared data.
	* @param type value to set
	 * @throws ApplicationException
	**/
	public void setType(String type) throws ApplicationException {
		type=type.toLowerCase().trim();
				
		if(type.equals("exclusive")) {
		    this.type=LockManager.TYPE_EXCLUSIVE;
		}
		else if(type.startsWith("read")){
		    this.type=LockManager.TYPE_READONLY;
		}
		else 
			throw new ApplicationException("invalid value ["+type+"] for attribute [type] from tag [lock]",
					"valid values are [exclusive,read-only]");
	}

	/** set the value scope
	*  Specifies the scope as one of the following: Application, Server, or Session. This attribute is mutually 
	* 	exclusive with the name attribute.
	* @param scope value to set
	 * @throws ApplicationException
	**/
	public void setScope(String scope) throws ApplicationException {
		scope=scope.toLowerCase().trim();
		
		if(scope.equals("server"))this.scope=SCOPE_SERVER;
		else if(scope.equals("application"))this.scope=SCOPE_APPLICATION;
		else if(scope.equals("session"))this.scope=SCOPE_SESSION;
		else if(scope.equals("request"))this.scope=SCOPE_REQUEST;
		else 
			throw new ApplicationException("invalid value ["+scope+"] for attribute [scope] from tag [lock]",
					"valid values are [server,application,session]");
	}

	/** set the value throwontimeout
	*  Yes or No. Specifies how timeout conditions are handled. If the value is Yes, an exception is 
	* 	generated to provide notification of the timeout. If the value is No, execution continues past the 
	*    cfclock tag. Default is Yes.
	* @param throwontimeout value to set
	**/
	public void setThrowontimeout(boolean throwontimeout) {
		this.throwontimeout = throwontimeout;
	}

	/** set the value name
	*  Specifies the name of the lock. Only one request can execute inside a cflock tag with a given 
	* 	name. Therefore, providing the name attribute allows for synchronizing access to resources from 
	* 	different parts of an application. Lock names are global to a ColdFusion server. They are shared 
	* 	between applications and user sessions, but not across clustered servers. This attribute is mutually 
	* 	exclusive with the scope attribute. Therefore, do not specify the scope attribute and the name attribute 
	* 	in a tag. The value of name cannot be an empty string.
	* @param name value to set
	 * @throws ApplicationException
	**/
	public void setName(String name) throws ApplicationException {
		this.name = name.trim();
		if(name.length()==0)throw new ApplicationException("invalid attribute definition","attribute [name] can't be a empty string");
	}

	/**
	 * @throws PageException
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws PageException {
		//if(timeoutInMillis==0)timeoutInMillis=30000;
		//print.out("doStartTag");
	    manager=pageContext.getConfig().getLockManager();
        // check attributes
	    if(name!=null && scope!=SCOPE_NONE) {
	        throw new LockException(
	                LockException.OPERATION_CREATE,
	                this.name,
	                "invalid attribute combination",
	                	"attribute [name] and [scope] can't be used together");
        }
	    if(name==null && scope==SCOPE_NONE)    {
	    	name="id-"+id;
        }
        
	    String lockType = null;
	    if(name==null) {
	        // Session
	        if(scope==SCOPE_REQUEST){
	            lockType="request"; 
	            name="__request_"+ ((RequestImpl)pageContext.requestScope())._getId();
	        }
	        // Session
	        else if(scope==SCOPE_SESSION){
	            lockType="session"; 
	            name="__session_"+ ((ScopeSupport)pageContext.sessionScope())._getId();
	        }
	        // Application 
	        else if(scope==SCOPE_APPLICATION){
	            lockType="application";
	            name="__application_"+((ApplicationImpl)pageContext.applicationScope())._getId();
	        }
	        // Server
	        else if(scope==SCOPE_SERVER){
	            lockType="server";
	            name="__server_"+((ServerImpl)pageContext.serverScope())._getId();
	        }
	    }
	    
	    Struct cflock=new StructImpl();
	    cflock.set("succeeded",Boolean.TRUE);
	    cflock.set("errortext","");
	    pageContext.variablesScope().set("cflock",cflock);
        
		try {
		    data = manager.lock(type,name,timeoutInMillis,pageContext.getId());
		} 
		catch (LockTimeoutException e) {
			//print.out("LockTimeoutException");
		    name=null;
			String errorText=e.getMessage();
		    if(lockType!=null)errorText=("there is a timeout occurred on ["+lockType+"] scope lock");
			
		    cflock.set("succeeded",Boolean.FALSE);
		    cflock.set("errortext",errorText);

			if(throwontimeout) throw new LockException(
	                LockException.OPERATION_TIMEOUT,
	                this.name,
	                errorText);
			
			return SKIP_BODY;
		} 
		catch (InterruptedException e) {
		    
		    cflock.set("succeeded",Boolean.FALSE);
		    cflock.set("errortext",e.getMessage());
		    
		    if(throwontimeout) throw Caster.toPageException(e);
			
			return SKIP_BODY;
		    
		    
		}
		
		return EVAL_BODY_INCLUDE;
	}
	
	/**
	 * @see javax.servlet.jsp.tagext.TryCatchFinally#doFinally()
	 */
	public void doFinally() {
		//print.out("unlock:"+data.getName()+":"+pageContext.getId());
        if(name!=null)manager.unlock(data);
	}
	
	
}