package railo.runtime.converter;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import railo.commons.date.TimeZoneConstants;
import railo.commons.lang.NumberUtil;
import railo.runtime.Component;
import railo.runtime.ComponentScope;
import railo.runtime.ComponentWrap;
import railo.runtime.PageContext;
import railo.runtime.coder.Base64Coder;
import railo.runtime.coder.CoderException;
import railo.runtime.component.Property;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.op.date.DateCaster;
import railo.runtime.orm.ORMUtil;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.type.util.KeyConstants;

/**
 * class to serialize and desirilize WDDX Packes
 */
public final class WDDXConverter extends ConverterSupport {
	private static final Collection.Key REMOTING_FETCH = KeyImpl.intern("remotingFetch");
	
	private int deep=1;
	private boolean xmlConform;
	private char _;
	private TimeZone timeZone;
	private boolean ignoreRemotingFetch=true;
    //private PageContext pcx;

	/**
	 * constructor of the class
	 * @param timeZone 
	 * @param xmlConform define if generated xml conform output or wddx conform output (wddx is not xml conform)
	 */
	public WDDXConverter(TimeZone timeZone, boolean xmlConform,boolean ignoreRemotingFetch) {
		this.xmlConform=xmlConform;
		_=(xmlConform)?'"':'\'';
		this.timeZone=timeZone;
		this.ignoreRemotingFetch=ignoreRemotingFetch;
	}
	
	/**
	 * defines timezone info will
	 * @param timeZone
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone=timeZone;
	}

	/**
	 * serialize a Date
	 * @param date Date to serialize
	 * @return serialized date
	 * @throws ConverterException
	 */
	private String _serializeDate(Date date) {
		return _serializeDateTime(new DateTimeImpl(date));
	}
	/**
	 * serialize a DateTime
	 * @param dateTime DateTime to serialize
	 * @return serialized dateTime
	 * @throws ConverterException
	 */
	private String _serializeDateTime(DateTime dateTime) {
		//try {
			

			String strDate = new railo.runtime.format.DateFormat(Locale.US).format(dateTime,"yyyy-m-d",TimeZoneConstants.UTC);
			String strTime = new railo.runtime.format.TimeFormat(Locale.US).format(dateTime,"H:m:s",TimeZoneConstants.UTC);
			
			return goIn()+"<dateTime>"+
				strDate+
				"T"+strTime+
				"+0:0"+
				"</dateTime>";
			
		/*} 
		catch (PageException e) {
			throw new ConverterException(e);
		}*/
	}
	
	private String _serializeBinary(byte[] binary) {
		return new StringBuilder("<binary length='")
		.append(binary.length)
		.append("'>")
		.append(Base64Coder.encode(binary))
		.append("</binary>").toString();
	}

	/**
	 * @param dateTime 
	 * @return returns the time zone info
	 */
	private String getTimeZoneInfo(DateTime dateTime) {
		timeZone=ThreadLocalPageContext.getTimeZone(timeZone);
		//if(timeZone==null) return "";
		
		int minutes=timeZone.getOffset(dateTime.getTime())/1000/60;
		String operator=(minutes>=0)?"+":"-";
		if(operator.equals("-"))minutes=minutes-(minutes+minutes);
		int hours=minutes/60;
		minutes=minutes%60;
		
		return operator+hours+":"+minutes;

		
	}

	/**
	 * serialize a Array
	 * @param array Array to serialize
	 * @param done 
	 * @return serialized array
	 * @throws ConverterException
	 */
	private String _serializeArray(Array array, Set<Object> done) throws ConverterException {
		return _serializeList(array.toList(),done);
	}
	
	/**
	 * serialize a List (as Array)
	 * @param list List to serialize
	 * @param done 
	 * @return serialized list
	 * @throws ConverterException
	 */
	private String _serializeList(List list, Set<Object> done) throws ConverterException {
		StringBuffer sb=new StringBuffer(goIn()+"<array length="+_+list.size()+_+">");
				
		ListIterator it=list.listIterator();
		while(it.hasNext()) {
			sb.append(_serialize(it.next(),done));
		}
		
		sb.append(goIn()+"</array>");
		return sb.toString();
	}

