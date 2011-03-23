package railo.runtime.java;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Date;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpUtil;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.runtime.op.date.DateCaster;
import railo.runtime.reflection.Reflector;
import railo.runtime.reflection.pairs.MethodInstance;
import railo.runtime.type.Collection;
import railo.runtime.type.ObjectWrap;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.util.VariableUtil;


/**
 * class to handle initialising and call native object from railo
 */
public class JavaObject implements Objects,ObjectWrap {
	
	private Class clazz;
	private boolean isInit=false;
	private Object object;
    private VariableUtil variableUtil;
    
	/**
	 * constructor with className to load
	 * @param variableUtil
	 * @param clazz
	 * @throws ExpressionException
	 */
	public JavaObject(VariableUtil variableUtil,Class clazz) {
	    this.variableUtil=variableUtil;
		this.clazz=clazz;
	}


	public JavaObject(VariableUtil variableUtil,Object object) {
	    this.variableUtil=variableUtil;
		this.clazz=object.getClass();
		this.object=object;
		isInit=true;
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String)
	 */
	public Object get(PageContext pc, String propertyName) throws PageException {
        if(isInit) {
            return variableUtil.get(pc,object,propertyName);
        }
            
        // Check Field
            Field[] fields = Reflector.getFieldsIgnoreCase(clazz,propertyName,null);
            if(!ArrayUtil.isEmpty(fields) && Modifier.isStatic(fields[0].getModifiers())) {
    			try {
                    return fields[0].get(null);
                } 
                catch (Exception e) {
                    throw Caster.toPageException(e);
                }
    		}
        // Getter
            MethodInstance mi = Reflector.getGetterEL(clazz,propertyName);
            if(mi!=null) {
                if(Modifier.isStatic(mi.getMethod().getModifiers())) {
                    try {
                        return mi.invoke(null);
                    } 
                    catch (IllegalAccessException e) {
                        throw Caster.toPageException(e);
                    } 
                    catch (InvocationTargetException e) {
                        throw Caster.toPageException(e.getTargetException());
                    }    
                }
            }
        // male Instance
        return variableUtil.get(pc,init(),propertyName);  
	}

	/**
	 *
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public Object get(PageContext pc, Collection.Key key) throws PageException {
		return get(pc, key.getString());
	}

    public Object get(PageContext pc, String propertyName, Object defaultValue) {
        if(isInit) {
            return variableUtil.get(pc,object,propertyName,defaultValue);  
        }
        // Field
        Field[] fields = Reflector.getFieldsIgnoreCase(clazz,propertyName,null);
        if(!ArrayUtil.isEmpty(fields) && Modifier.isStatic(fields[0].getModifiers())) {
			try {
                return fields[0].get(null);
            } catch (Exception e) {}
		}
        // Getter
        MethodInstance mi = Reflector.getGetterEL(clazz,propertyName);
        if(mi!=null) {
            if(Modifier.isStatic(mi.getMethod().getModifiers())) {
                try {
                    return mi.invoke(null);
                } 
                catch (Exception e) {}    
            }
        }
        try {
            return variableUtil.get(pc,init(),propertyName,defaultValue);  
        } catch (PageException e1) {
            return defaultValue;
        }         
    }

	/**
	 *
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(PageContext pc, Collection.Key key, Object defaultValue) {
		return get(pc, key.getString(), defaultValue);
	}

	/**
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object set(PageContext pc, String propertyName, Object value) throws PageException  {
		if(isInit) {
		    return variableUtil.set(pc,object,propertyName,value);
		}
	    // Field
		Field[] fields=Reflector.getFieldsIgnoreCase(clazz,propertyName,null);
		if(!ArrayUtil.isEmpty(fields) && Modifier.isStatic(fields[0].getModifiers())) {
			try {
                fields[0].set(null,value);
                return value;
            } catch (Exception e) {
                Caster.toPageException(e);
            }
		}
        // Getter
        MethodInstance mi = Reflector.getSetterEL(clazz,propertyName,value);
        if(mi!=null) {
            if(Modifier.isStatic(mi.getMethod().getModifiers())) {
                try {
                    return mi.invoke(null);
                } 
                catch (IllegalAccessException e) {
                    throw Caster.toPageException(e);
                } 
                catch (InvocationTargetException e) {
                    throw Caster.toPageException(e.getTargetException());
                }    
            }
        }
        

	    return variableUtil.set(pc,init(),propertyName,value);
	}

	/**
	 *
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(PageContext pc, Collection.Key propertyName, Object value) throws PageException {
		return set(pc, propertyName.toString(), value);
	}

    /**
     * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, java.lang.String, java.lang.Object)
     */
    public Object setEL(PageContext pc, String propertyName, Object value) {
		if(isInit) {
		    return variableUtil.setEL(pc,object,propertyName,value);
		}
	    // Field
		Field[] fields=Reflector.getFieldsIgnoreCase(clazz,propertyName,null);
		if(!ArrayUtil.isEmpty(fields) && Modifier.isStatic(fields[0].getModifiers())) {
			try {
                fields[0].set(null,value);
            } catch (Exception e) {}
			return value;
		}
        // Getter
        MethodInstance mi = Reflector.getSetterEL(clazz,propertyName,value);
        if(mi!=null) {
            if(Modifier.isStatic(mi.getMethod().getModifiers())) {
                try {
                    return mi.invoke(null);
                } 
                catch (Exception e) {}    
            }
        }
           
        try {
    	    return variableUtil.setEL(pc,init(),propertyName,value);
        } catch (PageException e1) {
            return value;
        }
    }

