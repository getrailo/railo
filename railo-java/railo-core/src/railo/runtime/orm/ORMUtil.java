package railo.runtime.orm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import railo.commons.io.SystemUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.SystemOut;
import railo.runtime.Component;
import railo.runtime.ComponentPro;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.component.Property;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.Constants;
import railo.runtime.db.DataSource;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.listener.ApplicationContextPro;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.op.Operator;
import railo.runtime.orm.hibernate.ExceptionUtil;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;

public class ORMUtil {

	public static ORMSession getSession(PageContext pc) throws PageException {
		return getSession(pc,true);
	}
	
	public static ORMSession getSession(PageContext pc, boolean create) throws PageException {
		return ((PageContextImpl) pc).getORMSession(create);
	}

	public static ORMEngine getEngine(PageContext pc) throws PageException {
		ConfigImpl config=(ConfigImpl) pc.getConfig();
		return config.getORMEngine(pc);
	}

	/**
	 * 
	 * @param pc
	 * @param force if set to false the engine is on loaded when the configuration has changed
	 * @throws PageException
	 */
	public static void resetEngine(PageContext pc, boolean force) throws PageException {
		ConfigImpl config=(ConfigImpl) pc.getConfig();
		config.resetORMEngine(pc,force);
	}
	
	public static void printError(Throwable t, ORMEngine engine) {
		printError(t, engine, t.getMessage());
	}

	public static void printError(String msg, ORMEngine engine) {
		printError(null, engine, msg);
	}
	
	public static void printError(Throwable t) {
		printError(t, null, t.getMessage());
	}

	public static void printError(String msg) {
		printError(null, null, msg);
	}

	private static void printError(Throwable t, ORMEngine engine,String msg) {
		if(engine!=null)SystemOut.printDate("{"+engine.getLabel().toUpperCase()+"} - "+msg,SystemUtil.ERR);
		else SystemOut.printDate(msg, SystemUtil.ERR);
		if(t==null)t=new Throwable();
		t.printStackTrace(SystemOut.getPrinWriter(SystemUtil.ERR));
	}

	public static boolean equals(Object left, Object right) {
		HashSet<Object> done=new HashSet<Object>();
		return _equals(done, left, right);
	}
	
	private static boolean _equals(HashSet<Object> done,Object left, Object right) {
		
		if(left==right) return true;
		if(left==null || right==null) return false;
		
		// components
		if(left instanceof Component && right instanceof Component){
			return _equals(done,(Component)left, (Component)right);
		}

		// arrays
		if(Decision.isArray(left) && Decision.isArray(right)){
			return _equals(done,Caster.toArray(left,null), Caster.toArray(right,null));
		}

		// struct
		if(Decision.isStruct(left) && Decision.isStruct(right)){
			return _equals(done,Caster.toStruct(left,null), Caster.toStruct(right,null));
		}
		
		try {
			return Operator.equals(left,right,false);
		} catch (PageException e) {
			return false;
		}
	}
	
	private static boolean _equals(HashSet<Object> done,Collection left, Collection right) {
		if(done.contains(left)) return done.contains(right);
		done.add(left);
		done.add(right);
		
		if(left.size()!=right.size()) return false;
		//Key[] keys = left.keys();
		Iterator<Entry<Key, Object>> it = left.entryIterator();
		Entry<Key, Object> e;
		Object l,r;
		while(it.hasNext()){
			e = it.next();
			l=e.getValue();
			r=right.get(e.getKey(),null);
			if(r==null || !_equals(done,l, r)) return false;
		}
		return true;
	}
	
	private static boolean _equals(HashSet<Object> done,Component left, Component right) {
		if(done.contains(left)) return done.contains(right);
		done.add(left);
		done.add(right);
	 	
		if(left==null || right==null) return false;
		if(!left.getPageSource().equals(right.getPageSource())) return false;
		Property[] props = getProperties(left);
		Object l,r;
		props=getIds(props);
		for(int i=0;i<props.length;i++){
			l=left.getComponentScope().get(KeyImpl.init(props[i].getName()),null);
			r=right.getComponentScope().get(KeyImpl.init(props[i].getName()),null);
			if(!_equals(done,l, r)) return false;
		}
		return true;
	}
	
	public static Property[] getIds(Property[] props) {
		ArrayList<Property> ids=new ArrayList<Property>();
        for(int y=0;y<props.length;y++){
        	String fieldType = Caster.toString(props[y].getDynamicAttributes().get(KeyConstants._fieldtype,null),null);
			if("id".equalsIgnoreCase(fieldType) || ListUtil.listFindNoCaseIgnoreEmpty(fieldType,"id",',')!=-1)
				ids.add(props[y]);
		}
        
        // no id field defined
        if(ids.size()==0) {
        	String fieldType;
        	for(int y=0;y<props.length;y++){
        		fieldType = Caster.toString(props[y].getDynamicAttributes().get(KeyConstants._fieldtype,null),null);
    			if(StringUtil.isEmpty(fieldType,true) && props[y].getName().equalsIgnoreCase("id")){
    				ids.add(props[y]);
    				props[y].getDynamicAttributes().setEL(KeyConstants._fieldtype, "id");
    			}
    		}
        } 
        
        // still no id field defined
        if(ids.size()==0 && props.length>0) {
        	String owner = props[0].getOwnerName();
			if(!StringUtil.isEmpty(owner)) owner=ListUtil.last(owner, '.').trim();
        	
        	String fieldType;
        	if(!StringUtil.isEmpty(owner)){
        		String id=owner+"id";
        		for(int y=0;y<props.length;y++){
        			fieldType = Caster.toString(props[y].getDynamicAttributes().get(KeyConstants._fieldtype,null),null);
	    			if(StringUtil.isEmpty(fieldType,true) && props[y].getName().equalsIgnoreCase(id)){
	    				ids.add(props[y]);
	    				props[y].getDynamicAttributes().setEL(KeyConstants._fieldtype, "id");
	    			}
	    		}
        	}
        } 
        return ids.toArray(new Property[ids.size()]);
	}
	
