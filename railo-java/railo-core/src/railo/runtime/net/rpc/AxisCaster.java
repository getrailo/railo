package railo.runtime.net.rpc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.TypeMapping;

import org.apache.axis.Constants;
import org.apache.axis.types.Day;
import org.apache.axis.types.Duration;
import org.apache.axis.types.Entities;
import org.apache.axis.types.Entity;
import org.apache.axis.types.Language;
import org.apache.axis.types.Month;
import org.apache.axis.types.MonthDay;
import org.apache.axis.types.NCName;
import org.apache.axis.types.NMToken;
import org.apache.axis.types.NMTokens;
import org.apache.axis.types.Name;
import org.apache.axis.types.Token;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.axis.types.Year;
import org.apache.axis.types.YearMonth;

import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.ComponentScope;
import railo.runtime.ComponentWrap;
import railo.runtime.PageContext;
import railo.runtime.component.Property;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.CFMLExpressionInterpreter;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.op.date.DateCaster;
import railo.runtime.reflection.Reflector;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.scope.Argument;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.ComponentUtil;
import coldfusion.xml.rpc.QueryBean;

/**
 * Axis Type Caster
 */
public final class AxisCaster {

	/**
     * cast a value to a Axis Compatible Type
     * @param type
     * @param value
     * @return Axis Compatible Type
     * @throws PageException
     */
    public static Object toAxisType(TypeMapping tm,TimeZone tz,QName type, Object value, Class targetClass) throws PageException {
		return _toAxisType(tm, tz, type, value, targetClass,new HashSet<Object>());
	}

    public static Object toAxisType(TypeMapping tm,Object value,Class targetClass) throws PageException {
    	return _toAxisType(tm,null,null, value, targetClass, new HashSet<Object>());
    }
	
    /**
     * cast a value to a Axis Compatible Type
     * @param type
     * @param value
     * @return Axis Compatible Type
     * @throws PageException
     */
    private static Object _toAxisType(TypeMapping tm,TimeZone tz,QName type, Object value, Class targetClass,Set<Object> done) throws PageException {
        if(done.contains(value)){
			return null;// TODO not sure what in this case is the best solution.
		}
    	
		done.add(value);
    	try{
    	
	    	if(type!=null) {
		        // XSD
		        for(int i=0;i<Constants.URIS_SCHEMA_XSD.length;i++) {
		            if(Constants.URIS_SCHEMA_XSD[i].equals(type.getNamespaceURI())) {
		                return toAxisTypeXSD(tm,tz,type, value,targetClass,done);
		            }
		        }
		        //SOAP
		        if(type.getNamespaceURI().indexOf("soap")!=-1) {
		            return toAxisTypeSoap(tm,type, value,targetClass,done);
		        }
		        // Specials
		        if(type.getLocalPart().equals("ArrayOf_xsd_anyType"))
		            return toArrayList(tm,value,targetClass,done);
	        }
	        return _toAxisTypeSub(tm,value,targetClass,done);
        
    	}
    	finally{
    		done.remove(value);
    	}
    }
    
