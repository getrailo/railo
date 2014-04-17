package railo.runtime.type.scope;

import java.io.UnsupportedEncodingException;

import railo.commons.lang.StringList;
import railo.commons.lang.StringUtil;
import railo.commons.net.URLDecoder;
import railo.commons.net.URLItem;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.ExpressionException;
import railo.runtime.op.Caster;
import railo.runtime.security.ScriptProtect;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.CastableStruct;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Sizeable;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.ListUtil;
import railo.runtime.type.util.StructUtil;



/**
 * Simple standart implementation of a Scope, for standart use.
 */
public abstract class ScopeSupport extends StructImpl implements Scope,Sizeable {
	
	private static final long serialVersionUID = -4185219623238374574L;
	
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
        super(sync?StructImpl.TYPE_SYNC:StructImpl.TYPE_UNDEFINED);
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
	
    @Override

	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return toDumpData(pageContext, maxlevel, dp, this, dspName);
	}
	
	public static DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp, Struct sct,String dspName) {
		if(StringUtil.isEmpty(dspName))dspName="Scope";
		
		return StructUtil.toDumpTable(sct, dspName, pageContext, maxlevel, dp);
		
	}
		
	protected ExpressionException invalidKey(String key) {
		return new ExpressionException("variable ["+key+"] doesn't exist in "+StringUtil.ucFirst(name)+" Scope (keys:"+ListUtil.arrayToList(keys(), ",")+")");
	}

    /**
     * write parameter defined in a query string (name1=value1&name2=value2) to the scope
     * @param qs Query String
     * @return parsed name value pair
     */
	protected static URLItem[] setFromQueryString(String str) {
		return setFrom___(str, '&');
	}

	protected static URLItem[] setFromTextPlain(String str) {
		return setFrom___(str, '\n');
	}
	protected static URLItem[] setFrom___(String tp,char delimiter) {
        if(tp==null) return new URLItem[0];
        Array arr=ListUtil.listToArrayRemoveEmpty(tp,delimiter);
        URLItem[] pairs=new URLItem[arr.size()];
        
        //Array item;
        int index;
        String name;
        
        for(int i=1;i<=pairs.length;i++) {
            name=Caster.toString(arr.get(i,""),"");
            //if(name.length()==0) continue;
            
            index=name.indexOf('=');
            if(index!=-1) pairs[i-1]=new URLItem(name.substring(0,index),name.substring(index+1),true);
            else pairs[i-1]=new URLItem(name,"",true);
          
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
    
    protected void fillDecodedEL(URLItem[] raw, String encoding, boolean scriptProteced, boolean sameAsArray) {
    	try {
			fillDecoded(raw, encoding,scriptProteced,sameAsArray);
		} catch (UnsupportedEncodingException e) {
			try {
				fillDecoded(raw, "iso-8859-1",scriptProteced,sameAsArray);
			} catch (UnsupportedEncodingException e1) {}
		}
    }
        
    
    /**
     * fill th data from given strut and decode it
     * @param raw
     * @param encoding
     * @throws UnsupportedEncodingException
     */
    protected void fillDecoded(URLItem[] raw, String encoding, boolean scriptProteced, boolean sameAsArray) throws UnsupportedEncodingException {
    	clear();
    	String name,value;
        //Object curr;
        for(int i=0;i<raw.length;i++) {
            name=raw[i].getName();
            value=raw[i].getValue();
            if(raw[i].isUrlEncoded()) {
            	name=URLDecoder.decode(name,encoding,true);
            	value=URLDecoder.decode(value,encoding,true);
            }
            // MUST valueStruct
            if(name.indexOf('.')!=-1) {
                StringList list=ListUtil.listToStringListRemoveEmpty(name,'.');
                Struct parent=this;
                while(list.hasNextNext()) {
                    parent=_fill(parent,list.next(),new CastableStruct(),false,scriptProteced,sameAsArray);
                }
                _fill(parent,list.next(),value,true,scriptProteced,sameAsArray);
            } 
            //else 
                _fill(this,name,value,true,scriptProteced,sameAsArray);
        }
    }
    
    private Struct _fill(Struct parent, String name, Object value, boolean isLast, boolean scriptProteced, boolean sameAsArray) {
    	Object curr;
        boolean isArrayDef=sameAsArray;
        Collection.Key key=KeyImpl.init(name);
        
        // script protect
        if(scriptProteced && value instanceof String) {
        	value=ScriptProtect.translate((String)value);
        }
        
        if(name.length() >2 && name.endsWith("[]")) {
            isArrayDef=true;
            name=name.substring(0,name.length()-2);
            key=KeyImpl.getInstance(name);
            curr=parent.get(key,null);                
        }
        else {
            curr=parent.get(key,null);
        }
        
        if(curr==null) {
        	if(isArrayDef) {
        		Array arr = new ArrayImpl();
        		arr.appendEL(value);
        		parent.setEL(key,arr);
        	}
            else parent.setEL(key,value); 
        }
        else if(curr instanceof Array){
            ((Array) curr).appendEL(value);
        }
        else if(curr instanceof CastableStruct){
        	if(isLast) ((CastableStruct)curr).setValue(value);
            else return (Struct) curr;
        	
        }
        else if(curr instanceof Struct){
            if(isLast) parent.setEL(key,value);
            else return (Struct) curr;
        }
        else if(curr instanceof String){
            if(isArrayDef) {
            	Array arr = new ArrayImpl();
            	arr.appendEL(curr);
            	arr.appendEL(value);
                parent.setEL(key,arr);
            }
            else if(value instanceof Struct) {
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
    
    @Override
	public boolean isInitalized() {
		return isInit;
	}

	@Override
	public void initialize(PageContext pc) {
        isInit=true;
	}

	@Override
	public void release() {
		clear();
		isInit=false;
	}
	
	@Override
	public void release(PageContext pc) {
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
    
    @Override
    public int getType() {
        return type;
    }
    
    @Override
    public String getTypeAsString() {
        return name;
    }
	public long sizeOf() {
		return StructUtil.sizeOf(this);
	}
    
}