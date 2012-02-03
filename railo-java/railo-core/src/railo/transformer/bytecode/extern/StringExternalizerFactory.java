package railo.transformer.bytecode.extern;

import java.io.IOException;
import java.util.Map;

import railo.commons.io.res.Resource;
import railo.commons.lang.SoftHashMap;

public class StringExternalizerFactory {
	private static Map<Resource,StringExternalizerReader> readers=new SoftHashMap();
	
	
	public static StringExternalizerReader getReader(Resource res) throws IOException {
		StringExternalizerReader reader = readers.get(res);
		if(reader==null) {
			reader=new StringExternalizerReader(res);
			readers.put(res, reader);
		}
		return reader;
	}
}