    private static Object toAxisTypeSoap(TypeMapping tm,QName type, Object value, Class targetClass, Set<Object> done) throws PageException {
        String local = type.getLocalPart();
        
        if(local.equals(Constants.SOAP_ARRAY.getLocalPart())) return toArrayList(tm,value,targetClass,done);
        if(local.equals(Constants.SOAP_ARRAY12.getLocalPart())) return toArrayList(tm,value,targetClass,done);
        if(local.equals(Constants.SOAP_ARRAY_ATTRS11.getLocalPart())) return toArrayList(tm,value,targetClass,done);
        if(local.equals(Constants.SOAP_ARRAY_ATTRS12.getLocalPart())) return toArrayList(tm,value,targetClass,done);
        if(local.equals(Constants.SOAP_BASE64.getLocalPart())) return Caster.toBinary(value);
        if(local.equals(Constants.SOAP_BASE64BINARY.getLocalPart())) return Caster.toBinary(value);
        if(local.equals(Constants.SOAP_BOOLEAN.getLocalPart())) return Caster.toBoolean(value);
        if(local.equals(Constants.SOAP_BYTE.getLocalPart())) return Caster.toByte(value);
        if(local.equals(Constants.SOAP_DECIMAL.getLocalPart())) return new BigDecimal(Caster.toDoubleValue(value));
        if(local.equals(Constants.SOAP_DOUBLE.getLocalPart())) return Caster.toDouble(value);
        if(local.equals(Constants.SOAP_FLOAT.getLocalPart())) return new Float(Caster.toDoubleValue(value));
        if(local.equals(Constants.SOAP_INT.getLocalPart())) return Caster.toInteger(value);
        if(local.equals(Constants.SOAP_INTEGER.getLocalPart())) return Caster.toInteger(value);
        if(local.equals(Constants.SOAP_LONG.getLocalPart())) return Caster.toLong(value);
        if(local.equals(Constants.SOAP_MAP.getLocalPart())) return toMap(tm,value,targetClass,done);
        if(local.equals(Constants.SOAP_SHORT.getLocalPart())) return Caster.toShort(value);
        if(local.equals(Constants.SOAP_STRING.getLocalPart())) return Caster.toString(value);
        if(local.equals(Constants.SOAP_VECTOR.getLocalPart())) return toVector(tm,value,targetClass,done);
        
        // TODO SOAP_COMMON_ATTRS11, SOAP_COMMON_ATTRS12, SOAP_DOCUMENT, SOAP_ELEMENT
        return _toAxisTypeSub(tm,value,targetClass,done);
        
        
    }

