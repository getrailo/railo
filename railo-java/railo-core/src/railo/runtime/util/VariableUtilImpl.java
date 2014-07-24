package railo.runtime.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.reflection.Reflector;
import railo.runtime.security.SecurityManager;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.text.xml.struct.XMLStructFactory;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.FunctionValue;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Objects;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.Struct;
import railo.runtime.type.UDFPlus;
import railo.runtime.type.scope.Undefined;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.Type;
import railo.runtime.type.wrap.MapAsStruct;

/**
 * Class to handle CF Variables (set,get,call)
 */
public final class VariableUtilImpl implements VariableUtil {

    @Override
	public Object getCollection(PageContext pc, Object coll, String key, Object defaultValue) {
        if(coll instanceof Query) {
        	// TODO sollte nicht null sein
            return ((Query)coll).getColumn(key,null);
        }
        return get(pc,coll,key,defaultValue);
    }
    
    public Object getCollection(PageContext pc, Object coll, Collection.Key key, Object defaultValue) {// FUTURE add to interface
        if(coll instanceof Query) {
        	QueryColumn qc = ((Query)coll).getColumn(key,null);
        	if(qc==null) return defaultValue;
        	return qc;
        }
        return get(pc,coll,key,defaultValue);
    }

    @Override
	public Object get(PageContext pc, Object coll, String key, Object defaultValue) {
        // Objects
        if(coll instanceof Objects) {
            return ((Objects)coll).get(pc,KeyImpl.init(key),defaultValue);
        }
		// Collection
        else if(coll instanceof Collection) {
            return ((Collection)coll).get(key,defaultValue);
        } 
		// Map
		else if(coll instanceof Map) {
			Object rtn=((Map)coll).get(key);
			//if(rtn==null)rtn=((Map)coll).get(MapAsStruct.getCaseSensitiveKey((Map)coll, key));
			if(rtn!=null) return rtn;
			return defaultValue;
		} 
		// List
		else if(coll instanceof List) {
			int index=Caster.toIntValue(key,Integer.MIN_VALUE);
		    if(index==Integer.MIN_VALUE) return defaultValue;
		    try {
		        return ((List)coll).get(index-1);
		    }
		    catch(IndexOutOfBoundsException e) {
		        return defaultValue;
		    }
		}
		// Native Array
		else if(Decision.isNativeArray(coll)) {
			return ArrayUtil.get(coll,Caster.toIntValue(key,Integer.MIN_VALUE)-1,defaultValue);
		}
		// Node
		else if(coll instanceof Node) {
		    return XMLStructFactory.newInstance((Node)coll,false).get(key,defaultValue);
		}
        // Direct Object Access
        if(pc.getConfig().getSecurityManager().getAccess(SecurityManager.TYPE_DIRECT_JAVA_ACCESS)==SecurityManager.VALUE_YES) {
			return Reflector.getProperty(coll,key,defaultValue);
		}
		return null;
		
	}
	
    
    @Override
	public Object get(PageContext pc, Object coll, Collection.Key key, Object defaultValue) {
        // Objects
		//print.out("key:"+key.getString());
		if(coll instanceof Objects) {
            return ((Objects)coll).get(pc,key,defaultValue);
        }
		// Collection
        else if(coll instanceof Collection) {
            return ((Collection)coll).get(key,defaultValue);
        } 
		// Map
		else if(coll instanceof Map) {
			
			Object rtn=((Map)coll).get(key.getString());
			//if(rtn==null)rtn=((Map)coll).get(MapAsStruct.getCaseSensitiveKey((Map)coll, key.getString()));
			if(rtn!=null) return rtn;
			return defaultValue;
			
		} 
		// List
		else if(coll instanceof List) {
			int index=Caster.toIntValue(key,Integer.MIN_VALUE);
		    if(index==Integer.MIN_VALUE) return defaultValue;
		    try {
		        return ((List)coll).get(index-1);
		    }
		    catch(IndexOutOfBoundsException e) {
		        return defaultValue;
		    }
		}
		// Native Array
		else if(Decision.isNativeArray(coll)) {
			return ArrayUtil.get(coll,Caster.toIntValue(key,Integer.MIN_VALUE)-1,defaultValue);
		}
		// Node
		else if(coll instanceof Node) {
		    return XMLStructFactory.newInstance((Node)coll,false).get(key,defaultValue);
		}
        // Direct Object Access
        if(pc.getConfig().getSecurityManager().getAccess(SecurityManager.TYPE_DIRECT_JAVA_ACCESS)==SecurityManager.VALUE_YES) {
			return Reflector.getProperty(coll,key.getString(),defaultValue);
		}
		return null;
		
	}
	
