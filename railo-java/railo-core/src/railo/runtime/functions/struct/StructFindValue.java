/**
 * Implements the Cold Fusion Function structfindvalue
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public final class StructFindValue implements Function {
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
        Key[] keys = coll.keys();
        //print.ln("->"+List.arrayToList(keys,","));
        boolean abort=false;
        
        
        Key key;
        loop:for(int i=0;i<keys.length;i++) {
            if(abort)break loop;
            key=keys[i];
            Object o=coll.get(key);
            
            // Collection (this function search first for sub)
            if(o instanceof Collection) {
                abort=getValues(pc,array,((Collection)o), value, all, StructFindKey.createKey(coll, path, key));
                
            }
            // matching value
            if(!abort && !StructFindKey.isArray(coll)){
            String target=Caster.toString(o,null);
            if((target!=null && target.equalsIgnoreCase(value)) /*|| (o instanceof Array && checkSub(array,((Array)o),value,all,path,abort))*/) {
                Struct sct=new StructImpl();
	                sct.setEL("key",key.getString());
		                sct.setEL("path",StructFindKey.createKey(coll, path, key));
		                sct.setEL("owner",coll);
                array.append(sct);
                if(!all)abort=true;
            }
        }
        }
        
        return abort;
    }
}