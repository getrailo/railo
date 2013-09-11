package railo.runtime;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import railo.commons.collection.HashMapPro;
import railo.commons.collection.MapFactory;
import railo.commons.collection.MapPro;
import railo.commons.io.DevNullOutputStream;
import railo.commons.lang.CFTypes;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.Pair;
import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.runtime.component.ComponentLoader;
import railo.runtime.component.DataMember;
import railo.runtime.component.InterfaceCollection;
import railo.runtime.component.Member;
import railo.runtime.component.Property;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.config.NullSupportHelper;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.debug.DebugEntryTemplate;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.system.ContractPath;
import railo.runtime.interpreter.CFMLExpressionInterpreter;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.op.Operator;
import railo.runtime.op.ThreadLocalDuplication;
import railo.runtime.op.date.DateCaster;
import railo.runtime.thread.ThreadUtil;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Sizeable;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFGSProperty;
import railo.runtime.type.UDFImpl;
import railo.runtime.type.UDFPlus;
import railo.runtime.type.UDFProperties;
import railo.runtime.type.UDFPropertiesImpl;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.cfc.ComponentAccessEntryIterator;
import railo.runtime.type.cfc.ComponentAccessValueIterator;
import railo.runtime.type.comparator.ArrayOfStructComparator;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.scope.Argument;
import railo.runtime.type.scope.ArgumentImpl;
import railo.runtime.type.scope.ArgumentIntKey;
import railo.runtime.type.scope.Variables;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;
import railo.runtime.type.util.PropertyFactory;
import railo.runtime.type.util.StructSupport;
import railo.runtime.type.util.StructUtil;
import railo.runtime.type.util.UDFUtil;

/**
 * %**%
 * MUST add handling for new attributes (style, namespace, serviceportname, porttypename, wsdlfile, bindingname, and output)
 */ 
public final class ComponentImpl extends StructSupport implements Externalizable,ComponentAccess,coldfusion.runtime.TemplateProxy,Sizeable {


	private ComponentProperties properties;
	private MapPro<Key,Member> _data;
    private MapPro<Key,UDF> _udfs;

	ComponentImpl top=this;
    ComponentImpl base;
    //private ComponentPage componentPage;
    private PageSource pageSource;
    private ComponentScope scope;
    
    // for all the same
    private int dataMemberDefaultAccess;
	private Boolean _triggerDataMember;
    	
	// state control of component
	boolean isInit=false;

	private InterfaceCollection interfaceCollection;

	private boolean useShadow;
	boolean afterConstructor;
	private Map<Key,UDF> constructorUDFs;
	private boolean loaded;



	
	public long sizeOf() {
		return 
			SizeOf.size(properties)+
			SizeOf.size(_data)+
			SizeOf.size(scope)+
			SizeOf.size(dataMemberDefaultAccess)+
			SizeOf.size(false)+
			SizeOf.size(interfaceCollection)+
			SizeOf.size(useShadow)+
			SizeOf.size(afterConstructor)+
			SizeOf.size(base);
	}
	
    /**
     * Constructor of the Component, USED ONLY FOR DESERIALIZE
     */
	 public ComponentImpl() {
	 }
	
    /**
     * Constructor of the class
     * @param componentPage
     * @param output
     * @param _synchronized
     * @param extend
     * @param implement
     * @param hint
     * @param dspName
     * @param callPath
     * @param realPath
     * @param style
     * @param meta
     * @throws ApplicationException
     */

    public ComponentImpl(ComponentPage componentPage,Boolean output,boolean _synchronized, 
    		String extend, String implement, String hint, String dspName,String callPath, boolean realPath, 
    		String style,StructImpl meta) throws ApplicationException {
    	this(componentPage,output,_synchronized, 
        		extend, implement, hint, dspName,callPath, realPath, style,false, false,meta);
    }
    
    public ComponentImpl(ComponentPage componentPage,Boolean output,boolean _synchronized, 
    		String extend, String implement, String hint, String dspName,String callPath, boolean realPath, 
    		String style,boolean persistent,StructImpl meta) throws ApplicationException {
    	this(componentPage,output,_synchronized, 
        		extend, implement, hint, dspName,callPath, realPath, style,persistent, false,meta);
    }
	 
    public ComponentImpl(ComponentPage componentPage,Boolean output,boolean _synchronized, 
    		String extend, String implement, String hint, String dspName,String callPath, boolean realPath, 
    		String style,boolean persistent,boolean accessors,StructImpl meta) throws ApplicationException {
    	this.properties=new ComponentProperties(dspName,extend.trim(),implement,hint,output,callPath,realPath,_synchronized,null,persistent,accessors,meta);
    	//this.componentPage=componentPage instanceof ComponentPageProxy?componentPage:PageProxy.toProxy(componentPage);
    	this.pageSource=componentPage.getPageSource();
    	
    	if(!StringUtil.isEmpty(style) && !"rpc".equals(style))
    		throw new ApplicationException("style ["+style+"] is not supported, only the following styles are supported: [rpc]");
    }
    

    @Override

    public Collection duplicate(boolean deepCopy) {
    	ComponentImpl top= _duplicate(deepCopy,true);
    	setTop(top,top);
    	
		
		return top;
    }
	
    
    
    
    private ComponentImpl _duplicate( boolean deepCopy, boolean isTop) {
    	ComponentImpl trg=new ComponentImpl();
    	ThreadLocalDuplication.set(this, trg);
    	try{
			// attributes
	    	trg.pageSource=pageSource;
	        trg._triggerDataMember=_triggerDataMember;
	        trg.useShadow=useShadow;
	        trg.afterConstructor=afterConstructor;
	        trg.dataMemberDefaultAccess=dataMemberDefaultAccess;
			trg.properties=properties.duplicate();
			trg.isInit=isInit;
			trg.interfaceCollection=interfaceCollection;
	    	
			boolean useShadow=scope instanceof ComponentScopeShadow;
			if(!useShadow)trg.scope=new ComponentScopeThis(trg);
			
	    	if(base!=null){
				trg.base=base._duplicate(deepCopy,false);
				
				trg._data=trg.base._data;
				trg._udfs=duplicateUTFMap(this,trg, _udfs,new HashMapPro<Key,UDF>(trg.base._udfs));

	    		if(useShadow) trg.scope=new ComponentScopeShadow(trg,(ComponentScopeShadow)trg.base.scope,false);
			}
	    	else {
	    		// clone data member, ignore udfs for the moment
	    		trg._data=duplicateDataMember(trg, _data, new HashMapPro(), deepCopy);
	    		trg._udfs=duplicateUTFMap(this,trg, _udfs,new HashMapPro<Key, UDF>());
	    		
	    		if(useShadow) {
	    			ComponentScopeShadow css = (ComponentScopeShadow)scope;
	    			trg.scope=new ComponentScopeShadow(trg,duplicateDataMember(trg,css.getShadow(),MapFactory.getConcurrentMap(),deepCopy));
	    		}
	    	}
	    	
	    	// at the moment this makes no sense, becuae this map is no more used after constructor has runned and for a clone the constructo is not executed, but perhaps this is used in future
	    	if(constructorUDFs!=null){
	    		trg.constructorUDFs=new HashMap<Collection.Key, UDF>();
	    		addUDFS(trg, constructorUDFs, trg.constructorUDFs);
	    	}
			
			
	    	if(isTop) {
	    		setTop(trg,trg);
	    		
	    		addUDFS(trg,_data,trg._data);
	    		if(useShadow){
	    			addUDFS(trg,((ComponentScopeShadow)scope).getShadow(),((ComponentScopeShadow)trg.scope).getShadow());
	    		}
	    	}
	    	
	    	
	    	
	    	
    	}
    	finally {
    		// ThreadLocalDuplication.remove(this); removed "remove" to catch sisters and brothers
    	}
    	
		return trg;
	}
    
    
    private static void addUDFS(ComponentImpl trgComp, Map src, Map trg) {
		Iterator it = src.entrySet().iterator();
		Map.Entry entry;
		Object key,value;
		UDF udf;
		ComponentImpl comp,owner;
		boolean done;
    	while(it.hasNext()){
    		entry=(Entry) it.next();
    		key=entry.getKey();
    		value=entry.getValue();
    		if(value instanceof UDF) {
    			udf=(UDF) value;
    			done=false;
    			// get udf from _udf
    			owner = (ComponentImpl)udf.getOwnerComponent();
    			if(owner!=null) {
	    			comp=trgComp;
	    			do{
	    				if(owner.pageSource==comp.pageSource)
	    					break;
	    			}
	    			while((comp=comp.base)!=null);
	    			if(comp!=null) {
	    				value=comp._udfs.get(key);
	    				trg.put(key, value);
	    				done=true;
	    			}
    			}
    			// udf with no owner
    			if(!done) 
    				trg.put(key, udf.duplicate());
    			
    			//print.o(owner.pageSource.getComponentName()+":"+udf.getFunctionName());
    		}
    	}
	}