	/**
	 * serialize a Component
	 * @param component Component to serialize
	 * @param done 
	 * @return serialized component
	 * @throws ConverterException 
	 */
	private String _serializeComponent(Component component, Set<Object> done) throws ConverterException {
		StringBuffer sb=new StringBuffer();
		ComponentAccess ca;
		try {
			component=new ComponentWrap(Component.ACCESS_PRIVATE, ca=ComponentUtil.toComponentAccess(component));
		} catch (ExpressionException e1) {
			throw toConverterException(e1);
		}
		boolean isPeristent=ca.isPersistent();
		
		
        deep++;
        Object member;
        Iterator<Key> it = component.keyIterator();
        Collection.Key key;
        while(it.hasNext()) {
        	key=Caster.toKey(it.next(),null);
        	member = component.get(key,null);
        	if(member instanceof UDF) continue;
        	sb.append(goIn()+"<var scope=\"this\" name="+_+key.toString()+_+">");
            sb.append(_serialize(member,done));
            sb.append(goIn()+"</var>");
        }

        Property p;
        Boolean remotingFetch;
    	Struct props = ignoreRemotingFetch?null:ComponentUtil.getPropertiesAsStruct(ca,false);
        ComponentScope scope = ca.getComponentScope();
        it=scope.keyIterator();
        while(it.hasNext()) {
        	key=Caster.toKey(it.next(),null);
        	if(!ignoreRemotingFetch) {
        		p=(Property) props.get(key,null);
            	if(p!=null) {
            		remotingFetch=Caster.toBoolean(p.getDynamicAttributes().get(REMOTING_FETCH,null),null);
	            	if(remotingFetch==null){
    					if(isPeristent  && ORMUtil.isRelated(p)) continue;
	    			}
	    			else if(!remotingFetch.booleanValue()) continue;
            	}
    		}
        	
        	member = scope.get(key,null);
        	if(member instanceof UDF || key.equals(KeyConstants._this)) continue;
            sb.append(goIn()+"<var scope=\"variables\" name="+_+key.toString()+_+">");
            sb.append(_serialize(member,done));
            sb.append(goIn()+"</var>");
        }
        
        
        deep--;
        try {
			//return goIn()+"<struct>"+sb+"</struct>";
			return goIn()+"<component md5=\""+ComponentUtil.md5(component)+"\" name=\""+component.getAbsName()+"\">"+sb+"</component>";
		} 
		catch (Exception e) {
			throw toConverterException(e);
		}
	}

	/**
	 * serialize a Struct
	 * @param struct Struct to serialize
	 * @param done 
	 * @return serialized struct
	 * @throws ConverterException
	 */
	private String _serializeStruct(Struct struct, Set<Object> done) throws ConverterException {
        StringBuffer sb=new StringBuffer(goIn()+"<struct>");
        
        Iterator<Key> it = struct.keyIterator();

        deep++;
        while(it.hasNext()) {
            Key key = it.next();
            sb.append(goIn()+"<var name="+_+key.toString()+_+">");
            sb.append(_serialize(struct.get(key,null),done));
            sb.append(goIn()+"</var>");
        }
        deep--;
        
        sb.append(goIn()+"</struct>");
        return sb.toString();
	}

	/**
	 * serialize a Map (as Struct)
	 * @param map Map to serialize
	 * @param done 
	 * @return serialized map
	 * @throws ConverterException
	 */
	private String _serializeMap(Map map, Set<Object> done) throws ConverterException {
		StringBuffer sb=new StringBuffer(goIn()+"<struct>");
		
		Iterator it=map.keySet().iterator();

		deep++;
		while(it.hasNext()) {
			Object key=it.next();
			sb.append(goIn()+"<var name="+_+key.toString()+_+">");
			sb.append(_serialize(map.get(key),done));
			sb.append(goIn()+"</var>");
		}
		deep--;
		
		sb.append(goIn()+"</struct>");
		return sb.toString();
	}

	/**
	 * serialize a Query
	 * @param query Query to serialize
	 * @param done 
	 * @return serialized query
	 * @throws ConverterException
	 */
	private String _serializeQuery(Query query, Set<Object> done) throws ConverterException {
		
		Collection.Key[] keys = CollectionUtil.keys(query);
		StringBuffer sb=new StringBuffer(goIn()+"<recordset rowCount="+_+query.getRecordcount()+_+" fieldNames="+_+railo.runtime.type.util.ListUtil.arrayToList(keys,",")+_+" type="+_+"coldfusion.sql.QueryTable"+_+">");
		
	
		deep++;
		int len=query.getRecordcount();
		for(int i=0;i<keys.length;i++) {
			sb.append(goIn()+"<field name="+_+keys[i].getString()+_+">");
				for(int y=1;y<=len;y++) {
					try {
						sb.append(_serialize(query.getAt(keys[i],y),done));
					} catch (PageException e) {
						sb.append(_serialize(e.getMessage(),done));
					}
				}
			
			sb.append(goIn()+"</field>");
		}
		deep--;
		
		sb.append(goIn()+"</recordset>");
		return sb.toString();
	}
	
