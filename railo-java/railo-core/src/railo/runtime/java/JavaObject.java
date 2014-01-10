package railo.runtime.java;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import railo.runtime.util.VariableUtilImpl;


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

	@Override
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

	@Override
	public Object get(PageContext pc, Collection.Key key, Object defaultValue) {
		return get(pc, key.getString(), defaultValue);
	}

	@Override
	public Object set(PageContext pc, Collection.Key propertyName, Object value) throws PageException  {
		if(isInit) {
		    return ((VariableUtilImpl)variableUtil).set(pc,object,propertyName,value);
		}
	    // Field
		Field[] fields=Reflector.getFieldsIgnoreCase(clazz,propertyName.getString(),null);
		if(!ArrayUtil.isEmpty(fields) && Modifier.isStatic(fields[0].getModifiers())) {
			try {
                fields[0].set(null,value);
                return value;
            } catch (Exception e) {
                Caster.toPageException(e);
            }
		}
        // Getter
        MethodInstance mi = Reflector.getSetter(clazz,propertyName.getString(),value,null);
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
        

	    return ((VariableUtilImpl)variableUtil).set(pc,init(),propertyName,value);
	}

    @Override
    public Object setEL(PageContext pc, Collection.Key propertyName, Object value) {
		if(isInit) {
		    return variableUtil.setEL(pc,object,propertyName,value);
		}
	    // Field
		Field[] fields=Reflector.getFieldsIgnoreCase(clazz,propertyName.getString(),null);
		if(!ArrayUtil.isEmpty(fields) && Modifier.isStatic(fields[0].getModifiers())) {
			try {
                fields[0].set(null,value);
            } catch (Exception e) {}
			return value;
		}
        // Getter
        MethodInstance mi = Reflector.getSetter(clazz,propertyName.getString(),value,null);
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
		    MethodInstance mi = Reflector.getMethodInstance(this,clazz,methodName,arguments);
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

	@Override
	public Object call(PageContext pc, Collection.Key methodName, Object[] arguments) throws PageException {
		return call(pc, methodName.getString(), arguments);
	}

    public Object callWithNamedValues(PageContext pc, String methodName, Struct args) throws PageException {
        Iterator<Object> it = args.valueIterator();
    	List<Object> values=new ArrayList<Object>();
        while(it.hasNext()) {
            values.add(it.next());
        }   
        return call(pc,methodName,values.toArray(new Object[values.size()]));
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


	@Override
	public Object getEmbededObject() throws PageException {
		if(object==null)init();
		return object;
	}

	@Override
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

    public boolean isInitalized() {
        return isInit;
    }

    @Override
    public String castToString() throws PageException {
        return Caster.toString(getEmbededObject());
    }
    


    @Override
    public String castToString(String defaultValue) {
    	try {
			return Caster.toString(getEmbededObject(),defaultValue);
		} catch (PageException e) {
			return defaultValue;
		}
    }
    

    @Override
    public boolean castToBooleanValue() throws PageException {
        return Caster.toBooleanValue(getEmbededObject());
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        try {
			return Caster.toBoolean(getEmbededObject(),defaultValue);
		} catch (PageException e) {
			return defaultValue;
		}
    }

    @Override
    public double castToDoubleValue() throws PageException {
        return Caster.toDoubleValue(getEmbededObject());
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        try {
			return Caster.toDoubleValue(getEmbededObject(),defaultValue);
		} catch (PageException e) {
			return defaultValue;
		}
    }

    @Override
    public DateTime castToDateTime() throws PageException {
        return Caster.toDatetime(getEmbededObject(),null);
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        try {
			return DateCaster.toDateAdvanced(getEmbededObject(),true,null,defaultValue);
		} catch (PageException e) {
			return defaultValue;
		}
    }

    @Override
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

	@Override
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(castToBooleanValue(), b);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare((Date)castToDateTime(), (Date)dt);
	}

	@Override
	public int compareTo(double d) throws PageException {
		return Operator.compare(castToDoubleValue(), d);
	}

	@Override
	public int compareTo(String str) throws PageException {
		return Operator.compare(castToString(), str);
	}

}