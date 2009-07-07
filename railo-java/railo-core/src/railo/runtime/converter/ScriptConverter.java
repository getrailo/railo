package railo.runtime.converter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.exp.PageException;
import railo.runtime.functions.displayFormatting.DateFormat;
import railo.runtime.functions.displayFormatting.TimeFormat;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.util.ComponentUtil;

/**
 * class to serialize and desirilize WDDX Packes
 */
public final class ScriptConverter {
    
	private int deep=1;
	
    /**
     * constructor of the class
     */
    public ScriptConverter() {
    }
	
	
	/**
	 * serialize Serializable class
	 * @param serializable
     * @param sb
	 * @throws ConverterException
     */
    private void _serializeSerializable(Serializable serializable, StringBuffer sb) throws ConverterException {
       
        sb.append(goIn());
	    sb.append("evaluateJava('");
	    try {
		    sb.append(JavaConverter.serialize(serializable));
        } catch (IOException e) {
            throw new ConverterException(e);
        }
	    sb.append("')");
        
    }
	
	/**
	 * serialize a Date
	 * @param date Date to serialize
	 * @param sb
	 * @throws ConverterException
	 */
	private void _serializeDate(Date date, StringBuffer sb) throws ConverterException {
		_serializeDateTime(new DateTimeImpl(date),sb);
	}
	/**
	 * serialize a DateTime
	 * @param dateTime DateTime to serialize
	 * @param sb
	 * @throws ConverterException
	 */
	private void _serializeDateTime(DateTime dateTime, StringBuffer sb) throws ConverterException {
	   

	    try {
	        sb.append(goIn());
		    sb.append("createDateTime(");
		    sb.append(DateFormat.call(null,dateTime,"yyyy,m,d"));
		    sb.append(',');
		    sb.append(TimeFormat.call(null,dateTime,"H,m,s,l"));
		    sb.append(')');
		} 
	    catch (PageException e) {
			throw new ConverterException(e);
		}
	}

	/**
	 * serialize a Array
	 * @param array Array to serialize
	 * @param sb
	 * @throws ConverterException
	 */
	private void _serializeArray(Array array, StringBuffer sb) throws ConverterException {
		_serializeList(array.toList(),sb);
	}
	
	/**
	 * serialize a List (as Array)
	 * @param list List to serialize
	 * @param sb
	 * @throws ConverterException
	 */
	private void _serializeList(List list, StringBuffer sb) throws ConverterException {
		
	    sb.append(goIn());
	    sb.append("[");
	    boolean doIt=false;
		ListIterator it=list.listIterator();
		while(it.hasNext()) {
		    if(doIt)sb.append(',');
		    doIt=true;
			_serialize(it.next(),sb);
		}
		
		sb.append(']');
	}

    /**
     * serialize a Struct
     * @param struct Struct to serialize
     * @param sb
     * @throws ConverterException
     */
    public void _serializeStruct(Struct struct, StringBuffer sb) throws ConverterException {
        sb.append(goIn());
        sb.append('{');
        Iterator it=struct.keyIterator();
        boolean doIt=false;
        deep++;
        while(it.hasNext()) {
            String key=Caster.toString(it.next(),"");
            if(doIt)sb.append(',');
            doIt=true;
            sb.append('\'');
            sb.append(escape(key));
            sb.append('\'');
            sb.append(':');
            _serialize(struct.get(key,null),sb);
        }
        deep--;
        
        sb.append('}');
    }
    
    public String serializeStruct(Struct struct, Set ignoreSet) throws ConverterException {
    	StringBuffer sb =new StringBuffer();
        sb.append(goIn());
        sb.append("struct(");
        boolean hasIgnores=ignoreSet!=null;
        Iterator it=struct.keyIterator();
        boolean doIt=false;
        deep++;
        while(it.hasNext()) {
            String key=Caster.toString(it.next(),"");
            if(hasIgnores && ignoreSet.contains(key.toLowerCase())) continue;
            if(doIt)sb.append(',');
            doIt=true;
            sb.append('\'');
            sb.append(escape(key));
            sb.append('\'');
            sb.append(':');
            _serialize(struct.get(key,null),sb);
        }
        deep--;
        
        return sb.append(')').toString();
    }

    /**
     * serialize a Map (as Struct)
     * @param map Map to serialize
     * @param sb
     * @throws ConverterException
     */
    private void _serializeMap(Map map, StringBuffer sb) throws ConverterException {
        sb.append(goIn());
        sb.append("struct(");
        
        Iterator it=map.keySet().iterator();
        boolean doIt=false;
        deep++;
        while(it.hasNext()) {
            Object key=it.next();
            if(doIt)sb.append(',');
            doIt=true;
            sb.append('\'');
            sb.append(escape(key.toString()));
            sb.append('\'');
            sb.append(':');
            _serialize(map.get(key),sb);
        }
        deep--;
        
        sb.append(')');
    }
    /**
     * serialize a Component
     * @param component Component to serialize
     * @param sb
     * @throws ConverterException
     */
    private void _serializeComponent(Component component, StringBuffer sb) throws ConverterException {
    	sb.append(goIn());
        try {
			sb.append("evaluateComponent('"+component.getAbsName()+"','"+ComponentUtil.md5(component)+"',struct(");
		} catch (IOException e) {
			throw new ConverterException(e.getMessage());
		}
        
		boolean doIt=false;
        Iterator it=component.keyIterator();
        Object member;
        deep++;
        while(it.hasNext()) {
            String key=Caster.toString(it.next(),"");
            member = component.get(key,null);
            if(member instanceof UDF)continue;
            if(doIt)sb.append(',');
            doIt=true;
            sb.append('\'');
            sb.append(escape(key));
            sb.append('\'');
            sb.append(':');
            _serialize(member,sb);
        }
        deep--;
        
        sb.append("))");
        //sb.append("");
        //throw new ConverterException("can't serialize a component "+component.getDisplayName());
    }

