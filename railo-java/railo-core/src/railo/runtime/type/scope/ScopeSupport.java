package railo.runtime.type.scope;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import railo.commons.lang.ByteNameValuePair;
import railo.commons.lang.SizeOf;
import railo.commons.lang.StringList;
import railo.commons.lang.StringUtil;
import railo.commons.net.URLDecoder;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.ExpressionException;
import railo.runtime.op.Caster;
import railo.runtime.security.ScriptProtect;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.CastableStruct;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Scope;
import railo.runtime.type.Sizeable;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;



/**
 * Simple standart implementation of a Scope, for standart use.
 */
public class ScopeSupport extends StructImpl implements Scope,Sizeable {
	

	public static final Key APPLICATION = KeyImpl.getInstance("application");
	public static final Key ARGUMENTS = KeyImpl.getInstance("arguments");
	public static final Key CGI = KeyImpl.getInstance("cgi");
	public static final Key COOKIE = KeyImpl.getInstance("cookie");
	public static final Key CLIENT = KeyImpl.getInstance("client");
	public static final Key CLUSTER = KeyImpl.getInstance("cluster");
	public static final Key FORM = KeyImpl.getInstance("form");
	public static final Key REQUEST = KeyImpl.getInstance("request");
	public static final Key SESSION = KeyImpl.getInstance("session");
	public static final Key SERVER = KeyImpl.getInstance("server");
	public static final Key URL = KeyImpl.getInstance("url");
	public static final Key VARIABLES = KeyImpl.getInstance("variables");
	
	private String name;
    private String dspName;
	private static int _id=0;
	private int id=0;
    private static final byte[] EMPTY="".getBytes();
	
	/**
	 * Field <code>isInit</code>
	 */
	protected boolean isInit;
    private int type;

    /**
     * constructor for the Simple class
     * @param name name of the scope
     * @param type scope type (SCOPE_APPLICATION,SCOPE_COOKIE use)
     */
    public ScopeSupport(boolean sync,String name, int type) {
        super(sync?StructImpl.TYPE_SYNC:StructImpl.TYPE_REGULAR);
        this.name=name;
        this.type=type;
        
        id=++_id;
    }
    /**
     * constructor for ScopeSupport
     * @param name name of the scope
     * @param type scope type (SCOPE_APPLICATION,SCOPE_COOKIE use)
     * @param doubleLinked mean that the struct has predictable iteration order this make the input order fix
     */
    public ScopeSupport(String name, int type, int mapType) {
        super(mapType);
        this.name=name;
        this.type=type;
        
        id=++_id;
    }
	
