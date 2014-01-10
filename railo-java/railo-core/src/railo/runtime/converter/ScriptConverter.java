package railo.runtime.converter;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.w3c.dom.Node;

import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.ComponentScope;
import railo.runtime.ComponentWrap;
import railo.runtime.PageContext;
import railo.runtime.component.Property;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.displayFormatting.DateFormat;
import railo.runtime.functions.displayFormatting.TimeFormat;
import railo.runtime.op.Caster;
import railo.runtime.orm.ORMUtil;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.ObjectWrap;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.type.util.KeyConstants;

/**
 * class to serialize and desirilize WDDX Packes
 */
public final class ScriptConverter extends ConverterSupport {
	private static final Collection.Key REMOTING_FETCH = KeyImpl.intern("remotingFetch");
    
	private int deep=1;
	private boolean ignoreRemotingFetch=true;
	
    /**
     * constructor of the class
     */
    public ScriptConverter() {
    }
    public ScriptConverter(boolean ignoreRemotingFetch) {
    	this.ignoreRemotingFetch=ignoreRemotingFetch;
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
            throw toConverterException(e);
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
		    sb.append(TimeFormat.call(null,dateTime,"H,m,s,l,\"z\""));
		    sb.append(')');
		} 
	    catch (PageException e) {
			throw toConverterException(e);
		}
	}

	/**
	 * serialize a Array
	 * @param array Array to serialize
	 * @param sb
	 * @param done 
	 * @throws ConverterException
	 */
	private void _serializeArray(Array array, StringBuffer sb, Set<Object> done) throws ConverterException {
		_serializeList(array.toList(),sb,done);
	}
	
	/**
	 * serialize a List (as Array)
	 * @param list List to serialize
	 * @param sb
	 * @param done 
	 * @throws ConverterException
	 */
	private void _serializeList(List list, StringBuffer sb, Set<Object> done) throws ConverterException {
		
	    sb.append(goIn());
	    sb.append("[");
	    boolean doIt=false;
		ListIterator it=list.listIterator();
		while(it.hasNext()) {
		    if(doIt)sb.append(',');
		    doIt=true;
			_serialize(it.next(),sb,done);
		}
		
		sb.append(']');
	}

    /**
     * serialize a Struct
     * @param struct Struct to serialize
     * @param sb
     * @param done 
     * @throws ConverterException
     */
    public void _serializeStruct(Struct struct, StringBuffer sb, Set<Object> done) throws ConverterException {
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
            _serialize(struct.get(key,null),sb,done);
        }
        deep--;
        
        sb.append('}');
    }
    
    public String serializeStruct(Struct struct, Set<Collection.Key> ignoreSet) throws ConverterException {
    	StringBuffer sb =new StringBuffer();
        sb.append(goIn());
        sb.append("{");
        boolean hasIgnores=ignoreSet!=null;
        Iterator<Key> it = struct.keyIterator();
        boolean doIt=false;
        deep++;
        Key key;
        while(it.hasNext()) {
            key = it.next();
            if(hasIgnores && ignoreSet.contains(key)) continue;
            if(doIt)sb.append(',');
            doIt=true;
            sb.append('\'');
            sb.append(escape(key.getString()));
            sb.append('\'');
            sb.append(':');
            _serialize(struct.get(key,null),sb,new HashSet<Object>());
        }
        deep--;
        
        return sb.append('}').toString();
    }

    /**
     * serialize a Map (as Struct)
     * @param map Map to serialize
     * @param sb
     * @param done 
     * @throws ConverterException
     */
    private void _serializeMap(Map map, StringBuffer sb, Set<Object> done) throws ConverterException {
        if(map instanceof Serializable) {
        	_serializeSerializable((Serializable)map,sb);
        	return;
        }
    	sb.append(goIn());
        sb.append("{");
        
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
            _serialize(map.get(key),sb,done);
        }
        deep--;
        
        sb.append('}');
    }
    /**
     * serialize a Component
     * @param component Component to serialize
     * @param sb
     * @param done 
     * @throws ConverterException
     */
    private void _serializeComponent(Component c, StringBuffer sb, Set<Object> done) throws ConverterException {
    	
    	ComponentAccess ci;
		try {
			ci = ComponentUtil.toComponentAccess(c);
		} catch (ExpressionException ee) {
			throw new ConverterException(ee.getMessage());
		}
		ComponentWrap cw = new ComponentWrap(Component.ACCESS_PRIVATE,ci);  
        
    	
    	sb.append(goIn());
        try {
        	sb.append("evaluateComponent('"+c.getAbsName()+"','"+ComponentUtil.md5(ci)+"',{");
		} catch (Exception e) {
			throw toConverterException(e);
		}
		
		boolean doIt=false;
		Object member;
	    {
			
			Iterator<Entry<Key, Object>> it = cw.entryIterator();
	        deep++;
	        Entry<Key, Object> e;
	        while(it.hasNext()) {
	            e = it.next();
	            member = e.getValue();
	            if(member instanceof UDF)continue;
	            if(doIt)sb.append(',');
	            doIt=true;
	            sb.append('\'');
	            sb.append(escape(e.getKey().getString()));
	            sb.append('\'');
	            sb.append(':');
	            _serialize(member,sb,done);
	        }
	        sb.append("}");
	        deep--;
		}
        {
        	boolean isPeristent=ci.isPersistent();
    		
        	ComponentScope scope = ci.getComponentScope();
        	Iterator<Entry<Key, Object>> it = scope.entryIterator();
            sb.append(",{");
        	deep++;
        	doIt=false;
        	Property p;
            Boolean remotingFetch;
        	Struct props = ignoreRemotingFetch?null:ComponentUtil.getPropertiesAsStruct(ci,false);
        	Entry<Key, Object> e;
        	Key k;
        	while(it.hasNext()) {
        		e = it.next();
        		k = e.getKey();
                //String key=Caster.toString(it.next(),"");
                if(KeyConstants._THIS.equalsIgnoreCase(k))continue;
                if(!ignoreRemotingFetch) {
                	p=(Property) props.get(k,null);
                	if(p!=null) {
                		remotingFetch=Caster.toBoolean(p.getDynamicAttributes().get(REMOTING_FETCH,null),null);
    	            	if(remotingFetch==null){
        					if(isPeristent  && ORMUtil.isRelated(p)) continue;
    	    			}
    	    			else if(!remotingFetch.booleanValue()) continue;
                	}
        		}
                
                
                
                member = e.getValue();
                if(member instanceof UDF)continue;
                if(doIt)sb.append(',');
                doIt=true;
                sb.append('\'');
                sb.append(escape(k.getString()));
                sb.append('\'');
                sb.append(':');
                _serialize(member,sb,done);
            }
            sb.append("}");
            deep--;
        }
        
        sb.append(")");
        //sb.append("");
        //throw new ConverterException("can't serialize a component "+component.getDisplayName());
    }

	/**
	 * serialize a Query
	 * @param query Query to serialize
	 * @param sb
	 * @param done 
	 * @throws ConverterException
	 */
	private void _serializeQuery(Query query, StringBuffer sb, Set<Object> done) throws ConverterException {
		
		//Collection.Key[] keys = query.keys();
		Iterator<Key> it = query.keyIterator();
		Key k;
		sb.append(goIn());
		sb.append("query(");
		
	
		deep++;
		boolean oDoIt=false;
		int len=query.getRecordcount();
		while(it.hasNext()) {
			k = it.next();
		    if(oDoIt)sb.append(',');
		    oDoIt=true;
		    sb.append(goIn());
            sb.append('\'');
            sb.append(escape(k.getString()));
            sb.append('\'');
			sb.append(":[");
			boolean doIt=false;
			for(int y=1;y<=len;y++) {
			    if(doIt)sb.append(',');
			    doIt=true;
			    try {
					_serialize(query.getAt(k,y),sb,done);
				} catch (PageException e) {
					_serialize(e.getMessage(),sb,done);
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
	 * @param done 
	 * @throws ConverterException
	 */
	private void _serialize(Object object, StringBuffer sb, Set<Object> done) throws ConverterException {
		//try	{
			deep++;
			// NULL
			if(object==null) {
			    sb.append(goIn());
			    sb.append("nullValue()");
			    deep--;
			    return;
			}
			// String
			if(object instanceof String) {
			    sb.append(goIn());
			    sb.append("'");
			    sb.append(escape(object.toString()));
			    sb.append("'");
			    deep--;
			    return;
			}
			// Number
			if(object instanceof Number) {
			    sb.append(goIn());
			    sb.append(Caster.toString(((Number)object)));
			    deep--;
			    return;
			}
			// Boolean
			if(object instanceof Boolean) {
			    sb.append(goIn());
			    sb.append(Caster.toString(((Boolean)object).booleanValue()));
			    deep--;
			    return;
			}
			// DateTime
			if(object instanceof DateTime) {
				_serializeDateTime((DateTime)object,sb);
			    deep--;
			    return;
			}
			// Date
			if(object instanceof Date) {
				_serializeDate((Date)object,sb);
			    deep--;
			    return;
			}
	        // XML
	        if(object instanceof Node) {
	            _serializeXML((Node)object,sb);
			    deep--;
			    return;
	        }
			if(object instanceof ObjectWrap) {
				try {
					_serialize(((ObjectWrap)object).getEmbededObject(), sb,done);
				} catch (PageException e) {
					throw toConverterException(e);
				}
			    deep--;
			    return;
			}
	        // Timespan
	        if(object instanceof TimeSpan) {
	        	_serializeTimeSpan((TimeSpan) object,sb);
			    deep--;
			    return;
	        }
			Object raw = LazyConverter.toRaw(object);
	        if(done.contains(raw)) {
	        	sb.append(goIn());
			    sb.append("nullValue()");
			    deep--;
			    return;
	        }
			
			done.add(raw);
			try {
		        // Component
		        if(object instanceof Component) {
		            _serializeComponent((Component)object,sb,done);
				    deep--;
				    return;
		        }
		
		        // Struct
		        if(object instanceof Struct) {
		            _serializeStruct((Struct)object,sb,done);
				    deep--;
				    return;
		        }
		        // Map
		        if(object instanceof Map) {
		            _serializeMap((Map)object,sb,done);
				    deep--;
				    return;
		        }
				// Array
				if(object instanceof Array) {
					_serializeArray((Array)object,sb,done);
				    deep--;
				    return;
				}
				// List
				if(object instanceof List) {
					_serializeList((List)object,sb,done);
				    deep--;
				    return;
				}
		        // Query
		        if(object instanceof Query) {
		            _serializeQuery((Query)object,sb,done);
				    deep--;
				    return;
		        }
				// String Converter
				if(object instanceof ScriptConvertable) {
				    sb.append(((ScriptConvertable)object).serialize());
				    deep--;
				    return;
				}
				if(object instanceof Serializable) {
					_serializeSerializable((Serializable)object,sb);
				    deep--;
				    return;
				}
			}
			finally {
				done.remove(raw);
			}
			throw new ConverterException("can't serialize Object of type [ "+Caster.toClassName(object)+" ]");
			//deep--;
		/*}
		catch(StackOverflowError soe){
			throw soe;
		}*/
	}
	


    private void _serializeXML(Node node, StringBuffer sb) {
    	node=XMLCaster.toRawNode(node);
    	sb.append(goIn());
	    sb.append("xmlParse('");
	    sb.append(escape(XMLCaster.toString(node,"")));
	    sb.append("')");
    	
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

	@Override
	public void writeOut(PageContext pc, Object source, Writer writer) throws ConverterException, IOException {
		writer.write(serialize(source));
		writer.flush();
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
		_serialize(object,sb,new HashSet<Object>());
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