	public Object getLight(PageContext pc, Object coll, Collection.Key key, Object defaultValue) {
        // Objects
        if(coll instanceof Objects) {
            return ((Objects)coll).get(pc,key,defaultValue);
        }
		// Collection
        else if(coll instanceof Collection) {
			return ((Collection)coll).get(key,defaultValue);
		} 
		// Map
		else if(coll instanceof Map) {
			//Object rtn=null;
			try {
				Object rtn=((Map)coll).get(key.getString());
				//if(rtn==null)rtn=((Map)coll).get(MapAsStruct.getCaseSensitiveKey((Map)coll, key.getString()));
				if(rtn!=null) return rtn;
			}
			catch(Throwable t) {}
			return Reflector.getField(coll,key.getString(),defaultValue);
			//return rtn;
		} 
		// List
		else if(coll instanceof List) {
			int index=Caster.toIntValue(key.getString(),Integer.MIN_VALUE);
		    if(index==Integer.MIN_VALUE) return null;
		    try {
		        return ((List)coll).get(index-1);
		    }
		    catch(IndexOutOfBoundsException e) {
		        return defaultValue;
		    }
		}
		return defaultValue;
	}
	
	@Override
	public Object getLight(PageContext pc, Object coll, String key, Object defaultValue) {
        // Objects
        if(coll instanceof Objects) {
            return ((Objects)coll).get(pc,KeyImpl.init(key),defaultValue);
        }
		// Collection
        else if(coll instanceof Collection) {
			return ((Collection)coll).get(key,defaultValue);
		} 
		// Map
		else if(coll instanceof Map) {
			try {
				Object rtn=((Map)coll).get(key);
				//if(rtn==null)rtn=((Map)coll).get(MapAsStruct.getCaseSensitiveKey((Map)coll, key));
				if(rtn!=null) return rtn;
			}
			catch(Throwable t) {}
			return Reflector.getProperty(coll,key,defaultValue);
			//return rtn;
		} 
		// List
		else if(coll instanceof List) {
			int index=Caster.toIntValue(key,Integer.MIN_VALUE);
		    if(index==Integer.MIN_VALUE) return null;
		    try {
		        return ((List)coll).get(index-1);
		    }
		    catch(IndexOutOfBoundsException e) {
		        return defaultValue;
		    }
		}
		return defaultValue;
	}
	
	@Override
	public Object getCollection(PageContext pc, Object coll, String key) throws PageException {
        if(coll instanceof Query) {
            return ((Query)coll).getColumn(key);
        }
        return get(pc,coll,key);
    }
    public Object getCollection(PageContext pc, Object coll, Collection.Key key) throws PageException {
        if(coll instanceof Query) {
            return ((Query)coll).getColumn(key);
        }
        return get(pc,coll,key);
    }
    
