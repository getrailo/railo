package railo.runtime.type.scope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import railo.commons.lang.ExceptionUtil;
import railo.runtime.ComponentScope;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.NullSupportHelper;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFPlus;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.StructSupport;
import railo.runtime.util.QueryStack;
import railo.runtime.util.QueryStackImpl;

/**
 * Undefined Scope
 */
public final class UndefinedImpl extends StructSupport implements Undefined {

	private static final long serialVersionUID = -5626787508494702023L;

	private Scope[] scopes;
	private QueryStackImpl qryStack=new QueryStackImpl();
	private Variables variable;
	private boolean allowImplicidQueryCall;
	private boolean checkArguments;
	


	private boolean localAlways;
	private short type;
	private boolean isInit;
	private Local local;
	private Argument argument;
	private PageContextImpl pc;
	private boolean debug;
	
	/**
	 * constructor of the class
	 * @param pageContextImpl 
	 * @param type type of the undefined scope (ServletConfigImpl.SCOPE_STRICT;ServletConfigImpl.SCOPE_SMALL;ServletConfigImpl.SCOPE_STANDART)
	 */
	public UndefinedImpl(PageContextImpl pc, short type) {
		this.type=type;
		this.pc=pc;
	}
	
	
	@Override
	public Local localScope() {
		return local;
	}
	
	@Override
	public Argument argumentsScope() {
		return argument;
	}

	@Override
	public Variables variablesScope() {
		return variable;
	}
	
	@Override
	public int setMode(int mode) {
		int m=Undefined.MODE_NO_LOCAL_AND_ARGUMENTS;
		if(checkArguments) {
			if(localAlways)m=Undefined.MODE_LOCAL_OR_ARGUMENTS_ALWAYS;
			else m=Undefined.MODE_LOCAL_OR_ARGUMENTS_ONLY_WHEN_EXISTS;
		}
		
		checkArguments=mode!=Undefined.MODE_NO_LOCAL_AND_ARGUMENTS;
		localAlways=mode==Undefined.MODE_LOCAL_OR_ARGUMENTS_ALWAYS;
		return m;
	}
	
	public boolean getLocalAlways(){
		return localAlways;
	}
	
	
	@Override
	public void setFunctionScopes(Local local, Argument argument) {
		this.local=local;
		this.argument=argument;
	}
	
	@Override
	public QueryStack getQueryStack() {
		return qryStack;
	}
	
	@Override
	public void setQueryStack(QueryStack qryStack) {
		this.qryStack=(QueryStackImpl) qryStack;
	}

	@Override
	public void addQuery(Query qry) {
		if(allowImplicidQueryCall)
			qryStack.addQuery(qry);
	}

	@Override
	public void removeQuery() {
		if(allowImplicidQueryCall)
			qryStack.removeQuery();
	}

	@Override
	public int size() {
		return variable.size();
	}

	@Override
	public Collection.Key[] keys() {
		return variable.keys();
	}

	@Override
	public Object remove(Collection.Key key) throws PageException {
		if(checkArguments && local.containsKey(key))
			return local.remove(key);
		return variable.remove(key);
	}

	@Override
	public Object removeEL(Collection.Key key) {
		if(checkArguments && local.containsKey(key))
			return local.removeEL(key);
		return variable.removeEL(key);
	}

	@Override
	public void clear() {
		variable.clear();
	}
	
