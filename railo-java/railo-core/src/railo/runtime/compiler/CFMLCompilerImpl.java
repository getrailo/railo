package railo.runtime.compiler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.runtime.PageSource;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Page;
import railo.transformer.cfml.tag.CFMLTransformer;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.util.AlreadyClassException;



/**
 * CFML Compiler compiles CFML source templates
 */
public final class CFMLCompilerImpl implements CFMLCompiler {
	
	private CFMLTransformer cfmlTransformer;
	
	
	/**
	 * Constructor of the compiler
	 * @param config
	 */
	public CFMLCompilerImpl() {
		cfmlTransformer=new CFMLTransformer();
	}
	
	/**
	 * @see railo.runtime.compiler.CFMLCompiler#compile(railo.runtime.config.ConfigImpl, railo.runtime.PageSource, railo.transformer.library.tag.TagLib[], railo.transformer.library.function.FunctionLib[], railo.commons.io.res.Resource, java.lang.String)
	 */
	public byte[] compile(ConfigImpl config,PageSource source, TagLib[] tld, FunctionLib[] fld, 
        Resource classRootDir, String className) throws TemplateException, IOException {
		//synchronized(source){
			//print.out("src:"+source.getDisplayPath());
    		//print.dumpStack();
			Resource classFile=classRootDir.getRealResource(className+".class");
			Resource classFileDirectory=classFile.getParentResource();
	        byte[] barr = null;
			Page page = null;
			
			if(!classFileDirectory.exists()) classFileDirectory.mkdirs(); 
			
	        try {
	        	page = cfmlTransformer.transform(config,source,tld,fld);
	        	barr = page.execute(classFile);
				IOUtil.copy(new ByteArrayInputStream(barr), classFile,true);
		        return barr;
			} 
	        catch (AlreadyClassException ace) {
	        	InputStream is=null;
	        	try{
	        		barr=IOUtil.toBytes(is=ace.getInputStream());
	        		barr=Page.setSourceLastModified(barr,source.getPhyscalFile().lastModified());
	        		IOUtil.copy(new ByteArrayInputStream(barr), classFile,true);
	        	}
	        	finally {
	        		IOUtil.closeEL(is);
	        	}
	        	return barr;
	        }
	        catch (BytecodeException bce) {
	        	bce.addContext(source, bce.getLineAsInt(), bce.getLineAsInt(),null);
	        	throw bce;
	        	//throw new TemplateException(source,e.getLine(),e.getColumn(),e.getMessage());
			}
	        /*finally {
	        	
	        }*/
		//}
	}

    /* *
     * @return Returns the cfmlTransformer.
     * /
    public CFMLTransformer getCfmlTransformer() {
        return cfmlTransformer;
    }*/
}