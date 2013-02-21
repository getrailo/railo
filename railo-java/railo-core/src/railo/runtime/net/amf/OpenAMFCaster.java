package railo.runtime.net.amf;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.openamf.AMFBody;
import org.openamf.config.OpenAMFConfig;
import org.openamf.config.PageableRecordsetConfig;
import org.openamf.recordset.ASRecordSet;

import railo.runtime.Component;
import railo.runtime.component.Property;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.cfc.ComponentAccess;
import flashgateway.io.ASObject;

/**
 * Cast a CFML object to AMF Objects and the other way
 */
public final class OpenAMFCaster implements AMFCaster {
	
	
	private static OpenAMFCaster singelton;


	private OpenAMFCaster(){}
	
	public static AMFCaster getInstance(){
		if(singelton==null){
			singelton= new OpenAMFCaster();
		}
		return singelton;
	}
    
    /**
     * cast cfml Object to AMF Object
     * @param o
     * @return
     * @throws PageException
     */
    public Object toAMFObject(Object o) throws PageException {
    	if(o instanceof ASObject) return o;
        if(Decision.isBinary(o))    return o;
        if(Decision.isArray(o))     return toAMFObject(Caster.toList(o,true));
        if(Decision.isBoolean(o))   return Caster.toBoolean(o);
        if(Decision.isComponent(o)) return toAMFObject((Component)o);
        if(Decision.isDateSimple(o,false))return Caster.toDate(o,null);
        if(Decision.isNumeric(o))   return Caster.toDouble(o);
        if(Decision.isQuery(o))     return toAMFObject(Caster.toQuery(o));
        if(Decision.isStruct(o))    return toAMFObject(Caster.toMap(o,true));
        if(Decision.isUserDefinedFunction(o))
                                    return toAMFObject((UDF)o);
        
        
        
        return o;
    }
    
    
    private List toAMFObject(List list) throws PageException {
        ListIterator it = list.listIterator();
        while(it.hasNext()) {
            list.set(it.nextIndex(),toAMFObject(it.next()));
        }
        return list;
    }
    private Map toAMFObject(Map map) throws PageException {
        Object[] keys = map.keySet().toArray();
        for(int i=0;i<keys.length;i++) {
            Object key=keys[i];
            Object value=map.get(key);
            if(key instanceof String) {
                map.remove(key);
                key=((String)key).toUpperCase();
            }
            map.put(key,toAMFObject(value));
        }
        return map;
    }
    private Object toAMFObject(Component c) throws PageException {
    	ASObject aso = new ASObject();
    	if(c instanceof ComponentAccess) {
    		Property[] prop = c.getProperties(false);
        	if(prop!=null)for(int i=0;i<prop.length;i++) {
        		aso.put(prop[i].getName().toUpperCase(), toAMFObject(c.get(prop[i].getName(),null)));
        	}
    	}
    	return aso;
    }
    private ASRecordSet toAMFObject(Query qry) throws PageException {
        PageableRecordsetConfig prsc = new PageableRecordsetConfig();
        prsc.setInitialDataRowCount(qry.getRowCount());
        OpenAMFConfig.getInstance().setPageableRecordsetConfig(prsc);
        
        
        ASRecordSet rs=new ASRecordSet();
        
        String[] keys=qry.getColumns();
        QueryColumn[] columns=new QueryColumn[keys.length];
        for(int i=0;i<columns.length;i++) {
            columns[i]=qry.getColumn(keys[i]);
            //rows.add(i,new ArrayList());
        }
        
        
        int iCol;
        ArrayList rows = new ArrayList();
        ArrayList row;
        int rowCount=qry.getRecordcount();
        for(int iRow=1;iRow<=rowCount;iRow++) {
            rows.add(row=new ArrayList());
            for(iCol=0;iCol<columns.length;iCol++) {
                row.add(toAMFObject(columns[iCol].get(iRow,null)));
            }
        }
        
        
        rs.populate(qry.getColumns(),rows);
        
        return rs;
    }
    private static Object toAMFObject(UDF udf) throws PageException {
        throw new ApplicationException("can't send a User Defined Function ("+udf.getFunctionName()+") via flash remoting");
    }
    
    public Object toCFMLObject(Object amf) throws PageException {
        if(amf instanceof List) return toCFMLObject((List)amf);
        if(amf instanceof Map) return toCFMLObject((Map)amf);
        if(amf instanceof ASRecordSet) return toCFMLObject((ASRecordSet)amf);
        
        return amf;
    }
    private List toCFMLObject(List list) throws PageException {
        ListIterator it = list.listIterator();
        while(it.hasNext()) {
            list.set(it.nextIndex(),toCFMLObject(it.next()));
        }
        return list;
    }
    
    private Map toCFMLObject(Map map) throws PageException {
        Object[] keys = map.keySet().toArray();
        for(int i=0;i<keys.length;i++) {
            Object key=keys[i];
            map.put(key,toCFMLObject(map.get(key)));
        }
        return map;
    }
    
    private Query toCFMLObject(ASRecordSet rs) throws PageException {
        String[] columns = rs.getColumnNames();
        List rows = rs.rows();
        int len=0;
        if(rows.size()>0) len=((List)rows.get(0)).size();
        Query qry=new QueryImpl(columns,len,"query");
        
        List row;
        for(int iCol=0;iCol<columns.length;iCol++) {
            row=(List) rows.get(iCol);
            QueryColumn column = qry.getColumn(columns[iCol]);
            
            for(int iRow=0;iRow<row.size();iRow++) {
                column.set(iRow+1,toCFMLObject(row.get(iRow)));
            }
        }
        return qry;
    }
    
    
    /**
     * translate a AMFBody type to a String type
     * @param type
     * @return string type
     */
    public static String toStringType(byte type) {
        switch(type) {
        case AMFBody.DATA_TYPE_ARRAY: return "array";
        case AMFBody.DATA_TYPE_AS_OBJECT: return "as object";
        case AMFBody.DATA_TYPE_BOOLEAN: return "boolean";
        case AMFBody.DATA_TYPE_CUSTOM_CLASS: return "custom";
        case AMFBody.DATA_TYPE_DATE: return "date";
        case AMFBody.DATA_TYPE_LONG_STRING: return "long string";
        case AMFBody.DATA_TYPE_MIXED_ARRAY: return "mixed array";
        case AMFBody.DATA_TYPE_MOVIE_CLIP: return "movie";
        case AMFBody.DATA_TYPE_NULL: return "null";
        case AMFBody.DATA_TYPE_NUMBER: return "number";
        case AMFBody.DATA_TYPE_OBJECT: return "object";
        case AMFBody.DATA_TYPE_OBJECT_END: return "object end";
        case AMFBody.DATA_TYPE_RECORDSET: return "recordset";
        case AMFBody.DATA_TYPE_REFERENCE_OBJECT: return "ref object";
        case AMFBody.DATA_TYPE_STRING: return "string";
        case AMFBody.DATA_TYPE_UNDEFINED: return "undefined";
        case AMFBody.DATA_TYPE_UNKNOWN: return "unknow";
        case AMFBody.DATA_TYPE_XML: return "xml";
        }

        return "";
    }

	public void init(Map arguments) {
		// TODO Auto-generated method stub
		
	}
}