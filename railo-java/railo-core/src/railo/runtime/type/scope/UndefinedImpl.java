package railo.runtime.type.scope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.config.Config;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.Scope;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.StructSupport;
import railo.runtime.util.QueryStack;
import railo.runtime.util.QueryStackImpl;

/**
 * Undefined Scope
 */
public final class UndefinedImpl extends StructSupport implements Undefined {

	private static final Collection.Key THREAD = KeyImpl.getInstance("thread");
	private static final Collection.Key VARIABLES = KeyImpl.getInstance("variables");
	private static final Collection.Key QUERY = KeyImpl.getInstance("query");
	private static final Collection.Key LOCAL = KeyImpl.getInstance("local");
	private static final Collection.Key ARGUMENTS = KeyImpl.getInstance("arguments");
	private Collection[] scopes;
	private QueryStackImpl qryStack=new QueryStackImpl();
	private Scope variable;
	private boolean allowImplicidQueryCall;
	private boolean checkArguments;
	


	private boolean localAlways;
	private short type;
	private boolean isInit;
	private Scope local;
	private ArgumentPro argument;
	private PageContextImpl pc;
	
	/**
	 * constructor of the class
	 * @param pageContextImpl 
	 * @param type type of the undefined scope (ServletConfigImpl.SCOPE_STRICT;ServletConfigImpl.SCOPE_SMALL;ServletConfigImpl.SCOPE_STANDART)
	 */
	public UndefinedImpl(PageContextImpl pc, short type) {
		this.type=type;
		this.pc=pc;
	}
	
	
	/**
     * @see railo.runtime.type.scope.Undefined#localScope()
     */
	public Scope localScope() {
		return local;
	}
	

	/*
     * @see railo.runtime.type.scope.Undefined#check Arguments(boolean)
     * /
	public boolean check Arguments(boolean b) {
		boolean old=this.check Arguments;
		this.check Arguments=b;
		return old;
	}*/
	
	/**
	 * @see railo.runtime.type.scope.Undefined#setMode(int)
	 */
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
	
	
	/**
     * @see railo.runtime.type.scope.Undefined#setFunctionScopes(railo.runtime.type.Scope, railo.runtime.type.Scope)
     */
	public void setFunctionScopes(Scope local, Scope argument) {// FUTURE setFunctionScopes(Local local,Argument argument)
		this.local=local;
		this.argument=(ArgumentPro) argument;
	}

	/**
     * @see railo.runtime.type.scope.Undefined#getCollectionStack()
     */
	public QueryStack getCollectionStack() {
		return getQueryStack();
	}
	
	/**
	 * @see railo.runtime.type.scope.Undefined#getQueryStack()
	 */
	public QueryStack getQueryStack() {
		return qryStack;
	}
	
	/**
     * @see railo.runtime.type.scope.Undefined#setCollectionStack(railo.runtime.util.QueryStack)
     */
	public void setCollectionStack(QueryStack collStack) {
		setQueryStack(collStack);
	}
	
	/**
	 * @see railo.runtime.type.scope.Undefined#setQueryStack(railo.runtime.util.QueryStack)
	 */
	public void setQueryStack(QueryStack qryStack) {
		this.qryStack=(QueryStackImpl) qryStack;
	}
	
	/**
     * @see railo.runtime.type.scope.Undefined#addCollection(railo.runtime.type.Query)
     */
	public void addCollection(Query coll) {
		addQuery(coll);
	}

	/**
	 * @see railo.runtime.type.scope.Undefined#addQuery(railo.runtime.type.Query)
	 */
	public void addQuery(Query qry) {
		if(allowImplicidQueryCall)
			qryStack.addQuery(qry);
	}

	/**
     * @see railo.runtime.type.scope.Undefined#removeCollection()
     */
	public void removeCollection() {
		removeQuery();
	}

	/**
	 * @see railo.runtime.type.scope.Undefined#removeQuery()
	 */
	public void removeQuery() {
		if(allowImplicidQueryCall)
			qryStack.removeQuery();
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return variable.size();
	}