    /**
     * duplicate the datamember in the map, ignores the udfs
     * @param c
     * @param map
     * @param newMap
     * @param deepCopy
     * @return
     */
    public static MapPro duplicateDataMember(ComponentImpl c,MapPro map,MapPro newMap,boolean deepCopy){
        Iterator it=map.entrySet().iterator();
        Map.Entry entry;
        Object value;
        while(it.hasNext()) {
            entry=(Entry) it.next();
        	value=entry.getValue();
            
            if(!(value instanceof UDF)) {
            	if(deepCopy) value=Duplicator.duplicate(value,deepCopy);
            	newMap.put(entry.getKey(),value);
            }
        }
        return newMap;
    }
    
    public static MapPro<Key, UDF> duplicateUTFMap(ComponentImpl src,ComponentImpl trg,MapPro<Key,UDF> srcMap, MapPro<Key, UDF> trgMap){
    	Iterator<Entry<Key, UDF>> it = srcMap.entrySet().iterator();
        Entry<Key, UDF> entry;
        UDF udf;
        while(it.hasNext()) {
            entry=it.next();
            udf=entry.getValue();
        	
            if(udf.getOwnerComponent()==src) {
            	UDFPlus clone=(UDFPlus) entry.getValue().duplicate();
        		clone.setOwnerComponent(trg);
        		clone.setAccess(udf.getAccess());
        		trgMap.put(entry.getKey(),clone);	
            }
        	
        }
        return trgMap;
    }
    

	/**
     * initalize the Component
     * @param pageContext
     * @param componentPage
     * @throws PageException
     */    
    public void init(PageContext pageContext, ComponentPage componentPage) throws PageException {
    	//this.componentPage=componentPage;
    	this.pageSource=componentPage.getPageSource();

        // extends
	    if(!StringUtil.isEmpty(properties.extend)) {
			base= ComponentLoader.loadComponent(pageContext,properties.extend,Boolean.TRUE,null);
		}
	    else { 
	    	Page p=((ConfigWebImpl)pageContext.getConfig()).getBaseComponentPage(pageContext);
	    	if(!componentPage.getPageSource().equals(p.getPageSource())) {
            	base=ComponentLoader.loadComponent(pageContext,p,p.getPageSource(),"Component",false);
	        } 
	    }
    	
	    if(base!=null){
	    	this.dataMemberDefaultAccess=base.dataMemberDefaultAccess;
	    	this._triggerDataMember=base._triggerDataMember;
	    	_data=base._data;
	    	_udfs=new HashMapPro<Key,UDF>(base._udfs);
	    	setTop(this,base);
	    }
	    else {
	    	this.dataMemberDefaultAccess=pageContext.getConfig().getComponentDataMemberDefaultAccess();
	    	// TODO get per CFC setting this._triggerDataMember=pageContext.getConfig().getTriggerComponentDataMember();
		    _udfs=new HashMapPro<Key,UDF>();
		    _data=MapFactory.getConcurrentMap();
	    }
	    
	    // implements
	    if(!StringUtil.isEmpty(properties.implement)) {
	    	interfaceCollection=new InterfaceCollection((PageContextImpl)pageContext,properties.implement);
	    }
	    
	    // scope
	    if(useShadow=pageContext.getConfig().useComponentShadow()) {
	        if(base==null) scope=new ComponentScopeShadow(this,MapFactory.getConcurrentMap());
		    else scope=new ComponentScopeShadow(this,(ComponentScopeShadow)base.scope,false);
	    }
	    else {
	    	scope=new ComponentScopeThis(this);
	    }
	    initProperties();
	}
    
    public void checkInterface(PageContext pc, ComponentPage componentPage) throws PageException {
    	if(interfaceCollection==null || interfaceCollection.lastUpdate()<=componentPage.lastCheck()) return;
    	
	    Iterator it = interfaceCollection.getUdfs().entrySet().iterator();
	    Map.Entry entry;
	    UDFPlus iUdf,cUdf;
	    FunctionArgument[] iFA,cFA;
    	while(it.hasNext()){
    		
    		entry=(Entry) it.next();
    		iUdf=(UDFPlus) entry.getValue();
    		cUdf=(UDFPlus) _udfs.get(entry.getKey());
    		
    		// UDF does not exist
    		if(cUdf==null ) {
    			throw new ExpressionException(
      					 "component ["+componentPage.getPageSource().getDisplayPath()+
      					 "] does not implement the function ["+iUdf.toString().toLowerCase()+"] of the interface ["+
      					 iUdf.getPageSource().getDisplayPath()+"]");
      					
    		}
    		
    		iFA=iUdf.getFunctionArguments();
    		cFA=cUdf.getFunctionArguments();
    // access
    		if(cUdf.getAccess()>Component.ACCESS_PUBLIC){
    			throw new ExpressionException( _getErrorMessage(cUdf,iUdf),
    				"access ["+ComponentUtil.toStringAccess(cUdf.getAccess())+"] has to be at least [public]");
    		}
    		
    // return type
    		if(iUdf.getReturnType()!=cUdf.getReturnType()){
				throw new ExpressionException( _getErrorMessage(cUdf,iUdf),
					"return type ["+cUdf.getReturnTypeAsString()+"] does not match interface function return type ["+iUdf.getReturnTypeAsString()+"]");
			}
			// none base types
			if(iUdf.getReturnType()==CFTypes.TYPE_UNKNOW && !iUdf.getReturnTypeAsString().equalsIgnoreCase(cUdf.getReturnTypeAsString())) {
				throw new ExpressionException( _getErrorMessage(cUdf,iUdf),
					"return type ["+cUdf.getReturnTypeAsString()+"] does not match interface function return type ["+iUdf.getReturnTypeAsString()+"]");
			}
	// output
    		if(iUdf.getOutput()!=cUdf.getOutput()){
				throw new ExpressionException( _getErrorMessage(cUdf,iUdf),
					"output does not match interface function output definition");
			}
			
    // arguments
    		if(iFA.length!=cFA.length) {
    			throw new ExpressionException( _getErrorMessage(cUdf,iUdf),"not the same argument count");
   			}
    		
    		for(int i=0;i<iFA.length;i++) {
    			// type
    			if(iFA[i].getType()!=cFA[i].getType()){
    				throw new ExpressionException( _getErrorMessage(cUdf,iUdf),
    					"argument type ["+cFA[i].getTypeAsString()+"] does not match interface function argument type ["+iFA[i].getTypeAsString()+"]");
    			}
    			// none base types
    			if(iFA[i].getType()==CFTypes.TYPE_UNKNOW && !iFA[i].getTypeAsString().equalsIgnoreCase(cFA[i].getTypeAsString())) {
    				throw new ExpressionException( _getErrorMessage(cUdf,iUdf),
        					"argument type ["+cFA[i].getTypeAsString()+"] does not match interface function argument type ["+iFA[i].getTypeAsString()+"]");
        		}
    			// name
    			if(!iFA[i].getName().equalsIgnoreCase(cFA[i].getName())){
    				throw new ExpressionException( _getErrorMessage(cUdf,iUdf),
    					"argument name ["+cFA[i].getName()+"] does not match interface function argument name ["+iFA[i].getName()+"]");
    			}
    			// required
    			if(iFA[i].isRequired()!=cFA[i].isRequired()){
    				throw new ExpressionException( _getErrorMessage(cUdf,iUdf),
    					"argument ["+cFA[i].getName()+"] should "+(iFA[i].isRequired()?"":"not ")+"be required");
    			}
    		}
    	}
	    componentPage.ckecked();
    }

	private String _getErrorMessage(UDFPlus cUdf,UDFPlus iUdf) {
		return "function ["+cUdf.toString().toLowerCase()+"] of component " +
			 "["+pageSource.getDisplayPath()+"]" +
			 " does not match the function declaration ["+iUdf.toString().toLowerCase()+"] of the interface " +
			 "["+iUdf.getPageSource().getDisplayPath()+"]";
	}


	private static void setTop(ComponentImpl top,ComponentImpl trg) {
		while(trg!=null){
			trg.top=top;
			trg=trg.base;
		}
	}

    Object _call(PageContext pc, Collection.Key key, Struct namedArgs, Object[] args,boolean superAccess) throws PageException {
    	
    	Member member=getMember(pc,key,false, superAccess);
    	
    	if(member instanceof UDFPlus) {
    		return _call(pc,key,(UDFPlus)member,namedArgs,args);
        }
        return onMissingMethod(pc, -1, member, key.getString(), args, namedArgs, superAccess);
    }

