package railo.runtime.net.amf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import railo.commons.lang.CFTypes;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.ComponentWrap;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.UDF;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.wrap.ArrayAsList;
import flex.messaging.io.amf.ASObject;


/**
 * Cast a CFML object to AMF Objects and the other way
 */
public final class ModernAMFCaster extends ClassicAMFCaster {

	private boolean doProperties=true;
	private boolean doGetters=true;
	private boolean doRemoteValues=true;
	
	@Override
	public void init(Map arguments){
		super.init(arguments);
		
		String strValues = Caster.toString(arguments.get("component-values"),null);
		if(!StringUtil.isEmpty(strValues)){
			doProperties = railo.runtime.type.util.ListUtil.listFindNoCase(strValues, "properties")!=-1;
			doGetters=railo.runtime.type.util.ListUtil.listFindNoCase(strValues, "getters")!=-1;
			doRemoteValues=railo.runtime.type.util.ListUtil.listFindNoCase(strValues, "remote-values")!=-1;
		}
	}

	public Object toAMFObject(Object cf) throws PageException {
		if(cf instanceof List) return toAMFObject((List)cf);
		if(cf instanceof Array) return toAMFObject(ArrayAsList.toList((Array)cf));
		if(cf instanceof Component)	return toAMFObject((Component)cf);
		if(cf instanceof Query) return super.toAMFObject((Query)cf);
		if(cf instanceof Map) return super.toAMFObject((Map)cf);
		if(cf instanceof Object[]) return toAMFObject((Object[])cf);
		
		return cf;
	}
	

	protected ASObject toAMFObject(Component cfc) throws PageException {
		// add properties
		ASObject aso = doProperties?super.toAMFObject(cfc):new ASObject();
		ComponentWrap cw=null;
		if(cfc instanceof ComponentAccess)cw=ComponentWrap.toComponentWrap(Component.ACCESS_REMOTE,cfc);
		
		Iterator it = cfc.entrySet().iterator();
        Map.Entry entry;
        Object v;
        Collection.Key k;
        UDF udf;
        String name;
        while(it.hasNext()) {
            entry=(Entry) it.next();
            k=KeyImpl.toKey(entry.getKey());
            v=entry.getValue();
            
            // add getters
            if(v instanceof UDF){
            	if(!doGetters) continue;
            	udf=(UDF) v;
            	name=udf.getFunctionName();
            	if(!StringUtil.startsWithIgnoreCase(name, "get"))continue;
            	if(udf.getReturnType()==CFTypes.TYPE_VOID) continue;
            	if(udf.getFunctionArguments().length>0) continue;
            	
            	try {
					v=cfc.call(ThreadLocalPageContext.get(), name, ArrayUtil.OBJECT_EMPTY);
				} catch (PageException e) {
					continue;
				}
            	name=name.substring(3);
            	
            	aso.put(toString(name,forceCFCLower), toAMFObject(v));
            }
            
            // add remote data members
            if(cw!=null && doRemoteValues){
            	v=cw.get(k,null);
            	if(v!=null)aso.put(toString(k,forceCFCLower), toAMFObject(v));
            }
        }
        return aso;
	}
    
	protected Object toAMFObject(List list) throws PageException {
		list = Duplicator.duplicateList(list, false);
        ListIterator it = list.listIterator();
        while(it.hasNext()) {
        	list.set(it.nextIndex(),toAMFObject(it.next()));
        }
        return list;
    }
	
	protected Object toAMFObject(Object[] src) throws PageException {
		ArrayList list=new ArrayList();
		for(int i=0;i<src.length;i++){
			list.add(toAMFObject(src[i]));
		}
		return list;
    }
}