	/**
	 * serialize a Object to his xml Format represenation
	 * @param object Object to serialize
	 * @param done 
	 * @return serialized Object
	 * @throws ConverterException
	 */
	private String _serialize(Object object, Set<Object> done) throws ConverterException {
		String rtn;
		deep++;
		// NULL
		if(object==null) {
			rtn= goIn()+"<null/>";
			deep--;
			return rtn;
		}
		// String
		if(object instanceof String) {
			rtn= goIn()+"<string>"+XMLUtil.escapeXMLString(object.toString())+"</string>";
			deep--;
			return rtn;
		}
		// Number
		if(object instanceof Number) {
			rtn= goIn()+"<number>"+((Number)object).doubleValue()+"</number>";
			deep--;
			return rtn;
		}
		// Boolean
		if(object instanceof Boolean) {
			rtn= goIn()+"<boolean value="+_+((Boolean)object).booleanValue()+_+"/>";
			deep--;
			return rtn;
		}
		// DateTime
		if(object instanceof DateTime) {
			rtn= _serializeDateTime((DateTime)object);
			deep--;
			return rtn;
		}
		// Date
		if(object instanceof Date) {
			rtn= _serializeDate((Date)object);
			deep--;
			return rtn;
		}
		// Date
		if(Decision.isCastableToBinary(object, false)) {
			rtn= _serializeBinary(Caster.toBinary(object,null));
			deep--;
			return rtn;
		}

		Object raw = LazyConverter.toRaw(object);
		if(done.contains(raw)){
			rtn= goIn()+"<null/>";
			deep--;
			return rtn;
		}
		
		done.add(raw);
		try {
			// Component
			if(object instanceof Component) {
				rtn= _serializeComponent((Component)object,done);
				deep--;
				return rtn;
			}
			// Struct
			if(object instanceof Struct) {
				rtn= _serializeStruct((Struct)object,done);
				deep--;
				return rtn;
			}
			// Map
			if(object instanceof Map) {
				rtn= _serializeMap((Map)object,done);
				deep--;
				return rtn;
			}
			// Array
			if(object instanceof Array) {
				rtn= _serializeArray((Array)object,done);
				deep--;
				return rtn;
			}
			// List
			if(object instanceof List) {
				rtn= _serializeList((List)object,done);
				deep--;
				return rtn;
			}
			// Query
			if(object instanceof Query) {
				rtn= _serializeQuery((Query)object,done);
				deep--;
				return rtn;
			}
		}
		finally{
			done.remove(raw);
		}
		// Others
		rtn="<struct type="+_+"L"+object.getClass().getName()+";"+_+"></struct>";
		deep--;
		return rtn;
	}

	@Override
	public void writeOut(PageContext pc, Object source, Writer writer) throws ConverterException, IOException {
		writer.write(serialize(source));
		writer.flush();
	}
	
	/**
	 * serialize a Object to his xml Format represenation and create a valid wddx representation
	 * @param object Object to serialize
	 * @return serialized wddx package
	 * @throws ConverterException
	 */
	public String serialize(Object object) throws ConverterException {
		deep=0;
		
		StringBuffer sb=new StringBuffer();	
		if(xmlConform)sb.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>");	
		sb.append("<wddxPacket version="+_+"1.0"+_+">");	
		deep++;
		sb.append(goIn()+"<header/>");
		sb.append(goIn()+"<data>");
		sb.append(_serialize(object,new HashSet<Object>()));
		sb.append(goIn()+"</data>");
		deep--;
		sb.append("</wddxPacket>");
		return sb.toString();
	}
	

	/**
	 * deserialize a WDDX Package (XML String Representation) to a runtime object
	 * @param strWddx
	 * @param validate
	 * @return Object represent WDDX Package
	 * @throws ConverterException
	 * @throws IOException
	 * @throws FactoryConfigurationError
	 */
	public Object deserialize(String strWddx, boolean validate) throws ConverterException, IOException, FactoryConfigurationError {
		try {
			DOMParser parser = new DOMParser();
			if(validate) parser.setEntityResolver(new WDDXEntityResolver());
			
            parser.parse(new InputSource(new StringReader(strWddx)));
            Document doc=parser.getDocument();
		    
		    // WDDX Package
		    NodeList docChldren = doc.getChildNodes();
		    Node wddxPacket=doc;
		    int len = docChldren.getLength();
		    for(int i = 0; i < len; i++) {
		    	Node node=docChldren.item(i);
		    	if(node.getNodeName().equalsIgnoreCase("wddxPacket")) {
		    		wddxPacket=node;
		    		break;
		    	}
		    }

			NodeList nl = wddxPacket.getChildNodes();
			int n = nl.getLength();

			
			for(int i = 0; i < n; i++) {
				Node data = nl.item(i);
				if(data.getNodeName().equals("data")) {
					NodeList list=data.getChildNodes();
					len=list.getLength();
					for(int y=0;y<len;y++) {
						Node node=list.item(y);
						if(node instanceof Element)
							return _deserialize((Element)node);
						
					}
				}
			}
			
			throw new IllegalArgumentException("Invalid WDDX Format: node 'data' not found in WDD packet");

		}
		catch(org.xml.sax.SAXException sxe) {
			throw new IllegalArgumentException("XML Error: " + sxe.toString());
		}
	}
	
	
	