    private static Object toAxisTypeXSD(TypeMapping tm,TimeZone tz,QName type, Object value, Class targetClass, Set<Object> done) throws PageException {
        String local = type.getLocalPart();
        if(local.equals(Constants.XSD_ANYSIMPLETYPE.getLocalPart())) return Caster.toString(value);
        if(local.equals(Constants.XSD_ANYURI.getLocalPart())) return toURI(value);
        if(local.equals(Constants.XSD_STRING.getLocalPart())) return Caster.toString(value);
        if(local.equals(Constants.XSD_BASE64.getLocalPart())) return Caster.toBinary(value);
        if(local.equals(Constants.XSD_BOOLEAN.getLocalPart())) return Caster.toBoolean(value);
        if(local.equals(Constants.XSD_BYTE.getLocalPart())) return Caster.toByte(value);
        if(local.equals(Constants.XSD_DATE.getLocalPart())) return Caster.toDate(value,null);
        if(local.equals(Constants.XSD_DATETIME.getLocalPart())) return Caster.toDate(value,null);
        if(local.equals(Constants.XSD_DAY.getLocalPart())) return toDay(value);
        if(local.equals(Constants.XSD_DECIMAL.getLocalPart())) return new BigDecimal(Caster.toDoubleValue(value));
        if(local.equals(Constants.XSD_DOUBLE.getLocalPart())) return Caster.toDouble(value);
        if(local.equals(Constants.XSD_DURATION.getLocalPart())) return toDuration(value);
        if(local.equals(Constants.XSD_ENTITIES.getLocalPart())) return toEntities(value);
        if(local.equals(Constants.XSD_ENTITY.getLocalPart())) return toEntity(value);
        if(local.equals(Constants.XSD_FLOAT.getLocalPart())) return new Float(Caster.toDoubleValue(value));
        if(local.equals(Constants.XSD_HEXBIN.getLocalPart())) return Caster.toBinary(value);
        if(local.equals(Constants.XSD_ID.getLocalPart())) return Caster.toString(value);
        if(local.equals(Constants.XSD_IDREF.getLocalPart())) return Caster.toString(value);
        if(local.equals(Constants.XSD_IDREFS.getLocalPart())) return Caster.toString(value);
        if(local.equals(Constants.XSD_INT.getLocalPart())) return Caster.toInteger(value);
        if(local.equals(Constants.XSD_INTEGER.getLocalPart())) return Caster.toInteger(value);
        if(local.equals(Constants.XSD_LANGUAGE.getLocalPart())) return toLanguage(value);
        if(local.equals(Constants.XSD_LONG.getLocalPart())) return Caster.toLong(value);
        if(local.equals(Constants.XSD_MONTH.getLocalPart())) return toMonth(value);
        if(local.equals(Constants.XSD_MONTHDAY.getLocalPart())) return toMonthDay(value);
        if(local.equals(Constants.XSD_NAME.getLocalPart())) return toName(value);
        if(local.equals(Constants.XSD_NCNAME.getLocalPart())) return toNCName(value);
        if(local.equals(Constants.XSD_NEGATIVEINTEGER.getLocalPart())) return Caster.toInteger(value);
        if(local.equals(Constants.XSD_NMTOKEN.getLocalPart())) return toNMToken(value);
        if(local.equals(Constants.XSD_NMTOKENS.getLocalPart())) return toNMTokens(value);
        if(local.equals(Constants.XSD_NONNEGATIVEINTEGER.getLocalPart())) return Caster.toInteger(value);
        if(local.equals(Constants.XSD_NONPOSITIVEINTEGER.getLocalPart())) return Caster.toInteger(value);
        if(local.equals(Constants.XSD_NORMALIZEDSTRING.getLocalPart())) return Caster.toString(value);
        if(local.equals(Constants.XSD_POSITIVEINTEGER.getLocalPart())) return Caster.toInteger(value);
        if(local.equals(Constants.XSD_QNAME.getLocalPart())) return toQName(value);
        if(local.equals(Constants.XSD_SCHEMA.getLocalPart())) return toQName(value);
        if(local.equals(Constants.XSD_SHORT.getLocalPart())) return Caster.toShort(value);
        if(local.equals(Constants.XSD_TIME.getLocalPart())) return DateCaster.toTime(tz,value);
        if(local.equals(Constants.XSD_TIMEINSTANT1999.getLocalPart())) return DateCaster.toTime(tz,value);
        if(local.equals(Constants.XSD_TIMEINSTANT2000.getLocalPart())) return DateCaster.toTime(tz,value);
        if(local.equals(Constants.XSD_TOKEN.getLocalPart())) return toToken(value);
        if(local.equals(Constants.XSD_UNSIGNEDBYTE.getLocalPart())) return Caster.toByte(value);
        if(local.equals(Constants.XSD_UNSIGNEDINT.getLocalPart())) return Caster.toInteger(value);
        if(local.equals(Constants.XSD_UNSIGNEDLONG.getLocalPart())) return Caster.toLong(value);
        if(local.equals(Constants.XSD_UNSIGNEDSHORT.getLocalPart())) return Caster.toShort(value);
        if(local.equals(Constants.XSD_YEAR.getLocalPart())) return toYear(value);
        if(local.equals(Constants.XSD_YEARMONTH.getLocalPart())) return toYearMonth(value);
        return _toAxisTypeSub(tm,value,targetClass,done);
    }

    private static ArrayList<Object> toArrayList(TypeMapping tm,Object value, Class targetClass, Set<Object> done) throws PageException {
        Array arr = Caster.toArray(value);
        ArrayList<Object> al=new ArrayList<Object>();
        int len=arr.size();
        Object o;
        for(int i=0;i<len;i++) {
            o=arr.get(i+1,null);
            al.add(i,_toAxisType(tm,null,null,o,targetClass,done));
        }
        return al;
    }
    private static Object[] toNativeArray(TypeMapping tm,Object value,Class targetClass, Set<Object> done) throws PageException {
    	Object[] objs = Caster.toNativeArray(value);
    	Object[] rtns;
    	Class componentType = null;
    	if(targetClass!=null) {
        	componentType = targetClass.getComponentType();
        }
        
    	if(componentType!=null) 
        	rtns = (Object[]) java.lang.reflect.Array.newInstance(componentType, objs.length);
        else 
        	rtns = new Object[objs.length];
    	
        for(int i=0;i<objs.length;i++) {
        	rtns[i]=_toAxisType(tm,null,null,objs[i],componentType,done);
        }
        return rtns;
    }

