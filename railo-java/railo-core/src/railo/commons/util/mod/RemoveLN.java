package railo.commons.util.mod;

import java.io.BufferedReader;
import java.io.IOException;

import railo.aprint;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.ResourcesImpl;

public class RemoveLN {
	public static void main(String[] args) throws IOException {
		ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
		Resource res = frp.getResource("/Users/mic/Projects/Railo/Source/railo/railo-java/railo-core/src/railo/commons/util/mod/SyncMap");
		BufferedReader r = IOUtil.toBufferedReader(IOUtil.getReader(res, null));
		String line;
		StringBuilder sb=new StringBuilder();
		while((line=r.readLine())!=null){
			sb.append(line.substring(5));
			sb.append('\n');
		}
		aprint.e(sb);
		
	}
}
