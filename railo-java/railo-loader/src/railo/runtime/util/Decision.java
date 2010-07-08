package railo.runtime.util;

import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;



/**
 * Object to test if a Object is a specific type
 */
public interface Decision {

    /**
     * tests if value is a simple value (Number,String,Boolean,Date,Printable)
     * @param value value to test
     * @return is value a simple value
     */
    public boolean isSimpleValue(Object value);

    /**
     * tests if value is Numeric
     * @param value value to test
     * @return is value numeric
     */
    public boolean isNumeric(Object value);
    
    /**
     * tests if String value is Numeric
     * @param str value to test
     * @return is value numeric
     */
    public boolean isNumeric(String str);

     /** tests if String value is Hex Value
     * @param str value to test
     * @return is value numeric
     */
    public boolean isHex(String str);

     /** tests if String value is UUID Value
     * @param str value to test
     * @return is value numeric
     */
    public boolean isUUID(String str);

    /**
     * tests if value is a Boolean (Numbers are not acctepeted)
     * @param value value to test
     * @return is value boolean
     */
    public boolean isBoolean(Object value);

    /**
     * tests if value is a Boolean
     * @param str value to test
     * @return is value boolean
     */
    public boolean isBoolean(String str);

    /**
     * tests if value is DateTime Object
     * @param value value to test
     * @param alsoNumbers interpret also a number as date
     * @return is value a DateTime Object
     */
    public boolean isDate(Object value,boolean alsoNumbers);

    /**
     * tests if object is a struct 
     * @param o
     * @return is struct or not
     */
    public boolean isStruct(Object o);

    /**
     * tests if object is a array 
     * @param o
     * @return is array or not
     */
    public boolean isArray(Object o);

    /**
     * tests if object is a native java array 
     * @param o
     * @return is a native (java) array
     */
    public boolean isNativeArray(Object o);

    /**
     * tests if object is a binary  
     * @param object
     * @return boolean
     */
    public boolean isBinary(Object object);

    /**
     * tests if object is a Component  
     * @param object
     * @return boolean
     */
    public boolean isComponent(Object object);

    /**
     * tests if object is a Query  
     * @param object
     * @return boolean
     */
    public boolean isQuery(Object object);

    /**
     * tests if object is a binary  
     * @param object
     * @return boolean
     */
    public boolean isUserDefinedFunction(Object object);
    
    /**
     * tests if year is a leap year 
     * @param year year to check
     * @return boolean
     */
    public boolean isLeapYear(int year);
    
    /**
     * tests if object is a WDDX Object 
     * @param o Object to check
     * @return boolean
     */
    public boolean isWddx(Object o);
    
    /**
     * tests if object is a XML Object 
     * @param o Object to check
     * @return boolean
     */
    public boolean isXML(Object o);
    
    /**
     * tests if object is a XML Element Object 
     * @param o Object to check
     * @return boolean
     */
    public boolean isXMLElement(Object o);
    
    /**
     * tests if object is a XML Document Object 
     * @param o Object to check
     * @return boolean
     */
    public boolean isXMLDocument(Object o);
    
    /**
     * tests if object is a XML Root Element Object 
     * @param o Object to check
     * @return boolean
     */
    public boolean isXMLRootElement(Object o);

    /**
     * @param string
     * @return returns if string represent a variable name
     */
    public boolean isVariableName(String string);
    
    /**
     * @param string
     * @return returns if string represent a variable name
     */
    public boolean isSimpleVariableName(String string);

    /**
     * returns if object is a cold fusion object
     * @param o Object to check
     * @return is or not
     */
    public boolean isObject(Object o);

    /**
     * 
     * @param str
     * @return return if a String is "Empty", that means NULL or String with length 0 (whitespaces will not counted) 
     */
    public boolean isEmpty(String str);
    
    /**
     * 
     * @param str
     * @param trim 
     * @return return if a String is "Empty", that means NULL or String with length 0 (whitespaces will not counted) 
     */
    public boolean isEmpty(String str, boolean trim);


	
	public Key toKey(Object obj) throws PageException;
	
	public Key toKey(Object obj, Collection.Key defaultValue);
	
}