    public Object get(PageContext pc, Object coll, Collection.Key key) throws PageException {
        // Objects
        if(coll instanceof Objects) {
            return ((Objects)coll).get(pc,key);
        }
        // Collection
        else if(coll instanceof Collection) {
            return ((Collection)coll).get(key);
		} 
		// Map
		else if(coll instanceof Map) {
			Object rtn=null;
			try {
				rtn=((Map)coll).get(key.getString());
				if(rtn==null && coll.getClass().getName().startsWith("org.hibernate."))
					rtn=((Map)coll).get(MapAsStruct.getCaseSensitiveKey((Map)coll, key.getString()));
				if(rtn!=null) return rtn;
			}
			catch(Throwable t) {}
			rtn = Reflector.getProperty(coll,key.getString(),null);
			if(rtn!=null) return rtn;
			
			String realKey = MapAsStruct.getCaseSensitiveKey((Map)coll, key.getString());
			String detail=null;
			if(realKey!=null) {
				detail="The keys for this Map are case-sensitive, use bracket notation like this \"map['"+realKey+"']\" instead of dot notation like this  \"map."+realKey+"\" to address the Map";
			}
			
			throw new ExpressionException("Key ["+key.getString()+"] doesn't exist in Map ("+((Map)coll).getClass().getName()+")",detail);
		} 
		// List
		else if(coll instanceof List) {
		    try {
		        Object rtn=((List)coll).get(Caster.toIntValue(key.getString())-1);
		        if(rtn==null) throw new ExpressionException("Key ["+key.getString()+"] doesn't exist in List");
				return rtn;
		    }
		    catch(IndexOutOfBoundsException e) {
		        throw new ExpressionException("Key ["+key.getString()+"] doesn't exist in List");
		    }
		}
		// Native Array
		else if(Decision.isNativeArray(coll)) {
			Object rtn=ArrayUtil.get(coll,Caster.toIntValue(key.getString())-1,null);
			if(rtn==null) throw new ExpressionException("Key ["+key.getString()+"] doesn't exist in Native Array");
			return rtn;
		}
        // Node
		else if(coll instanceof Node) {
			//print.out("get:"+key);
            return XMLStructFactory.newInstance((Node)coll,false).get(key);
        }
        // HTTPSession
		/*else if(coll instanceof HttpSession) {
            return ((HttpSession)coll).getAttribute(key.getString());
        }*/
        
        
        
        // Direct Object Access
		if(coll!=null && pc.getConfig().getSecurityManager().getAccess(SecurityManager.TYPE_DIRECT_JAVA_ACCESS)==SecurityManager.VALUE_YES) {
			return Reflector.getProperty(coll,key.getString());
		}
		throw new ExpressionException("No matching property ["+key.getString()+"] found");
	
    }
    
    @Override
	public Object get(PageContext pc, Object coll, String key) throws PageException {
        // Objects
        if(coll instanceof Objects) {
            return ((Objects)coll).get(pc,KeyImpl.init(key));
        }
        // Collection
        else if(coll instanceof Collection) {
            
			return ((Collection)coll).get(KeyImpl.init(key));
		} 
		// Map
		else if(coll instanceof Map) {
			Object rtn=null;
			try {
				rtn=((Map)coll).get(key);
				//if(rtn==null)rtn=((Map)coll).get(MapAsStruct.getCaseSensitiveKey((Map)coll, key));
				if(rtn!=null) return rtn;
				
			}
			catch(Throwable t) {}
			rtn = Reflector.getProperty(coll,key,null);
			if(rtn!=null) return rtn;
			throw new ExpressionException("Key ["+key+"] doesn't exist in Map ("+Caster.toClassName(coll)+")","keys are ["+keyList(((Map)coll))+"]");
		} 
		// List
		else if(coll instanceof List) {
		    try {
		        Object rtn=((List)coll).get(Caster.toIntValue(key)-1);
		        if(rtn==null) throw new ExpressionException("Key ["+key+"] doesn't exist in List");
				return rtn;
		    }
		    catch(IndexOutOfBoundsException e) {
		        throw new ExpressionException("Key ["+key+"] doesn't exist in List");
		    }
		}
		// Native Array
		else if(Decision.isNativeArray(coll)) {
			Object rtn=ArrayUtil.get(coll,Caster.toIntValue(key)-1,null);
			if(rtn==null) throw new ExpressionException("Key ["+key+"] doesn't exist in Native Array");
			return rtn;
		}
        // Node
        else if(coll instanceof Node) {
            return XMLStructFactory.newInstance((Node)coll,false).get(key);
        }
        // Direct Object Access
		if(pc.getConfig().getSecurityManager().getAccess(SecurityManager.TYPE_DIRECT_JAVA_ACCESS)==SecurityManager.VALUE_YES) {
			return Reflector.getProperty(coll,key);
		}
		throw new ExpressionException("No matching property ["+key+"] found");
	
    }


