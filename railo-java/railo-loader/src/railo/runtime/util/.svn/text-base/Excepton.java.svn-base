package railo.runtime.util;

import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.db.SQL;
import railo.runtime.exp.PageException;

/**
 * class to get exceptions of different types
 */
public interface Excepton {

    /**
     * Field <code>TYPE_ABORT</code>
     */
    public static final int TYPE_ABORT=0;
    /**
     * Field <code>TYPE_ABORT_EXP</code>
     */
    public static final int TYPE_ABORT_EXP=1;
    /**
     * Field <code>TYPE_APPLICATION_EXP</code>
     */
    public static final int TYPE_APPLICATION_EXP=2;
    /**
     * Field <code>TYPE_CASTER_EXP</code>
     */
    public static final int TYPE_CASTER_EXP=3;
    /**
     * Field <code>TYPE_CUSTOM_TYPE_EXP</code>
     */
    public static final int TYPE_CUSTOM_TYPE_EXP=4;
    /**
     * Field <code>TYPE_DATABASE_EXP</code>
     */
    public static final int TYPE_DATABASE_EXP=5;
    /**
     * Field <code>TYPE_EXPRESSION_EXP</code>
     */
    public static final int TYPE_EXPRESSION_EXP=6;
    /**
     * Field <code>TYPE_FUNCTION_EXP</code>
     */
    public static final int TYPE_FUNCTION_EXP=7;
    /**
     * Field <code>TYPE_LOCK_EXP</code>
     */
    public static final int TYPE_LOCK_EXP=8;
    /**
     * Field <code>TYPE_MISSING_INCLUDE_EXP</code>
     */
    public static final int TYPE_MISSING_INCLUDE_EXP=9;
    /**
     * Field <code>TYPE_NATIVE_EXP</code>
     */
    public static final int TYPE_NATIVE_EXP=10;
    /**
     * Field <code>TYPE_SECURITY_EXP</code>
     */
    public static final int TYPE_SECURITY_EXP=11;
    /**
     * Field <code>TYPE_TEMPLATE_EXP</code>
     */
    public static final int TYPE_TEMPLATE_EXP=12;
    /**
     * Field <code>TYPE_XML_EXP</code>
     */
    public static final int TYPE_XML_EXP=13;
    
    /**
     * create exception "Abort"
     * @return Abort
     */
    public PageException createAbort();
    
    /**
     * create exception "AbortException"
     * @param showError 
     * @return AbortException
     */
    public PageException createAbortException(String showError);
    
    /**
     * create exception "ApplicationException"
     * @param message 
     * @return ApplicationException
     */
    public PageException createApplicationException(String message);
    
    /**
     * create exception "ApplicationException"
     * @param message 
     * @param detail 
     * @return ApplicationException
     */
    public PageException createApplicationException(String message, String detail);
    
    /**
     * create exception "CasterException"
     * @param message 
     * @return CasterException
     */
    public PageException createCasterException(String message);
    
    /**
     * create exception "CustomTypeException"
     * @param message 
     * @param detail 
     * @param errorcode 
     * @param customType 
     * @return CustomTypeException
     */
    public PageException createCustomTypeException(String message, String detail, String errorcode, String customType);
    
    /**
     * create exception "DatabaseException"
     * @param message 
     * @return DatabaseException
     */
    public PageException createDatabaseException(String message);
    
    /**
     * create exception "DatabaseException"
     * @param message 
     * @param detail 
     * @return DatabaseException
     */
    public PageException createDatabaseException(String message, String detail);
    
    /**
     * create exception "DatabaseException"
     * @param message 
     * @param sql 
     * @return DatabaseException
     */
    public PageException createDatabaseException(String message, SQL sql);
    
    /**
     * create exception "ExpressionException"
     * @param message 
     * @return ExpressionException
     */
    public PageException createExpressionException(String message);
    
    /**
     * create exception "ExpressionException"
     * @param message 
     * @param detail 
     * @return ExpressionException
     */
    public PageException createExpressionException(String message, String detail);
    
    /**
     * create exception "FunctionException"
     * @param pc 
     * @param functionName 
     * @param badArgumentPosition 
     * @param badArgumentName 
     * @param message 
     * @return FunctionException
     */
    public PageException createFunctionException(PageContext pc,String functionName, String badArgumentPosition, String badArgumentName, String message);
    
    /**
     * create exception "LockException"
     * @param operation 
     * @param name 
     * @param message 
     * @return LockException
     */
    public PageException createLockException(String operation, String name, String message);
    
    /**
     * create exception "LockException" 
     * @param ps 
     * @return LockException
     */
    public PageException createMissingIncludeException(PageSource ps);
    
    /**
     * create exception "NativeException" 
     * @param t 
     * @return NativeException
     */
    public PageException createNativeException(Throwable t);
    
    /**
     * create exception "SecurityException" 
     * @param message 
     * @return SecurityException
     */
    public PageException createSecurityException(String message);
    
    /**
     * create exception "SecurityException" 
     * @param message 
     * @param detail 
     * @return SecurityException
     */
    public PageException createSecurityException(String message, String detail);
    
    /**
     * create exception "TemplateException" 
     * @param message 
     * @return TemplateException
     */
    public PageException createTemplateException(String message);
    
    /**
     * create exception "TemplateException" 
     * @param message 
     * @param detail 
     * @return TemplateException
     */
    public PageException createTemplateException(String message, String detail);
    
    /**
     * create exception "XMLException" 
     * @param message
     * @return XMLException
     */
    public PageException createXMLException(String message);
    
    /**
     * create exception "XMLException" 
     * @param message 
     * @param detail 
     * @return XMLException
     */
    public PageException createXMLException(String message, String detail);

    /**
     * check if exception is of given type
     * @param type type to check
     * @param t exception to check
     * @return is of type
     */
    public boolean isOfType(int type, Throwable t); 
}
