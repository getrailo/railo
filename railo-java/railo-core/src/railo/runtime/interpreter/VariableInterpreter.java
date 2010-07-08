package railo.runtime.interpreter;

import railo.commons.lang.ParserString;
import railo.commons.lang.StringList;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Scope;
import railo.runtime.type.ref.VariableReference;
import railo.runtime.type.scope.ScopeSupport;

/**
 * Class to check and interpret Variable Strings
 */
public final class VariableInterpreter {
    
	private static final Object NULL = new Object();

	/**
	 * reads a subelement from a struct
	 * @param pc
	 * @param collection
	 * @param var
	 * @return matching Object
	 * @throws PageException
	 */
	public static Object getVariable(PageContext pc, Collection collection,String var) throws PageException {			
	    StringList list = parse(pc,new ParserString(var));
        if(list==null) throw new ExpressionException("invalid variable declaration ["+var+"]");
        
        while(list.hasNextNext()) {
            collection=Caster.toCollection(collection.get(list.next()));
        }
        return collection.get(list.next());
	}
	

	public static Object getVariableEL(PageContext pc, Collection collection,String var) {			
	    StringList list = parse(pc,new ParserString(var));
        if(list==null) return null;
       
        while(list.hasNextNext()) {
        	collection=Caster.toCollection(collection.get(list.next(),null),null);
        	if(collection==null) return null;
        }
        return collection.get(list.next(),null);
	}
    
    /**
	 * get a variable from page context
	 * @param pc Page Context
	 * @param var variable string to get value to
	 * @return the value
     * @throws PageException
	 */
	public static Object getVariable(PageContext pc,String var) throws PageException {
        StringList list = parse(pc,new ParserString(var));
        if(list==null) throw new ExpressionException("invalid variable declaration ["+var+"]");
        
		int scope=scopeString2Int(list.next());
		Object coll =null; 
		if(scope==Scope.SCOPE_UNDEFINED) {
		    coll=pc.undefinedScope().get(list.current());
		}
		else {
		    coll=pc.scope(scope);
		}
		
		while(list.hasNext()) {
			coll=pc.getVariableUtil().get(pc,coll,list.next());
		}
		return coll;
    }
	
    /**
	 * get a variable from page context
	 * @param pc Page Context
	 * @param var variable string to get value to
	 * @return the value
	 */
	public static Object getVariableEL(PageContext pc,String var) {
        StringList list = parse(pc,new ParserString(var));
        if(list==null) return null;
        
		int scope=scopeString2Int(list.next());
		Object coll =null; 
		if(scope==Scope.SCOPE_UNDEFINED) {
		    coll=pc.undefinedScope().get(list.current(),null);
		    if(coll==null) return null;
		}
		else {
		    try {
                coll=pc.scope(scope);
            } catch (PageException e) {
                return null;
            }
		}
		
		while(list.hasNext()) {
			coll=pc.getVariableUtil().get(pc,coll,list.next(),null);
			if(coll==null) return null;
		}
		return coll;
    }
	
	/**
	 * return a variable reference by string syntax ("scopename.key.key" -> "url.name")
	 * a variable reference, references to variable, to modifed it, with global effect.
	 * @param pc
	 * @param var variable name to get
	 * @return variable as Reference
	 * @throws PageException
	 */
	public static VariableReference getVariableReference(PageContext pc,String var) throws PageException { 
	    StringList list = parse(pc,new ParserString(var));
        if(list==null) throw new ExpressionException("invalid variable declaration ["+var+"]");
        
		if(list.size()==1) {
			return new VariableReference(pc.undefinedScope(),list.next()); 
		}
		int scope=scopeString2Int(list.next());
		
		Object coll=(scope==Scope.SCOPE_UNDEFINED)?pc.touch(pc.undefinedScope(),list.current()):pc.scope(scope);
		while(list.hasNextNext()) {
			coll=pc.touch(coll,list.next());
		}

		if(!(coll instanceof Collection))
			throw new ExpressionException("invalid variable ["+var+"]");
		return new VariableReference((Collection)coll,list.next());
	} 
	
	/**
	 * sets a variable to page Context
	 * @param pc pagecontext of the new variable
	 * @param var String of variable definition
	 * @param value value to set to variable
	 * @return value setted
	 * @throws PageException
	 */
	public static Object setVariable(PageContext pc,String var, Object value) throws PageException {			
	    StringList list = parse(pc,new ParserString(var));
        if(list==null) throw new ExpressionException("invalid variable declaration ["+var+"]");

		if(list.size()==1) {
			return pc.undefinedScope().set(list.next(),value);
		}
		
		// min 2 elements
		int scope=scopeString2Int(list.next());
		Object coll=(scope==Scope.SCOPE_UNDEFINED)?pc.touch(pc.undefinedScope(),list.current()):pc.scope(scope);
		
		while(list.hasNextNext()) {
		    coll=pc.touch(coll,list.next());
		}
		return pc.set(coll,list.next(),value);
	}
	
