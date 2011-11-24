package railo.transformer.bytecode.extern;

import java.io.IOException;

import railo.aprint;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourcesImpl;
import railo.commons.io.res.util.ResourceUtil;

public class StringExternalizerWriter {
	
	private StringBuilder sb=new StringBuilder();
	private int offset=0;
	private Resource res;
	
	public StringExternalizerWriter(Resource res) throws IOException{
		this.res=res;
		if(res.exists())res.delete();
	}
	
	public Range write(String str){
		sb.append(str);
		return new Range(offset,(offset+=str.length())-1);
	} 
	
	public void writeOut() throws IOException{
		if(sb.length()>0)IOUtil.write(res, sb.toString(),"UTF-8",false);
	}
	
	public static class Range {

		public final int from;
		public final int to;

		public Range(int from, int to) {
			this.from=from;
			this.to=to;
		}
		public String toString(){
			return "from:"+from+";to:"+to+";";
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		Resource res = ResourcesImpl.getFileResourceProvider().getResource("/Users/mic/temp/externalize.txt");
		
		StringExternalizerWriter ext=new StringExternalizerWriter(res);
		Range r1 = ext.write("hallo");
		Range r2 = ext.write("peter");
		Range r3 = ext.write("müller");
		
		ext.writeOut();
		
		StringExternalizerReader reader=new StringExternalizerReader(res);
		aprint.o(r1);
		aprint.o(r2);
		aprint.o(r3);
		aprint.o(reader.read(r1.from, r1.to));
		aprint.o(reader.read(r2.from, r2.to));
		aprint.o(reader.read(r3.from, r3.to));
		
		
		
	}
}
