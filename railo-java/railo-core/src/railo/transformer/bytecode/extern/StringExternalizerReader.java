package railo.transformer.bytecode.extern;

import java.io.IOException;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;

public class StringExternalizerReader {

	private String str;

	public StringExternalizerReader(Resource res) throws IOException {
		str=IOUtil.toString(res, "UTF-8");
	}

	public String read(int from, int to){
		return str.substring(from,to+1);
	}
	
	
}
