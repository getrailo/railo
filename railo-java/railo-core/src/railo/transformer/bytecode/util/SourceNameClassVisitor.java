/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.transformer.bytecode.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.type.util.ListUtil;

public class SourceNameClassVisitor extends ClassVisitor {



	//private String sourceName;
	//private String sourcePath;
	private ExtensionResourceFilter filter;
	private SourceInfo source;

	public SourceNameClassVisitor(Config config, int arg0, boolean onlyCFC) {
		super(arg0);
		if(onlyCFC) {
			filter = new ExtensionResourceFilter(new String[]{config.getCFCExtension()},true,true);
		}
		else {
			filter = new ExtensionResourceFilter(config.getCFMLExtensions(),true,true);
			filter.addExtension(config.getCFCExtension());
		}
		
	}

	@Override
	public void visitSource(String source, String debug) {
		super.visitSource(source, debug);
		if(!StringUtil.isEmpty(source)){
			
			String name=ListUtil.last(source, "/\\");
			
			if(filter.accept(name)) {
				// older than 4.2.1.008
				if(StringUtil.isEmpty(debug)) {
					this.source=new SourceInfo(name,source); // source is a relative path
				}
				else {
					//in that case source holds the absolute path
					String[] arr=ListUtil.listToStringArray(debug, ';');
					String str; int index;
					Map<String,String> map=new HashMap<String, String>();
					for(int i=0;i<arr.length;i++){
						str=arr[i].trim();
						index=str.indexOf(':');
						if(index==-1) map.put(str.toLowerCase(),"");
						else  map.put(str.substring(0,index).toLowerCase(),str.substring(index+1));
						
					}
					String rel = map.get("rel");
					String abs = map.get("abs");
					if(StringUtil.isEmpty(abs)) abs=source;
					
					this.source=new SourceInfo(name,rel,abs);
				}
			}
		}
	}


    public static SourceInfo getSourceInfo(Config config,Class clazz, boolean onlyCFC) throws IOException {
    		String name = "/"+clazz.getName().replace('.', '/')+".class";
	    	InputStream in=clazz.getResourceAsStream(name);
	        ClassReader classReader=new ClassReader(in);
	        SourceNameClassVisitor visitor = new SourceNameClassVisitor(config,4,onlyCFC);
	        classReader.accept(visitor, 0);
	        if(visitor.source==null || visitor.source.name==null) return null;
	        return visitor.source;
    	
    }

	public static class SourceInfo {

		public final String name;
		public final  String relativePath;
		public final  String absolutePath;
		
		public SourceInfo(String name,String relativePath) {
			this(name,relativePath,null);
		}
		
		public SourceInfo(String name,String relativePath,String absolutePath) {
			this.name=name;
			this.relativePath=relativePath;
			this.absolutePath=absolutePath;
		}
		
		public String toString(){
			return new StringBuilder("absolute-path:"+absolutePath+";relative-path:"+relativePath+";name:"+name).toString();
		}
	}
}