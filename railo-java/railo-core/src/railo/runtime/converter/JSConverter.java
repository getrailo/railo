
package railo.runtime.converter;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.functions.dateTime.DateUtil;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.dt.DateTime;


/**
 * class to serialize to Convert Cold Fusion Objects (query,array,struct usw) to a JavaScript representation
 */
public final class JSConverter {

	private boolean useShortcuts=false;
	private boolean useWDDX=true;

	/**
	 * serialize a Cold Fusion object to a JavaScript Object
	 * @param object object to serialize
	 * @param clientVariableName name of the variable to create 
	 * @return vonverte Javascript Code as String
	 * @throws ConverterException
	 */
	public String serialize(Object object, String clientVariableName) throws ConverterException {
		StringBuffer sb=new StringBuffer();
		_serialize(clientVariableName,object,sb);
		String str = sb.toString().trim();
		return clientVariableName+"="+str+(StringUtil.endsWith(str, ';')?"":";");
		//return sb.toString();
	}
	
	private void _serialize(String name,Object object,StringBuffer sb) throws ConverterException {
		// NULL
		if(object==null) {
			sb.append(goIn());
			sb.append("null;");
		}
		// String
		else if(object instanceof String || object instanceof StringBuffer) {
			sb.append(goIn());
			sb.append("\"");
			sb.append(StringUtil.escapeJS(object.toString()));
			sb.append("\";");
		}
		// Number
		else if(object instanceof Number) {
			sb.append(goIn());
			sb.append("\"");
			sb.append(Caster.toString(((Number)object).doubleValue()));
			sb.append("\";");
		}
		// Date
		else if(Decision.isDateSimple(object,false)) {
			_serializeDateTime(Caster.toDate(object,false,null,null),sb);
		}
		// Boolean
		else if(object instanceof Boolean) {
			sb.append(goIn());
			sb.append("\"");
			sb.append((((Boolean)object).booleanValue()?"true":"false"));
			sb.append("\";");
		}
		
		// Struct
		else if(object instanceof Struct) {
			_serializeStruct(name,(Struct)object,sb);
		}
		// Map
		else if(object instanceof Map) {
			_serializeMap(name,(Map)object,sb);
		}
		// List
		else if(object instanceof List) {
			_serializeList(name,(List)object,sb);
		}
		// Array
		else if(Decision.isArray(object)) {
			_serializeArray(name,Caster.toArray(object,null),sb);
		}
		// Query
		else if(object instanceof Query) {
			_serializeQuery(name,(Query)object,sb);
		}
		
		else 
			throw new ConverterException("can't serialize Object of type ["+object.getClass().getName()+"] to a js representation");
		//deep--;
		//return rtn;
	}
	

	
	/**
	 * serialize a Array
	 * @param name 
	 * @param array Array to serialize
	 * @param sb 
	 * @return serialized array
	 * @throws ConverterException
	 */
	private void _serializeArray(String name, Array array, StringBuffer sb) throws ConverterException {
		_serializeList(name,array.toList(),sb);
	}
	
	/**
	 * serialize a List (as Array)
	 * @param name 
	 * @param list List to serialize
	 * @param sb 
	 * @return serialized list
	 * @throws ConverterException
	 */
	private void _serializeList(String name, List list, StringBuffer sb) throws ConverterException {
		if(useShortcuts)sb.append("[];");
		else sb.append("new Array();");
		
		ListIterator it=list.listIterator();
		int index=-1;
		while(it.hasNext()) {
			//if(index!=-1)sb.append(",");
			index = it.nextIndex();
			sb.append(name+"["+index+"]=");
			_serialize(name+"["+index+"]",it.next(),sb);
			//sb.append(";");
		}
	}

	/**
	 * serialize a Struct
	 * @param name 
	 * @param struct Struct to serialize
	 * @param sb2 
	 * @return serialized struct
	 * @throws ConverterException
	 */
	private String _serializeStruct(String name, Struct struct, StringBuffer sb) throws ConverterException {
		if(useShortcuts)sb.append("{};");
		else sb.append("new Object();");
		
		Key[] keys = struct.keys();
		for(int i=0;i<keys.length;i++) {
			// lower case ist ok!
			String key=StringUtil.escapeJS(Caster.toString(keys[i].getLowerString(),""));
            sb.append(name+"[\""+key+"\"]=");
			try {
				_serialize(name+"[\""+key+"\"]",struct.get(keys[i]),sb);
			} 
			catch (PageException e) {
				_serialize(name+"[\""+key+"\"]",e.getMessage(),sb);
			}
		}
        return sb.toString();
	}

	/**
	 * serialize a Map (as Struct)
	 * @param name 
	 * @param map Map to serialize
	 * @param sb2 
	 * @return serialized map
	 * @throws ConverterException
	 */
	private String _serializeMap(String name, Map map, StringBuffer sb) throws ConverterException {

		if(useShortcuts)sb.append("{}");
		else sb.append("new Object();");
		Iterator it=map.keySet().iterator();
		while(it.hasNext()) {
			Object key=it.next();
			String skey=StringUtil.toLowerCase(StringUtil.escapeJS(key.toString()));
            sb.append(name+"[\""+skey+"\"]=");
			_serialize(name+"[\""+skey+"\"]",map.get(key),sb);
			//sb.append(";");
		}
		return sb.toString();
	}
	