    /**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */

	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return toDumpData(pageContext, maxlevel, dp, this, dspName);
	}
	public static DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp, Struct sct,String dspName) {
		maxlevel--;
		DumpTable table = new DumpTable("#5965e4","#9999ff","#000000");//new DumpTable("#99cc00","#669900","#263300");
		if(dspName!=null)table.setTitle(StringUtil.ucFirst(dspName)+" Scope");
        else table.setTitle("Scope");
        
		//Map mapx=getMap();
		Iterator it=sct.keyIterator();//mapx.keySet().iterator();
		String key;
		int maxkeys=dp.getMaxKeys();
		int index=0;
		while(it.hasNext()) {
			key=it.next().toString();
			if(DumpUtil.keyValid(dp,maxlevel, key)) {
				if(maxkeys<=index++)break;
				table.appendRow(1,
					new SimpleDumpData(key),
					DumpUtil.toDumpData(sct.get(key,null), pageContext,maxlevel,dp));
			}
		}
		return table;
	}
		
	/**
	 * @see railo.runtime.type.StructImpl#invalidKey(java.lang.String)
	 */
	protected ExpressionException invalidKey(String key) {
		return new ExpressionException("variable ["+key+"] doesn't exist in "+StringUtil.ucFirst(name)+" Scope (keys:"+List.arrayToList(keysAsString(), ",")+")");
	}

    /**
     * write parameter defined in a query string (name1=value1&name2=value2) to the scope
     * @param qs Query String
     * @return parsed name value pair
     */
	protected static ByteNameValuePair[] setFromQueryString(String str) {
		return setFrom___(str, '&');
	}

	protected static ByteNameValuePair[] setFromTextPlain(String str) {
		return setFrom___(str, '\n');
	}
	protected static ByteNameValuePair[] setFrom___(String tp,char delimeter) {
        if(tp==null) return new ByteNameValuePair[0];
        Array arr=List.listToArrayRemoveEmpty(tp,delimeter);
        ByteNameValuePair[] pairs=new ByteNameValuePair[arr.size()];
        
        //Array item;
        int index;
        String name;
        
        for(int i=1;i<=pairs.length;i++) {
            name=Caster.toString(arr.get(i,""),"");
            //if(name.length()==0) continue;
            
            index=name.indexOf('=');
            if(index!=-1) pairs[i-1]=new ByteNameValuePair(getBytes(name.substring(0,index),"ISO-8859-1"),getBytes(name.substring(index+1),"ISO-8859-1"),true);
            else pairs[i-1]=new ByteNameValuePair(getBytes(name,"ISO-8859-1"),EMPTY,true);
          
        }
        return pairs;
    }

    protected static byte[] getBytes(String str) {
        return str.getBytes();
    }
    protected static byte[] getBytes(String str,String encoding) {
        try {
            return str.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            return EMPTY;
        }
    }
    
    protected void fillDecodedEL(ByteNameValuePair[] raw, String encoding, boolean scriptProteced) {
    	try {
			fillDecoded(raw, encoding,scriptProteced);
		} catch (UnsupportedEncodingException e) {
			try {
				fillDecoded(raw, "iso-8859-1",scriptProteced);
			} catch (UnsupportedEncodingException e1) {}
		}
    }
        
    
    /**
     * fill th data from given strut and decode it
     * @param raw
     * @param encoding
     * @throws UnsupportedEncodingException
     */
    protected void fillDecoded(ByteNameValuePair[] raw, String encoding, boolean scriptProteced) throws UnsupportedEncodingException {
    	clear();
    	String name,value;
        //Object curr;
        for(int i=0;i<raw.length;i++) {
            name=raw[i].getName(encoding);
            value=raw[i].getValue(encoding);
            if(raw[i].isUrlEncoded()) {
            	name=URLDecoder.decode(name,encoding);
            	value=URLDecoder.decode(value,encoding);
            }
            // MUST valueStruct
            if(name.indexOf('.')!=-1) {
                StringList list=List.listToStringListRemoveEmpty(name,'.');
                Struct parent=this;
                while(list.hasNextNext()) {
                    parent=_fill(parent,list.next(),new CastableStruct(),false,scriptProteced);
                }
                _fill(parent,list.next(),value,true,scriptProteced);
            } 
            //else 
                _fill(this,name,value,true,scriptProteced);
        }
    }
    
    private Struct _fill(Struct parent, String name, Object value, boolean isLast, boolean scriptProteced) {
        Object curr;
        boolean isArrayDef=false;
        Collection.Key key=KeyImpl.init(name);
        
        // script protect
        if(scriptProteced && value instanceof String) {
        	value=ScriptProtect.translate((String)value);
        }
        
        if(name.length() >2 && name.endsWith("[]")) {
            isArrayDef=true;
            name=name.substring(0,name.length()-2);
            key=KeyImpl.init(name);
            curr=parent.get(key,null);                
        }
        else {
            curr=parent.get(key,null);
        }
        
        if(curr==null) {
        	if(isArrayDef) {
        		ArrayImpl arr = new ArrayImpl();
        		arr.add(value);
        		parent.setEL(key,arr);
        	}
            else parent.setEL(key,value); 
        }
        else if(curr instanceof ArrayImpl){
            ((ArrayImpl) curr).appendEL(value);
        }
        else if(curr instanceof CastableStruct){
        	if(isLast) ((CastableStruct)curr).setValue(value);
            else return (Struct) curr;
        	
        }
        else if(curr instanceof StructImpl){
            if(isLast) parent.setEL(key,value);
            else return (Struct) curr;
        }
        else if(curr instanceof String){
            if(isArrayDef) {
            	ArrayImpl arr = new ArrayImpl();
            	arr.add(curr);
            	arr.add(value);
                parent.setEL(key,arr);
            }
            else if(value instanceof StructImpl) {
                parent.setEL(key,value);
            }
            else {
            	if(!StringUtil.isEmpty(value)) {
            		String existing=Caster.toString(curr,"");
            		if(StringUtil.isEmpty(existing))
            			parent.setEL(key,value);
            		else 
            			parent.setEL(key,Caster.toString(curr,"")+','+value);
            	}
            }
        }
        if(!isLast) {
            return (Struct)value;
        }
        return null;
    }
    
    /*
    private String decode(Object value,String encoding) throws UnsupportedEncodingException {
        return URLDecoder.decode(new String(Caster.toString(value,"").getBytes("ISO-8859-1"),encoding),encoding);
    }*/
    
    /**
	 * @see railo.runtime.type.Scope#isInitalized()
	 */
	public boolean isInitalized() {
		return isInit;
	}

	/**
	 * @see railo.runtime.type.Scope#initialize(railo.runtime.PageContext)
	 */
	public void initialize(PageContext pc) {
        isInit=true;
	}

	/**
	 * @see railo.runtime.type.Scope#release()
	 */
	public void release() {
		clear();
		isInit=false;
	}


    /**
     * @return Returns the id.
     */
    public int _getId() {
        return id;
    }
    
    /**
     * display name for dump
     * @param dspName
     */
    protected void setDisplayName(String dspName) {
        this.dspName=dspName;
    }
    
    /**
     * @see railo.runtime.type.Scope#getType()
     */
    public int getType() {
        return type;
    }
    
    /**
     * @see railo.runtime.type.Scope#getTypeAsString()
     */
    public String getTypeAsString() {
        return name;
    }
	public long sizeOf() {
		return SizeOf.size(getMap());
	}
    
}