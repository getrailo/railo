package railo.runtime.exp;

import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.type.Struct;

/**
 * Exception throwed when missing include
 */
public final class MissingIncludeException extends PageExceptionImpl {

	private PageSource pageSource;

	/**
	 * constructor of the exception
     * @param pageSource
     */
    public MissingIncludeException(PageSource pageSource) {
        super(createMessage(pageSource),"missinginclude");
        this.pageSource=pageSource;
    }

	/**
	 * @return the pageSource
	 */
	public PageSource getPageSource() {
		return pageSource;
	}

	private static String createMessage(PageSource pageSource) {
		String dsp=pageSource.getDisplayPath();
		if(dsp==null) return "Page "+pageSource.getRealpath()+" not found";
		return "Page "+pageSource.getRealpath()+" ["+dsp+"] not found";
	}

	/**
	 *
	 * @see railo.runtime.exp.PageExceptionImpl#getCatchBlock(railo.runtime.PageContext)
	 */
	public Struct getCatchBlock(PageContext pc) {
		Struct sct=super.getCatchBlock(pc);
		sct.setEL("MissingFileName",pageSource.getRealpath());
		sct.setEL("MissingFileName_rel",pageSource.getRealpath());
		sct.setEL("MissingFileName_abs",pageSource.getDisplayPath());
		return sct;
	}
}