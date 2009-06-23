

package railo.runtime.op;

import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.db.SQL;
import railo.runtime.exp.Abort;
import railo.runtime.exp.AbortException;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.CustomTypeException;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.LockException;
import railo.runtime.exp.MissingIncludeException;
import railo.runtime.exp.NativeException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.SecurityException;
import railo.runtime.exp.TemplateException;
import railo.runtime.exp.XMLException;
import railo.runtime.reflection.Reflector;
import railo.runtime.util.Excepton;

/**
 * Implementation of Exception Util
 */
public final class ExceptonImpl implements Excepton {

    private static Class[] exceptions=new Class[14];
    
    static {
        exceptions[TYPE_ABORT]=Abort.class;
        exceptions[TYPE_ABORT_EXP]=AbortException.class;
        exceptions[TYPE_APPLICATION_EXP]=ApplicationException.class;
        exceptions[TYPE_CASTER_EXP]=CasterException.class;
        exceptions[TYPE_CUSTOM_TYPE_EXP]=CustomTypeException.class;
        exceptions[TYPE_DATABASE_EXP]=DatabaseException.class;
        exceptions[TYPE_EXPRESSION_EXP]=ExpressionException.class;
        exceptions[TYPE_FUNCTION_EXP]=FunctionException.class;
        exceptions[TYPE_LOCK_EXP]=LockException.class;
        exceptions[TYPE_MISSING_INCLUDE_EXP]=MissingIncludeException.class;
        exceptions[TYPE_NATIVE_EXP]=NativeException.class;
        exceptions[TYPE_SECURITY_EXP]=SecurityException.class;
        exceptions[TYPE_TEMPLATE_EXP]=TemplateException.class;
        exceptions[TYPE_XML_EXP]=XMLException.class;
    }
    
    private static ExceptonImpl singelton;

    /**
     * @return singleton instance
     */
    public static Excepton getInstance() {
        if(singelton==null)singelton=new ExceptonImpl();
        return singelton;
    }

    /**
     * @see railo.runtime.util.Excepton#createAbort()
     */
    public PageException createAbort() {
        return new Abort(Abort.SCOPE_REQUEST);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createAbortException(java.lang.String)
     */
    public PageException createAbortException(String showError) {
        return new AbortException(showError);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createApplicationException(java.lang.String)
     */
    public PageException createApplicationException(String message) {
        return new ApplicationException(message);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createApplicationException(java.lang.String, java.lang.String)
     */
    public PageException createApplicationException(String message, String detail) {
        return new ApplicationException(message,detail);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createCasterException(java.lang.String)
     */
    public PageException createCasterException(String message) {
        return new CasterException(message);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createCustomTypeException(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public PageException createCustomTypeException(String message, String detail, String errorcode, String customType) {
        return new CustomTypeException(message,detail,errorcode,customType);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createDatabaseException(java.lang.String)
     */
    public PageException createDatabaseException(String message) {
        return new DatabaseException(message,null,null,null);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createDatabaseException(java.lang.String, java.lang.String)
     */
    public PageException createDatabaseException(String message, String detail) {
        return new DatabaseException(message,detail,null,null,null);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createDatabaseException(java.lang.String, railo.runtime.db.SQL)
     */
    public PageException createDatabaseException(String message, SQL sql) {
        return new DatabaseException(message,null,sql,null);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createExpressionException(java.lang.String)
     */
    public PageException createExpressionException(String message) {
        return new ExpressionException(message);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createExpressionException(java.lang.String, java.lang.String)
     */
    public PageException createExpressionException(String message, String detail) {
        return new ExpressionException(message, detail);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createFunctionException(railo.runtime.PageContext, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public PageException createFunctionException(PageContext pc,String functionName, String badArgumentPosition, String badArgumentName, String message) {
        return new FunctionException(pc,functionName, badArgumentPosition, badArgumentName,message);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createLockException(java.lang.String, java.lang.String, java.lang.String)
     */
    public PageException createLockException(String operation, String name, String message) {
        return new LockException(operation,name,message);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createMissingIncludeException(railo.runtime.PageSource)
     */
    public PageException createMissingIncludeException(PageSource ps) {
        return new MissingIncludeException(ps);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createNativeException(java.lang.Throwable)
     */
    public PageException createNativeException(Throwable t) {
        return new NativeException(t);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createSecurityException(java.lang.String)
     */
    public PageException createSecurityException(String message) {
        return new SecurityException(message);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createSecurityException(java.lang.String, java.lang.String)
     */
    public PageException createSecurityException(String message, String detail) {
        return new SecurityException(message,detail);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createTemplateException(java.lang.String)
     */
    public PageException createTemplateException(String message) {
        return new TemplateException(message);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createTemplateException(java.lang.String, java.lang.String)
     */
    public PageException createTemplateException(String message, String detail) {
        return new TemplateException(message,detail);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createXMLException(java.lang.String)
     */
    public PageException createXMLException(String message) {
        return new XMLException(message);
    }
    
    /**
     * @see railo.runtime.util.Excepton#createXMLException(java.lang.String, java.lang.String)
     */
    public PageException createXMLException(String message, String detail) {
        return new XMLException(message,detail);
    }

    /**
     * @see railo.runtime.util.Excepton#isOfType(int, java.lang.Throwable)
     */
    public boolean isOfType(int type, Throwable t) {
        return Reflector.isInstaneOf(t.getClass(),exceptions[type]);
    }
    

}
