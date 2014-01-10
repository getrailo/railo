package railo.runtime.net.amf;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Node;

import railo.commons.lang.CFTypes;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.ComponentWrap;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSourceImpl;
import railo.runtime.component.ComponentLoader;
import railo.runtime.component.Property;
import railo.runtime.config.ConfigWeb;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.op.Duplicator;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.wrap.ArrayAsList;
import railo.runtime.type.wrap.ListAsArray;
import railo.runtime.type.wrap.MapAsStruct;
import flex.messaging.io.amf.ASObject;


/**
 * Cast a CFML object to AMF Objects and the other way
 */
public class ClassicAMFCaster implements AMFCaster {

	
	
	private static final Collection.Key REMOTING_FETCH = KeyImpl.intern("remotingFetch");

	//private static ClassicAMFCaster singelton;
	
	protected boolean forceCFCLower;
	protected boolean forceStructLower;
	protected boolean forceQueryLower;

	private int methodAccessLevel;

	@Override
	public void init(Map arguments){
		forceCFCLower=Caster.toBooleanValue(arguments.get("force-cfc-lowercase"),false);
		forceQueryLower=Caster.toBooleanValue(arguments.get("force-query-lowercase"),false);
		forceStructLower=Caster.toBooleanValue(arguments.get("force-struct-lowercase"),false);
		// method access level
		String str=Caster.toString(arguments.get("method-access-level"),"remote");
		if("private".equalsIgnoreCase(str))methodAccessLevel=Component.ACCESS_PRIVATE;
		else if("package".equalsIgnoreCase(str))methodAccessLevel=Component.ACCESS_PACKAGE;
		else if("public".equalsIgnoreCase(str))methodAccessLevel=Component.ACCESS_PUBLIC;
		else methodAccessLevel=Component.ACCESS_REMOTE;
		
	}
	

	@Override
	public Object toAMFObject(Object cf) throws PageException {
		if(cf instanceof Node) return toAMFObject((Node)cf);
		if(cf instanceof List) return toAMFObject((List)cf);
		if(cf instanceof Array) return toAMFObject(ArrayAsList.toList((Array)cf));
		if(cf instanceof Component)	return toAMFObject((Component)cf);
		if(cf instanceof Query) return toAMFObject((Query)cf);
		if(cf instanceof Image) return toAMFObject((Image)cf);
		if(cf instanceof Map) return toAMFObject((Map)cf);
		if(cf instanceof Object[]) return toAMFObject((Object[])cf);
		
		return cf;
	}

	protected Object toAMFObject(Node node) {
		return XMLCaster.toRawNode(node);
	}
	protected Object toAMFObject(Query query) throws PageException {
		List<ASObject> result = new ArrayList<ASObject>();
		int len=query.getRecordcount();
        Collection.Key[] columns=CollectionUtil.keys(query);
    	ASObject row;
        for(int r=1;r<=len;r++) {
        	result.add(row = new ASObject());
            for(int c=0;c<columns.length;c++) {
                row.put(toString(columns[c],forceQueryLower), toAMFObject(query.getAt(columns[c],r)) ); 
            }
        }
		return result;
	}
	
	protected Object toAMFObject(Image img) throws PageException {
		try{
			return img.getImageBytes(null);
		}
		catch(Throwable t){
			return img.getImageBytes("png");
		}
	}

	protected ASObject toAMFObject(Component cfc) throws PageException {
		ASObject aso = new ASObject();
		aso.setType(cfc.getCallName());
		
		
		Component c=cfc;
		if(cfc instanceof ComponentAccess)c=ComponentWrap.toComponentWrap(methodAccessLevel,cfc);
		

		Property[] prop = cfc.getProperties(false);
		Object v; UDF udf;
    	if(prop!=null)for(int i=0;i<prop.length;i++) {
    		boolean remotingFetch = Caster.toBooleanValue(prop[i].getDynamicAttributes().get(REMOTING_FETCH,Boolean.TRUE),true);
    		if(!remotingFetch) continue;
    		
    		v=cfc.get(prop[i].getName(),null);
    		if(v==null){
    			v=c.get("get"+prop[i].getName(),null);
	    		if(v instanceof UDF){
	            	udf=(UDF) v;
	            	if(udf.getReturnType()==CFTypes.TYPE_VOID) continue;
	            	if(udf.getFunctionArguments().length>0) continue;
	            	
	            	try {
						v=c.call(ThreadLocalPageContext.get(), udf.getFunctionName(), ArrayUtil.OBJECT_EMPTY);
					} catch (PageException e) {
						continue;
					}
	            }
    		}
    		
    		aso.put(toString(prop[i].getName(),forceCFCLower), toAMFObject(v));
    	}
    	return aso;
	}
    
