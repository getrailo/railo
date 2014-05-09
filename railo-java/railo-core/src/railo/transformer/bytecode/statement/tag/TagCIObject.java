package railo.transformer.bytecode.statement.tag;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.ExceptionUtil;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.PageSource;
import railo.runtime.op.Decision;
import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.statement.FlowControlFinal;
import railo.transformer.cfml.evaluator.EvaluatorException;

public abstract class TagCIObject extends TagBase{

	private boolean main;
	private String name;


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
		writeOut(bc.getPage());
	}
	
	public void writeOut(Page p) throws TransformerException {
		
		PageSource ps = p.getPageSource();
		ps.getFileName();
		Page page=new Page(
				p.getFactory()
				, ps
				, ps.getPhyscalFile()
				,this
				, CFMLEngineFactory.getInstance().getInfo().getFullVersionInfo()
				, p.getLastModifed()
				, p.writeLog()
				, p.getSupressWSbeforeArg());
		//page.setIsComponent(true); // MUST can be a interface as well
		page.addStatement(this);
		
		byte[] barr = page.execute(ps);
		
		Resource classFile = ps.getMapping().getClassRootDirectory().getRealResource(page.getClassName()+".class");
		
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
	public TagCIObject(Factory f, Position start,Position end) {
		super(f,start, end);
	}

	
	@Override
	public FlowControlFinal getFlowControlFinal() {
		return null;
	}


	public void setMain(boolean main) {
		this.main=main;
	}
	public boolean isMain() {
		return main;
	}


	public void setName(String name) throws EvaluatorException {
		if(!Decision.isVariableName(name))
			throw new EvaluatorException("component name ["+name+"] is invalid");
		
		this.name=name;
	}
	public String getName() {
		return name;
	}

}