    Object _call(PageContext pc, int access, Collection.Key key, Struct namedArgs, Object[] args,boolean superAccess) throws PageException {
        Member member=getMember(access,key,false,superAccess);
        if(member instanceof UDF) {
            return _call(pc,key,(UDFPlus)member,namedArgs,args);
        }
        return onMissingMethod(pc, access, member, key.getString(), args, namedArgs, superAccess);
    }
    
    public Object onMissingMethod(PageContext pc, int access,Member member,String name,Object _args[],Struct _namedArgs, boolean superAccess) throws PageException {
    	Member ommm = access==-1?
    			getMember(pc,KeyConstants._onmissingmethod,false, superAccess):
    			getMember(access,KeyConstants._onmissingmethod,false, superAccess);
        if(ommm instanceof UDF) {
        	Argument args=new ArgumentImpl();
        	if(_args!=null) {
        		for(int i=0;i<_args.length;i++) {
        			args.setEL(ArgumentIntKey.init(i+1), _args[i]);
        		}
        	}
        	else if(_namedArgs!=null) {
        		UDFUtil.argumentCollection(_namedArgs, new FunctionArgument[]{});
        		
        		Iterator<Entry<Key, Object>> it = _namedArgs.entryIterator();
        		Entry<Key, Object> e;
        		while(it.hasNext()){
        			e = it.next();
        			args.setEL(e.getKey(),e.getValue());
        		}
        		
        	}
        	
        	//Struct newArgs=new StructImpl(StructImpl.TYPE_SYNC);
        	//newArgs.setEL(MISSING_METHOD_NAME, name);
        	//newArgs.setEL(MISSING_METHOD_ARGS, args); 
        	Object[] newArgs=new Object[]{name,args};
        	
        	return _call(pc,KeyConstants._onmissingmethod,(UDFPlus)ommm,null,newArgs);
        }
        if(member==null)throw ComponentUtil.notFunction(this, KeyImpl.init(name), null,access);
        throw ComponentUtil.notFunction(this, KeyImpl.init(name), member.getValue(),access);
    }
	
	Object _call(PageContext pc, Collection.Key calledName,UDFPlus udf, Struct namedArgs, Object[] args) throws PageException {
			
		Object rtn=null;
		Variables parent=null;
        
		// INFO duplicate code is for faster execution -> less contions
		
		
		// debug yes
		if(pc.getConfig().debug()) {
		    DebugEntryTemplate debugEntry=pc.getDebugger().getEntry(pc,pageSource,udf.getFunctionName());//new DebugEntry(src,udf.getFunctionName());
			int currTime=pc.getExecutionTime();
			long time=System.nanoTime();
			
			// sync yes
			if(top.properties._synchronized){
				synchronized (this) {
					try {
						parent=beforeCall(pc);
						if(args!=null)rtn=udf.call(pc,calledName,args,true);
						else rtn=udf.callWithNamedValues(pc,calledName,namedArgs,true);
					}		
					finally {
						pc.setVariablesScope(parent);
						int diff= ((int)(System.nanoTime()-time)-(pc.getExecutionTime()-currTime));
						pc.setExecutionTime(pc.getExecutionTime()+diff);
						debugEntry.updateExeTime(diff);	
					}	
				}
			}

			// sync no
			else {
				try {
					parent=beforeCall(pc);
					if(args!=null)rtn=udf.call(pc,calledName,args,true);
					else rtn=udf.callWithNamedValues(pc,calledName,namedArgs,true);
				}		
				finally {
					pc.setVariablesScope(parent);
					int diff= ((int)(System.nanoTime()-time)-(pc.getExecutionTime()-currTime));
					pc.setExecutionTime(pc.getExecutionTime()+diff);
					debugEntry.updateExeTime(diff);	
				}	
			}
			
			
		}
		
		// debug no
		else {
			
			// sync yes
			if(top.properties._synchronized){
				synchronized (this) {
				    try {
		            	parent=beforeCall(pc); 
		            	if(args!=null)rtn=udf.call(pc,calledName,args,true);
						else rtn=udf.callWithNamedValues(pc,calledName,namedArgs,true);
					}		
					finally {
						pc.setVariablesScope(parent);
					}
				}
			}
			
			// sync no 385|263
			else {
			    try {
	            	parent=beforeCall(pc);
	            	if(args!=null)rtn=udf.call(pc,calledName,args,true);
					else rtn=udf.callWithNamedValues(pc,calledName,namedArgs,true);
				}		
				finally {
					pc.setVariablesScope(parent);
				}
			}
		}
		return rtn;
	}
	
	/**
     * will be called before executing method or constructor
     * @param pc
     * @return the old scope map
     */
	public Variables beforeCall(PageContext pc) {
    	Variables parent=pc.variablesScope();
    	pc.setVariablesScope(scope);
    	return parent;
    }
	
	/**
     * will be called after invoking constructor, only invoked by constructor (component body execution)
	 * @param pc
	 * @param parent
	 */
    public void afterConstructor(PageContext pc, Variables parent) {
    	pc.setVariablesScope(parent);
    	this.afterConstructor=true;
    	
    	if(constructorUDFs!=null){
    		Iterator<Entry<Key, UDF>> it = constructorUDFs.entrySet().iterator();
    		Map.Entry<Key, UDF> entry;
    		Key key;
    		UDFPlus udf;
    		while(it.hasNext()){
    			entry=it.next();
    			key=entry.getKey();
    			udf=(UDFPlus) entry.getValue();
    			registerUDF(key, udf,false,true);
    		}
    	}
	}
    
    /**
     * this function may be called by generated code inside a ra file
     * @deprecated replaced with <code>afterConstructor(PageContext pc, Variables parent)</code>
     * @param pc
     * @param parent
     */
    public void afterCall(PageContext pc, Variables parent) {
    	afterConstructor(pc, parent);
	}
	
    /**
     * sets the callpath
     * @param callPath
     * /
    public void setCallPath(String callPath) {
		properties.callPath=callPath;
	}*/

	/**
     * rerturn the size
	 * @param access
	 * @return size
	 */
    public int size(int access) {
	    return keys(access).length;
	}

    /**
     * list of keys
     * @param c 
     * @param access
     * @param doBase 
     * @return key set
     */
	public Set<Key> keySet(int access) {
    	HashSet<Key> set=new HashSet<Key>();
        Map.Entry<Key, Member> entry;    
        Iterator<Entry<Key, Member>> it = _data.entrySet().iterator();
        while(it.hasNext()) {
            entry=it.next();
            if(entry.getValue().getAccess()<=access)set.add(entry.getKey());
        }
        return set;
    }
    
    /*protected Set<Key> udfKeySet(int access) {
    	Set<Key> set=new HashSet<Key>();
        Member m;
        Map.Entry<Key, UDF> entry;
        Iterator<Entry<Key, UDF>> it = _udfs.entrySet().iterator();
        while(it.hasNext()) {
            entry= it.next();
            m=entry.getValue();
            if(m.getAccess()<=access)set.add(entry.getKey());
        }
        return set;
    }*/
    
    
    protected java.util.List<Member> getMembers(int access) {
        java.util.List<Member> members=new ArrayList<Member>();
        Member e;
        Iterator<Entry<Key, Member>> it = _data.entrySet().iterator();
        while(it.hasNext()) {
        	e=it.next().getValue();
            if(e.getAccess()<=access)members.add(e);
        }
        return members;
    }


    @Override
	public Iterator<Collection.Key> keyIterator(int access) {
        return keySet(access).iterator();
    }
    
    @Override
	public Iterator<String> keysAsStringIterator(int access) {
        return new StringIterator(keys(access));
    }

	@Override
	public Iterator<Entry<Key, Object>> entryIterator(int access) {
		return new ComponentAccessEntryIterator(this, keys(access),access);
	}

	@Override
	public Iterator<Object> valueIterator(int access) {
		return new ComponentAccessValueIterator(this,keys(access),access);
	}

	
	@Override
	public Iterator<Object> valueIterator() {
		return valueIterator(getAccess(ThreadLocalPageContext.get()));
	}

	@Override
	 public Collection.Key[] keys(int access) {
        Set<Key> set = keySet(access);
        return set.toArray(new Collection.Key[set.size()]);
    }
	
	@Override
	public void clear() {
		_data.clear();
		_udfs.clear();
	}

	@Override
	public Member getMember(int access,Collection.Key key, boolean dataMember,boolean superAccess) {
    	// check super
        if(dataMember && access==ACCESS_PRIVATE && key.equalsIgnoreCase(KeyConstants._super)) {
        	return SuperComponent.superMember((ComponentImpl)ComponentUtil.getActiveComponent(ThreadLocalPageContext.get(),this)._base());
            //return SuperComponent . superMember(base);
        }
    	if(superAccess) {
    		return  _udfs.get(key);
        }
        // check data
        Member member=_data.get(key);
        if(member!=null) {
            if(member.getAccess()<=access)return member;
            return null;
        }
        return null;
    }


