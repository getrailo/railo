package railo.runtime.converter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import railo.runtime.Component;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.util.ComponentUtil;

/**
 * class to serialize and desirilize WDDX Packes
 */
public final class JSONConverter {
    
	
    /**
     * constructor of the class
     */
    public JSONConverter() {
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
	private void _serializeDate(Date date, StringBuffer sb) {
		_serializeDateTime(new DateTimeImpl(date),sb);
	}
	/**
	 * serialize a DateTime
	 * @param dateTime DateTime to serialize
	 * @param sb
	 * @throws ConverterException
	 */
	private void _serializeDateTime(DateTime dateTime, StringBuffer sb) {
		
		sb.append('"');
		
		//sb.append(escape(dateTime.toString()));
		sb.append(escape(JSONDateFormat.format(dateTime)));
		sb.append('"');
		
		/*try {
	        sb.append(goIn());
		    sb.append("createDateTime(");
		    sb.append(DateFormat.call(null,dateTime,"yyyy,m,d"));
		    sb.append(' ');
		    sb.append(TimeFormat.call(null,dateTime,"HH:mm:ss"));
		    sb.append(')');
		} 
	    catch (PageException e) {
			throw new ConverterException(e);
		}*/
	    //Januar, 01 2000 01:01:01
	}

	/**
	 * serialize a Array
	 * @param array Array to serialize
	 * @param sb
	 * @param serializeQueryByColumns 
	 * @throws ConverterException
	 */
	private void _serializeArray(Array array, StringBuffer sb, boolean serializeQueryByColumns) throws ConverterException {
		_serializeList(array.toList(),sb,serializeQueryByColumns);
	}
	
	/**
	 * serialize a List (as Array)
	 * @param list List to serialize
	 * @param sb
	 * @param serializeQueryByColumns 
	 * @throws ConverterException
	 */
	private void _serializeList(List list, StringBuffer sb, boolean serializeQueryByColumns) throws ConverterException {
		
	    sb.append(goIn());
	    sb.append("[");
	    boolean doIt=false;
		ListIterator it=list.listIterator();
		while(it.hasNext()) {
		    if(doIt)sb.append(',');
		    doIt=true;
			_serialize(it.next(),sb,serializeQueryByColumns);
		}
		
		sb.append(']');
	}

    /**
     * serialize a Struct
     * @param struct Struct to serialize
     * @param sb
     * @param serializeQueryByColumns 
     * @throws ConverterException
     */
    public void _serializeStruct(Struct struct, StringBuffer sb, boolean serializeQueryByColumns) throws ConverterException {
        sb.append(goIn());
        sb.append("{");
        Key[] keys = struct.keys();
        Key key;
        boolean doIt=false;
        for(int i=0;i<keys.length;i++) {
        	key=keys[i];
        	if(doIt)sb.append(',');
            doIt=true;
            sb.append('"');
            sb.append(escape(key.getString()));
            sb.append('"');
            sb.append(':');
            _serialize(struct.get(key,null),sb,serializeQueryByColumns);
        }
        sb.append('}');
    }
    

    /**
     * serialize a Map (as Struct)
     * @param map Map to serialize
     * @param sb
     * @param serializeQueryByColumns 
     * @throws ConverterException
     */
    private void _serializeMap(Map map, StringBuffer sb, boolean serializeQueryByColumns) throws ConverterException {
        sb.append(goIn());
        sb.append("{");
        
        Iterator it=map.keySet().iterator();
        boolean doIt=false;
        while(it.hasNext()) {
            Object key=it.next();
            if(doIt)sb.append(',');
            doIt=true;
            sb.append('"');
            sb.append(escape(key.toString()));
            sb.append('"');
            sb.append(':');
            _serialize(map.get(key),sb,serializeQueryByColumns);
        }
        
        sb.append('}');
    }
    /**
     * serialize a Component
     * @param component Component to serialize
     * @param sb
     * @param serializeQueryByColumns 
     * @throws ConverterException
     */
    private void _serializeComponent(Component component, StringBuffer sb, boolean serializeQueryByColumns) throws ConverterException {
    	sb.append(goIn());
        try {
			sb.append("evaluateComponent('"+component.getAbsName()+"','"+ComponentUtil.md5(component)+"',struct(");
		} catch (IOException e) {
			throw new ConverterException(e.getMessage());
		}
        
		boolean doIt=false;
        Iterator it=component.keyIterator();
        Object member;
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
            _serialize(member,sb,serializeQueryByColumns);
        }
        
        sb.append("))");
        //sb.append("");
        //throw new ConverterException("can't serialize a component "+component.getDisplayName());
    }

