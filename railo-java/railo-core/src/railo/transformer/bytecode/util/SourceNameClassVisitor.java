package railo.transformer.bytecode.util;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.lang.Pair;
import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.type.util.ListUtil;

public class SourceNameClassVisitor extends ClassVisitor {

	private String sourceName;
	private String sourcePath;
	private ExtensionResourceFilter filter;

	public SourceNameClassVisitor(Config config, int arg0, boolean onlyCFC) {
		super(arg0);
		if(onlyCFC) {
			filter = new ExtensionResourceFilter(new String[]{config.getComponentExtension()},true,true);
		}
		else {
			filter = new ExtensionResourceFilter(config.getAllExtensions(),true,true);
			filter.addExtension(config.getComponentExtension());
		}
		
	}

	@Override
	public void visitSource(String source, String debug) {
		super.visitSource(source, debug);
		if(!StringUtil.isEmpty(source)){
			String name=ListUtil.last(source, "/\\");
			if(filter.accept(name)) {
				this.sourceName=name;
				this.sourcePath=source;
			}
		}
	}


    public static Pair<String,String> getSourceNameAndPath(Config config,Class clazz, boolean onlyCFC) throws IOException {
    		String name = "/"+clazz.getName().replace('.', '/')+".class";
	    	InputStream in=clazz.getResourceAsStream(name);
	        ClassReader classReader=new ClassReader(in);
	        SourceNameClassVisitor visitor = new SourceNameClassVisitor(config,4,onlyCFC);
	        classReader.accept(visitor, 0);
	        if(visitor.sourceName==null) return null;
	        return new Pair<String,String>(visitor.sourceName,visitor.sourcePath);
    	
    }

}