	public Object get(Collection.Key key) throws PageException {
		
		Object rtn;
		if(checkArguments) {
		    rtn=local.get(key,NullSupportHelper.NULL());
		    if(rtn!=NullSupportHelper.NULL()) return rtn;

		    rtn=argument.getFunctionArgument(key,NullSupportHelper.NULL());
		    if(rtn!=NullSupportHelper.NULL()) {
		    	if(debug) debugCascadedAccess(pc,argument.getTypeAsString(), key);
				return rtn;
		    }
		}
		
		// get data from queries
		if(allowImplicidQueryCall && !qryStack.isEmpty()) {
			rtn=qryStack.getDataFromACollection(pc,key,NullSupportHelper.NULL());
			if(rtn!=NullSupportHelper.NULL()) {
				if(debug) debugCascadedAccess(pc,"query", key);
				if(!NullSupportHelper.full() && rtn==null) return "";
				return rtn;
		    }
		}
		
		// variable
		rtn=variable.get(key,NullSupportHelper.NULL());
		if(rtn!=NullSupportHelper.NULL()) {
			if(debug && checkArguments) debugCascadedAccess(pc,variable,rtn, key);
			return rtn;
	    }
		
		// thread scopes
		if(pc.hasFamily()) {
			rtn = pc.getThreadScope(key,NullSupportHelper.NULL());
			if(rtn!=NullSupportHelper.NULL()) {
				if(debug) debugCascadedAccess(pc,"thread", key);
				return rtn;
			}
		}
		
		// get a scope value
		for(int i=0;i<scopes.length;i++) {
		    rtn=scopes[i].get(key,NullSupportHelper.NULL());
			if(rtn!=NullSupportHelper.NULL()) {
				if(debug) debugCascadedAccess(pc,scopes[i].getTypeAsString(),key);
				return rtn;
			}
		}
		if(pc.getConfig().debug())
			throw new ExpressionException(ExceptionUtil.similarKeyMessage(this, key.getString(), "key", "keys",false));
			
		throw new ExpressionException("variable ["+key.getString()+"] doesn't exist");
	}
	
	public static void debugCascadedAccess(PageContext pc,Variables var, Object value, Collection.Key key) {
		if(var instanceof ComponentScope){
			if(key.equals(KeyConstants._THIS) || key.equals(KeyConstants._SUPER)) return;
			if(value instanceof UDF) {
				return;
			}
		}
		
		debugCascadedAccess(pc,"variables", key);
	}
	
	public static void debugCascadedAccess(PageContext pc,String name, Collection.Key key) {
		if(pc!=null)pc.getDebugger().addImplicitAccess(name,key.getString());
	}
	
	@Override
	public Object getCollection(String key) throws PageException {
		return getCollection(KeyImpl.init(key));
	}
	
	public Struct getScope(Collection.Key key) {
		Object rtn=null;
		Struct sct=new StructImpl(Struct.TYPE_LINKED);
		
		if(checkArguments) {
		    rtn=local.get(key,NullSupportHelper.NULL());
		    if(rtn!=NullSupportHelper.NULL()) sct.setEL(KeyConstants._local, rtn);
		    rtn=argument.getFunctionArgument(key,NullSupportHelper.NULL());
		    if(rtn!=NullSupportHelper.NULL()) sct.setEL(KeyConstants._arguments, rtn);
		}
				
		// get data from queries
		if(allowImplicidQueryCall && !qryStack.isEmpty()) {
			rtn=qryStack.getColumnFromACollection(key);
			if(rtn!=null) sct.setEL(KeyConstants._query, rtn);
		}
		
		// variable
		rtn=variable.get(key,NullSupportHelper.NULL());
		if(rtn!=NullSupportHelper.NULL()) {
			sct.setEL(KeyConstants._variables, rtn);
		}
		
		// thread scopes
		if(pc.hasFamily()) {
			rtn = pc.getThreadScope(key,NullSupportHelper.NULL());
			if(rtn!=NullSupportHelper.NULL()) sct.setEL(KeyConstants._thread, rtn); 
		}
		
		// get a scope value
		for(int i=0;i<scopes.length;i++) {
			rtn=scopes[i].get(key,NullSupportHelper.NULL());
			if(rtn!=NullSupportHelper.NULL()) {
				sct.setEL(KeyImpl.init(scopes[i].getTypeAsString()), rtn); 
			}
		}
		return sct;
	}


