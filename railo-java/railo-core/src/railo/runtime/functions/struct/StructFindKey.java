/**
 * Implements the Cold Fusion Function structfindkey
 */
package railo.runtime.functions.struct;

import java.util.Iterator;
import java.util.Map.Entry;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.scope.Argument;

public final class StructFindKey implements Function {
	
	public static Array call(PageContext pc , railo.runtime.type.Struct struct, String value) throws PageException {
		return _call(pc,struct,value,false);
	}
    public static Array call(PageContext pc , Struct struct, String value, String scope) throws PageException {
        // Scope
        boolean all=false;
        if(scope.equalsIgnoreCase("one")) all=false;
        else if(scope.equalsIgnoreCase("all")) all=true;
        else throw new FunctionException(pc,"structFindValue",3,"scope","invalid scope definition ["+scope+"], valid scopes are [one, all]");
        return _call(pc,struct,value,all);
    }
    private static Array _call(PageContext pc , Struct struct, String value, boolean all) throws PageException {
        Array array=new ArrayImpl();
        getValues(array,struct,value,all,"");
        return array;
    }
    /**
     * @param coll
     * @param value
     * @param all
     * @param buffer
     * @return
     * @throws PageException
     */
    private static boolean getValues(Array array,Collection coll, String value, boolean all, String path) throws PageException {
        //Collection.Key[] keys=coll.keys();
    	Iterator<Entry<Key, Object>> it = coll.entryIterator();
    	Entry<Key, Object> e;
        boolean abort=false;
        Collection.Key key;
        
        while(it.hasNext()) {
        	e = it.next();
            if(abort)break;
            key=e.getKey();
            Object o=e.getValue();

            // matching value  (this function search first for base)
            if(key.getString().equalsIgnoreCase(value)) {
                Struct sct=new StructImpl();
                
	            sct.setEL(KeyImpl.VALUE,o);
                sct.setEL(KeyImpl.PATH,createKey(coll,path,key));
                sct.setEL(KeyImpl.OWNER,coll);
                array.append(sct);
                if(!all)abort=true;
            }
            
            // Collection
            if(!abort && o instanceof Collection) {
                abort=getValues(array,((Collection)o), value, all, createKey(coll,path,key));
            }
        }
        
        return abort;
    }
	static String createKey(Collection coll,String path,Collection.Key key) {
		StringBuffer p=new StringBuffer(path.toString());
        if(isArray(coll)){
        	p.append('[').append(key.getString()).append(']');
        }
        else{
        	p.append('.').append(key.getString());
        }
		return p.toString();
	}
	static boolean isArray(Collection coll) {
		return coll instanceof Array && !(coll instanceof Argument);
	}
}