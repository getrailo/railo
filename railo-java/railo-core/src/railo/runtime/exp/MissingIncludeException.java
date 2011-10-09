package railo.runtime.exp;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;

/**
 * Exception throwed when missing include
 */
public final class MissingIncludeException extends PageExceptionImpl {

	private static final Collection.Key MISSING_FILE_NAME = KeyImpl.intern("MissingFileName");
	private static final Collection.Key MISSING_FILE_NAME_REL = KeyImpl.intern("MissingFileName_rel");
	private static final Collection.Key MISSING_FILE_NAME_ABS = KeyImpl.intern("MissingFileName_abs");
	
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
		String mapping="";
		if(StringUtil.startsWith(pageSource.getRealpath(),'/')){
			mapping = pageSource.getMapping().getVirtual();
			if(StringUtil.endsWith(mapping, '/'))
				mapping=mapping.substring(0,mapping.length()-1);
		}
		sct.setEL(MISSING_FILE_NAME,mapping+pageSource.getRealpath());
		
		sct.setEL(MISSING_FILE_NAME_REL,mapping+pageSource.getRealpath());
		sct.setEL(MISSING_FILE_NAME_ABS,pageSource.getDisplayPath());
		return sct;
	}
}