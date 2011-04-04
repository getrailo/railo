package railo.runtime.exp;

import railo.runtime.Info;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.op.Caster;
import railo.runtime.reflection.Reflector;


/**
 * Box a Native Exception, Native = !PageException
 */
public final class NativeException extends PageExceptionImpl {

	private Throwable t;

    /**
	 * Standart constructor for native Exception class
	 * @param t Throwable
	 */
	public NativeException(Throwable t) {
        super(t,t.getClass().getName());
        this.t=t;
        //setStackTrace(t.getStackTrace());
        setAdditional("Cause", t.getClass().getName());
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
	    DumpData data = super.toDumpData(pageContext, maxlevel,dp);
	    if(data instanceof DumpTable)
        ((DumpTable)data).setTitle("Railo ["+Info.getVersionAsString()+"] - Error ("+Caster.toClassName(t)+")");
        
        return data;
    }

    /**
     * @see railo.runtime.exp.IPageException#typeEqual(java.lang.String)
     */
    public boolean typeEqual(String type) {
    	if(super.typeEqual(type))return true;
        return Reflector.isInstaneOfIgnoreCase(t.getClass(),type);
    }

	/**
	 * @see railo.runtime.exp.PageExceptionImpl#setAdditional(java.lang.String, java.lang.Object)
	 */
	public void setAdditional(String key, Object value) {
		super.setAdditional(key, value);
	}
}