	/**
     * get entry matching key
     * @param access 
     * @param keyLowerCase key lower case (case sensitive)
     * @param doBase do check also base component
     * @param dataMember do also check if key super
     * @return matching entry if exists otherwise null
     */
	protected Member getMember(PageContext pc, Collection.Key key, boolean dataMember,boolean superAccess) {
        // check super
        if(dataMember && isPrivate(pc) && key.equalsIgnoreCase(KeyConstants._super)) {
        	return SuperComponent.superMember((ComponentImpl)ComponentUtil.getActiveComponent(pc,this)._base());
        }
        if(superAccess) 
        	return  _udfs.get(key);
        
        // check data
        Member member=_data.get(key);
        if(isAccessible(pc,member)) return member;
        return null;
    }
    
    private boolean isAccessible(PageContext pc, Member member) {
        // TODO geschwindigkeit 
    	if(member!=null) {
    		int access=member.getAccess();
    		if(access<=ACCESS_PUBLIC) return true;
    		else if(access==ACCESS_PRIVATE && isPrivate(pc)) return true;
    		else if(access==ACCESS_PACKAGE && isPackage(pc)) return true;
        }
        return false;
	}
    
    private boolean isAccessible(PageContext pc, int access) {
    	if(access<=ACCESS_PUBLIC) return true;
		else if(access==ACCESS_PRIVATE && isPrivate(pc)) return true;
		else if(access==ACCESS_PACKAGE && isPackage(pc)) return true;
    	return false;
	}

    /**
     * @param pc
     * @return returns if is private
     */
    private boolean isPrivate(PageContext pc) {
    	if(pc==null) return true;
    	Component ac = pc.getActiveComponent();
    	return (ac!=null && (ac==this || 
                ((ComponentImpl)ac).top.pageSource.equals(top.pageSource))) ;
    }
    /**
     * @param pc
     * @return returns if is package
     */
    private boolean isPackage(PageContext pc) {
        Component ac = pc.getActiveComponent();
        if(ac!=null) {
            if(ac==this) return true;
            ComponentImpl aci = ((ComponentImpl)ac);
            if(aci.top.pageSource.equals(top.pageSource))return true;
            
            int index;
            String other=aci.top.getAbsName();
            index=other.lastIndexOf('.');
            if(index==-1)other="";
            else other=other.substring(0,index);
            
            String my=top.getAbsName();
            index=my.lastIndexOf('.');
            if(index==-1)my="";
            else my=my.substring(0,index);
            
            return my.equalsIgnoreCase(other);
        }
        return false;
    }
    
	/**
	 * return the access of a member
	 * @param key
	 * @return returns the access (Component.ACCESS_REMOTE, ACCESS_PUBLIC, ACCESS_PACKAGE,Component.ACCESS_PRIVATE)
	 */
	private int getAccess(Collection.Key key){
        Member member=getMember(ACCESS_PRIVATE,key,false,false);
        if(member==null) return Component.ACCESS_PRIVATE;
        return member.getAccess();
	}
    
    /** 
     * returns current access to this component
     * @param pc
     * @return access
     */
    private int getAccess(PageContext pc) {
        if(pc==null) return ACCESS_PUBLIC;
        
        if(isPrivate(pc)) return ACCESS_PRIVATE;
        if(isPackage(pc)) return ACCESS_PACKAGE;
        return ACCESS_PUBLIC;
    }
	
