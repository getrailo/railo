package railo.transformer.bytecode.statement.tag;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import railo.aprint;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.ExceptionUtil;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.PageSource;
import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.statement.FlowControlFinal;

public class TagComponent extends TagBase{

	@Override
	public void _writeOut(BytecodeContext bc) throws TransformerException {
		_writeOut(bc,true,null);
	}


	@Override
	public void _writeOut(BytecodeContext bc, boolean doReuse) throws TransformerException {
		_writeOut(bc,doReuse,null);
	}


	@Override
	protected void _writeOut(BytecodeContext bc, boolean doReuse, FlowControlFinal fcf) throws TransformerException {
		
		
		
		PageSource ps = bc.getPage().getPageSource();
		//Resource res = ps.getPhyscalFile();
		PageSource rel = ps.getRealPage("Inliner.cfc");
		aprint.e(getBody().getParent());
		Resource classFile = rel.getMapping().getClassRootDirectory().getRealResource(rel.getJavaName()+".class");
		
		//Resource classFile=classRootDir.getRealResource(className+".class");
		
		
		Page page=new Page(
				bc.getFactory()
				, rel
				, rel.getPhyscalFile()
				, rel.getFullClassName()
				, CFMLEngineFactory.getInstance().getInfo().getFullVersionInfo()
				, bc.getPage().getLastModifed()
				, bc.getPage().writeLog()
				, bc.getSupressWSbeforeArg());
		page.setIsComponent(true); // MUST can be a interface as well
		page.addStatement(this);
		
		byte[] barr = page.execute(rel);
		
		try {
			IOUtil.copy(new ByteArrayInputStream(barr), classFile,true);
		}
		catch (IOException e) {
			new TransformerException(ExceptionUtil.getMessage(e),getStart());
		}
	}


	/**
	 * Constructor of the class
	 * @param startLine
	 * @param endLine
	 */
	public TagComponent(Factory f, Position start,Position end) {
		super(f,start, end);
	}

	
	@Override
	public FlowControlFinal getFlowControlFinal() {
		return null;
	}

}
