package railo.runtime.tag;

import javax.servlet.jsp.tagext.Tag;

import railo.runtime.exp.ApplicationException;
import railo.runtime.ext.tag.TagSupport;
import railo.runtime.op.Caster;

public class ProcResult extends TagSupport {
	
	private ProcResultBean result=new ProcResultBean();

	public void release() {
		result=new ProcResultBean();
		super.release();
	}
	
	/**
	 * @param maxrows The maxrows to set.
	 */
	public void setMaxrows(double maxrows) {
		result.setMaxrows(Caster.toIntValue(maxrows));
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		result.setName(name);
	}

	/**
	 * @param resultset The resultset to set.
	 * @throws ApplicationException 
	 */
	public void setResultset(double resultset) throws ApplicationException {
		if(resultset<1)throw new ApplicationException("value of attribute resultset must be a numeric value greater or equal to 1");
		result.setResultset((int) resultset);
	}	
	public int doStartTag() throws ApplicationException {
		
		// provide to parent
		Tag parent=getParent();
		while(parent!=null && !(parent instanceof StoredProc)) {
			parent=parent.getParent();
		}
		
		if(parent instanceof StoredProc) {
			((StoredProc)parent).addProcResult(result);
		}
		else {
			throw new ApplicationException("Wrong Context, tag ProcResult must be inside a StoredProc tag");	
		}
		return SKIP_BODY;
	}
}