	public static Object getPropertyValue(Component cfc, String name, Object defaultValue) {
		Property[] props=getProperties(cfc);
		
		for(int i=0;i<props.length;i++){
			if(!props[i].getName().equalsIgnoreCase(name)) continue;
			return cfc.getComponentScope().get(KeyImpl.getInstance(name),null);
		}
		return defaultValue;
	}
	/* jira2049
	public static Object getPropertyValue(ORMSession session,Component cfc, String name, Object defaultValue) {
		Property[] props=getProperties(cfc);
		Object raw=null;
		SessionImpl sess=null;
		if(session!=null){
			raw=session.getRawSession();
			if(raw instanceof SessionImpl)
				sess=(SessionImpl) raw;
		}
		Object val;
		for(int i=0;i<props.length;i++){
			if(!props[i].getName().equalsIgnoreCase(name)) continue;
			val = cfc.getComponentScope().get(KeyImpl.getInstance(name),null);
			if(sess!=null && !(val instanceof PersistentCollection)){
				if(val instanceof List)
					return new PersistentList(sess,(List)val);
				if(val instanceof Map && !(val instanceof Component))
					return new PersistentMap(sess,(Map)val);
				if(val instanceof Set)
					return new PersistentSet(sess,(Set)val);
				if(val instanceof Array)
					return new PersistentList(sess,Caster.toList(val,null));
					
			}
			return val;
		}
		return defaultValue;
	}*/

	private static Property[] getProperties(Component cfc) {
		if(cfc instanceof ComponentPro)
			return ((ComponentPro)cfc).getProperties(true,true,false,false);
		return cfc.getProperties(true);
	}
	
	public static boolean isRelated(Property prop) {
		String fieldType = Caster.toString(prop.getDynamicAttributes().get(KeyConstants._fieldtype,"column"),"column");
		if(StringUtil.isEmpty(fieldType,true)) return false;
		fieldType=fieldType.toLowerCase().trim();
		
		if("one-to-one".equals(fieldType)) 		return true;
		if("many-to-one".equals(fieldType)) 	return true;
		if("one-to-many".equals(fieldType)) 	return true;
		if("many-to-many".equals(fieldType)) 	return true;
		return false;
	}
	
	public static Struct convertToSimpleMap(String paramsStr) {
		paramsStr=paramsStr.trim();
        if(!StringUtil.startsWith(paramsStr, '{') || !StringUtil.endsWith(paramsStr, '}'))
        	return null;
        	
		paramsStr = paramsStr.substring(1, paramsStr.length() - 1);
		String items[] = ListUtil.listToStringArray(paramsStr, ','); 
		
		Struct params=new StructImpl();
		String arr$[] = items;
		int index;
        for(int i = 0; i < arr$.length; i++)	{
            String pair = arr$[i];
            index = pair.indexOf('=');
            if(index == -1) return null;
            
            params.setEL(
            		KeyImpl.init(deleteQuotes(pair.substring(0, index).trim()).trim()), 
            		deleteQuotes(pair.substring(index + 1).trim()));
        }

        return params;
    }
	
	private static String deleteQuotes(String str)	{
        if(StringUtil.isEmpty(str,true))return "";
        char first=str.charAt(0);
        if((first=='\'' || first=='"') && StringUtil.endsWith(str, first))
        	return str.substring(1, str.length() - 1);
        return str;
    }
	
	public static DataSource getDataSource(PageContext pc) throws PageException{
		pc=ThreadLocalPageContext.get(pc);
		Object o=((ApplicationContextPro)pc.getApplicationContext()).getORMDataSource();
		
		if(StringUtil.isEmpty(o))
			throw ExceptionUtil.createException(ORMUtil.getSession(pc),null,"missing datasource defintion in "+Constants.APP_CFC+"/"+Constants.CFAPP_NAME,null);
		return o instanceof DataSource?(DataSource)o:((PageContextImpl)pc).getDataSource(Caster.toString(o));
	
		
	
	
	}
	
	public static DataSource getDataSource(PageContext pc, DataSource defaultValue) {
		pc=ThreadLocalPageContext.get(pc);
		Object o=((ApplicationContextPro)pc.getApplicationContext()).getORMDataSource();
		if(StringUtil.isEmpty(o))
			return defaultValue;
		try {
			return o instanceof DataSource?(DataSource)o:((PageContextImpl)pc).getDataSource(Caster.toString(o));
		}
		catch (PageException e) {
			return defaultValue;
		}
	}
}