	/**
	 * return a list of String with the scope names
	 * @param key
	 * @return
	 */
	public List<String> getScopeNames() {
		List<String> scopeNames=new ArrayList<String>();
		
		if(checkArguments) {
			scopeNames.add("local");
			scopeNames.add("arguments");
		}
		scopeNames.add("variables");
		
		// thread scopes
		if(pc.hasFamily()) {
			String[] names = pc.getThreadScopeNames();
			for(int i=0;i<names.length;i++)scopeNames.add(i,names[i]);
		}
		
		for(int i=0;i<scopes.length;i++) {
			scopeNames.add((scopes[i]).getTypeAsString()); 
		}
		return scopeNames;
	}

	public Object getCollection(Key key) throws PageException {
		Object rtn=null;
		
		if(checkArguments) {
		    rtn=local.get(key,NullSupportHelper.NULL());
		    if(rtn!=NullSupportHelper.NULL()) return rtn;
		    rtn=argument.getFunctionArgument(key,NullSupportHelper.NULL());
		    if(rtn!=NullSupportHelper.NULL()) {
		    	if(debug)debugCascadedAccess(pc,argument.getTypeAsString(), key);
		    	return rtn;
		    }
		}
				
		// get data from queries
		if(allowImplicidQueryCall && !qryStack.isEmpty()) {
			rtn=qryStack.getColumnFromACollection(key);
			if(rtn!=null) {
				if(debug)debugCascadedAccess(pc,"query", key);
				return rtn;
			}
		}
		
		// variable
		rtn=variable.get(key,NullSupportHelper.NULL());
		if(rtn!=NullSupportHelper.NULL()) {
			if(debug && checkArguments) debugCascadedAccess(pc,variable,rtn, key);
			return rtn;
		}
		
		// thread scopes
		if(pc.hasFamily()) {
			rtn = pc.getThreadScope(key,NullSupportHelper.NULL());
			if(rtn!=NullSupportHelper.NULL()) {
				if(debug) debugCascadedAccess(pc,"thread", key);
				return rtn;
			}
		}
		
		// get a scope value
		for(int i=0;i<scopes.length;i++) {
			rtn=scopes[i].get(key,NullSupportHelper.NULL());
			if(rtn!=NullSupportHelper.NULL()) {
				if(debug)debugCascadedAccess(pc,scopes[i].getTypeAsString(),key);
				return rtn;
			}
		}
		throw new ExpressionException("variable ["+key.getString()+"] doesn't exist");
	}

    public Object get(Collection.Key key, Object defaultValue) {
    	Object rtn=null;
		if(checkArguments) {
			rtn=local.get(key,NullSupportHelper.NULL());
			if(rtn!=NullSupportHelper.NULL()) return rtn;
            
            rtn=argument.getFunctionArgument(key,NullSupportHelper.NULL());
            if(rtn!=NullSupportHelper.NULL()) {
            	if(debug) debugCascadedAccess(pc,argument.getTypeAsString(), key);
				return rtn;
            }
        }
        
        // get data from queries
        if(allowImplicidQueryCall && !qryStack.isEmpty()) {
        	rtn=qryStack.getDataFromACollection(pc,key,NullSupportHelper.NULL());
			if(rtn!=NullSupportHelper.NULL()) {
            	if(debug) debugCascadedAccess(pc,"query", key);
				return rtn;
            }
        }
        
        // variable
        rtn=variable.get(key,NullSupportHelper.NULL());
        if(rtn!=NullSupportHelper.NULL()) {
        	if(debug && checkArguments) debugCascadedAccess(pc,variable, rtn, key);
			return rtn;
        }
        
		// thread scopes
		if(pc.hasFamily()) {
			rtn = pc.getThreadScope(key,NullSupportHelper.NULL());
			if(rtn!=NullSupportHelper.NULL()) {
				if(debug && checkArguments) debugCascadedAccess(pc,"thread", key);
				return rtn;
			}
		}

        // get a scope value
        for(int i=0;i<scopes.length;i++) {
            rtn=scopes[i].get(key,NullSupportHelper.NULL());
            if(rtn!=NullSupportHelper.NULL()) {
            	if(debug) debugCascadedAccess(pc,scopes[i].getTypeAsString(), key);
    			return rtn;
            }
        }
        
        return defaultValue;
    }
    
    
    @Override
    public Object getCascading(String strKey) {
        return getCascading(KeyImpl.init(strKey));
    }


