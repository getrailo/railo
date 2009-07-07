package railo.runtime.tag;

import railo.commons.lang.StringUtil;
import railo.runtime.PageSource;
import railo.runtime.config.ConfigWeb;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;

/**
 * To create cfimport custom tags
 */
public final class CFImportTag extends CFTag {

    
	/**
	 * @see railo.runtime.tag.CFTag#initFile()
	 */
	public void initFile() throws PageException {
		ConfigWeb config = pageContext.getConfig();
        
		String[] filenames=CFTag.getFileNames(config, getAppendix());// = appendix+'.'+config.getCFMLExtension();
        
		
		String realPath=attributesScope.remove("__custom_tag_path").toString();
	    if(!StringUtil.endsWith(realPath,'/'))realPath=realPath+"/";
	    
		// page source
	    PageSource ps;
	    for(int i=0;i<filenames.length;i++){
            ps=pageContext.getRelativePageSource(realPath+filenames[i]);
            if(ps.exists()){
            	source=new InitFile(ps,filenames[i],filenames[i].endsWith('.'+config.getCFCExtension()));
            	return;
            }
		}
	    
	    
	// EXCEPTION
	    // message
        StringBuffer msg=new StringBuffer("could not find template [");
        msg.append(getDisplayName(config, realPath+getAppendix()));
        msg.append("] is not defined in directory [");
        msg.append(pageContext.getCurrentPageSource().getPhyscalFile().getParent());
        msg.append(']');
        
	    
		throw new ExpressionException(msg.toString(),getDetail(config));
	    
	}

}