    @Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return toDumpData(pageContext,maxlevel,dp,getAccess(pageContext));
    }
    

    /**
     * to html output print only with access less than given access
     * @param pageContext
     * @param access
     * @return html output
     */
    public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp, int access) {
	    DumpTable table = new DumpTable("component","#99cc99","#ccffcc","#000000");
        table.setTitle("Component "+getCallPath()+""+(" "+StringUtil.escapeHTML(top.properties.dspName)));
        table.setComment("Only the functions and data members that are accessible from your location are displayed");
        if(top.properties.extend.length()>0)table.appendRow(1,new SimpleDumpData("Extends"),new SimpleDumpData(top.properties.extend));
        if(top.properties.hint.trim().length()>0)table.appendRow(1,new SimpleDumpData("Hint"),new SimpleDumpData(top.properties.hint));
        
        DumpTable content = _toDumpData(top,pageContext,maxlevel,dp,access);
        if(!content.isEmpty())table.appendRow(1,new SimpleDumpData(""),content);
        return table;
    }
    
	static DumpTable _toDumpData(ComponentImpl ci,PageContext pc, int maxlevel, DumpProperties dp,int access) {
		maxlevel--;
		ComponentWrap cw=new ComponentWrap(Component.ACCESS_PRIVATE, ci);
		Collection.Key[] keys= cw.keys();
		
		
		
		DumpTable[] accesses=new DumpTable[4];
		accesses[Component.ACCESS_PRIVATE] = new DumpTable("#ff6633","#ff9966","#000000");
		accesses[Component.ACCESS_PRIVATE].setTitle("private");
		accesses[Component.ACCESS_PRIVATE].setWidth("100%");
		//accesses[Component.ACCESS_PRIVATE].setRow(1,"100%");
		accesses[Component.ACCESS_PACKAGE] = new DumpTable("#ff9966","#ffcc99","#000000");
		accesses[Component.ACCESS_PACKAGE].setTitle("package");
		accesses[Component.ACCESS_PACKAGE].setWidth("100%");
		accesses[Component.ACCESS_PUBLIC] = new DumpTable("#ffcc99","#ffffcc","#000000");
		accesses[Component.ACCESS_PUBLIC].setTitle("public");
		accesses[Component.ACCESS_PUBLIC].setWidth("100%");
		accesses[Component.ACCESS_REMOTE] = new DumpTable("#ccffcc","#ffffff","#000000");
		accesses[Component.ACCESS_REMOTE].setTitle("remote");
		accesses[Component.ACCESS_REMOTE].setWidth("100%");
		
		Collection.Key key;
		for(int i=0;i<keys.length;i++) {
			key=keys[i];
			int a=ci.getAccess(key);
			DumpTable box=accesses[a];
			Object o=cw.get(key,null);
			if(o==ci)o="[this]";
			if(DumpUtil.keyValid(dp,maxlevel, key))
				box.appendRow(1,new SimpleDumpData(key.getString()),DumpUtil.toDumpData(o,pc,maxlevel,dp));
		}
		
		
		DumpTable table=new DumpTable("#ffffff","#cccccc","#000000");
		
		// properties
		if(ci.top.properties.persistent || ci.top.properties.accessors){
			Property[] properties=ci.getProperties(false);
			DumpTable prop = new DumpTable("#99cc99","#ccffcc","#000000");

			prop.setTitle("Properties");
			prop.setWidth("100%");
			Property p;
			Object child;
			for(int i=0;i<properties.length;i++) {
				p=properties[i];
				child = ci.scope.get(KeyImpl.init(p.getName()),null);
				DumpData dd;
				if(child instanceof Component) {
					DumpTable t = new DumpTable("component","#99cc99","#ffffff","#000000");
					t.appendRow(1,new SimpleDumpData("Component"),new SimpleDumpData(((Component)child).getCallName()));
					dd=t;
					
				}
				else 
					dd=DumpUtil.toDumpData(child, pc, maxlevel-1, dp);
				
				
				
				prop.appendRow(1, new SimpleDumpData(p.getName()),dd);
			}
			
			if(access>=ACCESS_PUBLIC && !prop.isEmpty()) {
				table.appendRow(0,prop);
			}
		}
		

		

		if(!accesses[ACCESS_REMOTE].isEmpty()) {
			table.appendRow(0,accesses[Component.ACCESS_REMOTE]);
		}
		if(!accesses[ACCESS_PUBLIC].isEmpty()) {
			table.appendRow(0,accesses[Component.ACCESS_PUBLIC]);
		}
		if(!accesses[ACCESS_PACKAGE].isEmpty()) {
			table.appendRow(0,accesses[Component.ACCESS_PACKAGE]);
		}
		if(!accesses[ACCESS_PRIVATE].isEmpty()) {
			table.appendRow(0,accesses[Component.ACCESS_PRIVATE]);
		}
		return table;
	}
	
	/**
	 * @return return call path
	 */
	protected String getCallPath() {
		if(StringUtil.isEmpty(top.properties.callPath)) return getName();
		try {
            return "("+ListUtil.arrayToList(ListUtil.listToArrayTrim(top.properties.callPath.replace('/','.').replace('\\','.'),"."),".")+")";
        } catch (PageException e) {
            return top.properties.callPath;
        }
	}

    @Override
    public String getDisplayName() {
		return top.properties.dspName;
	}
	
    @Override
    public String getExtends() {
		return top.properties.extend;
	}
    public String getBaseAbsName() {
		return top.base.pageSource.getComponentName();
	}
    
    public boolean isBasePeristent() {
		return top.base!=null && top.base.properties.persistent;
	}
	
	
    @Override
    public String getHint() {
		return top.properties.hint;
	}
    
    @Override
    public String getWSDLFile() {
		return top.properties.getWsdlFile();
	}

    @Override
    public String getName() {
	    if(top.properties.callPath==null) return "";
	    return ListUtil.last(top.properties.callPath,"./",true);
	}
    public String _getName() { // MUST nicht so toll
	    if(properties.callPath==null) return "";
	    return ListUtil.last(properties.callPath,"./",true);
	}
    public PageSource _getPageSource() {
    	return pageSource;
	}
	
    @Override
    public String getCallName() {
	    return top.properties.callPath;
	}
    
    @Override
    public String getAbsName() {
    	return top.pageSource.getComponentName();
	}
    

    @Override
    public boolean getOutput() {
    	if(top.properties.output==null) return true;
        return top.properties.output.booleanValue();
    }

    @Override
    public boolean instanceOf(String type) {
    	
    	ComponentImpl c=top;
    	do {
        	if(type.equalsIgnoreCase(c.properties.callPath)) return true;
            if(type.equalsIgnoreCase(c.pageSource.getComponentName())) return true;
            if(type.equalsIgnoreCase(c._getName())) return true;       
            
    		// check interfaces
    		if(c.interfaceCollection!=null){
	    		InterfaceImpl[] interfaces = c.interfaceCollection.getInterfaces();
	    		if(interfaces!=null)for(int i=0;i<interfaces.length;i++){
	        		if(interfaces[i].instanceOf(type))return true;
	        	}
    		}
    		c=c.base;
    	}
    	while(c!=null);
    	if(StringUtil.endsWithIgnoreCase(type, "component")){
    		if(type.equalsIgnoreCase("component"))							return true;
    		if(type.equalsIgnoreCase("web-inf.cftags.component"))			return true;
    		//if(type.equalsIgnoreCase("web-inf.railo.context.component"))	return true;
    		
    	}
    	return false;
    }
    
    public boolean equalTo(String type) {
    	ComponentImpl c=top;
    	
    	if(type.equalsIgnoreCase(c.properties.callPath)) return true;
        if(type.equalsIgnoreCase(c.pageSource.getComponentName())) return true;
        if(type.equalsIgnoreCase(c._getName())) return true;       
            
		// check interfaces
		if(c.interfaceCollection!=null){
    		InterfaceImpl[] interfaces = c.interfaceCollection.getInterfaces();
    		if(interfaces!=null)for(int i=0;i<interfaces.length;i++){
        		if(interfaces[i].instanceOf(type))return true;
        	}
		}
		
    	if(StringUtil.endsWithIgnoreCase(type, "component")){
    		if(type.equalsIgnoreCase("component"))							return true;
    		if(type.equalsIgnoreCase("web-inf.cftags.component"))			return true;
    	}
    	return false;
    }
    

    @Override
    public boolean isValidAccess(int access) {
		return !(access <0 || access>ACCESS_COUNT);
	}
    
    @Override
    public PageSource getPageSource() {
        return top.pageSource;
    }
    

    @Override
    public String castToString() throws PageException {
    	return castToString(false);
    }
    
	@Override
	public String castToString(String defaultValue) {
		return castToString(false,defaultValue);
	}
    
    String castToString(boolean superAccess) throws PageException {
    	// magic function
    	PageContext pc = ThreadLocalPageContext.get();
		if(pc!=null) {
			Member member = getMember(pc,KeyConstants.__toString,true,superAccess);
			//Object o = get(pc,"_toString",null);
			if(member instanceof UDFPlus) {
				UDFPlus udf = (UDFPlus)member;
				if(udf.getReturnType()==CFTypes.TYPE_STRING && udf.getFunctionArguments().length==0) {
					return Caster.toString(_call(pc, KeyConstants.__toString,udf, null, new Object[0]));
				}
			}
		}
		
		
		throw ExceptionUtil.addHint(new ExpressionException("Can't cast Component ["+getName()+"] to String"),"Add a User-Defined-Function to Component with the following pattern [_toString():String] to cast it to a String or use Built-In-Function \"serialize(Component):String\" to convert it to a serialized String");
        
    }
    
    
    
    
    String castToString(boolean superAccess,String defaultValue) {
    	// magic function
    	PageContext pc = ThreadLocalPageContext.get();
		if(pc!=null) {
			Member member = getMember(pc,KeyConstants.__toString,true,superAccess);
			//Object o = get(pc,"_toString",null);
			if(member instanceof UDFPlus) {
				UDFPlus udf = (UDFPlus)member;
				if(udf.getReturnType()==CFTypes.TYPE_STRING && udf.getFunctionArguments().length==0) {
					try {
						return Caster.toString(_call(pc,KeyConstants.__toString, udf, null, new Object[0]),defaultValue);
					} catch (PageException e) {
						return defaultValue;
					}
				}
			}
		}
		return defaultValue;
    }

    @Override
    public boolean castToBooleanValue() throws PageException {
    	return castToBooleanValue(false);
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return castToBoolean(false, defaultValue);
    }

    boolean castToBooleanValue(boolean superAccess) throws PageException {
    	// magic function
    	PageContext pc = ThreadLocalPageContext.get();
		if(pc!=null) {
			Member member = getMember(pc,KeyConstants.__toBoolean,true,superAccess);
			//Object o = get(pc,"_toBoolean",null);
			if(member instanceof UDFPlus) {
				UDFPlus udf = (UDFPlus)member;
				if(udf.getReturnType()==CFTypes.TYPE_BOOLEAN && udf.getFunctionArguments().length==0) {
					return Caster.toBooleanValue(_call(pc, KeyConstants.__toBoolean,udf, null, new Object[0]));
				}
			}
		}
    	
        throw ExceptionUtil.addHint(new ExpressionException("Can't cast Component ["+getName()+"] to a boolean value"),
                "Add a User-Defined-Function to Component with the following pattern [_toBoolean():boolean] to cast it to a boolean value");
    }
    
    Boolean castToBoolean(boolean superAccess,Boolean defaultValue) {
    	// magic function
    	PageContext pc = ThreadLocalPageContext.get();
		if(pc!=null) {
			Member member = getMember(pc,KeyConstants.__toBoolean,true,superAccess);
			//Object o = get(pc,"_toBoolean",null);
			if(member instanceof UDFPlus) {
				UDFPlus udf = (UDFPlus)member;
				if(udf.getReturnType()==CFTypes.TYPE_BOOLEAN && udf.getFunctionArguments().length==0) {
					try {
						return Caster.toBoolean(_call(pc,KeyConstants.__toBoolean, udf, null, new Object[0]),defaultValue);
					} catch (PageException e) {
						return defaultValue;
					}
				}
			}
		}
    	return defaultValue;
    }

    @Override
    public double castToDoubleValue() throws PageException {
    	return castToDoubleValue(false);
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return castToDoubleValue(false, defaultValue);
    }
    
    
    double castToDoubleValue(boolean superAccess) throws PageException {
    	// magic function
    	PageContext pc = ThreadLocalPageContext.get();
		if(pc!=null) {
			Member member = getMember(pc,KeyConstants.__toNumeric,true,superAccess);
			//Object o = get(pc,"_toNumeric",null);
			if(member instanceof UDFPlus) {
				UDFPlus udf = (UDFPlus)member;
				if(udf.getReturnType()==CFTypes.TYPE_NUMERIC && udf.getFunctionArguments().length==0) {
					return Caster.toDoubleValue(_call(pc, KeyConstants.__toNumeric,udf, null, new Object[0]));
				}
			}
		}
    
        throw ExceptionUtil.addHint(new ExpressionException("Can't cast Component ["+getName()+"] to a numeric value"),
                "Add a User-Defined-Function to Component with the following pattern [_toNumeric():numeric] to cast it to a numeric value");
    }
    double castToDoubleValue(boolean superAccess,double defaultValue) {
    	// magic function
    	PageContext pc = ThreadLocalPageContext.get();
		if(pc!=null) {
			Member member = getMember(pc,KeyConstants.__toNumeric,true,superAccess);
			//Object o = get(pc,"_toNumeric",null);
			if(member instanceof UDFPlus) {
				UDFPlus udf = (UDFPlus)member;
				if(udf.getReturnType()==CFTypes.TYPE_NUMERIC && udf.getFunctionArguments().length==0) {
					try {
						return Caster.toDoubleValue(_call(pc, KeyConstants.__toNumeric,udf, null, new Object[0]),defaultValue);
					} catch (PageException e) {
						return defaultValue;
					}
				}
			}
		}
		return defaultValue;
    }

    @Override
    public DateTime castToDateTime() throws PageException {
    	return castToDateTime(false);
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return castToDateTime(false, defaultValue);
    }

    DateTime castToDateTime(boolean superAccess) throws PageException {
    	// magic function
    	PageContext pc = ThreadLocalPageContext.get();
		if(pc!=null) {
			Member member = getMember(pc,KeyConstants.__toDateTime,true,superAccess);
			//Object o = get(pc,"_toDateTime",null);
			if(member instanceof UDFPlus) {
				UDFPlus udf = (UDFPlus)member;
				if(udf.getReturnType()==CFTypes.TYPE_DATETIME && udf.getFunctionArguments().length==0) {
					return Caster.toDate(_call(pc, KeyConstants.__toDateTime,udf, null, new Object[0]),pc.getTimeZone());
				}
			}
		}
    
		throw ExceptionUtil.addHint(new ExpressionException("Can't cast Component ["+getName()+"] to a date"),
                "Add a User-Defined-Function to Component with the following pattern [_toDateTime():datetime] to cast it to a date");
    }
    DateTime castToDateTime(boolean superAccess,DateTime defaultValue) {
    	// magic function
    	PageContext pc = ThreadLocalPageContext.get();
		if(pc!=null) {
			Member member = getMember(pc,KeyConstants.__toDateTime,true,superAccess);
			//Object o = get(pc,"_toDateTime",null);
			if(member instanceof UDFPlus) {
				UDFPlus udf = (UDFPlus)member;
				if(udf.getReturnType()==CFTypes.TYPE_DATETIME && udf.getFunctionArguments().length==0) {
					
					try {
						return DateCaster.toDateAdvanced(_call(pc, KeyConstants.__toDateTime,udf, null, new Object[0]),true,pc.getTimeZone(),defaultValue);
					} catch (PageException e) {
						return defaultValue;
					}
					
				}
			}
		}
		return defaultValue;
    }

    @Override
    public synchronized Struct getMetaData(PageContext pc) throws PageException {
    	return getMetaData(ACCESS_PRIVATE,pc,top);
    }
    

    public synchronized Object getMetaStructItem(Collection.Key name) {
    	if(top.properties.meta!=null) {
        	return top.properties.meta.get(name,null);
        }
    	return null;
    }

    protected static Struct getMetaData(int access,PageContext pc, ComponentImpl comp) throws PageException {
    	// Cache
    	Page page = ((PageSourceImpl)comp.pageSource).getPage();
    	if(page==null) page = comp.pageSource.loadPage(pc.getConfig());
    	if(page.metaData!=null && page.metaData.get()!=null) {
    		return page.metaData.get();

    	}
    	
    	StructImpl sct=new StructImpl();
    	
        // fill udfs
        metaUDFs(pc, comp, sct,access);
        
        // meta
        if(comp.properties.meta!=null) 
        	StructUtil.copy(comp.properties.meta, sct, true);
            
        String hint=comp.properties.hint;
        String displayname=comp.properties.dspName;
        if(!StringUtil.isEmpty(hint))sct.set(KeyConstants._hint,hint);
        if(!StringUtil.isEmpty(displayname))sct.set(KeyConstants._displayname,displayname);
        
        sct.set(KeyConstants._persistent,comp.properties.persistent);
        sct.set(KeyConstants._hashCode,comp.hashCode());
        sct.set(KeyConstants._accessors,comp.properties.accessors);
        sct.set(KeyConstants._synchronized,comp.properties._synchronized);
        if(comp.properties.output!=null)
        sct.set(KeyConstants._output,comp.properties.output);
            
        // extends
        Struct ex=null;
        if(comp.base!=null) ex=getMetaData(access,pc,comp.base);
        if(ex!=null)sct.set(KeyConstants._extends,ex);
        
        // implements
        InterfaceCollection ic = comp.interfaceCollection;
        if(ic!=null){
        	Set<String> set = ListUtil.listToSet(comp.properties.implement, ",",true);
            InterfaceImpl[] interfaces = comp.interfaceCollection.getInterfaces();
            if(!ArrayUtil.isEmpty(interfaces)){
	            Struct imp=new StructImpl();
            	for(int i=0;i<interfaces.length;i++){
            		if(!set.contains(interfaces[i].getCallPath())) continue;
            		//print.e("-"+interfaces[i].getCallPath());
            		imp.setEL(KeyImpl.init(interfaces[i].getCallPath()), interfaces[i].getMetaData(pc));
	            }
	            sct.set(KeyConstants._implements,imp);
            }
        }
         
        // PageSource
        PageSource ps = comp.pageSource;
        sct.set(KeyConstants._fullname,ps.getComponentName());
        sct.set(KeyConstants._name,ps.getComponentName());
        sct.set(KeyConstants._path,ps.getDisplayPath());
        sct.set(KeyConstants._type,"component");
            
        Class<?> skeleton = comp.getJavaAccessClass(pc,new RefBooleanImpl(false),((ConfigImpl)pc.getConfig()).getExecutionLogEnabled(),false,false,((ConfigImpl)pc.getConfig()).getSupressWSBeforeArg());
        if(skeleton !=null)sct.set(KeyConstants._skeleton, skeleton);
        
        HttpServletRequest req = pc.getHttpServletRequest();
            try {
            	String path=ContractPath.call(pc, ps.getDisplayPath()); // MUST better impl !!!
				sct.set("remoteAddress",""+new URL(req.getScheme(),req.getServerName(),req.getServerPort(),req.getContextPath()+path+"?wsdl"));
			} catch (Throwable t) {}
            
        
        // Properties
        if(comp.properties.properties!=null) {
        	ArrayImpl parr = new ArrayImpl();
        	Property p;
        	Iterator<Entry<String, Property>> pit = comp.properties.properties.entrySet().iterator();
        	while(pit.hasNext()){
        		p=pit.next().getValue();
        		parr.append(p.getMetaData());
        	}
        	parr.sort(new ArrayOfStructComparator(KeyConstants._name));
        	sct.set(KeyConstants._properties,parr);
        }
        page.metaData=new SoftReference<Struct>(sct);
        return page.metaData.get();
    }    

    private static void metaUDFs(PageContext pc,ComponentImpl comp,Struct sct, int access) throws PageException {
    	ArrayImpl arr=new ArrayImpl();
    	//Collection.Key name;
        
    	Page page = ((PageSourceImpl)comp._getPageSource()).getPage();
    	if(page!=null && page.udfs!=null){
    		for(int i=0;i<page.udfs.length;i++){
    			if(page.udfs[i].getAccess()>access) continue;
        		arr.append(ComponentUtil.getMetaData(pc,(UDFPropertiesImpl) page.udfs[i]));
    		}
    	}
    	
    	// property functions
    	Iterator<Entry<Key, UDF>> it = comp._udfs.entrySet().iterator();
        Entry<Key, UDF> entry;
		UDF udf;
		while(it.hasNext()) {
    		entry= it.next();
    		udf=entry.getValue();
            if(udf.getAccess()>access || !(udf instanceof UDFGSProperty)) continue;
    			if(comp.base!=null) {
            		if(udf==comp.base.getMember(access,entry.getKey(),true,true))
            			continue;
            	}
            	arr.append(udf.getMetaData(pc));
            
        }
        if(arr.size()!=0)sct.set(KeyConstants._functions,arr);
	}

	public boolean isInitalized() {
        return isInit;
    }
    
    public void setInitalized(boolean isInit) {
        this.isInit=isInit;;
    }
    
        
    /**
     * sets a value to the current Component, dont to base Component
     * @param key
     * @param value
     * @return value set
     * @throws ExpressionException 
     */
    private synchronized Object _set(PageContext pc,Collection.Key key, Object value) throws ExpressionException {
    	if(value instanceof UDFPlus) {
        	UDFPlus udf = (UDFPlus)((UDFPlus)value).duplicate();
        	//udf.isComponentMember(true);///+++
        	udf.setOwnerComponent(this);
        	if(udf.getAccess()>Component.ACCESS_PUBLIC)
        		udf.setAccess(Component.ACCESS_PUBLIC);
        	_data.put(key,udf);
        	_udfs.put(key,udf);
        	
        }
        else {
        	if(loaded && !isAccessible(ThreadLocalPageContext.get(pc), dataMemberDefaultAccess))
        		throw new ExpressionException("Component ["+getCallName()+"] has no accessible Member with name ["+key+"]","enable [trigger data member] in admininistrator to also invoke getters and setters");
            
        	_data.put(key,new DataMember(dataMemberDefaultAccess,value));
        }
        return value;
    }

    

    public void reg(Collection.Key key, UDFPlus udf) {
    	registerUDF(key, udf,useShadow,false);
    }
    public void reg(String key, UDFPlus udf) {
    	registerUDF(KeyImpl.init(key), udf,useShadow,false);
    }

    public void registerUDF(String key, UDF udf) {
    	registerUDF(KeyImpl.init(key), (UDFPlus) udf,useShadow,false);
    }
    public void registerUDF(String key, UDFProperties prop) {
    	registerUDF(KeyImpl.init(key), new UDFImpl( prop),useShadow,false);
    }

    public void registerUDF(Collection.Key key, UDF udf) {
    	registerUDF(key, (UDFPlus) udf,useShadow,false);
    }
    public void registerUDF(Collection.Key key, UDFProperties prop) {
    	registerUDF(key, new UDFImpl( prop),useShadow,false);
    }
    
    /*
     *  @deprecated injected is not used
     */
    public void registerUDF(Collection.Key key, UDFPlus udf,boolean useShadow,boolean injected) {
    	udf.setOwnerComponent(this);//+++
    	_udfs.put(key,udf);
    	_data.put(key,udf);
    	if(useShadow)scope.setEL(key, udf);
    }
    
	@Override
	public Object remove(Key key) throws PageException {
    	return _data.remove(key);
	}

    public Object removeEL(Collection.Key key) {
    	// MUST access muss beruecksichtigt werden
    	return _data.remove(key);
    }

    /*public Object set(PageContext pc, String name, Object value) throws PageException {
    	return set(pc, KeyImpl.init(name), value);
    }*/

    @Override
    public Object set(PageContext pc, Collection.Key key, Object value) throws PageException {
    	if(pc==null)pc=ThreadLocalPageContext.get();
    	if(triggerDataMember(pc) && isInit) {
    		if(!isPrivate(pc)) {
        		return callSetter(pc, key, value);
        	}
        }
    	return _set(pc,key,value);
    }

	@Override
	public Object set(Collection.Key key, Object value) throws PageException {
        return set(null,key,value);
	}

    /*public Object setEL(PageContext pc, String name, Object value) {
    	try {return set(pc, name, value);} 
    	catch (PageException e) {return null;}
    }*/
    
    @Override
    public Object setEL(PageContext pc, Collection.Key name, Object value) {
    	try {return set(pc, name, value);} 
    	catch (PageException e) {return null;}
    }

	@Override
	public Object setEL(Key key, Object value) {
    	return setEL(null, key, value);
	}
    
    /*public Object get(PageContext pc, String name) throws PageException {
        return get(pc, KeyImpl.init(name));
    }*/
    
    public Object get(PageContext pc, Collection.Key key) throws PageException {
        Member member=getMember(pc,key,true,false);
        if(member!=null) return member.getValue();
        
        // trigger
        if(triggerDataMember(pc) && !isPrivate(pc)) {
        	return callGetter(pc,key);
        }
        throw new ExpressionException("Component ["+getCallName()+"] has no accessible Member with name ["+key+"]","enable [trigger data member] in admininistrator to also invoke getters and setters");
        //throw new ExpressionException("Component ["+getCallName()+"] has no accessible Member with name ["+name+"]");
    }

    private Object callGetter(PageContext pc,Collection.Key key) throws PageException {
    	Key getterName = KeyImpl.getInstance("get"+key.getLowerString());
    	Member member=getMember(pc,getterName,false,false);
        if(member instanceof UDFPlus) {
            UDFPlus udf = (UDFPlus)member;
            if(udf.getFunctionArguments().length==0 && udf.getReturnType()!=CFTypes.TYPE_VOID) {
                return _call(pc,getterName,udf,null,ArrayUtil.OBJECT_EMPTY);
            }
        } 
        throw new ExpressionException("Component ["+getCallName()+"] has no accessible Member with name ["+key+"]");
	}
    
    private Object callGetter(PageContext pc,Collection.Key key, Object defaultValue) {
    	Key getterName = KeyImpl.getInstance("get"+key.getLowerString());
    	Member member=getMember(pc,getterName,false,false);
        if(member instanceof UDFPlus) {
            UDFPlus udf = (UDFPlus)member;
            if(udf.getFunctionArguments().length==0 && udf.getReturnType()!=CFTypes.TYPE_VOID) {
                try {
					return _call(pc,getterName,udf,null,ArrayUtil.OBJECT_EMPTY);
				} catch (PageException e) {
					return defaultValue;
				}
            }
        } 
        return defaultValue;
	}
    
    private Object callSetter(PageContext pc,Collection.Key key, Object value) throws PageException {
    	Collection.Key setterName = KeyImpl.getInstance("set"+key.getLowerString());
    	Member member=getMember(pc,setterName,false,false);
    	if(member instanceof UDFPlus) {
        	UDFPlus udf = (UDFPlus)member;
        	if(udf.getFunctionArguments().length==1 && (udf.getReturnType()==CFTypes.TYPE_VOID) || udf.getReturnType()==CFTypes.TYPE_ANY   ) {// TDOO support int return type
                return _call(pc,setterName,udf,null,new Object[]{value});
            }    
        }
        return _set(pc,key,value);
	}
    

	/**
     * return element that has at least given access or null
     * @param access
     * @param name
     * @return matching value
     * @throws PageException
     */
    public Object get(int access, String name) throws PageException {
        return get(access, KeyImpl.init(name));
    }
    
    public Object get(int access, Collection.Key key) throws PageException {
        Member member=getMember(access,key,true,false);
        if(member!=null) return member.getValue();
        
        // Trigger
        PageContext pc = ThreadLocalPageContext.get();
        if(triggerDataMember(pc) && !isPrivate(pc)) {
        	return callGetter(pc,key);
        }
        throw new ExpressionException("Component ["+getCallName()+"] has no accessible Member with name ["+key+"]");
    }

    /*public Object get(PageContext pc, String name, Object defaultValue) {
        return get(pc, KeyImpl.init(name), defaultValue);
    }*/

    @Override
    public Object get(PageContext pc, Collection.Key key, Object defaultValue) {
        Member member=getMember(pc,key,true,false);
        if(member!=null) return member.getValue();
        
        // trigger
        if(triggerDataMember(pc) && !isPrivate(pc)) {
        	return callGetter(pc,key,defaultValue);
        }
        return defaultValue;
    }
    
    /**
     * return element that has at least given access or null
     * @param access
     * @param name
     * @return matching value
     */
    protected Object get(int access, String name, Object defaultValue) {
        return get(access, KeyImpl.init(name), defaultValue);
    }

    /**
     * @param access
     * @param key
     * @param defaultValue
     * @return
     */
    public Object get(int access, Collection.Key key, Object defaultValue) { 
        Member member=getMember(access,key,true,false);
        if(member!=null) return member.getValue();
        
        // trigger
        PageContext pc = ThreadLocalPageContext.get();
        if(triggerDataMember(pc) && !isPrivate(pc)) {
        	return callGetter(pc,key,defaultValue);
        }
        return defaultValue;
    }
    
	@Override
	public Object get(Collection.Key key) throws PageException {
    	return get(ThreadLocalPageContext.get(),key);
	}

	@Override
	public Object get(Collection.Key key, Object defaultValue) {
    	return get(ThreadLocalPageContext.get(),key,defaultValue);
	}

    @Override
    public Object call(PageContext pc, String name, Object[] args) throws PageException {
        return _call(pc,KeyImpl.init(name),null,args,false);
    }

	public Object call(PageContext pc, Collection.Key name, Object[] args) throws PageException {
		return _call(pc,name,null,args,false);
	}
    
    protected Object call(PageContext pc, int access, String name, Object[] args) throws PageException {
        return _call(pc,access,KeyImpl.init(name),null,args,false);
    }
    
    public Object call(PageContext pc, int access, Collection.Key name, Object[] args) throws PageException {
        return _call(pc,access,name,null,args,false);
    }

    @Override
    public Object callWithNamedValues(PageContext pc, String name, Struct args) throws PageException {
        return _call(pc,KeyImpl.init(name),args,null,false);
    }

    public Object callWithNamedValues(PageContext pc, Collection.Key methodName, Struct args) throws PageException {
		return _call(pc,methodName,args,null,false);
	}
    
    protected Object callWithNamedValues(PageContext pc, int access, String name, Struct args) throws PageException {
        return _call(pc,access,KeyImpl.init(name),args,null,false);
    }
    
    public Object callWithNamedValues(PageContext pc, int access, Collection.Key name, Struct args) throws PageException {
        return _call(pc,access,name,args,null,false);
    }

    public boolean contains(PageContext pc,String name) {
       	return get(getAccess(pc),name,NullSupportHelper.NULL())!=NullSupportHelper.NULL();
    }

	/**
	 * @param pc
	 * @param key
	 * @return
	 */
	public boolean contains(PageContext pc,Key key) {
	   	return get(getAccess(pc),key,NullSupportHelper.NULL())!=NullSupportHelper.NULL();
	}
	
	@Override
	public boolean containsKey(Key key) {
	   	return contains(ThreadLocalPageContext.get(),key);
	}
    
    public boolean contains(int access,String name) {
    	return get(access,name,NullSupportHelper.NULL())!=NullSupportHelper.NULL();
   }
    
    public boolean contains(int access,Key name) {
    	return get(access,name,NullSupportHelper.NULL())!=NullSupportHelper.NULL();
    }

    @Override
	public Iterator<Collection.Key> keyIterator() {
    	return keyIterator(getAccess(ThreadLocalPageContext.get()));
    }
    
	@Override
	public Iterator<String> keysAsStringIterator() {
    	return keysAsStringIterator(getAccess(ThreadLocalPageContext.get()));
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return entryIterator(getAccess(ThreadLocalPageContext.get()));
	}

    public Collection.Key[] keys() {
    	return keys(getAccess(ThreadLocalPageContext.get()));
    }

    @Override
    public int size() {
    	return size(getAccess(ThreadLocalPageContext.get()));
    }

    
    @Override
    public Class getJavaAccessClass(RefBoolean isNew) throws PageException {
    	return getJavaAccessClass(ThreadLocalPageContext.get(),isNew, false,true,true,true);
    }
    
    public Class getJavaAccessClass(PageContext pc,RefBoolean isNew) throws PageException {
    	return getJavaAccessClass(pc,isNew, false,true,true,true);
    }

    public Class getJavaAccessClass(PageContext pc,RefBoolean isNew,boolean writeLog, boolean takeTop, boolean create, boolean supressWSbeforeArg) throws PageException {
    	isNew.setValue(false);
    	ComponentProperties props =(takeTop)?top.properties:properties;
    	if(props.javaAccessClass==null) {
    		props.javaAccessClass=ComponentUtil.getComponentJavaAccess(pc,this,isNew,create,writeLog,supressWSbeforeArg);
		}
    	return props.javaAccessClass;
    }
    
    public boolean isPersistent() {
    	return top.properties.persistent;
    }
    
    public boolean isAccessors() {
    	return top.properties.accessors;
    }

	public void setProperty(Property property) throws PageException {
		top.properties.properties.put(StringUtil.toLowerCase(property.getName()),property);
		if(top.properties.persistent || top.properties.accessors){
			if(property.getDefault()!=null)scope.setEL(KeyImpl.init(property.getName()), property.getDefault());
			PropertyFactory.createPropertyUDFs(this,property);
		}
	}

	

	private void initProperties() throws PageException {
		top.properties.properties=new LinkedHashMap<String,Property>();
		
		// MappedSuperClass  
		if(isPersistent() && !isBasePeristent() && top.base!=null && top.base.properties.properties!=null && top.base.properties.meta!=null) {
			boolean msc = Caster.toBooleanValue(top.base.properties.meta.get(KeyConstants._mappedSuperClass,Boolean.FALSE),false);
			if(msc){
				Property p;
				Iterator<Entry<String, Property>> it = top.base.properties.properties.entrySet().iterator();
				while(it.hasNext())	{
					p = it.next().getValue();
					if(p.isPeristent()) {
						
						setProperty(p);
					}
				}
			}
		}
	}

	public Property[] getProperties(boolean onlyPeristent) {
		return getProperties(onlyPeristent, false,false,false);
	}

	public Property[] getProperties(boolean onlyPeristent, boolean includeBaseProperties, boolean preferBaseProperties, boolean inheritedMappedSuperClassOnly) {
		Map<String,Property> props=new LinkedHashMap<String,Property>();
		_getProperties(top,props,onlyPeristent, includeBaseProperties, preferBaseProperties, inheritedMappedSuperClassOnly);
		return props.values().toArray(new Property[props.size()]);
	}

	private static void _getProperties(ComponentImpl c,Map<String,Property> props,boolean onlyPeristent, boolean includeBaseProperties, boolean preferBaseProperties, boolean inheritedMappedSuperClassOnly) {
		//if(c.properties.properties==null) return new Property[0];
		
		// collect with filter
		if(c.properties.properties!=null){
			Property p;
			Iterator<Entry<String, Property>> it = c.properties.properties.entrySet().iterator();
			while(it.hasNext())	{
				p = it.next().getValue();
				if(!onlyPeristent || p.isPeristent()) {
					if (!preferBaseProperties || !props.containsKey(p.getName().toLowerCase())) {
						props.put(p.getName().toLowerCase(),p);
					}
				}
			}
		}

		// MZ: Moved to the bottom to allow base properties to override inherited versions
		if(includeBaseProperties && c.base!=null) {
			if (!inheritedMappedSuperClassOnly || (c.base.properties.meta != null && Caster.toBooleanValue(c.base.properties.meta.get(KeyConstants._mappedSuperClass, Boolean.FALSE), false))) {
				_getProperties(c.base, props, onlyPeristent, includeBaseProperties, preferBaseProperties, inheritedMappedSuperClassOnly);
			}
		}

	}

	public ComponentScope getComponentScope() {
		return scope;
	}


	@Override
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(castToBooleanValue(), b);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare((Date)castToDateTime(), (Date)dt);
	}

	@Override
	public int compareTo(double d) throws PageException {
		return Operator.compare(castToDoubleValue(), d);
	}

	@Override
	public int compareTo(String str) throws PageException {
		return Operator.compare(castToString(), str);
	}

	public void addConstructorUDF(Key key, UDF value) {
		if(constructorUDFs==null)
			constructorUDFs=new HashMap<Key,UDF>();
		constructorUDFs.put(key, value);
	}