	/**
	 * serialize a Query
	 * @param query Query to serialize
	 * @param sb
	 * @param serializeQueryByColumns 
	 * @throws ConverterException
	 */
	private void _serializeQuery(Query query, StringBuffer sb, boolean serializeQueryByColumns) throws ConverterException {
		
		String[] keys = query.keysAsString();
		sb.append(goIn());
		sb.append("{");
		
		/*

{"DATA":[["a","b"],["c","d"]]}
{"DATA":{"aaa":["a","c"],"bbb":["b","d"]}}
		 * */
		// Rowcount
		if(serializeQueryByColumns){
			sb.append("\"ROWCOUNT\":");
			sb.append(Caster.toString(query.getRecordcount()));
			sb.append(',');
		}
		
		// Columns
		sb.append("\"COLUMNS\":[");
		String[] cols = query.getColumns();
		for(int i=0;i<cols.length;i++) {
			if(i>0)sb.append(",\"");
			else sb.append('"');
            sb.append(escape(cols[i].toUpperCase()));
            sb.append('"');
		}
		sb.append("],");
		
		// Data
		sb.append("\"DATA\":");
		if(serializeQueryByColumns) {
			sb.append('{');
			boolean oDoIt=false;
			int len=query.getRecordcount();
			for(int i=0;i<keys.length;i++) {
			    if(oDoIt)sb.append(',');
			    oDoIt=true;
			    sb.append(goIn());
	            sb.append('"');
	            sb.append(escape(keys[i]));
	            sb.append('"');
				sb.append(":[");
				boolean doIt=false;
					for(int y=1;y<=len;y++) {
					    if(doIt)sb.append(',');
					    doIt=true;
					    try {
							_serialize(query.getAt(keys[i],y),sb,serializeQueryByColumns);
						} catch (PageException e) {
							_serialize(e.getMessage(),sb,serializeQueryByColumns);
						}
					}
				
				sb.append(']');
			}
	
			sb.append('}');
		}
		else {
			sb.append('[');
			boolean oDoIt=false;
			int len=query.getRecordcount();
			for(int row=1;row<=len;row++) {
			    if(oDoIt)sb.append(',');
			    oDoIt=true;
	
				sb.append("[");
				boolean doIt=false;
					for(int col=0;col<keys.length;col++) {
					    if(doIt)sb.append(',');
					    doIt=true;
					    try {
							_serialize(query.getAt(keys[col],row),sb,serializeQueryByColumns);
						} catch (PageException e) {
							_serialize(e.getMessage(),sb,serializeQueryByColumns);
						}
					}
				sb.append(']');
			}
			sb.append(']');
		}
		sb.append('}');
	}
	
	/**
	 * serialize a Object to his xml Format represenation
	 * @param object Object to serialize
	 * @param sb StringBuffer to write data
	 * @param serializeQueryByColumns 
	 * @throws ConverterException
	 */
	private void _serialize(Object object, StringBuffer sb, boolean serializeQueryByColumns) throws ConverterException {
		
		// NULL
		if(object==null) {
		    sb.append(goIn());
		    sb.append("''");
		}
		// String
		else if(object instanceof String) {
		    sb.append(goIn());
		    sb.append('"');
		    sb.append(escape(object.toString()));
		    sb.append('"');
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
            _serializeComponent((Component)object,sb,serializeQueryByColumns);
        }
        // Struct
        else if(object instanceof Struct) {
            _serializeStruct((Struct)object,sb,serializeQueryByColumns);
        }
        // Map
        else if(object instanceof Map) {
            _serializeMap((Map)object,sb,serializeQueryByColumns);
        }
		// Array
		else if(object instanceof Array) {
			_serializeArray((Array)object,sb,serializeQueryByColumns);
		}
		// List
		else if(object instanceof List) {
			_serializeList((List)object,sb,serializeQueryByColumns);
		}
        // Query
        else if(object instanceof Query) {
            _serializeQuery((Query)object,sb,serializeQueryByColumns);
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

	
	public static String escape(String str) {
		char[] arr=str.toCharArray();
		StringBuffer rtn=new StringBuffer(arr.length);
		for(int i=0;i<arr.length;i++) {
			switch(arr[i]) {
				case '\\': rtn.append("\\\\"); break;
				case '\n': rtn.append("\\n"); break;
				case '\r': rtn.append("\\r"); break;
				case '\f': rtn.append("\\f"); break;
				case '\b': rtn.append("\\b"); break;
				case '\t': rtn.append("\\t"); break;
				case '"' : rtn.append("\\\""); break;
				default : rtn.append(arr[i]); break;
			}
		}
		return rtn.toString();
	}

    /**
	 * serialize a Object to his literal Format
	 * @param object Object to serialize
     * @param serializeQueryByColumns 
	 * @return serialized wddx package
	 * @throws ConverterException
	 */
	public String serialize(Object object, boolean serializeQueryByColumns) throws ConverterException {
		StringBuffer sb=new StringBuffer();
		_serialize(object,sb,serializeQueryByColumns);
		return sb.toString();
	}
	
	
	/**
	 * @return return current blockquote
	 */
	private String goIn() {
	    return "";
	}


}