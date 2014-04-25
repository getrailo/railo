package railo.transformer.bytecode.statement.tag;

import railo.runtime.exp.TemplateException;
import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.statement.FlowControlFinal;
import railo.transformer.bytecode.visitor.ParseBodyVisitor;

public final class TagOutput extends TagGroup {

	public static final int TYPE_QUERY = 0;
	public static final int TYPE_GROUP = 1;
	public static final int TYPE_INNER_GROUP = 2;
	public static final int TYPE_INNER_QUERY = 3;
	public static final int TYPE_NORMAL= 4;
	
	
	private int type;
	

	public TagOutput(Factory f, Position start,Position end) {
		super(f,start,end);
	}


	public static TagOutput getParentTagOutputQuery(Statement stat) throws TransformerException {
		Statement parent=stat.getParent();
		if(parent==null) throw new TransformerException("there is no parent output with query",null);
		else if(parent instanceof TagOutput) {
			if(((TagOutput)parent).hasQuery())
				return ((TagOutput)parent);
		}
		return getParentTagOutputQuery(parent);
	}

	public void setType(int type) {
		this.type=type;
	}


	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	@Override
	public void _writeOut(BytecodeContext bc) throws TransformerException {
		boolean old;
		switch(type) {
		case TYPE_GROUP:
			old = bc.changeDoSubFunctions(false);
			TagGroupUtil.writeOutTypeGroup(this,bc);
			bc.changeDoSubFunctions(old);
		break;
		case TYPE_INNER_GROUP:
			old = bc.changeDoSubFunctions(false);
			TagGroupUtil.writeOutTypeInnerGroup(this,bc);
			bc.changeDoSubFunctions(old);
		break;
		case TYPE_INNER_QUERY:
			old = bc.changeDoSubFunctions(false);
			TagGroupUtil.writeOutTypeInnerQuery(this,bc);
			bc.changeDoSubFunctions(old);
		break;
		case TYPE_NORMAL:
			writeOutTypeNormal(bc);
		break;
		case TYPE_QUERY:
			old = bc.changeDoSubFunctions(false);
			TagGroupUtil.writeOutTypeQuery(this,bc);
			bc.changeDoSubFunctions(old);
		break;
		
		default:
			throw new TransformerException("invalid type",getStart());
		}
	}


	
	


	


	/**
	 * write out normal query
	 * @param adapter
	 * @throws TemplateException
	 */
	private void writeOutTypeNormal(BytecodeContext bc) throws TransformerException {
		ParseBodyVisitor pbv=new ParseBodyVisitor();
		pbv.visitBegin(bc);
			getBody().writeOut(bc);
		pbv.visitEnd(bc);
	}


	@Override
	public short getType() {
		return TAG_OUTPUT;
	}


	@Override
	public FlowControlFinal getFlowControlFinal() {
		return null;
	}

}