	protected Object toAMFObject(Map map) throws PageException {
    	if(forceStructLower && map instanceof Struct) toAMFObject((Struct)map);
    	
    	map=(Map) Duplicator.duplicate(map,false);
    	Iterator it = map.entrySet().iterator();
        Map.Entry entry;
        while(it.hasNext()) {
            entry=(Entry) it.next();
            entry.setValue(toAMFObject(entry.getValue()));
        }
        return MapAsStruct.toStruct(map, false);
    }
    
	protected Object toAMFObject(Struct src) throws PageException {
    	Struct trg=new StructImpl();
    	//Key[] keys = src.keys();
    	Iterator<Entry<Key, Object>> it = src.entryIterator();
    	Entry<Key, Object> e;
        while(it.hasNext()) {
        	e = it.next();
            trg.set(KeyImpl.init(toString(e.getKey(),forceStructLower)), toAMFObject(e.getValue()));
        }
        return trg;
    }
    
    
	
	protected Object toAMFObject(List list) throws PageException {
		Object[] trg=new Object[list.size()];
		ListIterator it = list.listIterator();
        
        while(it.hasNext()) {
        	trg[it.nextIndex()]=toAMFObject(it.next());
        }
        return trg;
    }
	
	protected Object toAMFObject(Object[] src) throws PageException {
		Object[] trg=new Object[src.length];
		for(int i=0;i<src.length;i++){
			trg[i]=toAMFObject(src[i]);
		}
		return trg;
    }
	

	@Override
	public Object toCFMLObject(Object amf) throws PageException {
		if(amf instanceof Node) return toCFMLObject((Node)amf);
		if(amf instanceof List) return toCFMLObject((List)amf);
		if(Decision.isNativeArray(amf)) {
			if(amf instanceof byte[]) return amf;
			if(amf instanceof char[]) return new String((char[])amf);
			return toCFMLObject(Caster.toNativeArray(amf));
		}
		if(amf instanceof ASObject) return toCFMLObject((ASObject)amf);
		if(amf instanceof Map) return toCFMLObject((Map)amf);
		if(amf instanceof Date) return new DateTimeImpl((Date)amf);
        if(amf == null) return "";
        
		return amf;
	}

	protected Object toCFMLObject(Node node) {
		return XMLCaster.toXMLStruct(node, true);
    }
	protected Object toCFMLObject(Object[] arr) throws PageException {
		Array trg=new ArrayImpl();
		for(int i=0;i<arr.length;i++){
			trg.setEL(i+1, toCFMLObject(arr[i]));
		}
		return trg;
    }
	
	protected Object toCFMLObject(List list) throws PageException {
        ListIterator it = list.listIterator();
        while(it.hasNext()) {
        	//arr.setE(it.nextIndex()+1, toCFMLObject(it.next()));
            list.set(it.nextIndex(),toCFMLObject(it.next()));
        }
        return ListAsArray.toArray(list);
    }

	protected Object toCFMLObject(Map map) throws PageException {
		Iterator it = map.entrySet().iterator();
        Map.Entry entry;
        while(it.hasNext()) {
            entry=(Entry) it.next();
            entry.setValue(toCFMLObject(entry.getValue()));
        }
        return MapAsStruct.toStruct(map, false);
    }
	
	protected Object toCFMLObject(ASObject aso) throws PageException {
		if(!StringUtil.isEmpty(aso.getType())){
			PageContext pc = ThreadLocalPageContext.get();
			ConfigWeb config = pc.getConfig();
			
				String name="/"+aso.getType().replace('.', '/')+".cfc";

				Page p = PageSourceImpl.loadPage(pc, ((PageContextImpl)pc).getPageSources(name), null) ;

				if(p==null)throw new ApplicationException("Could not find a Component with name ["+aso.getType()+"]");
				
				Component cfc = ComponentLoader.loadComponent(pc, p, p.getPageSource(), aso.getType(), false);
				ComponentWrap cw=ComponentWrap.toComponentWrap(config.getComponentDataMemberDefaultAccess(),cfc);
				
				Iterator it = aso.entrySet().iterator();
				Map.Entry entry;
				while(it.hasNext()){
					entry = (Entry) it.next();
					cw.set(KeyImpl.toKey(entry.getKey()), toCFMLObject(entry.getValue()));
				}
				return cfc;
			
			
		}
		return toCFMLObject((Map)aso);
    }
	
	protected String toString(Object key, boolean forceLower) {
		if(key instanceof Key) return toString((Key)key, forceLower);
		return toString(Caster.toString(key,""), forceLower);
	}
	
	protected String toString(Key key, boolean forceLower) {
		if(forceLower) return key.getLowerString();
		return key.getString();
	}
	
	protected String toString(String key, boolean forceLower) {
		if(forceLower) return key.toLowerCase();
		return key;
	}
}