	/**
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		return variable.keysAsString();
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Collection.Key[] keys() {
		return variable.keys();
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key key) throws PageException {
		if(checkArguments && local.containsKey(key))
			return local.remove(key);
		return variable.remove(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Collection.Key key) {
		if(checkArguments && local.containsKey(key))
			return local.removeEL(key);
		return variable.removeEL(key);
	}

	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		variable.clear();
	}
	
	public Object get(Collection.Key key) throws PageException {
		
		Object rtn=null;
		if(checkArguments) {
		    rtn=local.get(key,null);
		    if(rtn!=null) return rtn;

		    rtn=argument.getFunctionArgument(key,null);
		    if(rtn!=null) return rtn;
		}
		
		// get data from queries
		if(allowImplicidQueryCall && !qryStack.isEmpty()) {
			rtn=qryStack.getDataFromACollection(pc,key);
			if(rtn!=null) {
		    	return rtn;
		    }
		}
		
		// variable
		rtn=variable.get(key,null);
		if(rtn!=null) {
			return rtn;
	    }
		
		// thread scopes
		if(pc.hasFamily()) {
			rtn = pc.getThreadScope(key,null);
			if(rtn!=null) return rtn;
		}
		
		// get a scope value
		for(int i=0;i<scopes.length;i++) {
		    rtn=scopes[i].get(key,null);
			if(rtn!=null) {
				return rtn;
			}
		}
		throw new ExpressionException("variable ["+key.getString()+"] doesn't exist");
	}
	
	/**
     * @see railo.runtime.type.scope.Undefined#getCollection(java.lang.String)
     */
	public Object getCollection(String key) throws ExpressionException {
		Object rtn=null;
		
		if(checkArguments) {
		    rtn=local.get(key,null);
		    if(rtn!=null) return rtn;
		    rtn=argument.getFunctionArgument(key,null);
		    if(rtn!=null) return rtn;
		}
				
		// get data from queries
		if(allowImplicidQueryCall && !qryStack.isEmpty()) {
			rtn=qryStack.getColumnFromACollection(key);
			if(rtn!=null) return rtn;
		}
		
		// variable
		rtn=variable.get(key,null);
		if(rtn!=null) {
			return rtn;
		}
		
		// thread scopes
		if(pc.hasFamily()) {
			rtn = pc.getThreadScope(key,null);
			if(rtn!=null) return rtn;
		}
		
		// get a scope value
		for(int i=0;i<scopes.length;i++) {
			rtn=scopes[i].get(key,null);
			if(rtn!=null) {
				return rtn;
			}
		}
		throw new ExpressionException("variable ["+key+"] doesn't exist");
	}
	
	public Struct getScope(String key) {
		Object rtn=null;
		Struct sct=new StructImpl(Struct.TYPE_LINKED);
		
		if(checkArguments) {
		    rtn=local.get(key,null);
		    if(rtn!=null) sct.setEL(LOCAL, rtn);
		    rtn=argument.getFunctionArgument(key,null);
		    if(rtn!=null) sct.setEL(ARGUMENTS, rtn);
		}
				
		// get data from queries
		if(allowImplicidQueryCall && !qryStack.isEmpty()) {
			rtn=qryStack.getColumnFromACollection(key);
			if(rtn!=null) sct.setEL(QUERY, rtn);
		}
		
		// variable
		rtn=variable.get(key,null);
		if(rtn!=null) {
			sct.setEL(VARIABLES, rtn);
		}
		
		// thread scopes
		if(pc.hasFamily()) {
			rtn = pc.getThreadScope(key,null);
			if(rtn!=null) sct.setEL(THREAD, rtn); 
		}
		
		// get a scope value
		for(int i=0;i<scopes.length;i++) {
			rtn=scopes[i].get(key,null);
			if(rtn!=null) {
				sct.setEL(((Scope)scopes[i]).getTypeAsString(), rtn); 
			}
		}
		return sct;
	}
	
