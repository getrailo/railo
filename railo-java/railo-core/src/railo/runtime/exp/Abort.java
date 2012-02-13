package railo.runtime.exp;


/**
 * This Exception will be Throwed, when page Excecution will be aborted (tag abort).
 */
public class Abort extends AbortException {

    public final static int SCOPE_PAGE=0;
    public final static int SCOPE_REQUEST=1;
    private int scope;
    
	/**
	 * Constructor of the Class
	 */
	public Abort(int scope) {
		super("Page request is aborted");
        this.scope=scope;
	}
	protected Abort(int scope, String msg) {
		super(msg);
        this.scope=scope;
	}
	public static Abort newInstance(int scope) {
		return new Abort(scope);
	}
    
    public int getScope() {
        return scope;
    }
	
	public static boolean isSilentAbort(Throwable t){
		if(t instanceof  PageExceptionBox) {
			return isSilentAbort(((PageExceptionBox)t).getPageException());
		}
		return t instanceof Abort && !(t instanceof RequestTimeoutException);
	}
	
	public static boolean isAbort(Throwable t) {
		if(t instanceof Abort) return true;
		if(t instanceof  PageExceptionBox) {
			return (((PageExceptionBox)t).getPageException() instanceof Abort);
		}
		return false;
	}

	public static boolean isAbort(Throwable t, int scope) {
		if(t instanceof  PageExceptionBox) {
			return isAbort(((PageExceptionBox)t).getPageException(),scope);
		}
		return t instanceof Abort && ((Abort) t).getScope()==scope;
	}
}