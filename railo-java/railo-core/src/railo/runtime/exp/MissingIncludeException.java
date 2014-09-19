package railo.runtime.exp;

import railo.commons.lang.StringUtil;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;

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
    public MissingIncludeException(PageSource pageSource,String msg) {
        super(msg,"missinginclude");
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

	@Override
	public CatchBlock getCatchBlock(Config config) {
		CatchBlock sct=super.getCatchBlock(config);
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
	
	public boolean typeEqual(String type) {
    	if(super.typeEqual(type)) return true;
        type=type.toLowerCase().trim();
        return type.equals("template");
    }
}