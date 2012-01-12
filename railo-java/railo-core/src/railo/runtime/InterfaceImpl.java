package railo.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import railo.commons.lang.StringUtil;
import railo.runtime.component.ComponentLoader;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpTablePro;
import railo.runtime.dump.Dumpable;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFImpl;
import railo.runtime.type.UDFProperties;
import railo.runtime.type.util.ArrayUtil;

/**
 * 
 * MUST add handling for new attributes (style, namespace, serviceportname, porttypename, wsdlfile, bindingname, and output)
 */ 
public class InterfaceImpl implements Dumpable { // FUTURE to a Interface for this and a base interface for this and Coponent

	private static final InterfaceImpl[] EMPTY = new InterfaceImpl[]{};
	
	//private InterfacePage page;
	private PageSource pageSource;
	private String extend;
	private String hint;
	private String dspName;
	private String callPath;
	private boolean realPath;
	private Map meta;
	
	private InterfaceImpl[] superInterfaces;
	
	private Map<Collection.Key,UDF> udfs=new HashMap<Collection.Key,UDF>();
	private Map<Collection.Key,UDF> interfacesUDFs;

	/**
     * Constructor of the Component
     * @param output 
     * @param extend 
     * @param hint 
     * @param dspName 
     */
	public InterfaceImpl(InterfacePage page,String extend, String hint, String dspName,String callPath, boolean realPath,Map interfacesUDFs) {
    	this(page.getPageSource(),extend, hint, dspName,callPath, realPath,interfacesUDFs,null);
	}
	public InterfaceImpl(InterfacePage page,String extend, String hint, String dspName,String callPath, boolean realPath,Map interfacesUDFs, Map meta) {
    	this(page.getPageSource(),extend, hint, dspName,callPath, realPath,interfacesUDFs,meta);
	}
	public InterfaceImpl(PageSource pageSource,String extend, String hint, String dspName,String callPath, boolean realPath,Map interfacesUDFs) {
    	this(pageSource, extend, hint, dspName, callPath, realPath, interfacesUDFs, null);
	}
	public InterfaceImpl(PageSource pageSource,String extend, String hint, String dspName,String callPath, boolean realPath,Map interfacesUDFs, Map meta) {
    	this.pageSource=pageSource;
    	this.extend=extend;
    	this.hint=hint;
    	this.dspName=dspName;
    	this.callPath=callPath;
    	this.realPath=realPath;
    	this.interfacesUDFs=interfacesUDFs;
    	this.meta=meta;
}
	 
	 
	 
	    

	private static void init(PageContext pc,InterfaceImpl icfc) throws PageException {

		if(!StringUtil.isEmpty(icfc.extend) && (icfc.superInterfaces==null || icfc.superInterfaces.length==0)) {
			icfc.superInterfaces=loadImplements(ThreadLocalPageContext.get(pc),icfc.extend,icfc.interfacesUDFs);
		}
		else icfc.superInterfaces=EMPTY;
	}


    public static InterfaceImpl[] loadImplements(PageContext pc, String lstExtend, Map interfaceUdfs) throws PageException {
    	List<InterfaceImpl> interfaces=new ArrayList<InterfaceImpl>();
    	loadImplements(pc, lstExtend, interfaces, interfaceUdfs);
    	return (InterfaceImpl[]) interfaces.toArray(new InterfaceImpl[interfaces.size()]);
    	
	}

    private static void loadImplements(PageContext pc, String lstExtend,List interfaces, Map interfaceUdfs) throws PageException {
    	
    	Array arr = railo.runtime.type.List.listToArrayRemoveEmpty(lstExtend, ',');
    	Iterator<?> it = arr.iterator();
    	InterfaceImpl ic;
    	String extend;

    	while(it.hasNext()) {
    		extend=((String) it.next()).trim();
    		ic=ComponentLoader.loadInterface(pc,extend,interfaceUdfs);
    		interfaces.add(ic);
    		ic.setUDFListener(interfaceUdfs);
    		if(!StringUtil.isEmpty(ic.extend)) {
    			loadImplements(pc,ic.extend,interfaces,interfaceUdfs);
    		}
    	}
	}
    
    private void setUDFListener(Map<Collection.Key,UDF> interfacesUDFs) {
		this.interfacesUDFs=interfacesUDFs;
	}



