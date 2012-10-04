package railo.runtime.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Node;

import railo.runtime.exp.CasterException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.type.ForEachIteratorable;
import railo.runtime.type.ObjectWrap;
import railo.runtime.type.it.EnumAsIt;
import railo.runtime.type.it.ForEachQueryIterator;
import railo.runtime.type.wrap.MapAsStruct;

public class ForEachUtil {

	public static Iterator toIterator(Object o) throws PageException {
		
		
		if(o instanceof ForEachIteratorable) 
			return ((ForEachIteratorable)o).getIterator();
			
		else if(o instanceof Node)return XMLCaster.toXMLStruct((Node)o,false).getIterator();
        else if(o instanceof Map) {
            return MapAsStruct.toStruct((Map)o,true).getIterator();
        }
        else if(o instanceof ObjectWrap) {
            return toIterator(((ObjectWrap)o).getEmbededObject());
        }
        else if(Decision.isArray(o)) {
            return Caster.toArray(o).getIterator();
        }
        else if(o instanceof Iterator) {
            return (Iterator)o;
        }
        else if(o instanceof Enumeration) {
            return new EnumAsIt((Enumeration)o);
        }
		
        throw new CasterException(o,"collection");
	}
	
	public static void reset(Iterator it) throws PageException {
		
		if(it instanceof ForEachQueryIterator) {
			((ForEachQueryIterator)it).reset();
		}
	}

}
