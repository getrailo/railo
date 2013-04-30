package railo.transformer.bytecode.util;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import railo.commons.lang.StringUtil;
import railo.runtime.type.util.ListUtil;

public class SourceNameClassVisitor extends ClassVisitor {

	private String sourceName;

	public SourceNameClassVisitor(int arg0) {
		super(arg0);
	}

	@Override
	public void visitSource(String source, String debug) {
		super.visitSource(source, debug);
		if(!StringUtil.isEmpty(source)){
			String name=ListUtil.last(source, "/\\");
			if(StringUtil.endsWithIgnoreCase(name, ".cfc")) {
				this.sourceName=name;
			}
		}
	}


    public static String getSourceName(Class clazz) throws IOException {
    		String name = "/"+clazz.getName().replace('.', '/')+".class";
	    	InputStream in=clazz.getResourceAsStream(name);
	        ClassReader classReader=new ClassReader(in);
	        SourceNameClassVisitor visitor = new SourceNameClassVisitor(4);
	        classReader.accept(visitor, 0);
	        return visitor.sourceName;
    	
    }

}