package railo.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

/**
 * 
 * MUST add handling for new attributes (style, namespace, serviceportname, porttypename, wsdlfile, bindingname, and output)
 */ 
public class InterfaceImpl implements Dumpable {

	private static final InterfaceImpl[] EMPTY = new InterfaceImpl[]{};
	
	private InterfacePage page;
	private String extend;
	private String hint;
	private String dspName;
	private String callPath;
	private boolean realPath;
	
	private InterfaceImpl[] superInterfaces;
	
	private Map udfs=new HashMap();
	private Map interfacesUDFs;

	/**
     * Constructor of the Component
     * @param output 
     * @param extend 
     * @param hint 
     * @param dspName 
     */
    public InterfaceImpl(InterfacePage page,String extend, String hint, String dspName,String callPath, boolean realPath,Map interfacesUDFs) {
    	this.page=page;
    	this.extend=extend;
    	this.hint=hint;
    	this.dspName=dspName;
    	this.callPath=callPath;
    	this.realPath=realPath;
    	this.interfacesUDFs=interfacesUDFs;
    }
    

	private static void init(PageContext pc,InterfaceImpl icfc) throws PageException {

		if(!StringUtil.isEmpty(icfc.extend) && (icfc.superInterfaces==null || icfc.superInterfaces.length==0)) {
			icfc.superInterfaces=loadImplements(ThreadLocalPageContext.get(pc),icfc.extend,icfc.interfacesUDFs);
		}
		else icfc.superInterfaces=EMPTY;
	}


    public static InterfaceImpl[] loadImplements(PageContext pc, String lstExtend, Map interfaceUdfs) throws PageException {
    	List interfaces=new ArrayList();
    	loadImplements(pc, lstExtend, interfaces, interfaceUdfs);
    	return (InterfaceImpl[]) interfaces.toArray(new InterfaceImpl[interfaces.size()]);
    	
	}

    private static void loadImplements(PageContext pc, String lstExtend,List interfaces, Map interfaceUdfs) throws PageException {
    	
    	Array arr = railo.runtime.type.List.listToArrayRemoveEmpty(lstExtend, ',');
    	Iterator it = arr.iterator();
    	InterfaceImpl ic;
    	String extend;

    	while(it.hasNext()) {
    		extend=((String) it.next()).trim();
    		ic=ComponentLoader.loadInterface(pc,extend,true,interfaceUdfs);
    		interfaces.add(ic);
    		ic.setUDFListener(interfaceUdfs);
    		if(!StringUtil.isEmpty(ic.extend)) {
    			loadImplements(pc,ic.extend,interfaces,interfaceUdfs);
    		}
    	}
	}
    
    private void setUDFListener(Map interfacesUDFs) {
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
            if(type.equalsIgnoreCase(page.getPageSource().getComponentName())) return true;
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
	    return railo.runtime.type.List.last(callPath,"./");
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
	    DumpTable table = new DumpTablePro("interface","#97C0AB","#EAF2EE","#000000");
        table.setTitle("Interface "+callPath+""+(" "+StringUtil.escapeHTML(dspName)));
        table.setComment("Interface can not directly invoked as a object");
        //if(top.properties.extend.length()>0)table.appendRow(1,new SimpleDumpData("Extends"),new SimpleDumpData(top.properties.extend));
        //if(top.properties.hint.trim().length()>0)table.appendRow(1,new SimpleDumpData("Hint"),new SimpleDumpData(top.properties.hint));
        
        //table.appendRow(1,new SimpleDumpData(""),_toDumpData(top,pageContext,maxlevel,access));
        return table;
    }

	/**
	 * @return the page
	 */
	public InterfacePage getPage() {
		return page;
	}


	public Struct getMetaData(PageContext pc) throws PageException {
		StructImpl sct=new StructImpl();
		_getMetaData(pc,this,sct);
		return sct;
	}
	private static void _getMetaData(PageContext pc,InterfaceImpl icfc,Struct sct) throws PageException {
		ArrayImpl arr=new ArrayImpl();
        Set set=icfc.udfs.keySet();
        Iterator it = set.iterator();
        //Collection.Key name;
        Object oKey;
        UDF udf;
        //String[] keys=comp.keysOnlyThis(access);
        while(it.hasNext()) {
        	oKey=it.next();
        	//name=KeyImpl.toKey(oKey,null);
        	udf=(UDF)icfc.udfs.get(oKey);
            arr.append(udf.getMetaData(pc));
            //}
        }
        
        
        if(!StringUtil.isEmpty(icfc.hint))sct.set("hint",icfc.hint);
        if(!StringUtil.isEmpty(icfc.dspName))sct.set("displayname",icfc.dspName);
        init(pc,icfc);
        if(icfc.superInterfaces!=null && icfc.superInterfaces.length>0){
        	Struct ex=new StructImpl();
        	sct.set(ComponentImpl.EXTENDS,ex);
        	_getMetaData(pc,icfc.superInterfaces[0], ex);
        }
        
        if(arr.size()!=0)sct.set(ComponentImpl.FUNCTIONS,arr);
        PageSource ps = icfc.page.getPageSource();
        sct.set(ComponentImpl.NAME,ps.getComponentName());
        sct.set("fullname",ps.getComponentName());
       
        sct.set(ComponentImpl.PATH,ps.getDisplayPath());
        sct.set(ComponentImpl.TYPE,"interface");
        
	}

}