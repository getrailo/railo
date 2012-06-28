package railo.runtime.util;

import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Node;

import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.type.Collection;
import railo.runtime.type.ObjectWrap;
import railo.runtime.type.Query;
import railo.runtime.type.it.ForEachQueryIterator;
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.wrap.MapAsStruct;

public class ForEachUtil {

	public static Iterator toIterator(Object o) throws PageException {
		if(o instanceof Collection) {
			if(o instanceof Query) {
				return new ForEachQueryIterator((Query)o, ThreadLocalPageContext.get().getId());
			}
			return ((Collection)o).keyIterator();
		}
        else if(o instanceof Node)return XMLCaster.toXMLStruct((Node)o,false).keysAsStringIterator();
        else if(o instanceof Map) {
            return MapAsStruct.toStruct((Map)o,true).keysAsStringIterator();
        }
        else if(o instanceof ObjectWrap) {
            return toIterator(((ObjectWrap)o).getEmbededObject());
        }
        else if(Decision.isArray(o)) {
            return Caster.toArray(o).valueIterator();
        }
        throw new CasterException(o,"collection");
	}
	
	public static void reset(Iterator it) throws PageException {
		
		if(it instanceof ForEachQueryIterator) {
			((ForEachQueryIterator)it).reset();
		}
	}

}