    private String keyList(Map map) {
    	StringBuffer sb=new StringBuffer();
		Iterator it = map.keySet().iterator();
		while(it.hasNext()) {
			if(sb.length()>0)sb.append(',');
			sb.append(it.next().toString());
		}
		return sb.toString();
	}

	public Object set(PageContext pc, Object coll, Collection.Key key,Object value) throws PageException {
        // Objects
        if(coll instanceof Objects) { 
            ((Objects)coll).set(pc,key,value);
            return value;
        }
        // Collection
        else if(coll instanceof Collection) { 
            ((Collection)coll).set(key,value);
            return value;
        }
		// Map
		else if(coll instanceof Map) {
			/* no idea why this is here
			try {
				Reflector.setProperty(coll,key.getString(),value);
				return value;
			}
			catch(Throwable t) {t.printStackTrace();}*/
			((Map)coll).put(key.getString(),value);
			return value;
		} 
		// List
		else if(coll instanceof List) {
		    List list=((List)coll);
		    int index=Caster.toIntValue(key.getString());
		    if(list.size()>=index)list.set(index-1,value);
		    else {
		        while(list.size()<index-1)list.add(null);
		        list.add(value);
		    }
			return value;
		}
		// Native Array
		else if(Decision.isNativeArray(coll)) {
			try {
                return ArrayUtil.set(coll,Caster.toIntValue(key.getString())-1,value);
            } catch (Exception e) {
                throw new ExpressionException("invalid index ["+key.getString()+"] for Native Array, can't expand Native Arrays");
            }
		}
		// Node
		else if(coll instanceof Node) {
			return XMLUtil.setProperty((Node)coll,key,value);
		}
        // Direct Object Access
		if(pc.getConfig().getSecurityManager().getAccess(SecurityManager.TYPE_DIRECT_JAVA_ACCESS)==SecurityManager.VALUE_YES) {
			try {
		        Reflector.setProperty(coll,key.getString(),value);
		        return value;
		    }
		    catch(PageException pe) {} 
		}	
		throw new ExpressionException("can't assign value to a Object of this type ["+Type.getName(coll)+"] with key "+key.getString());
    }
    
    
    /**
     * @see railo.runtime.util.VariableUtil#set(railo.runtime.PageContext, java.lang.Object, java.lang.String, java.lang.Object)
     */
    public Object set(PageContext pc, Object coll, String key,Object value) throws PageException {
        // Objects
        if(coll instanceof Objects) { 
            ((Objects)coll).set(pc,KeyImpl.init(key),value);
            return value;
        }
        // Collection
        else if(coll instanceof Collection) { 
            ((Collection)coll).set(key,value);
            return value;
        }
		// Map
		else if(coll instanceof Map) {
			/*try {
				Reflector.setProperty(coll,key,value);
				return value;
			}
			catch(Throwable t) {}*/
			((Map)coll).put(key,value);
			return value;
		} 
		// List
		else if(coll instanceof List) {
		    List list=((List)coll);
		    int index=Caster.toIntValue(key);
		    if(list.size()>=index)list.set(index-1,value);
		    else {
		        while(list.size()<index-1)list.add(null);
		        list.add(value);
		    }
			return value;
		}
		// Native Array
		else if(Decision.isNativeArray(coll)) {
			try {
                return ArrayUtil.set(coll,Caster.toIntValue(key)-1,value);
            } catch (Exception e) {
                throw new ExpressionException("invalid index ["+key+"] for Native Array, can't expand Native Arrays");
            }
		}
		// Node
		else if(coll instanceof Node) {
			return XMLUtil.setProperty((Node)coll,KeyImpl.init(key),value);
		}
        // Direct Object Access
		if(pc.getConfig().getSecurityManager().getAccess(SecurityManager.TYPE_DIRECT_JAVA_ACCESS)==SecurityManager.VALUE_YES) {
			try {
		        Reflector.setProperty(coll,key,value);
		        return value;
		    }
		    catch(PageException pe) {} 
		}	
		throw new ExpressionException("can't assign value to a Object of this type ["+Type.getName(coll)+"] with key "+key);
    }
    
