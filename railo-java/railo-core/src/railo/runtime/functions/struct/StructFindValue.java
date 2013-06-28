/**
 * Implements the CFML Function structfindvalue
 */
package railo.runtime.functions.struct;

import java.util.Iterator;
import java.util.Map.Entry;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;

public final class StructFindValue extends BIF {

	private static final long serialVersionUID = 1499023912262918840L;

	public static Array call(PageContext pc , railo.runtime.type.Struct struct, String value) throws PageException {
		return call(pc,struct,value,"one");
	}
	public static Array call(PageContext pc , Struct struct, String value, String scope) throws PageException {
		// Scope
	    boolean all=false;
	    if(scope.equalsIgnoreCase("one")) all=false;
	    else if(scope.equalsIgnoreCase("all")) all=true;
	    else throw new FunctionException(pc,"structFindValue",3,"scope","invalid scope definition ["+scope+"], valid scopes are [one, all]");
	    
	    Array array=new ArrayImpl();
	    getValues(pc,array,struct,value,all,"");
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
    private static boolean getValues(PageContext pc,Array array,Collection coll, String value, boolean all, String path) throws PageException {
        //Key[] keys = coll.keys();
        boolean abort=false;
        Key key;
        Iterator<Entry<Key, Object>> it = coll.entryIterator();
        Entry<Key, Object> e;
        loop:while(it.hasNext()) {
        	e = it.next();
            if(abort)break loop;
            key=e.getKey();
            Object o=e.getValue();
            
            // Collection (this function search first for sub)
            if(o instanceof Collection) {
                abort=getValues(pc,array,((Collection)o), value, all, StructFindKey.createKey(coll, path, key));
                
            }
            // matching value
            if(!abort && !StructFindKey.isArray(coll)){
            String target=Caster.toString(o,null);
            if((target!=null && target.equalsIgnoreCase(value)) /*|| (o instanceof Array && checkSub(array,((Array)o),value,all,path,abort))*/) {
                Struct sct=new StructImpl();
	                sct.setEL(KeyConstants._key,key.getString());
		                sct.setEL(KeyConstants._path,StructFindKey.createKey(coll, path, key));
		                sct.setEL(KeyConstants._owner,coll);
                array.append(sct);
                if(!all)abort=true;
            }
        }
        }
        
        return abort;
    }
    
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==3) return call(pc,Caster.toStruct(args[0]),Caster.toString(args[1]),Caster.toString(args[2]));
		return call(pc,Caster.toStruct(args[0]),Caster.toString(args[1]));
	}
}