	/**
	 * serialize a Query
	 * @param query Query to serialize
	 * @param sb
	 * @throws ConverterException
	 */
	private void _serializeQuery(Query query, StringBuffer sb) throws ConverterException {
		
		String[] keys = query.keysAsString();
		sb.append(goIn());
		sb.append("query(");
		
	
		deep++;
		boolean oDoIt=false;
		int len=query.getRecordcount();
		for(int i=0;i<keys.length;i++) {
		    if(oDoIt)sb.append(',');
		    oDoIt=true;
		    sb.append(goIn());
            sb.append('\'');
            sb.append(escape(keys[i]));
            sb.append('\'');
			sb.append(":[");
			boolean doIt=false;
			for(int y=1;y<=len;y++) {
			    if(doIt)sb.append(',');
			    doIt=true;
			    try {
					_serialize(query.getAt(keys[i],y),sb);
				} catch (PageException e) {
					_serialize(e.getMessage(),sb);
				}
			}
			sb.append(']');
		}
		deep--;
		
		sb.append(')');
		
	}
	
	/**
	 * serialize a Object to his xml Format represenation
	 * @param object Object to serialize
	 * @param sb StringBuffer to write data
	 * @throws ConverterException
	 */
	private void _serialize(Object object, StringBuffer sb) throws ConverterException {
		
		deep++;
		// NULL
		if(object==null) {
		    sb.append(goIn());
		    sb.append("''");
		}
		// String
		else if(object instanceof String) {
		    sb.append(goIn());
		    sb.append("'");
		    sb.append(escape(object.toString()));
		    sb.append("'");
		}
		// Number
		else if(object instanceof Number) {
		    sb.append(goIn());
		    sb.append(Caster.toString(((Number)object).doubleValue()));
		}
		// Boolean
		else if(object instanceof Boolean) {
		    sb.append(goIn());
		    sb.append(Caster.toString(((Boolean)object).booleanValue()));
		}
		// DateTime
		else if(object instanceof DateTime) {
			_serializeDateTime((DateTime)object,sb);
		}
		// Date
		else if(object instanceof Date) {
			_serializeDate((Date)object,sb);
		}
        // Component
        else if(object instanceof Component) {
            _serializeComponent((Component)object,sb);
        }
        // Struct
        else if(object instanceof Struct) {
            _serializeStruct((Struct)object,sb);
        }
        // Map
        else if(object instanceof Map) {
            _serializeMap((Map)object,sb);
        }
		// Array
		else if(object instanceof Array) {
			_serializeArray((Array)object,sb);
		}
		// List
		else if(object instanceof List) {
			_serializeList((List)object,sb);
		}
        // Query
        else if(object instanceof Query) {
            _serializeQuery((Query)object,sb);
        }
        // Timespan
        else if(object instanceof TimeSpan) {
        	_serializeTimeSpan((TimeSpan) object,sb);
        }
		// String Converter
		else if(object instanceof ScriptConvertable) {
		    sb.append(((ScriptConvertable)object).serialize());
		}
		else if(object instanceof Serializable) {
			_serializeSerializable((Serializable)object,sb);
		}
		else throw new ConverterException("can't serialize Object of type [ "+object.getClass().getName()+" ]");
		
		
		deep--;
	}

    private void _serializeTimeSpan(TimeSpan span, StringBuffer sb) {
    	
	        sb.append(goIn());
		    sb.append("createTimeSpan(");
		    sb.append(span.getDay());
		    sb.append(',');
		    sb.append(span.getHour());
		    sb.append(',');
		    sb.append(span.getMinute());
		    sb.append(',');
		    sb.append(span.getSecond());
		    sb.append(')');
		
	}


	private String escape(String str) {
        return StringUtil.replace(StringUtil.replace(str,"'","''",false),"#","##",false);
    }

    /**
	 * serialize a Object to his literal Format
	 * @param object Object to serialize
	 * @return serialized wddx package
	 * @throws ConverterException
	 */
	public String serialize(Object object) throws ConverterException {
		deep=0;
		StringBuffer sb=new StringBuffer();
		_serialize(object,sb);
		return sb.toString();
	}
	
	
	/**
	 * @return return current blockquote
	 */
	private String goIn() {
	    /*StringBuffer rtn=new StringBuffer('\n');
		for(int i=0;i<deep;i++) rtn.append('\t');
		return rtn.toString();
		/*/
		
		return "";
	}


}