    /**
     *
     * @see railo.runtime.util.VariableUtil#setEL(railo.runtime.PageContext, java.lang.Object, java.lang.String, java.lang.Object)
     */
    public Object setEL(PageContext pc, Object coll, String key,Object value) {
        // Objects
        if(coll instanceof Objects) { 
            ((Objects)coll).setEL(pc,KeyImpl.init(key),value);
            return value;
        }
        // Collection
        else if(coll instanceof Collection) { 
			((Collection)coll).setEL(KeyImpl.init(key),value);
			return value;
		}
		// Map
		else if(coll instanceof Map) {
			try {
				Reflector.setProperty(coll,key,value);
				return value;
			}
			catch(Throwable t) {}
			((Map)coll).put(key,value);
			return value;
		} 
		// List
		else if(coll instanceof List) {
		    List list=((List)coll);
		    int index=Caster.toIntValue(key,Integer.MIN_VALUE);
		    if(index==Integer.MIN_VALUE) return null;
		    if(list.size()>=index)list.set(index-1,value);
		    else {
		        while(list.size()<index-1)list.add(null);
		        list.add(value);
		    }
			return value;
		}
		// Native Array
		else if(Decision.isNativeArray(coll)) {
			return ArrayUtil.setEL(coll,Caster.toIntValue(key,Integer.MIN_VALUE)-1,value);
		}
		// Node
		else if(coll instanceof Node) {
			return XMLUtil.setPropertyEL((Node)coll,KeyImpl.init(key),value);
		}
        // Direct Object Access
		if(pc.getConfig().getSecurityManager().getAccess(SecurityManager.TYPE_DIRECT_JAVA_ACCESS)==SecurityManager.VALUE_YES) {
			Reflector.setPropertyEL(coll,key,value);
			return value; 
		}
		return null;
    }
    
    /**
     * @see railo.runtime.util.VariableUtil#setEL(railo.runtime.PageContext, java.lang.Object, railo.runtime.type.Collection.Key, java.lang.Object)
     */
    public Object setEL(PageContext pc, Object coll, Collection.Key key,Object value) {
        // Objects
        if(coll instanceof Objects) { 
            ((Objects)coll).setEL(pc,key,value);
            return value;
        }
        // Collection
        else if(coll instanceof Collection) { 
			((Collection)coll).setEL(key,value);
			return value;
		}
		// Map
		else if(coll instanceof Map) {
			try {
				Reflector.setProperty(coll,key.getString(),value);
				return value;
			}
			catch(Throwable t) {}
			((Map)coll).put(key,value);
			return value;
		} 
		// List
		else if(coll instanceof List) {
		    List list=((List)coll);
		    int index=Caster.toIntValue(key,Integer.MIN_VALUE);
		    if(index==Integer.MIN_VALUE) return null;
		    if(list.size()>=index)list.set(index-1,value);
		    else {
		        while(list.size()<index-1)list.add(null);
		        list.add(value);
		    }
			return value;
		}
		// Native Array
		else if(Decision.isNativeArray(coll)) {
			return ArrayUtil.setEL(coll,Caster.toIntValue(key,Integer.MIN_VALUE)-1,value);
		}
		// Node
		else if(coll instanceof Node) {
			return XMLUtil.setPropertyEL((Node)coll,key,value);
		}
        // Direct Object Access
		if(pc.getConfig().getSecurityManager().getAccess(SecurityManager.TYPE_DIRECT_JAVA_ACCESS)==SecurityManager.VALUE_YES) {
			Reflector.setPropertyEL(coll,key.getString(),value);
			return value; 
		}
		return null;
    }

    /**
     *
     * @see railo.runtime.util.VariableUtil#removeEL(java.lang.Object, java.lang.String)
     */
    public Object removeEL(Object coll, String key) {
        // Collection
        if(coll instanceof Collection) { 
			return ((Collection)coll).removeEL(KeyImpl.init(key));
		}
		// Map
		else if(coll instanceof Map) { 
			Object obj = ((Map)coll).remove(key);
			//if(obj==null)obj=((Map)coll).remove(MapAsStruct.getCaseSensitiveKey((Map)coll, key));
			return obj;
		}
		// List
		else if(coll instanceof List) {
		    int i=Caster.toIntValue(key,Integer.MIN_VALUE);
		    if(i==Integer.MIN_VALUE) return null;
		    return ((List)coll).remove(i);
		}
		return null;
    }
    