	/**
	 * serialize a Query
	 * @param query Query to serialize
	 * @return serialized query
	 * @throws ConverterException
	 */
	private void _serializeQuery(String name,Query query,StringBuffer sb) throws ConverterException {
		if(useWDDX)_serializeWDDXQuery(name,query,sb);
		else _serializeASQuery(name,query,sb);
	}

	private void _serializeWDDXQuery(String name,Query query,StringBuffer sb) throws ConverterException {
		
		Key[] keys = query.keys();
		sb.append("new WddxRecordset();");
		
		int recordcount=query.getRecordcount();
		for(int i=0;i<keys.length;i++) {
			if(useShortcuts)sb.append("col"+i+"=[];");
			else sb.append("col"+i+"=new Array();");
			// lower case ist ok!
			String skey = StringUtil.escapeJS(keys[i].getLowerString());
			for(int y=0;y<recordcount;y++) {
				
				sb.append("col"+i+"["+y+"]=");
				
				_serialize("col"+i+"["+y+"]",query.getAt(keys[i],y+1,null),sb);
				
			}
			sb.append(name+"[\""+skey+"\"]=col"+i+";col"+i+"=null;");
		}
	}

	private void _serializeASQuery(String name,Query query,StringBuffer sb) throws ConverterException {
		
		String[] keys = query.keysAsString();
		for(int i=0;i<keys.length;i++) {
			keys[i] = StringUtil.escapeJS(keys[i]);
		}
		if(useShortcuts)sb.append("[];");
		else sb.append("new Array();");
		
		int recordcount=query.getRecordcount();
		for(int i=0;i<recordcount;i++) {
			if(useShortcuts)sb.append(name+"["+i+"]={};");
			else sb.append(name+"["+i+"]=new Object();");
			
			for(int y=0;y<keys.length;y++) {
				sb.append(name+"["+i+"]['"+keys[y]+"']=");
				_serialize(name+"["+i+"]['"+keys[y]+"']",query.getAt(keys[y],i+1,null),sb);
			}
		}
	}
	
	

	/**
	 * serialize a DateTime
	 * @param dateTime DateTime to serialize
	 * @param sb 
	 * @param sb
	 * @throws ConverterException
	 */
	private void _serializeDateTime(DateTime dateTime, StringBuffer sb) {
	   
		Calendar c = Calendar.getInstance();
		DateUtil.setTimeZone(c, dateTime,null);
		c.setTime(dateTime);
	    sb.append(goIn());
	    sb.append("new Date(");
	    sb.append(c.get(Calendar.YEAR));
	    sb.append(",");
	    sb.append(c.get(Calendar.MONTH));
	    sb.append(",");
	    sb.append(c.get(Calendar.DAY_OF_MONTH));
	    sb.append(",");
	    sb.append(c.get(Calendar.HOUR_OF_DAY));
	    sb.append(",");
	    sb.append(c.get(Calendar.MINUTE));
	    sb.append(",");
	    sb.append(c.get(Calendar.SECOND));
	    sb.append(");");
	}

	private String goIn() {
		//StringBuffer rtn=new StringBuffer(deep);
		//for(int i=0;i<deep;i++) rtn.append('\t');
		return "";//rtn.toString();
	}

	public void useShortcuts(boolean useShortcuts) { 
		this.useShortcuts=useShortcuts;
		
	}

	public void useWDDX(boolean useWDDX) {
		this.useWDDX=useWDDX;
	}
	
	/*
	 * @param args
	 * @throws Exception
	 
	public static void main(String[] args) throws Exception {
		JSConverter js=new JSConverter();
		Query query=QueryNew.call(null,"aaa,bbb,ccc");
		QueryAddRow.call(null,query);
		QuerySetCell.call(null,query,"aaa","1.1");
		QuerySetCell.call(null,query,"bbb","1.2");
		QuerySetCell.call(null,query,"ccc","1.3");
		QueryAddRow.call(null,query);
		QuerySetCell.call(null,query,"aaa","2.1");
		QuerySetCell.call(null,query,"bbb","2.2");
		QuerySetCell.call(null,query,"ccc","2.3");
		QueryAddRow.call(null,query);
		QuerySetCell.call(null,query,"aaa","3.1");
		QuerySetCell.call(null,query,"bbb","3.2");
		QuerySetCell.call(null,query,"ccc","3.3<hello>");
		Array arr2=List ToArray.call(null,"111,222");
		Array arr=List ToArray.call(null,"aaaa,bbb,ccc,dddd,eee");
		
		arr.set(10,arr2);

		Struct sct= new Struct();
		sct.set("aaa","val1");
		sct.set("bbb","val2");
		sct.set("ccc","val3");
		sct.set("ddd",arr2);
		
		/*
	}*/
}