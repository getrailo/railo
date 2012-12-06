package railo.intergral.fusiondebug.server.type.qry;

import railo.intergral.fusiondebug.server.type.FDNodeValueSupport;
import railo.intergral.fusiondebug.server.util.FDCaster;
import railo.runtime.type.Query;

import com.intergral.fusiondebug.server.FDLanguageException;
import com.intergral.fusiondebug.server.FDMutabilityException;
import com.intergral.fusiondebug.server.IFDStackFrame;

public class FDQueryNode extends FDNodeValueSupport {

	private Query qry;
	private int row;
	private String column;

	public FDQueryNode(IFDStackFrame frame, Query qry, int row, String column) {
		super(frame);
		this.qry=qry;
		this.row=row;
		this.column=column;
	}

	public String getName() {
		return column;
	}

	@Override
	protected Object getRawValue() {
		return qry.getAt(column, row,null);
	}

	public boolean isMutable() {
		return true;
	}

	public void set(String value) throws FDMutabilityException,FDLanguageException {
		qry.setAtEL(column,row, FDCaster.unserialize(value));
	}
}