    public Object removeEL(Object coll, Collection.Key key) {
        // Collection
        if(coll instanceof Collection) { 
			return ((Collection)coll).removeEL(key);
		}
		// Map
		else if(coll instanceof Map) { 
			Object obj = ((Map)coll).remove(key.getString());
			//if(obj==null)obj=((Map)coll).remove(MapAsStruct.getCaseSensitiveKey((Map)coll, key));
			return obj;
		}
		// List
		else if(coll instanceof List) {
		    int i=Caster.toIntValue(key,Integer.MIN_VALUE);
		    if(i==Integer.MIN_VALUE) return null;
		    return ((List)coll).remove(i);
		}
		return null;
    }
    

    /**
     * @see railo.runtime.util.VariableUtil#remove(java.lang.Object, java.lang.String)
     */
    public Object remove(Object coll, String key) throws PageException {
        // Collection
		if(coll instanceof Collection) { 
			return ((Collection)coll).remove(KeyImpl.init(key));
		}
		// Map
		else if(coll instanceof Map) { 
			Object obj=((Map)coll).remove(key);
			//if(obj==null)obj=((Map)coll).remove(MapAsStruct.getCaseSensitiveKey((Map)coll, key));
			if(obj==null) throw new ExpressionException("can't remove key ["+key+"] from map");
			return obj;
		}
		// List
		else if(coll instanceof List) {
		    int i=Caster.toIntValue(key);
		    Object obj=((List)coll).remove(i);
			if(obj==null) throw new ExpressionException("can't remove index ["+key+"] from list");
			return obj;
		}
		/*/ Native Array TODO this below
		else if(Decision.isNativeArray(o)) {
			try {
				return ArrayUtil.set(o,Caster.toIntValue(key)-1,value);
			} catch (Exception e) {
				return getDirectProperty(o, key, new ExpressionException("Key doesn't exist in Native Array"),false);
			}
		}*/
		// TODO Support for Node
		throw new ExpressionException("can't remove key ["+key+"] from Object of type ["+Caster.toTypeName(coll)+"]");
    }
    
    public Object remove(Object coll, Collection.Key key) throws PageException {
        // Collection
		if(coll instanceof Collection) { 
			return ((Collection)coll).remove(key);
		}
		// Map
		else if(coll instanceof Map) { 
			Object obj=((Map)coll).remove(key.getString());
			//if(obj==null)obj=((Map)coll).remove(MapAsStruct.getCaseSensitiveKey((Map)coll, key));
			if(obj==null) throw new ExpressionException("can't remove key ["+key+"] from map");
			return obj;
		}
		// List
		else if(coll instanceof List) {
		    int i=Caster.toIntValue(key);
		    Object obj=((List)coll).remove(i);
			if(obj==null) throw new ExpressionException("can't remove index ["+key+"] from list");
			return obj;
		}
		/*/ Native Array TODO this below
		else if(Decision.isNativeArray(o)) {
			try {
				return ArrayUtil.set(o,Caster.toIntValue(key)-1,value);
			} catch (Exception e) {
				return getDirectProperty(o, key, new ExpressionException("Key doesn't exist in Native Array"),false);
			}
		}*/
		// TODO Support for Node
		throw new ExpressionException("can't remove key ["+key+"] from Object of type ["+Caster.toTypeName(coll)+"]");
    }
    
    /**
     * @see railo.runtime.util.VariableUtil#callFunction(railo.runtime.PageContext, java.lang.Object, java.lang.String, java.lang.Object[])
     */
    public Object callFunction(PageContext pc, Object coll, String key, Object[] args) throws PageException {
		if(args.length>0 && args[0] instanceof FunctionValue)
			return callFunctionWithNamedValues(pc, coll, key, args);
		return callFunctionWithoutNamedValues(pc, coll, key, args);
	}
    