    private static Vector<Object> toVector(TypeMapping tm,Object value, Class targetClass, Set<Object> done) throws PageException {
        Array arr = Caster.toArray(value);
        Vector<Object> v=new Vector<Object>();
        int len=arr.size();
        Object o;
        for(int i=0;i<len;i++) {
            o=arr.get(i+1,null);
            v.set(i,_toAxisType(tm,null,null,o,targetClass,done));
        }
        return v;
    }

    private static Object toPojo(TypeMapping tm,Component comp, Class targetClass, Set<Object> done) throws PageException {

    	PageContext pc = ThreadLocalPageContext.get(); 
    	
	    try {
	    	return _toPojo(pc, tm, comp,targetClass,done);
			
		}
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
		/*finally {
			//pc.removeLastPageSource(true);
		}*/
    }
    
    private static Object _toPojo(PageContext pc, TypeMapping tm,Component comp, Class targetClass, Set<Object> done) throws PageException {//print.ds();System.exit(0);
    	ComponentAccess ca = ComponentUtil.toComponentAccess(comp);
    	comp=ComponentWrap.toComponentWrap(Component.ACCESS_PRIVATE,ca);
		ComponentScope scope = ca.getComponentScope();
    	
    	Property[] props=comp.getProperties(false);
    	//Map rtn=new HashTable();
    	Object obj=null;
		try {
			obj = ClassUtil.loadInstance(ComponentUtil.getServerComponentPropertiesClass(pc,comp));
		} catch (ClassException e) {
			throw Caster.toPageException(e);
		}
    	
    	//PageContext pc = ThreadLocalPageContext.get();
    	Property p;
    	Object v;
    	Collection.Key k;
		CFMLExpressionInterpreter interpreter = new CFMLExpressionInterpreter();
    	for(int i=0;i<props.length;i++){
    		p=props[i];
    		k=Caster.toKey(p.getName());
    	// value
    		v=scope.get(k,null);
    		if(v==null)v=comp.get(k, null);
    	// default
    		
    		if(v!=null)v=Caster.castTo(pc, p.getType(), v, false);
    		else{
	    		if(!StringUtil.isEmpty(p.getDefault())){
	    			try {
	    				v=Caster.castTo(pc, p.getType(), p.getDefault(), false);
	    				
	    			}
	        		catch(PageException pe) {
	        			try {
	        				v=interpreter.interpret(pc, p.getDefault());
	        				v=Caster.castTo(pc, p.getType(), v, false);
	        			}
	            		catch(PageException pe2) {
	        				throw new ExpressionException("can not use default value ["+p.getDefault()+"] for property ["+p.getName()+"] with type ["+p.getType()+"]");
	            		}
	        		}
	    		}
    		}
    		
    	// set or throw
    		if(v==null) {
    			if(p.isRequired())throw new ExpressionException("required property ["+p.getName()+"] is not defined");
    		}
    		else {
    			Reflector.callSetter(obj, p.getName().toLowerCase(), _toAxisType(tm,null,null,v,targetClass,done));	
    		}
    	}
    	return obj;
    }
    
