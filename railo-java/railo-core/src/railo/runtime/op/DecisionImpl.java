package railo.runtime.op;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;


/**
 * implementation of the interface Decision
 */
public final class DecisionImpl implements railo.runtime.util.Decision {

    private static DecisionImpl singelton;

    /**
     * @see railo.runtime.util.Decision#isArray(java.lang.Object)
     */
    public boolean isArray(Object o) {
        return Decision.isArray(o);
    }

    /**
     * @see railo.runtime.util.Decision#isBinary(java.lang.Object)
     */
    public boolean isBinary(Object object) {
        return Decision.isBinary(object);
    }

    /**
     * @see railo.runtime.util.Decision#isBoolean(java.lang.Object)
     */
    public boolean isBoolean(Object value) {
        return Decision.isBoolean(value);
    }

    /**
     * @see railo.runtime.util.Decision#isBoolean(java.lang.String)
     */
    public boolean isBoolean(String str) {
        return Decision.isBoolean(str);
    }

    /**
     * @see railo.runtime.util.Decision#isComponent(java.lang.Object)
     */
    public boolean isComponent(Object object) {
        return Decision.isComponent(object);
    }

    /**
     * @see railo.runtime.util.Decision#isDate(java.lang.Object, boolean)
     */
    public boolean isDate(Object value, boolean alsoNumbers) {
        return Decision.isDateAdvanced(value,alsoNumbers);
    }

    /**
     * @see railo.runtime.util.Decision#isEmpty(java.lang.String, boolean)
     */
    public boolean isEmpty(String str, boolean trim) {
        return StringUtil.isEmpty(str,trim);
    }

    /**
     * @see railo.runtime.util.Decision#isEmpty(java.lang.String)
     */
    public boolean isEmpty(String str) {
        return StringUtil.isEmpty(str);
    }

    /**
     * @see railo.runtime.util.Decision#isHex(java.lang.String)
     */
    public boolean isHex(String str) {
        return Decision.isHex(str);
    }

    /**
     * @see railo.runtime.util.Decision#isLeapYear(int)
     */
    public boolean isLeapYear(int year) {
        return Decision.isLeapYear(year);
    }

    /**
     * @see railo.runtime.util.Decision#isNativeArray(java.lang.Object)
     */
    public boolean isNativeArray(Object o) {
        return Decision.isNativeArray(o);
    }

    /**
     * @see railo.runtime.util.Decision#isNumeric(java.lang.Object)
     */
    public boolean isNumeric(Object value) {
        return Decision.isNumeric(value);
    }

    /**
     * @see railo.runtime.util.Decision#isNumeric(java.lang.String)
     */
    public boolean isNumeric(String str) {
        return Decision.isNumeric(str);
    }

    /**
     * @see railo.runtime.util.Decision#isObject(java.lang.Object)
     */
    public boolean isObject(Object o) {
        return Decision.isObject(o);
    }

    /**
     * @see railo.runtime.util.Decision#isQuery(java.lang.Object)
     */
    public boolean isQuery(Object object) {
        return Decision.isQuery(object);
    }

    /**
     * @see railo.runtime.util.Decision#isSimpleValue(java.lang.Object)
     */
    public boolean isSimpleValue(Object value) {
        return Decision.isSimpleValue(value);
    }

    /**
     * @see railo.runtime.util.Decision#isSimpleVariableName(java.lang.String)
     */
    public boolean isSimpleVariableName(String string) {
        return Decision.isSimpleVariableName(string);
    }

    /**
     * @see railo.runtime.util.Decision#isStruct(java.lang.Object)
     */
    public boolean isStruct(Object o) {
        return Decision.isStruct(o);
    }

    /**
     * @see railo.runtime.util.Decision#isUserDefinedFunction(java.lang.Object)
     */
    public boolean isUserDefinedFunction(Object object) {
        return Decision.isUserDefinedFunction(object);
    }

    /**
     * @see railo.runtime.util.Decision#isUUID(java.lang.String)
     */
    public boolean isUUID(String str) {
        return Decision.isUUId(str);
    }

    /**
     * @see railo.runtime.util.Decision#isVariableName(java.lang.String)
     */
    public boolean isVariableName(String string) {
        return Decision.isVariableName(string);
    }

    /**
     * @see railo.runtime.util.Decision#isWddx(java.lang.Object)
     */
    public boolean isWddx(Object o) {
        return Decision.isWddx(o);
    }

    /**
     * @see railo.runtime.util.Decision#isXML(java.lang.Object)
     */
    public boolean isXML(Object o) {
        return Decision.isXML(o);
    }

    /**
     * @see railo.runtime.util.Decision#isXMLDocument(java.lang.Object)
     */
    public boolean isXMLDocument(Object o) {
        return Decision.isXMLDocument(o);
    }

    /**
     * @see railo.runtime.util.Decision#isXMLElement(java.lang.Object)
     */
    public boolean isXMLElement(Object o) {
        return Decision.isXMLElement(o);
    }

    /**
     * @see railo.runtime.util.Decision#isXMLRootElement(java.lang.Object)
     */
    public boolean isXMLRootElement(Object o) {
        return Decision.isXMLRootElement(o);
    }

    public static railo.runtime.util.Decision getInstance() {
        if(singelton==null)singelton=new DecisionImpl();
        return singelton;
    }

	/**
	 * @see railo.runtime.util.Decision#toKey(java.lang.Object)
	 */
	public Key toKey(Object obj) throws PageException {
		return KeyImpl.toKey(obj);
	}

	/**
	 * @see railo.runtime.util.Decision#toKey(java.lang.Object, railo.runtime.type.Collection.Key)
	 */
	public Key toKey(Object obj, Key defaultValue) {
		return KeyImpl.toKey(obj,defaultValue);
	}

}
