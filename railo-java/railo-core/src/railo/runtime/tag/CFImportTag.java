package railo.runtime.tag;

import java.io.File;

import railo.commons.lang.StringUtil;
import railo.runtime.PageSource;
import railo.runtime.config.ConfigWeb;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.List;

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
        
		
		String strRealPathes=attributesScope.remove("__custom_tag_path").toString();
		String[] realPathes=List.listToStringArray(strRealPathes, File.pathSeparatorChar);
	    for(int i=0;i<realPathes.length;i++){
	    	if(!StringUtil.endsWith(realPathes[i],'/'))realPathes[i]=realPathes[i]+"/";
	    }
		//if(!StringUtil.endsWith(realPath,'/'))realPath=realPath+"/";
	    
		// page source
	    PageSource ps;
	    for(int rp=0;rp<realPathes.length;rp++){
		    for(int fn=0;fn<filenames.length;fn++){
	            ps=pageContext.getRelativePageSource(realPathes[rp]+filenames[fn]);
	            if(ps.exists()){
	            	source=new InitFile(ps,filenames[fn],filenames[fn].endsWith('.'+config.getCFCExtension()));
	            	return;
	            }
			} 
	    }
	    
	// EXCEPTION
	    // message
	    
        StringBuffer msg=new StringBuffer("could not find template [");
        msg.append(getDisplayName(config, getAppendix()));
        msg.append("] in the following directories [");
        msg.append(strRealPathes.replace(File.pathSeparatorChar, ','));
        msg.append(']');
        
	    
		throw new ExpressionException(msg.toString(),getDetail(config));
	    
	}

}