    /**
     * @see railo.runtime.util.VariableUtil#callFunctionWithoutNamedValues(railo.runtime.PageContext, java.lang.Object, java.lang.String, java.lang.Object[])
     */
	public Object callFunctionWithoutNamedValues(PageContext pc, Object coll, String key, Object[] args) throws PageException {
	    return callFunctionWithoutNamedValues(pc, coll, KeyImpl.init(key), args);
	}
	
	public Object callFunctionWithoutNamedValues(PageContext pc, Object coll, Collection.Key key, Object[] args) throws PageException {
		// Objects
        if(coll instanceof Objects) {
        	return ((Objects)coll).call(pc,key,args);
        }
        // call UDF
	    Object prop=getLight(pc,coll,key,null);	
	    if(prop instanceof UDFPlus) {
	    	return ((UDFPlus)prop).call(pc,key,args,false);
		}
        // call Object Wrapper      
	    if(pc.getConfig().getSecurityManager().getAccess(SecurityManager.TYPE_DIRECT_JAVA_ACCESS)==SecurityManager.VALUE_YES) {
	    	if(!(coll instanceof Undefined))return Reflector.callMethod(coll,key,args);
	    }
		throw new ExpressionException("No matching Method/Function for "+key+"("+Reflector.getDspMethods(Reflector.getClasses(args))+")");

    
	}
	
	/**
     * @see railo.runtime.util.VariableUtil#callFunctionWithNamedValues(railo.runtime.PageContext, java.lang.Object, java.lang.String, java.lang.Object[])
     */
	public Object callFunctionWithNamedValues(PageContext pc, Object coll, String key, Object[] args) throws PageException {
		return callFunctionWithNamedValues(pc, coll, KeyImpl.init(key), args);
	}

	public Object callFunctionWithNamedValues(PageContext pc, Object coll, Collection.Key key, Object[] args) throws PageException {
		// Objects
        if(coll instanceof Objects) {
            return ((Objects)coll).callWithNamedValues(pc,key, Caster.toFunctionValues(args));
        }
        // call UDF
		Object prop=getLight(pc,coll,key,null);	
        if(prop instanceof UDFPlus) 		{
            return ((UDFPlus)prop).callWithNamedValues(pc,key,Caster.toFunctionValues(args),false);
        }
        throw new ExpressionException("No matching Method/Function ["+key+"] for call with named arguments found ");
	}

	public Object callFunctionWithNamedValues(PageContext pc, Object coll, Collection.Key key, Struct args) throws PageException {
		// Objects
        if(coll instanceof Objects) {
            return ((Objects)coll).callWithNamedValues(pc,key, args);
        }
        // call UDF
		Object prop=getLight(pc,coll,key,null);	
        if(prop instanceof UDFPlus) 		{
            return ((UDFPlus)prop).callWithNamedValues(pc,key,args,false);
        }
        throw new ExpressionException("No matching Method/Function for call with named arguments found");
	}
	
	// used by generated bytecode
	public static Object recordcount(PageContext pc,Object obj) throws PageException{
		if(obj instanceof Query) return Caster.toDouble(((Query)obj).getRecordcount());
		return pc.getCollection(obj, KeyConstants._RECORDCOUNT);
	}
	// used by generated bytecode
	public static Object currentrow(PageContext pc,Object obj) throws PageException{
		if(obj instanceof Query) return Caster.toDouble(((Query)obj).getCurrentrow(pc.getId()));
		return pc.getCollection(obj, KeyConstants._CURRENTROW);
	}
	// used by generated bytecode
	public static Object columnlist(PageContext pc,Object obj) throws PageException{
		if(obj instanceof Query) {
			Key[] columnNames = ((Query)obj).getColumnNames();
			
			boolean upperCase=true;//((ConfigImpl)ThreadLocalPageContext.getConfig()).getDotNotationUpperCase();
			
			
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<columnNames.length;i++) {
				if(i>0)sb.append(',');
				sb.append(upperCase?columnNames[i].getUpperString():columnNames[i].getString());
			}
			return sb.toString();
			
			
		}
		return pc.getCollection(obj, KeyConstants._COLUMNLIST);
	}
    
}