	/**
	 * return a list of String with the scope names
	 * @param key
	 * @return
	 */
	public List getScopeNames() {
		List scopeNames=new ArrayList();
		
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
			
			scopeNames.add(((Scope)scopes[i]).getTypeAsString()); 
		}
		return scopeNames;
		
		
		
	}

	public Object getCollection(Key key) throws PageException {
		Object rtn=null;
		
		if(checkArguments) {
		    rtn=local.get(key,null);
		    if(rtn!=null) return rtn;
		    rtn=argument.getFunctionArgument(key,null);
		    if(rtn!=null) return rtn;
		}
				
		// get data from queries
		if(allowImplicidQueryCall && !qryStack.isEmpty()) {
			rtn=qryStack.getColumnFromACollection(key);
			if(rtn!=null) return rtn;
		}
		
		// variable
		rtn=variable.get(key,null);
		if(rtn!=null) {
			return rtn;
		}
		
		// thread scopes
		if(pc.hasFamily()) {
			rtn = pc.getThreadScope(key,null);
			if(rtn!=null) return rtn;
		}
		
		// get a scope value
		for(int i=0;i<scopes.length;i++) {
			rtn=scopes[i].get(key,null);
			if(rtn!=null) {
				return rtn;
			}
		}
		throw new ExpressionException("variable ["+key.getString()+"] doesn't exist");
	}

    public Object get(Collection.Key key, Object defaultValue) {
		Object rtn=null;
        
        if(checkArguments) {
            rtn=local.get(key,null);
            if(rtn!=null) return rtn;
            rtn=argument.getFunctionArgument(key,null);
            if(rtn!=null) return rtn;
        }
        
        // get data from queries
        if(allowImplicidQueryCall && !qryStack.isEmpty()) {
            rtn=qryStack.getDataFromACollection(key);
            if(rtn!=null) return rtn;
        }
        
        // variable
        rtn=variable.get(key,null);
        if(rtn!=null) {
            return rtn;
        }
		
		// thread scopes
		if(pc.hasFamily()) {
			rtn = pc.getThreadScope(key,null);
			if(rtn!=null) return rtn;
		}
        
        // get a scope value
        for(int i=0;i<scopes.length;i++) {
            rtn=scopes[i].get(key,null);
            if(rtn!=null) {
                return rtn;
            }
        }
        return defaultValue;
    }
    
    
    /**
     * @see railo.runtime.type.scope.Undefined#getCascading(java.lang.String)
     */
    public Object getCascading(String strKey) {
        Object rtn=null;
        Key key = KeyImpl.init(strKey);
        // get a scope value
        for(int i=0;i<scopes.length;i++) {
            rtn=scopes[i].get(key,null);
            if(rtn!=null) {
                return rtn;
            }
        }
        return null;
    }


	/**
	 *
	 * @see railo.runtime.type.scope.Undefined#getCascading(railo.runtime.type.Collection.Key)
	 */
	public Object getCascading(Collection.Key key) {
        Object rtn=null;
          
        // get a scope value
        for(int i=0;i<scopes.length;i++) {
            rtn=scopes[i].get(key,null);
            if(rtn!=null) {
                return rtn;
            }
        }
        return null;
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Collection.Key key, Object value) {
		if(checkArguments) {
            if(localAlways || local.containsKey(key))     return local.setEL(key,value);
            if(argument.containsFunctionArgumentKey(key))  return argument.setEL(key,value);
        }
			
		return variable.setEL(key,value);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws PageException {
		if(checkArguments) {
        	if(localAlways || local.containsKey(key))     return local.set(key,value);
            if(argument.containsFunctionArgumentKey(key))  return argument.set(key,value);
            
        }
		return variable.set(key,value);
	}
	
	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return variable.toDumpData(pageContext, maxlevel,dp);
	}

	/**
	 * @see railo.runtime.type.Collection#keyIterator()
	 */
	public Iterator keyIterator() {
		return variable.keyIterator();
	}
	
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
		if(isInitalized()) return;
		isInit=true;
		variable=pc.variablesScope();
        argument=(ArgumentPro) pc.argumentsScope();
		local=pc.localScope();
		allowImplicidQueryCall=pc.getConfig().allowImplicidQueryCall();
        type=pc.getConfig().getScopeCascadingType();
        
		// Strict
		if(type==Config.SCOPE_STRICT) {
			//print.ln("strict");
			scopes=new Collection[] {};
		}
		// small
		else if(type==Config.SCOPE_SMALL) {
			//print.ln("small");
			if(pc.getConfig().mergeFormAndURL()) {
				scopes=new Collection[] {
						pc.formScope()
					};
			}
			else {
				scopes=new Collection[] {
						pc.urlScope(),
						pc.formScope()
					};
			}
		}
		// standard
		else  {
			reinitialize((PageContextImpl) pc);
		}
		
		
	}

	public void reinitialize(PageContextImpl pc) {
		if(type!=Config.SCOPE_STANDARD) return;
		Client cs = pc.clientScopeEL();
//		print.ln("standard");
		if(pc.getConfig().mergeFormAndURL()) {
            scopes=new Collection[cs==null?3:4]; 
            scopes[0]=pc.cgiScope();
            scopes[1]=pc.formScope();
            scopes[2]=pc.cookieScope();
            if(cs!=null)scopes[3]=cs;
		}
		else {
            scopes=new Collection[cs==null?4:5]; 
            scopes[0]=pc.cgiScope();
            scopes[1]=pc.urlScope();
            scopes[2]=pc.formScope();
            scopes[3]=pc.cookieScope();
            if(cs!=null)scopes[4]=cs;
		}
	}


	/**
	 * @see railo.runtime.type.Scope#release()
	 */
	public void release() {
		isInit=false;
		argument=null;
		local=null;
		variable=null;
		scopes=null;
		checkArguments=false;
		localAlways=false;
		if(allowImplicidQueryCall)qryStack.clear();
		
		
		//threads=null;
		//hasThreads=false;
	}

	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCoppy) {
		if(checkArguments)
			return local.duplicate(deepCoppy);
		return variable.duplicate(deepCoppy);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
        return get(key,null)!=null;
	}

    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct to String",
          "Use Build-In-Function \"serialize(Struct):String\" to create a String from Struct");
    }
    
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return defaultValue;
	}

    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct to a boolean value");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }


    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct to a number value");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }


    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct to a Date");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws ExpressionException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a boolean value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a DateTime Object");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a numeric value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a String");
	}

    /**
     * @see railo.runtime.type.scope.Undefined#setVariableScope(railo.runtime.type.Scope)
     */
    public void setVariableScope(Scope scope) {
    	variable=scope;
    }

    /**
     * @see railo.runtime.type.Scope#getType()
     */
    public int getType() {
        return SCOPE_UNDEFINED;
    }

    /**
     * @see railo.runtime.type.Scope#getTypeAsString()
     */
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
}