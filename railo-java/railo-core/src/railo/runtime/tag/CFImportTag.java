package railo.runtime.tag;

import java.io.File;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.config.ConfigWeb;
import railo.runtime.customtag.CustomTagUtil;
import railo.runtime.customtag.InitFile;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.util.ListUtil;

/**
 * To create cfimport custom tags
 */
public final class CFImportTag extends CFTag {

    
	@Override
	public void initFile() throws PageException {
		ConfigWeb config = pageContext.getConfig();
        
		String[] filenames=CustomTagUtil.getFileNames(config, getAppendix());// = appendix+'.'+config.getCFMLExtension();
        
		
		String strRealPathes=attributesScope.remove("__custom_tag_path").toString();
		String[] realPathes=ListUtil.listToStringArray(strRealPathes, File.pathSeparatorChar);
	    for(int i=0;i<realPathes.length;i++){
	    	if(!StringUtil.endsWith(realPathes[i],'/'))realPathes[i]=realPathes[i]+"/";
	    }
	    
	    // MUSTMUST use cache like regular ct
		// page source
	    PageSource ps;
	    for(int rp=0;rp<realPathes.length;rp++){
		    for(int fn=0;fn<filenames.length;fn++){
	            ps=((PageContextImpl)pageContext).getRelativePageSourceExisting(realPathes[rp]+filenames[fn]);
	            if(ps!=null){
	            	source=new InitFile(ps,filenames[fn],filenames[fn].endsWith('.'+config.getCFCExtension()));
	            	return;
	            }
			} 
	    }
	    
	// EXCEPTION
	    // message
	    
        StringBuffer msg=new StringBuffer("could not find template [");
        msg.append(CustomTagUtil.getDisplayName(config, getAppendix()));
        msg.append("] in the following directories [");
        msg.append(strRealPathes.replace(File.pathSeparatorChar, ','));
        msg.append(']');
        
	    
		throw new ExpressionException(msg.toString(),CustomTagUtil.getDetail(config));
	    
	}

}