    private static QueryBean toQueryBean(TypeMapping tm,Object value, Class targetClass, Set<Object> done) throws PageException {
    	Query query = Caster.toQuery(value);
		int recordcount=query.getRecordcount();
        String[] columnList = query.getColumns();
        QueryColumn[] columns=new QueryColumn[columnList.length];
        Object[][] data = new Object[recordcount][columnList.length];
        
        for(int i=0;i<columnList.length;i++) {
        	columns[i]=query.getColumn(columnList[i]);
        }
        
        int row;
        for(row=1;row<=recordcount;row++) {
            for(int i=0;i<columns.length;i++) {
            	data[row-1][i]=_toAxisType(tm,null,null,columns[i].get(row,null),targetClass,done);
            }
        }
    	
    	QueryBean qb = new QueryBean();
    	qb.setColumnList(columnList);
    	qb.setData(data);
    	return qb;
    	
    }
    

    
    private static Map<String,Object> toMap(TypeMapping tm,Object value, Class targetClass, Set<Object> done) throws PageException {
        Struct src = Caster.toStruct(value);
        Map<String,Object> trg=new HashMap<String,Object>();
        Iterator<Entry<Key, Object>> it = src.entryIterator();
        Entry<Key, Object> e;
        while(it.hasNext()) {
        	e = it.next();
            trg.put(e.getKey().getString(),_toAxisType(tm,null,null,e.getValue(),targetClass,done));

        }
        return trg;
        
    }

    private static Object _toAxisTypeSub(TypeMapping tm,Object value,Class targetClass,Set<Object> done) throws PageException {
    	// Date
    	if(value instanceof Date) {// not set to Decision.isDate(value)
        	return new Date(((Date)value).getTime());
    	}
    	
    	// Array
    	if(Decision.isArray(value) && !(value instanceof Argument)) {
    		if(value instanceof byte[]) return value;
    		return toNativeArray(tm,value,targetClass,done);
    	}
    	// Struct
        if(Decision.isStruct(value)) {
        	if(value instanceof Component) {
        		Object pojo= toPojo(tm,(Component)value,targetClass,done);
        		try	{	
	        		QName name = new QName("http://rpc.xml.coldfusion",pojo.getClass().getName());
		    		TypeMappingUtil.registerBeanTypeMapping(tm, pojo.getClass(), name);
	        		
        		}
        		catch(Throwable fault){
        			throw Caster.toPageException(fault);
        		}
        		return pojo;
        	}
        	return toMap(tm,value,targetClass,done);
        }
        // Query
        if(Decision.isQuery(value)) return toQueryBean(tm,value,targetClass,done);
        // Other
        return value;
    }
    
    public static Class toAxisTypeClass(Class clazz) {
        if(Query.class==clazz) return QueryBean.class;
        if(Array.class==clazz) return Object[].class;
        if(Struct.class==clazz) return Map.class;
        return clazz;
    }
    
    private static Object toURI(Object value) throws PageException {
        if(value instanceof URI) return value;
        if(value instanceof java.net.URI) return value;
        try {
            return new URI(Caster.toString(value));
        } catch (MalformedURIException e) {
            throw Caster.toPageException(e);
        }
    }

    private static Token toToken(Object value) throws PageException {
        if(value instanceof Token) return (Token) value;
        return new Token(Caster.toString(value));
    }
    
    private static QName toQName(Object value) throws PageException {
        if(value instanceof QName) return (QName) value;
        return new QName(Caster.toString(value));
    }

    private static NMTokens toNMTokens(Object value) throws PageException {
        if(value instanceof NMTokens) return (NMTokens) value;
        return new NMTokens(Caster.toString(value));
    }
    
    private static NMToken toNMToken(Object value) throws PageException {
        if(value instanceof NMToken) return (NMToken) value;
        return new NMToken(Caster.toString(value));
    }
    private static NCName toNCName(Object value) throws PageException {
        if(value instanceof NCName) return (NCName) value;
        return new NCName(Caster.toString(value));
    }

    private static Name toName(Object value) throws PageException {
        if(value instanceof Name) return (Name) value;
        return new Name(Caster.toString(value));
    }

    private static Language toLanguage(Object value) throws PageException {
        if(value instanceof Language) return (Language) value;
        return new Language(Caster.toString(value));
    }