	/**
	 * removes a variable eith matching name from page context
	 * @param pc
	 * @param var
	 * @return has removed or not
	 * @throws PageException
	 */
	public static Object removeVariable(PageContext pc,String var) throws PageException {	
	    //print.ln("var:"+var);
	    StringList list = parse(pc,new ParserString(var));
        if(list==null) throw new ExpressionException("invalid variable declaration ["+var+"]");
        
		if(list.size()==1) {
			return pc.undefinedScope().remove(KeyImpl.init(list.next()));
		}
        
		int scope=scopeString2Int(list.next());
		Object coll = (scope==Scope.SCOPE_UNDEFINED)?pc.undefinedScope().get(list.current()):pc.scope(scope);
		
		while(list.hasNextNext()) {
		    coll=pc.get(coll,list.next());
		}
		return Caster.toCollection(coll).remove(KeyImpl.init(list.next()));
	}

	

	/**
	 * check if a variable is defined in Page Context
	 * @param pc PageContext to check
	 * @param var variable String
	 * @return exists or not
	 */
	public static boolean isDefined(PageContext pc,String var) {
		StringList list = parse(pc,new ParserString(var));
		if(list==null) return false;
        try {
			int scope=scopeString2Int(list.next());
			Object coll =NULL; 
			if(scope==Scope.SCOPE_UNDEFINED) {
				coll=pc.undefinedScope().get(list.current(),null);
				if(coll==null)return false;
			}
			else coll=pc.scope(scope);
			
			while(list.hasNext()) {
				coll=pc.getVariableUtil().getCollection(pc,coll,list.next(),null);
				if(coll==null)return false;
			}
		} catch (PageException e) {
	        return false;
	    }
		return true;
    }
		

	
	
	/*
	public static boolean isDefined(PageContext pc,String var) {
        StringList list = parse(pc,new ParserString(var));
        if(list==null) return false;
        
		int scope=scopeString2Int(list.next());
		Object coll =NULL; 
		if(scope==Scope.SCOPE_UNDEFINED) {
		    coll=pc.undefinedScope().get(list.current(),NULL);
		    if(coll==NULL) return false;
		}
		else {
		    try {
                coll=pc.scope(scope);
            } catch (PageException e) {
                return false;
            }
		}
		
		while(list.hasNext()) {
			coll=pc.getVariableUtil().get(pc,coll,list.next(),NULL);
			//print.out(coll);
			if(coll==NULL) return false;
		}
       
		return true;
    }
	 */
	
    
    /**
     * parse a Literal variable String and return result as String List
     * @param pc Page Context
     * @param ps ParserString to read
     * @return Variable Definition in a String List
     */
    private static StringList parse(PageContext pc,ParserString ps) {
        String id=readIdentifier(ps);
        if(id==null)return null;
        StringList list=new StringList(id);
        CFMLExpressionInterpreter interpreter=null;
        
        while(true) {
            if(ps.forwardIfCurrent('.')) {
	            id=readIdentifier(ps);
	            if(id==null)return null;
	            list.add(id);
            }
            else if(ps.forwardIfCurrent('[')) {
                if(interpreter==null)interpreter=new CFMLExpressionInterpreter();
                try {
                    list.add(Caster.toString(interpreter.interpretPart(pc,ps)));
                } catch (PageException e) {
                    return null;
                }
                if(!ps.forwardIfCurrent(']')) return null;
                ps.removeSpace();
            }
            else break;
        }
        if(ps.isValidIndex()) return null;
        list.reset();
        return list;
    }
    
    public static StringList parse(String var) {
    	ParserString ps = new ParserString(var);
        String id=readIdentifier(ps);
        if(id==null)return null;
        StringList list=new StringList(id);
        
        while(true) {
            if(ps.forwardIfCurrent('.')) {
	            id=readIdentifier(ps);
	            if(id==null)return null;
	            list.add(id);
            }
            else break;
        }
        if(ps.isValidIndex()) return null;
        list.reset();
        return list;
    }
    
    
	/**
	 * translate a int scope type to String type
	 * @param type int type
	 * @return matching String Type
	 */
	public static String scopeInt2String(int type) {
		switch(type) {
			case Scope.SCOPE_APPLICATION:	return "application";
			case Scope.SCOPE_ARGUMENTS:		return "arguments";
			case Scope.SCOPE_CGI:			return "cgi";
			case Scope.SCOPE_COOKIE:		return "cookie";
			case Scope.SCOPE_CLIENT:		return "client";
			case Scope.SCOPE_FORM:			return "form";
			case Scope.SCOPE_REQUEST:		return "request";
			case Scope.SCOPE_SESSION:		return "session";
			case Scope.SCOPE_SERVER:		return "server";
			case Scope.SCOPE_URL:			return "url";
			case Scope.SCOPE_VARIABLES:		return "variables";
			case Scope.SCOPE_CLUSTER:		return "cluster";
			case Scope.SCOPE_LOCAL:			return "local";// LLL
		}
		return null;
	}

