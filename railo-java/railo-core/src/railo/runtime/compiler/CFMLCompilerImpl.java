package railo.runtime.compiler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.PageSource;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.Constants;
import railo.runtime.exp.TemplateException;
import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeFactory;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.ClassRenamer;
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
	
	@Override
	public byte[] compile(ConfigImpl config,PageSource source, TagLib[] tld, FunctionLib[] fld, 
        Resource classRootDir, String className) throws TemplateException, IOException {
		//synchronized(source){
			//print.out("src:"+source.getDisplayPath());
    		//print.dumpStack();
			byte[] barr = null;
			Page page = null;
			
			Factory factory = BytecodeFactory.getInstance();
	        try {
	        	page = cfmlTransformer.transform(factory,config,source,tld,fld);
	        	page.setSplitIfNecessary(false);
	        	try {
	        		barr = page.execute(source);
	        	}
	        	catch(RuntimeException re) {
	        		String msg=StringUtil.emptyIfNull(re.getMessage());
	        		if(StringUtil.indexOfIgnoreCase(msg, "Method code too large!")!=-1) {
	        			page = cfmlTransformer.transform(factory,config,source,tld,fld); // MUST a new transform is necessary because the page object cannot be reused, rewrite the page that reusing it is possible
	    	        	page.setSplitIfNecessary(true);
	        			barr = page.execute(source);
	        		}
	        		else throw re;
	        	}
		        catch(ClassFormatError cfe) {
		        	String msg=StringUtil.emptyIfNull(cfe.getMessage());
		        	if(StringUtil.indexOfIgnoreCase(msg, "Invalid method Code length")!=-1) {
		        		page = cfmlTransformer.transform(factory,config,source,tld,fld); // MUST see above
			        	page.setSplitIfNecessary(true);
		        		barr = page.execute(source);
		        	}
		        	else throw cfe;
		        }
		        // Resource classFile = classRootDir.getRealResource(page.getClassName()+".class");
		        Resource classFile = classRootDir.getRealResource(page.getClassName()+".class");
				Resource classFileDirectory=classFile.getParentResource();
		        if(!classFileDirectory.exists()) classFileDirectory.mkdirs(); 
				
		        

				IOUtil.copy(new ByteArrayInputStream(barr), classFile,true);
		        return barr;
			} 
	        catch (AlreadyClassException ace) {
	        	InputStream is=null;
	        	try{
	        		barr=IOUtil.toBytes(is=ace.getInputStream());
	        		
	        		String srcName = ASMUtil.getClassName(barr);
	        		// source is cfm and target cfc
	        		if(
	        				srcName.endsWith("_"+Constants.TEMPLATE_EXTENSION+Constants.CLASS_SUFFIX) 
	        				&& 
	        				className.endsWith("_"+Constants.COMPONENT_EXTENSION+Constants.CLASS_SUFFIX))
	        				throw new TemplateException("source file ["+source.getDisplayPath()+"] contains the bytecode for a regular cfm template not for a component");
	        		// source is cfc and target cfm
	        		if(srcName.endsWith("_"+Constants.COMPONENT_EXTENSION+Constants.CLASS_SUFFIX) && className.endsWith("_"+Constants.TEMPLATE_EXTENSION+Constants.CLASS_SUFFIX))
	        				throw new TemplateException("source file ["+source.getDisplayPath()+"] contains a component not a regular cfm template");
	        		
	        		// rename class name when needed
	        		if(!srcName.equals(className))barr=ClassRenamer.rename(barr, className);
	        		
	        		Resource classFile=classRootDir.getRealResource(className+".class");
	    			Resource classFileDirectory=classFile.getParentResource();
	    			if(!classFileDirectory.exists()) classFileDirectory.mkdirs(); 
	    			
	        		
	        		barr=Page.setSourceLastModified(barr,source.getPhyscalFile().lastModified());
	        		IOUtil.copy(new ByteArrayInputStream(barr), classFile,true);
	        		
	        	}
	        	finally {
	        		IOUtil.closeEL(is);
	        	}
	        	return barr;
	        }
	        catch (TransformerException bce) {
	        	Position pos = bce.getPosition();
	        	int line=pos==null?-1:pos.line;
	        	int col=pos==null?-1:pos.column;
	        	bce.addContext(source, line, col,null);
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