    private static Entities toEntities(Object value) throws PageException {
        if(value instanceof Entities) return (Entities) value;
        return new Entities(Caster.toString(value));
    }
    
    private static Entity toEntity(Object value) throws PageException {
        if(value instanceof Entity) return (Entity) value;
        return new Entity(Caster.toString(value));
    }

    private static Day toDay(Object value) throws PageException {
        if(value instanceof Day) return (Day) value;
        if(Decision.isDateSimple(value,false)) {
            return new Day(Caster.toDate(value,null).getDate());
        }
        
        try {
            return new Day(Caster.toIntValue(value));
        } 
        catch (Exception e) {
            try {
                return new Day(Caster.toString(value));
            } catch (NumberFormatException nfe) {
                throw Caster.toPageException(nfe);
            } 
            catch (ExpressionException ee) {
                throw ee;
            }
        }
    }

    private static Year toYear(Object value) throws PageException {
        if(value instanceof Year) return (Year) value;
        if(Decision.isDateSimple(value,false)) {
            return new Year(Caster.toDate(value,null).getYear());
        }
        try {
            return new Year(Caster.toIntValue(value));
        } 
        catch (Exception e) {
            try {
                return new Year(Caster.toString(value));
            } catch (NumberFormatException nfe) {
                throw Caster.toPageException(nfe);
            } 
            catch (ExpressionException ee) {
                throw ee;
            }
        }
    }

    private static Month toMonth(Object value) throws PageException {
        if(value instanceof Month) return (Month) value;
        if(Decision.isDateSimple(value,false)) {
            return new Month(Caster.toDate(value,null).getMonth());
        }
        try {
            return new Month(Caster.toIntValue(value));
        } 
        catch (Exception e) {
            try {
                return new Month(Caster.toString(value));
            } catch (NumberFormatException nfe) {
                throw Caster.toPageException(nfe);
            } 
            catch (ExpressionException ee) {
                throw ee;
            }
        }
    }

    private static YearMonth toYearMonth(Object value) throws PageException {
        if(value instanceof YearMonth) return (YearMonth) value;
        if(Decision.isDateSimple(value,false)) {
            DateTime dt = Caster.toDate(value,null);
            return new YearMonth(dt.getYear(),dt.getMonth());
        }
        
        try {
            return new YearMonth(Caster.toString(value));
        } catch (NumberFormatException nfe) {
            throw Caster.toPageException(nfe);
        } 
        catch (ExpressionException ee) {
            throw ee;
        }
    }

    private static MonthDay toMonthDay(Object value) throws PageException {
        if(value instanceof MonthDay) return (MonthDay) value;
        if(Decision.isDateSimple(value,false)) {
            DateTime dt = Caster.toDate(value,null);
            return new MonthDay(dt.getMonth(),dt.getDate());
        }
        
        try {
            return new MonthDay(Caster.toString(value));
        } catch (NumberFormatException nfe) {
            throw Caster.toPageException(nfe);
        } 
        catch (ExpressionException ee) {
            throw ee;
        }
    }

    private static Duration toDuration(Object value) throws PageException, IllegalArgumentException {
        if(value instanceof Duration) return (Duration) value;
        try {
            TimeSpan ts = Caster.toTimespan(value);
            return new Duration(true, 0, 0, ts.getDay(), ts.getHour(), ts.getMinute(), ts.getSecond());
        } catch (PageException e) {
            return new Duration(Caster.toString(value));
        }
    }

