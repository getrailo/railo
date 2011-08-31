package railo.transformer.bytecode.extern;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import railo.print;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourcesImpl;
import railo.transformer.bytecode.extern.StringExternalizerWriter.Range;

public class StringExternalizerWriter {
	
	private StringBuilder sb=new StringBuilder();
	private int offset=0;
	
	public Range write(String str){
		sb.append(str);
		return new Range(offset,(offset+=str.length())-1);
	} 
	
	public void writeOut(Resource res) throws IOException{
		IOUtil.write(res, sb.toString(),"UTF-8",false);
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
		
		StringExternalizerWriter ext=new StringExternalizerWriter();
		Range r1 = ext.write("hallo");
		Range r2 = ext.write("peter");
		Range r3 = ext.write("müller");
		
		ext.writeOut(res);
		
		StringExternalizerReader reader=new StringExternalizerReader(res);
		print.o(r1);
		print.o(r2);
		print.o(r3);
		print.o(reader.read(r1.from, r1.to));
		print.o(reader.read(r2.from, r2.to));
		print.o(reader.read(r3.from, r3.to));
		
		
		
	}
}