	/**
	 *
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(PageContext pc, Collection.Key propertyName, Object value) {
		return setEL(pc, propertyName.toString(), value);
	}

	/**
	 * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, java.lang.String, java.lang.Object[])
	 */
	public Object call(PageContext pc, String methodName, Object[] arguments) throws PageException {
        if(arguments==null)arguments=new Object[0];
        
        // init
        if(methodName.equalsIgnoreCase("init")) {
            return init(arguments);
        }
        else if(isInit) {
		    return Reflector.callMethod(object,methodName,arguments);
		}
        
        
	    try {
		    // get method
		    MethodInstance mi = Reflector.getMethodInstance(clazz,methodName,arguments);
			// call static method if exist
		    if(Modifier.isStatic(mi.getMethod().getModifiers())) {
				return mi.invoke(null);
			}
		    
		    if(arguments.length==0 && methodName.equalsIgnoreCase("getClass")){
		    	return clazz;
		    }
		    
		    // invoke constructor and call instance method
			return mi.invoke(init());
		}
		catch(InvocationTargetException e) {
			Throwable target = e.getTargetException();
			if(target instanceof PageException) throw (PageException)target;
			throw Caster.toPageException(e.getTargetException());
		}
		catch(Exception e) {
			throw Caster.toPageException(e);
		}
	}

	/**
	 *
	 * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	public Object call(PageContext pc, Collection.Key methodName, Object[] arguments) throws PageException {
		return call(pc, methodName.getString(), arguments);
	}

    /**
     * @see railo.runtime.type.Objects#callWithNamedValues(railo.runtime.PageContext, java.lang.String, railo.runtime.type.Struct)
     */
    public Object callWithNamedValues(PageContext pc, String methodName, Struct args) throws PageException {
        Collection.Key[] keys = args.keys();
        Object[] values=new Object[keys.length];
        for(int i=0;i<keys.length;i++) {
            values[i]=args.get(keys[i],null);
        }   
        return call(pc,methodName,values);
    }

	public Object callWithNamedValues(PageContext pc, Collection.Key methodName, Struct args) throws PageException {
		return callWithNamedValues(pc, methodName.getString(), args);
	}

	/**
	 * initialize method (default no object)
	 * @return initialize object
	 * @throws PageException
	 */
	private Object init() throws PageException {
		return init(new Object[0]);
	}
	
	private Object init(Object defaultValue) {
		return init(new Object[0],defaultValue);
	}
	
	/**
	 * initialize method
	 * @param arguments
	 * @return Initalised Object
	 * @throws PageException
	 */
	private Object init(Object[] arguments) throws PageException {
		object=Reflector.callConstructor(clazz,arguments);
		isInit=true;
		return object;
	}
	private Object init(Object[] arguments, Object defaultValue) {
		object=Reflector.callConstructor(clazz,arguments,defaultValue);
		isInit=object!=defaultValue;
		return object;
	}


	/**
	 * @see ObjectWrap#getEmbededObject()
	 */
	public Object getEmbededObject() throws PageException {
		if(object==null)init();
		return object;
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties props) {
		try {
			return DumpUtil.toDumpData(getEmbededObject(), pageContext,maxlevel,props);
		} catch (PageException e) {
			return DumpUtil.toDumpData(clazz, pageContext,maxlevel,props);
		}
	}
	
	/**
	 * @return the containing Class
	 */
	public Class getClazz() {return clazz;}

    /**
     * @see railo.runtime.type.Objects#isInitalized()
     */
    public boolean isInitalized() {
        return isInit;
    }

    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws PageException {
        return Caster.toString(getEmbededObject());
    }
    


    /**
     * @see railo.runtime.op.Castable#castToString(java.lang.String)
     */
    public String castToString(String defaultValue) {
    	try {
			return Caster.toString(getEmbededObject(),defaultValue);
		} catch (PageException e) {
			return defaultValue;
		}
    }
    

    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws PageException {
        return Caster.toBooleanValue(getEmbededObject());
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        try {
			return Caster.toBoolean(getEmbededObject(),defaultValue);
		} catch (PageException e) {
			return defaultValue;
		}
    }

    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws PageException {
        return Caster.toDoubleValue(getEmbededObject());
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        try {
			return Caster.toDoubleValue(getEmbededObject(),defaultValue);
		} catch (PageException e) {
			return defaultValue;
		}
    }

    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws PageException {
        return Caster.toDatetime(getEmbededObject(),null);
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        try {
			return DateCaster.toDateAdvanced(getEmbededObject(),true,null,defaultValue);
		} catch (PageException e) {
			return defaultValue;
		}
    }

    /**
     * @see railo.runtime.type.ObjectWrap#getEmbededObject(Object)
     */
    public Object getEmbededObject(Object def) {
    	if(object==null)init(def);
		return object;
    }

	/**
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(castToBooleanValue(), b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare((Date)castToDateTime(), (Date)dt);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return Operator.compare(castToDoubleValue(), d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return Operator.compare(castToString(), str);
	}

}