package railo.runtime.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import railo.runtime.ext.tag.BodyTagTryCatchFinallyImpl;
import railo.runtime.listener.ApplicationContextSupport;
import railo.runtime.writer.BodyContentImpl;

public final class Silent extends BodyTagTryCatchFinallyImpl {


    private Boolean bufferOutput=true;
	private BodyContentImpl bc;
	private boolean wasSilent;



	/**
	 * @param bufferoutput the bufferoutput to set
	 */
	public void setBufferoutput(boolean bufferOutput) {
		this.bufferOutput = bufferOutput?Boolean.TRUE:Boolean.FALSE;
	}


	@Override
    public int doStartTag() throws JspException {
    	if(bufferOutput==null)
    		bufferOutput=((ApplicationContextSupport)pageContext.getApplicationContext()).getBufferOutput()?Boolean.TRUE:Boolean.FALSE;
    	
    	if(bufferOutput.booleanValue()) bc = (BodyContentImpl) pageContext.pushBody();
    	else wasSilent=pageContext.setSilent();
    	
    	return EVAL_BODY_INCLUDE;
    }
    

	@Override
	public void doCatch(Throwable t) throws Throwable {
		if(bufferOutput.booleanValue()){
	    	try {
				bc.flush();
			} catch (IOException e) {}
			pageContext.popBody();
			bc=null;
    	}
    	else if(!wasSilent)pageContext.unsetSilent();
	    super.doCatch(t);
	}

    
    @Override
    public void doFinally() {
    	if(bufferOutput.booleanValue()){
	    	if(bc!=null){
	        	bc.clearBody();
	        	pageContext.popBody();
	        }
    	}
    	else if(!wasSilent)pageContext.unsetSilent();
    }


	@Override
	public void release() {
		super.release();
		bc=null;
		this.bufferOutput=null;
	}


}