	/**
	 * deserialize a WDDX Package (XML Element) to a runtime object
	 * @param element
	 * @return deserialized Element
	 * @throws ConverterException
	 */
	private Object _deserialize(Element element) throws ConverterException {
		String nodeName=element.getNodeName().toLowerCase();
		
		// NULL
		if(nodeName.equals("null")) {
			return null;
		}
		// String
		else if(nodeName.equals("string")) {
			return _deserializeString(element);
			/*Node data=element.getFirstChild();
			if(data==null) return "";
			
			String value=data.getNodeValue();
			
			if(value==null) return "";
			return XMLUtil.unescapeXMLString(value);*/
		}
		// Number
		else if(nodeName.equals("number")) {
			try {
				Node data=element.getFirstChild();
				if(data==null) return new Double(0);
				return Caster.toDouble(data.getNodeValue());
			} catch (Exception e) {
				throw toConverterException(e);
			}
		}
		// Boolean
		else if(nodeName.equals("boolean")) {
			try {
				return Caster.toBoolean(element.getAttribute("value"));
			} catch (PageException e) {
				throw toConverterException(e);
				
			}
		}
		// Array
		else if(nodeName.equals("array")) {
			return _deserializeArray(element);
		}
		// Component
		else if(nodeName.equals("component")) {
			return  _deserializeComponent(element);
		}
		// Struct
		else if(nodeName.equals("struct")) {
			return  _deserializeStruct(element);
		}
		// Query
		else if(nodeName.equals("recordset")) {
			return  _deserializeQuery(element);
		}
		// DateTime
		else if(nodeName.equalsIgnoreCase("dateTime")) {
			try {
				return DateCaster.toDateAdvanced(element.getFirstChild().getNodeValue(),timeZone);
			} 
            catch (Exception e) {
				throw toConverterException(e);
			} 
		}
		// Query
		else if(nodeName.equals("binary")) {
			return  _deserializeBinary(element);
		}
		else 
			throw new ConverterException("can't deserialize Element of type ["+nodeName+"] to a Object representation");
		
	}

	private Object _deserializeString(Element element) {
		NodeList childList = element.getChildNodes();
		int len = childList.getLength();
		StringBuffer sb=new StringBuffer();
		Node data;
		String str;
		for(int i=0;i<len;i++) {
			data=childList.item(i);
			if(data==null)continue;
			
			//<char code="0a"/>
			if("char".equals(data.getNodeName())) {
				str=((Element)data).getAttribute("code");
				sb.append((char)NumberUtil.hexToInt(str, 10));
			}
			else {
				sb.append(str=data.getNodeValue());
			}
		}
		return sb.toString();
		//return XMLUtil.unescapeXMLString(sb.toString());
	}

	/**
	 * Desirialize a Query Object
	 * @param recordset Query Object as XML Element
	 * @return Query Object
	 * @throws ConverterException
	 */
	private Object _deserializeQuery(Element recordset) throws ConverterException {
		try {
			// create Query Object
			Query query=new QueryImpl(
					railo.runtime.type.util.ListUtil.listToArray(
							recordset.getAttribute("fieldNames"),','
					)
				,Caster.toIntValue(recordset.getAttribute("rowCount")),"query"
			);
			
			NodeList list = recordset.getChildNodes();
			int len=list.getLength();
			for(int i=0;i<len;i++) {
				Node node=list.item(i);
				if(node instanceof Element) {
					_deserializeQueryField(query,(Element) node);
				}			
			}
			return query;
		}
		catch(PageException e) {
			throw toConverterException(e);
		}
		
	}
	


	private Object _deserializeBinary(Element el) throws ConverterException {
		Node node = el.getFirstChild();
		if(node instanceof CharacterData) {
			String data=((CharacterData)node).getData();
			try {
				return Base64Coder.decode(data);
			} catch (CoderException e) {
				throw new ConverterException(e.getMessage());
			}
		}
		throw new ConverterException("cannot convert serialized binary back to binary data");
	}