    public static Object toRailoType(PageContext pc, Object value) throws PageException {
    	pc=ThreadLocalPageContext.get(pc);
    	if(pc!=null && value instanceof Pojo) {
    		try{
    			ComponentAccess c = ComponentUtil.toComponentAccess(pc.loadComponent(value.getClass().getName()));
    			ComponentWrap cw=ComponentWrap.toComponentWrap(Component.ACCESS_PRIVATE,c);
    		
    			// delete this scope data members
        		Collection.Key[] keys = cw.keys();
        		Object member;
        		for(int i=0;i<keys.length;i++) {
        			member = cw.get(keys[i]);
        			if(member instanceof UDF) continue;
                    cw.removeEL(keys[i]);
        		}
        		
        		
        		Property[] props = c.getProperties(false);
        		Property prop;
        		for(int i=0;i<props.length;i++){
        			prop=props[i];
        			try{
        				cw.set(pc, KeyImpl.init(prop.getName()), toRailoType(pc,Reflector.callGetter(value, prop.getName())));
        			}
        			catch(PageException pe){
        				pe.printStackTrace();
        			}
        		}
        		return c;
    		}
    		catch(Throwable t){
    			return value;
    		}
        }
        if(value instanceof Date || value instanceof Calendar) {// do not change to caster.isDate
        	return Caster.toDate(value,null);
        }
        if(value instanceof Object[]) {
        	Object[] arr=(Object[]) value;
        	if(!ArrayUtil.isEmpty(arr)){
        		boolean allTheSame=true;
        		// byte
        		if(arr[0] instanceof Byte){
        			for(int i=1;i<arr.length;i++){
        				if(!(arr[i] instanceof Byte)){
        					allTheSame=false;
        					break;
        				}
        			}
        			if(allTheSame){
        				byte[] bytes=new byte[arr.length];
        				for(int i=0;i<arr.length;i++){
            				bytes[i]=Caster.toByteValue(arr[i]);
            			}
        				return bytes;
        			}
        		}
        	}
        }
        if(value instanceof Byte[]) {
        	Byte[] arr=(Byte[]) value;
        	if(!ArrayUtil.isEmpty(arr)){
				byte[] bytes=new byte[arr.length];
				for(int i=0;i<arr.length;i++){
    				bytes[i]=arr[i].byteValue();
    			}
				return bytes;
        	}
        }
        if(value instanceof byte[]) {
        	return value;
        }
        if(Decision.isArray(value)) {
            Array a = Caster.toArray(value);
            int len=a.size();
            Object o;
            for(int i=1;i<=len;i++) {
                o=a.get(i,null);
                if(o!=null)a.setEL(i,toRailoType(pc,o));
            }
            return a;
        }
        if(value instanceof Map) {
        	Struct sct = new StructImpl();
            Iterator it=((Map)value).entrySet().iterator();
            Map.Entry entry;
            while(it.hasNext()) {
                entry=(Entry) it.next();
                sct.setEL(Caster.toString(entry.getKey()),toRailoType(pc,entry.getValue()));
            }
            return sct;
        	
        	
        	//return StructUtil.copyToStruct((Map)value);
        }
        if(isQueryBean(value)) {
        	QueryBean qb = (QueryBean) value;
            String[] strColumns = qb.getColumnList();
            Object[][] data = qb.getData();
            int recorcount=data.length;
            Query qry=new QueryImpl(strColumns,recorcount,"QueryBean");
            QueryColumn[] columns=new QueryColumn[strColumns.length];
            for(int i=0;i<columns.length;i++) {
            	columns[i]=qry.getColumn(strColumns[i]);
            }
            
            int row;
            for(row=1;row<=recorcount;row++) {
            	for(int i=0;i<columns.length;i++) {
            		columns[i].set(row,toRailoType(pc,data[row-1][i]));
                }
            }
            return qry;
        }
        if(Decision.isQuery(value)) {
            Query q = Caster.toQuery(value);
            int recorcount=q.getRecordcount();
            String[] strColumns = q.getColumns();
            
            QueryColumn col;
            int row;
            for(int i=0;i<strColumns.length;i++) {
                col=q.getColumn(strColumns[i]);
                for(row=1;row<=recorcount;row++) {
                    col.set(row,toRailoType(pc,col.get(row,null)));
                }
            }
            return q;
        }
        return value;
    }

	private static boolean isQueryBean(Object value) {
		return (value instanceof QueryBean);
	}


}