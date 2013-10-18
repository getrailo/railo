package railo.runtime.tag;

import javax.servlet.jsp.tagext.Tag;

import railo.commons.lang.StringUtil;
import railo.runtime.db.SQLCaster;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.DatabaseException;
import railo.runtime.ext.tag.TagSupport;
import railo.runtime.tag.util.DeprecatedUtil;

public class ProcParam extends TagSupport {

	private ProcParamBean param=new ProcParamBean();

	public void release() {
		param=new ProcParamBean();
		super.release();
	}

	/**
	 * @param cfsqltype The cfsqltype to set.
	 * @throws DatabaseException 
	 */
	public void setCfsqltype(String cfsqltype) throws DatabaseException {
		param.setType(SQLCaster.toIntType(cfsqltype));
	}

	public void setSqltype(String type) throws DatabaseException	{
		param.setType(SQLCaster.toIntType(type));
	}

	/**
	 * @param ignoreNull The ignoreNull to set.
	 */
	public void setNull(boolean _null) {
		param.setNull(_null);
	}

	/**
	 * @param maxLength The maxLength to set.
	 */
	public void setMaxlength(double maxLength) {
		param.setMaxLength((int) maxLength);
	}

	/**
	 * @param scale The scale to set.
	 */
	public void setScale(double scale) {
		param.setScale((int) scale);
	}

	/**
	 * @param type The type to set.
	 * @throws ApplicationException 
	 */
	public void setType(String type) throws ApplicationException {
		type=type.trim().toLowerCase();
		if("in".equals(type))			param.setDirection(ProcParamBean.DIRECTION_IN);
		else if("inout".equals(type))	param.setDirection(ProcParamBean.DIRECTION_INOUT);
		else if("in_out".equals(type))	param.setDirection(ProcParamBean.DIRECTION_INOUT);
		else if("outin".equals(type))	param.setDirection(ProcParamBean.DIRECTION_INOUT);
		else if("out_in".equals(type))	param.setDirection(ProcParamBean.DIRECTION_INOUT);
		else if("out".equals(type))		param.setDirection(ProcParamBean.DIRECTION_OUT);
		else throw new ApplicationException("attribute type of tag procparam has an invalid value ["+type+"], valid values are [in, out, inout]");
		
	}

	/**
	 * @param value The value to set.
	 */
	public void setValue(Object value) {
		param.setValue(value);
	}

	/**
	 * @param variable The variable to set.
	 */
	public void setVariable(String variable) {
		param.setVariable(variable);
	}
	public void setDbvarname(String dbvarname) {
		DeprecatedUtil.tagAttribute(pageContext,"procparam","dbvarname");
	}
	
	
	public int doStartTag() throws ApplicationException {
		// check 
		if(param.getDirection()!=ProcParamBean.DIRECTION_IN && StringUtil.isEmpty(param.getVariable()))
			throw new ApplicationException("attribute variable of tag ProcParam is required, when attribute type has value \"out\" or \"inout\"");
		if(param.getDirection()==ProcParamBean.DIRECTION_IN && param.getValue()==null && !param.getNull())
			throw new ApplicationException("attribute value of tag ProcParam is required, when attribute type has value \"in\"");
		if(!param.getNull() && param.getValue() == null && param.getDirection()!=ProcParamBean.DIRECTION_OUT)
            throw new ApplicationException("required attribute value is empty");
		
		
		Tag parent=getParent();
		while(parent!=null && !(parent instanceof StoredProc)) {
			parent=parent.getParent();
		}
		
		if(parent instanceof StoredProc) {
			((StoredProc)parent).addProcParam(param);
		}
		else {
			throw new ApplicationException("Wrong Context, tag ProcParam must be inside a StoredProc tag");	
		}
		return SKIP_BODY;
	}
}
