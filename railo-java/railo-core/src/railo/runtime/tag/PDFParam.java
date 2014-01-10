package railo.runtime.tag;

import javax.servlet.jsp.tagext.Tag;

import railo.runtime.exp.ApplicationException;
import railo.runtime.ext.tag.TagImpl;

/**
* Required for cfhttp POST operations, cfhttpparam is used to specify the parameters necessary to 
* 	 build a cfhttp POST.
*
*
*
**/
public final class PDFParam extends TagImpl {
	
	PDFParamBean param=new PDFParamBean();


	/**
	 * @param pages the pages to set
	 */
	public void setPages(String pages) {
		param.setPages(pages);
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		param.setPassword(password);
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(Object source) {
		param.setSource(source);
	}
	
	
	@Override
	public int doStartTag() throws ApplicationException	{
        
        
		// get HTTP Tag
		Tag parent=getParent();
		while(parent!=null && !(parent instanceof PDF)) {
			parent=parent.getParent();
		}
		
		if(parent instanceof PDF) {
			PDF pdf = (PDF)parent;
			pdf.setParam(param);
		}
		else {
			throw new ApplicationException("Wrong Context, tag PDFParam must be inside a PDF tag");	
		}
		return SKIP_BODY;
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}

	@Override
	public void release()	{
		super.release();
		param=new PDFParamBean();
	}
}