// MUST more native impl
	public void readExternal(ObjectInput in) throws IOException,ClassNotFoundException {
		boolean pcCreated=false;
		PageContext pc = ThreadLocalPageContext.get();
		// MUST this is just a workaround
		if(pc==null){
			pcCreated=true;
			ConfigWeb config = (ConfigWeb) ThreadLocalPageContext.getConfig();
			Pair[] parr = new Pair[0];
			pc=ThreadUtil.createPageContext(config, DevNullOutputStream.DEV_NULL_OUTPUT_STREAM, "localhost", "/","", new Cookie[0], parr, parr, new StructImpl());
		}
		
		try {
			// MUST do serialisation more like the cloning way
			ComponentImpl other=(ComponentImpl) new CFMLExpressionInterpreter().interpret(pc,in.readUTF());
			
			
			this._data=other._data;
			this._udfs=other._udfs;
			setOwner(_udfs);
			setOwner(_data);
			this.afterConstructor=other.afterConstructor;
			this.base=other.base;
			//this.componentPage=other.componentPage;
			this.pageSource=other.pageSource;
			this.constructorUDFs=other.constructorUDFs;
			this.dataMemberDefaultAccess=other.dataMemberDefaultAccess;
			this.interfaceCollection=other.interfaceCollection;
			this.isInit=other.isInit;
			this.properties=other.properties;
			this.scope=other.scope;
			this.top=this;
			this._triggerDataMember=other._triggerDataMember;
			this.useShadow=other.useShadow;
			
			
		} catch (PageException e) {
			throw new IOException(e.getMessage());
		}
		finally {
			if(pcCreated)ThreadLocalPageContext.release();
		}
	}

	private void  setOwner(Map<Key,? extends Member> data) {
		Member m;
		Iterator<? extends Member> it = data.values().iterator();
		while(it.hasNext()){
			m=it.next();
			if(m instanceof UDFPlus) {
				((UDFPlus)m).setOwnerComponent(this);
			}
		}
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		try {
			out.writeUTF(new ScriptConverter().serialize(this));
		}
		catch (ConverterException e) {
			throw ExceptionUtil.toIOException(e);
		}
	}

	@Override
	public ComponentAccess _base() {
		return base;
	}	
	


	private boolean triggerDataMember(PageContext pc) {
		if(_triggerDataMember!=null) return _triggerDataMember.booleanValue();
		if(pc==null || pc.getApplicationContext()==null){
			//print.ds(""+(pc==null));// TODO why this is true sometimes?
			return false;
		}
		return pc.getApplicationContext().getTriggerComponentDataMember();
	}

	public void setLoaded(boolean loaded) {
		this.loaded=loaded;
	}
}