	/**
	 * deserilize a single Field of a query WDDX Object
	 * @param query
	 * @param field
	 * @throws ConverterException 
	 * @throws PageException
	 */
	private void _deserializeQueryField(Query query,Element field) throws PageException, ConverterException {
		String name=field.getAttribute("name");
		NodeList list = field.getChildNodes();
		int len=list.getLength();
		int count=0;
		for(int i=0;i<len;i++) {
			Node node=list.item(i);
			if(node instanceof Element) {
				query.setAt(KeyImpl.init(name),++count,_deserialize((Element) node));
			}			
		}
		
	}
	
	/**
	 * Desirialize a Component Object
	 * @param elComp Component Object as XML Element
	 * @return Component Object
	 * @throws ConverterException 
	 * @throws ConverterException
	 */
	private Object _deserializeComponent(Element elComp) throws ConverterException {
//		String type=elStruct.getAttribute("type");
		String name=elComp.getAttribute("name");
		String md5=elComp.getAttribute("md5");
		
		// TLPC
		PageContext pc = ThreadLocalPageContext.get();
		
		// Load comp
		ComponentAccess comp=null;
		try {
			comp = ComponentUtil.toComponentAccess(pc.loadComponent(name));
			if(!ComponentUtil.md5(comp).equals(md5)){
				throw new ConverterException("component ["+name+"] in this enviroment has not the same interface as the component to load, it is possible that one off the components has Functions added dynamicly.");
			}
		} 
		catch (ConverterException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ConverterException(e.getMessage());
		}
		
		
		NodeList list=elComp.getChildNodes();
		ComponentScope scope = comp.getComponentScope();
		int len=list.getLength();
		String scopeName;
		Element var,value;
		Collection.Key key;
		for(int i=0;i<len;i++) {
            Node node=list.item(i);
			if(node instanceof Element) {
				var=(Element)node;
				value=getChildElement((Element)node);
				scopeName=var.getAttribute("scope");
				if(value!=null) {
					key=Caster.toKey(var.getAttribute("name"),null);
					if(key==null) continue;
					if("variables".equalsIgnoreCase(scopeName))
						scope.setEL(key,_deserialize(value));
					else
						comp.setEL(key,_deserialize(value));
				}
            }
		}
        return comp;
	}

	/**
	 * Desirialize a Struct Object
	 * @param elStruct Struct Object as XML Element
	 * @return Struct Object
	 * @throws ConverterException
	 */
	private Object _deserializeStruct(Element elStruct) throws ConverterException {
		String type=elStruct.getAttribute("type");
		Struct struct=new StructImpl();
        
		NodeList list=elStruct.getChildNodes();
		int len=list.getLength();
		for(int i=0;i<len;i++) {
            //print.ln(i);
            
			Node node=list.item(i);
			if(node instanceof Element) {
				Element var=(Element)node;
				Element value=getChildElement((Element)node);
				if(value!=null) {
					struct.setEL(var.getAttribute("name"),_deserialize(value));
					
				}
            }
		}
        if(struct.size()==0 && type!=null && type.length()>0) {
            return "";
        }        
		return struct;
	}

	/**
	 * Desirialize a Struct Object
	 * @param el Struct Object as XML Element
	 * @return Struct Object
	 * @throws ConverterException
	 */
	private Array _deserializeArray(Element el) throws ConverterException {
		Array array=new ArrayImpl();
		
		NodeList list=el.getChildNodes();
		int len=list.getLength();
		for(int i=0;i<len;i++) {
			Node node=list.item(i);
			if(node instanceof Element)
				try {
					array.append(_deserialize((Element)node));
				} catch (PageException e) {
					throw toConverterException(e);
				}
			
		}
		return array;
	}

	/**
	 * return fitst child Element of a Element, if there are no child Elements return null
	 * @param parent parent node
	 * @return child Element
	 */
	private Element getChildElement(Element parent) {
		NodeList list=parent.getChildNodes();
		int len=list.getLength();
		for(int i=0;i<len;i++) {
			Node node=list.item(i);
			if(node instanceof Element) {
				return (Element)node;
			}			
		}
		return null;
	}
	
	
	/**
	 * @return return current blockquote
	 */
	private String goIn() {
		//StringBuffer rtn=new StringBuffer(deep);
		//for(int i=0;i<deep;i++) rtn.append('\t');
		//return rtn.toString();
		return "";
	}

    @Override
    public boolean equals(Object obj) {
        return timeZone.equals(obj);
    }
}