	@Override
	public Object getCascading(Collection.Key key) {
        throw new RuntimeException("this method is no longer supported, use getCascading(Collection.Key key, Object defaultValue) instead");
	}
	
	// FUTURE add to interface and set above to deprecated
	public Object getCascading(Collection.Key key, Object defaultValue) {
        Object rtn;
          
        // get a scope value
        for(int i=0;i<scopes.length;i++) {
            rtn=scopes[i].get(key,NullSupportHelper.NULL());
            if(rtn!=NullSupportHelper.NULL()) {
                return rtn;
            }
        }
        return defaultValue;
	}

	@Override
	public Object setEL(Collection.Key key, Object value) {
		if(checkArguments) {
            if(localAlways || local.containsKey(key))     return local.setEL(key,value);
            if(argument.containsFunctionArgumentKey(key))  {
            	if(debug)debugCascadedAccess(pc,argument.getTypeAsString(), key);
            	return argument.setEL(key,value);
            }
        }
			
		if(debug && checkArguments)debugCascadedAccess(pc,variable.getTypeAsString(), key);
    	return variable.setEL(key,value);
	}

	@Override
	public Object set(Collection.Key key, Object value) throws PageException {
		if(checkArguments) {
        	if(localAlways || local.containsKey(key))     return local.set(key,value);
            if(argument.containsFunctionArgumentKey(key))  {
            	if(debug)debugCascadedAccess(pc,argument.getTypeAsString(), key);
            	return argument.set(key,value);
            }
            
        }
		if(debug && checkArguments)debugCascadedAccess(pc,variable.getTypeAsString(), key);
    	return variable.set(key,value);
	}
	
	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return variable.toDumpData(pageContext, maxlevel,dp);
	}

	@Override
	public Iterator<Collection.Key> keyIterator() {
		return variable.keyIterator();
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return variable.keysAsStringIterator();
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return variable.entryIterator();
	}
	
	@Override
	public Iterator<Object> valueIterator() {
		return variable.valueIterator();
	}
	
	@Override
	public boolean isInitalized() {
		return isInit;
	}

	@Override
	public void initialize(PageContext pc) {
		if(isInitalized()) return;
		isInit=true;
		variable=pc.variablesScope();
        argument=pc.argumentsScope();
		local=pc.localScope();
		allowImplicidQueryCall=pc.getConfig().allowImplicidQueryCall();
        type=pc.getConfig().getScopeCascadingType();
        debug=pc.getConfig().debug() && ((ConfigImpl)pc.getConfig()).hasDebugOptions(ConfigImpl.DEBUG_IMPLICIT_ACCESS);
		
		// Strict
		if(type==Config.SCOPE_STRICT) {
			//print.ln("strict");
			scopes=new Scope[] {};
		}
		// small
		else if(type==Config.SCOPE_SMALL) {
			//print.ln("small");
			if(pc.getConfig().mergeFormAndURL()) {
				scopes=new Scope[] {
						pc.formScope()
					};
			}
			else {
				scopes=new Scope[] {
						pc.urlScope(),
						pc.formScope()
					};
			}
		}
		// standard
		else  {
			reinitialize( pc);
		}
		
		
	}

	public void reinitialize(PageContext pc) {
		if(type!=Config.SCOPE_STANDARD) return;
		Client cs = pc.clientScopeEL();
//		print.ln("standard");
		if(pc.getConfig().mergeFormAndURL()) {
            scopes=new Scope[cs==null?3:4]; 
            scopes[0]=pc.cgiScope();
            scopes[1]=pc.formScope();
            scopes[2]=pc.cookieScope();
            if(cs!=null)scopes[3]=cs;
		}
		else {
            scopes=new Scope[cs==null?4:5]; 
            scopes[0]=pc.cgiScope();
            scopes[1]=pc.urlScope();
            scopes[2]=pc.formScope();
            scopes[3]=pc.cookieScope();
            if(cs!=null)scopes[4]=cs;
		}
	}


	@Override
	public final void release() {
		isInit=false;
		argument=null;
		local=null;
		variable=null;
		scopes=null;
		checkArguments=false;
		localAlways=false;
		if(allowImplicidQueryCall)qryStack.clear();
	}
	
	@Override
	public final void release(PageContext pc) {
		isInit=false;
		argument=null;
		local=null;
		variable=null;
		scopes=null;
		checkArguments=false;
		localAlways=false;
		if(allowImplicidQueryCall)qryStack.clear();
	}

	@Override
	public Collection duplicate(boolean deepCopy) {
		UndefinedImpl dupl = new UndefinedImpl(pc, type);
		dupl.allowImplicidQueryCall=allowImplicidQueryCall;
		dupl.checkArguments=checkArguments;
		dupl.argument=deepCopy?(Argument)Duplicator.duplicate(argument,deepCopy):argument;
		dupl.isInit=isInit;
		dupl.local=deepCopy?(Local)Duplicator.duplicate(local,deepCopy):local;
		dupl.localAlways=localAlways;
		dupl.qryStack= (deepCopy?(QueryStackImpl)Duplicator.duplicate(qryStack,deepCopy):qryStack);
		
		dupl.variable=deepCopy?(Variables)Duplicator.duplicate(variable,deepCopy):variable;
		dupl.pc=pc;
		dupl.debug=debug;
		
		// scopes
		if(deepCopy) {
			dupl.scopes=new Scope[scopes.length];
			for(int i=0;i<scopes.length;i++) {
				dupl.scopes[i]=(Scope)Duplicator.duplicate(scopes[i],deepCopy);
			}
		}
		else dupl.scopes=scopes;
		
		return dupl;
	}
	

	@Override
	public boolean containsKey(Key key) {
        return get(key,null)!=null;
	}

    @Override
    public String castToString() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct to String",
          "Use Built-In-Function \"serialize(Struct):String\" to create a String from Struct");
    }
    
	@Override
	public String castToString(String defaultValue) {
		return defaultValue;
	}

    @Override
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct to a boolean value");
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }


    @Override
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct to a number value");
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }


    @Override
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct to a Date");
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	@Override
	public int compareTo(boolean b) throws ExpressionException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a boolean value");
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a DateTime Object");
	}

	@Override
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a numeric value");
	}

	@Override
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a String");
	}

    @Override
    public void setVariableScope(Variables scope) {
    	variable=scope;
    }

    @Override
    public int getType() {
        return SCOPE_UNDEFINED;
    }

    @Override
    public String getTypeAsString() {
        return "undefined";
    }


	/**
	 * @return the allowImplicidQueryCall
	 */
	public boolean isAllowImplicidQueryCall() {
		return allowImplicidQueryCall;
	}


	/**
	 * @param allowImplicidQueryCall the allowImplicidQueryCall to set
	 */
	public boolean setAllowImplicidQueryCall(boolean allowImplicidQueryCall) {
		boolean old=this.allowImplicidQueryCall;
		this.allowImplicidQueryCall = allowImplicidQueryCall;
		return old;
	}
	
	/**
	 * @return the checkArguments
	 */
	public boolean getCheckArguments() {
		return checkArguments;
	}
	
	@Override
	public Object call(PageContext pc, Key methodName, Object[] args) throws PageException {
		Object obj = get(methodName,null); // every none UDF value is fine as default argument
		if(obj instanceof UDFPlus) {
			return ((UDFPlus)obj).call(pc,methodName,args,false);
		}
		throw new ExpressionException("No matching function ["+methodName+"] found");
	}

    @Override
	public Object callWithNamedValues(PageContext pc, Key methodName, Struct args) throws PageException {
		Object obj = get(methodName,null);
		if(obj instanceof UDFPlus) {
			return ((UDFPlus)obj).callWithNamedValues(pc,methodName,args,false);
		}
		throw new ExpressionException("No matching function ["+methodName+"] found");
	}
}