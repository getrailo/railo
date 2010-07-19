package railo.runtime.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import railo.runtime.ext.tag.BodyTagTryCatchFinallyImpl;
import railo.runtime.writer.BodyContentImpl;

public final class Silent extends BodyTagTryCatchFinallyImpl {


    private boolean bufferoutput=true;
	private BodyContentImpl bc;
	private boolean wasSilent;



	/**
	 * @param bufferoutput the bufferoutput to set
	 */
	public void setBufferoutput(boolean bufferoutput) {
		this.bufferoutput = bufferoutput;
	}


	/**
     * @see railo.runtime.ext.tag.TagImpl#doStartTag()
     */
    public int doStartTag() throws JspException {
    	if(bufferoutput) bc = (BodyContentImpl) pageContext.pushBody();
    	else wasSilent=pageContext.setSilent();
    	
    	return EVAL_BODY_INCLUDE;
    }
    

	/**
	 *
	 * @see railo.runtime.ext.tag.BodyTagTryCatchFinallyImpl#doCatch(java.lang.Throwable)
	 */
	public void doCatch(Throwable t) throws Throwable {
		if(bufferoutput){
	    	try {
				bc.flush();
			} catch (IOException e) {}
			pageContext.popBody();
			bc=null;
    	}
    	else if(!wasSilent)pageContext.unsetSilent();
	    super.doCatch(t);
	}

    
    /**
     * @see railo.runtime.ext.tag.BodyTagTryCatchFinallyImpl#doFinally()
     */
    public void doFinally() {
    	if(bufferoutput){
	    	if(bc!=null){
	        	bc.clearBody();
	        	pageContext.popBody();
	        }
    	}
    	else if(!wasSilent)pageContext.unsetSilent();
    }


	/**
	 *
	 * @see railo.runtime.ext.tag.BodyTagImpl#release()
	 */
	public void release() {
		super.release();
		bc=null;
		this.bufferoutput=true;
	}


}