	/*public boolean instanceOf(String type) {
    	boolean b = _instanceOf(type);
    	print.out("instanceOf("+type+"):"+page+":"+b);
    	return b;
    }*/
    public boolean instanceOf(String type) {
		if(realPath) {
        	if(type.equalsIgnoreCase(callPath)) return true;
            if(type.equalsIgnoreCase(pageSource.getComponentName())) return true;
            if(type.equalsIgnoreCase(_getName())) return true;       
        }
        else {
        	if(type.equalsIgnoreCase(callPath)) return true;
            if(type.equalsIgnoreCase(_getName())) return true; 
        }
		if(superInterfaces==null){
			try {
				init(null,this);
			} catch (PageException e) {
				superInterfaces=EMPTY;
			}
		}
    	for(int i=0;i<superInterfaces.length;i++){
    		if(superInterfaces[i].instanceOf(type))return true;
    	}
    	return false;
    }

 
    /**
	 * @return the callPath
	 */
	public String getCallPath() {
		return callPath;
	}



	private String _getName() { // MUST nicht so toll
	    if(callPath==null) return "";
	    return railo.runtime.type.List.last(callPath,"./",true);
	}
    
    public void registerUDF(String key, UDF udf) {
    	udfs.put(KeyImpl.init(key),udf);
    	interfacesUDFs.put(KeyImpl.init(key), udf);
    }
    
    public void registerUDF(Collection.Key key, UDF udf) {
    	udfs.put(key,udf);
    	interfacesUDFs.put(key, udf);
    }
    
    public void registerUDF(String key, UDFProperties props) {
    	registerUDF(key, new UDFImpl(props));
    }
    
    public void registerUDF(Collection.Key key, UDFProperties props) {
    	registerUDF(key, new UDFImpl(props));
    }
    
    
    
    
    
    
    
    
    /**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
	    DumpTable table = new DumpTablePro("interface","#99cc99","#ffffff","#000000");
        table.setTitle("Interface "+callPath+""+(" "+StringUtil.escapeHTML(dspName)));
        table.setComment("Interface can not directly invoked as a object");
        //if(top.properties.extend.length()>0)table.appendRow(1,new SimpleDumpData("Extends"),new SimpleDumpData(top.properties.extend));
        //if(top.properties.hint.trim().length()>0)table.appendRow(1,new SimpleDumpData("Hint"),new SimpleDumpData(top.properties.hint));
        
        //table.appendRow(1,new SimpleDumpData(""),_toDumpData(top,pageContext,maxlevel,access));
        return table;
    }

	/* *
	 * @return the page
	 * /
	public InterfacePage getPage() {
		return page;
	}*/
	
	public PageSource getPageSource() {
		return pageSource;
	}


	public Struct getMetaData(PageContext pc) throws PageException {
		return _getMetaData(pc,this);
	}
	private static Struct _getMetaData(PageContext pc,InterfaceImpl icfc) throws PageException {
		Struct sct=new StructImpl();
		ArrayImpl arr=new ArrayImpl();
        {
			Iterator<UDF> it = icfc.udfs.values().iterator();
	        while(it.hasNext()) {
	        	arr.append(it.next().getMetaData(pc));
	        }
		}
        
        if(icfc.meta!=null) {
        	Iterator it = icfc.meta.entrySet().iterator();
        	Map.Entry entry;
        	while(it.hasNext()){
        		entry=(Entry) it.next();
        		sct.setEL(KeyImpl.toKey(entry.getKey()), entry.getValue());
        	}
        }
        
        
        if(!StringUtil.isEmpty(icfc.hint,true))sct.set(KeyImpl.HINT,icfc.hint);
        if(!StringUtil.isEmpty(icfc.dspName,true))sct.set(ComponentImpl.DISPLAY_NAME,icfc.dspName);
        init(pc,icfc);
        if(!ArrayUtil.isEmpty(icfc.superInterfaces)){
            Set<String> _set = railo.runtime.type.List.listToSet(icfc.extend,',',true);
            Struct ex=new StructImpl();
        	sct.set(ComponentImpl.EXTENDS,ex);
        	for(int i=0;i<icfc.superInterfaces.length;i++){
        		if(!_set.contains(icfc.superInterfaces[i].getCallPath())) continue;
        		ex.setEL(KeyImpl.init(icfc.superInterfaces[i].getCallPath()),_getMetaData(pc,icfc.superInterfaces[i]));
        	}
        	
        }
        
        if(arr.size()!=0)sct.set(ComponentImpl.FUNCTIONS,arr);
        PageSource ps = icfc.pageSource;
        sct.set(KeyImpl.NAME,ps.getComponentName());
        sct.set(ComponentImpl.FULLNAME,ps.getComponentName());
       
        sct.set(KeyImpl.PATH,ps.getDisplayPath());
        sct.set(KeyImpl.TYPE,"interface");
        return sct;
	}

}