	/**
	 * translate a string type definition to its int representation
	 * @param type type to translate
	 * @return int representation matching to given string
	 */
	public static int scopeString2Int(String type) {
		type=StringUtil.toLowerCase(type);
		char c=type.charAt(0);
		if('a'==c) {
			if("application".equals(type)) 		return Scope.SCOPE_APPLICATION;
			else if("arguments".equals(type))	return Scope.SCOPE_ARGUMENTS;
		}
		else if('c'==c) {
			if("cgi".equals(type))				return Scope.SCOPE_CGI;
			if("cookie".equals(type))			return Scope.SCOPE_COOKIE;
			if("client".equals(type))			return Scope.SCOPE_CLIENT;
			if("cluster".equals(type))			return Scope.SCOPE_CLUSTER;
		}
		else if('f'==c) {
			if("form".equals(type))				return Scope.SCOPE_FORM;
		}
		else if('l'==c) {
			if("local".equals(type))				return Scope.SCOPE_LOCAL;// LLL
		}
		else if('r'==c) {
			if("request".equals(type))			return Scope.SCOPE_REQUEST;
		}
		else if('s'==c) {
			if("session".equals(type))			return Scope.SCOPE_SESSION;
			if("server".equals(type))			return Scope.SCOPE_SERVER;
		}
		else if('u'==c) {
			if("url".equals(type))				return Scope.SCOPE_URL;
		}
		else if('v'==c) {
			if("variables".equals(type))		return Scope.SCOPE_VARIABLES;
		}
		return Scope.SCOPE_UNDEFINED;
	}
	
	public static int scopeKey2Int(Collection.Key type) {
		char c=type.lowerCharAt(0);
		if('a'==c) {
			if(ScopeSupport.APPLICATION.equalsIgnoreCase(type)) 		return Scope.SCOPE_APPLICATION;
			else if(ScopeSupport.ARGUMENTS.equalsIgnoreCase(type))	return Scope.SCOPE_ARGUMENTS;
		}
		else if('c'==c) {
			if(ScopeSupport.CGI.equalsIgnoreCase(type))				return Scope.SCOPE_CGI;
			if(ScopeSupport.COOKIE.equalsIgnoreCase(type))			return Scope.SCOPE_COOKIE;
			if(ScopeSupport.CLIENT.equalsIgnoreCase(type))			return Scope.SCOPE_CLIENT;
			if(ScopeSupport.CLUSTER.equalsIgnoreCase(type))			return Scope.SCOPE_CLUSTER; 
		}
		else if('f'==c) {
			if(ScopeSupport.FORM.equalsIgnoreCase(type))				return Scope.SCOPE_FORM;
		}
		else if('r'==c) {
			if(ScopeSupport.REQUEST.equalsIgnoreCase(type))			return Scope.SCOPE_REQUEST;
		}
		else if('s'==c) {
			if(ScopeSupport.SESSION.equalsIgnoreCase(type))			return Scope.SCOPE_SESSION;
			if(ScopeSupport.SERVER.equalsIgnoreCase(type))			return Scope.SCOPE_SERVER;
		}
		else if('u'==c) {
			if(ScopeSupport.URL.equalsIgnoreCase(type))				return Scope.SCOPE_URL;
		}
		else if('v'==c) {
			if(ScopeSupport.VARIABLES.equalsIgnoreCase(type))		return Scope.SCOPE_VARIABLES;
		}
		return Scope.SCOPE_UNDEFINED;
	}
    
    private static String readIdentifier(ParserString ps) {
        
        ps.removeSpace();
        if(ps.isAfterLast())return null;
        int start=ps.getPos();
        if(!isFirstVarLetter(ps.getCurrentLower())) return null;
        ps.next();
        
        while(ps.isValidIndex()) {
            if(isVarLetter(ps.getCurrentLower()))ps.next();
            else break;
        }
        ps.removeSpace();
        return ps.substringLower(start,ps.getPos()-start);
    }


    private static boolean isFirstVarLetter(char c) {
        return (c>='a' && c<='z') || c=='_' || c=='$';
    }

    private static boolean isVarLetter(char c) {
        return (c>='a' && c<='z') || (c>='0' && c<='9') || c=='_' || c=='$';
    }
    
}