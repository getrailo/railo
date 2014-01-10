package railo.runtime.tag;

import javax.servlet.jsp.tagext.Tag;

import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.TagNotSupported;
import railo.runtime.ext.tag.TagImpl;

public final class ReportParam extends TagImpl {
	
	private ReportParamBean param=new ReportParamBean();
	

	public ReportParam() throws TagNotSupported {
		// TODO implement tag
		throw new TagNotSupported("ReportParam");
	}
	
	@Override
	public void release() {
		this.param=new ReportParamBean();
		super.release();
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		param.setName(name);
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		param.setValue(value);
	}
	
	public int doStartTag() throws ApplicationException {
		// check 
		
		// provide to parent
		Tag parent=this;
		do{
			parent = parent.getParent();
			if(parent instanceof Report) {
				((Report)parent).addReportParam(param);
				break;
			}
		}
		while(parent!=null);
		
		return SKIP_BODY;
	}
}

