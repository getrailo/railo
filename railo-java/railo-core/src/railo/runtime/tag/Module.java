package railo.runtime.tag;

import railo.commons.lang.StringUtil;
import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWeb;
import railo.runtime.exp.ExpressionException;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Collection.Key;

/**
* Invokes a custom tag for use in ColdFusion application pages. The cfmodule tag can help deal with 
*   custom tag name conflicts. Use the template attribute to name a ColdFusion page that contains the custom 
*   tag definition, including its path. Use the name attribute to refer to the custom tag using a dot 
*   notation scheme to indicate the location of the custom tag in the ColdFusion installation directory.
**/
public final class Module extends CFTag {

	private static final Key TEMPLATE = KeyImpl.getInstance("template");
	private static final Key NAME = KeyImpl.getInstance("name");

	/**
	 * @see railo.runtime.tag.CFTag#initFile()
	 */
	public void initFile() throws ExpressionException {
		ConfigWeb config = pageContext.getConfig();
        
		//String[] filenames=getFileNames(config,getAppendix());// = appendix+'.'+config.getCFMLExtension();
        
	    Object objTemplate =attributesScope.get(TEMPLATE,null);
	    Object objName =attributesScope.get(NAME,null);
	    source=null;
	    if(objTemplate!=null) {
		    String template=objTemplate.toString();
			

            if(StringUtil.startsWith(template,'/'))  source=new InitFile(pageContext.getPageSource(template),template,template.endsWith('.'+pageContext.getConfig().getCFCExtension()));
            else source=new InitFile(pageContext.getCurrentPageSource().getRealPage(template),template,StringUtil.endsWithIgnoreCase(template,'.'+pageContext.getConfig().getCFCExtension()));
    		
            //attributesScope.removeEL(TEMPLATE);
			if(!MappingImpl.isOK(source.ps))
					throw new ExpressionException("could not find template ["+template+"]","file ["+source.ps.getDisplayPath()+"] doesn't exist");
			setAppendix(source.ps);
	    }
	    else if(objName!=null) {
	        String[] filenames = toRealPath(config,objName.toString());
	        boolean exist=false;
	        
	        // appcontext mappings
	        Mapping[] ctms = pageContext.getApplicationContext().getCustomTagMappings(); 
	        if(ctms!=null) {
	        	outer:for(int f=0;f<filenames.length;f++){
		        	for(int i=0;i<ctms.length;i++){
		            	source=new InitFile(ctms[i].getPageSource(filenames[f]),filenames[f],filenames[f].endsWith('.'+config.getCFCExtension()));
		            	if(MappingImpl.isOK(source.ps)) {
		            		exist=true;
		            		break outer;
		            	}
		            }
	        	}
	        }
	        
	        // config mappings
	        if(!exist) {
		        ctms = config.getCustomTagMappings();
	        	outer:for(int f=0;f<filenames.length;f++){ 
		            for(int i=0;i<ctms.length;i++){// TODO optimieren siehe CFTag
		            	source=new InitFile(ctms[i].getPageSource(filenames[f]),filenames[f],filenames[f].endsWith('.'+config.getCFCExtension()));
		            	if(MappingImpl.isOK(source.ps)) {
		            		exist=true;
		            		break outer;
		            	}
		            }
	        	}
	        }
            
			if(!exist)
				throw new ExpressionException("custom tag ("+getDisplayName(config, objName.toString())+") is not defined in custom tag directory ["+(ctms.length==0?"no custom tag directory defined":toString(ctms))+"]");
			
			setAppendix(source.ps);
	    }
	    else {
	        throw new ExpressionException("you must define attribute template or name for tag module");
	    }
	    
	}

	private void setAppendix(PageSource source) {
		String appendix=source.getFileName();
        int index=appendix.lastIndexOf('.');
        appendix=appendix.substring(0,index);
        setAppendix(appendix);
	}

	/**
	 * translate a dot-notation path to a realpath
     * @param dotPath
     * @return realpath
	 * @throws ExpressionException 
     */
    private static String[] toRealPath(Config config ,String dotPath) throws ExpressionException {
        dotPath=dotPath.trim();
        
        while(dotPath.indexOf('.')==0) {
            dotPath=dotPath.substring(1);
        }
        int len=-1;
        while((len=dotPath.length())>0 && dotPath.lastIndexOf('.')==len-1) {
            dotPath=dotPath.substring(0,len-2);
        }
        //dotPath.replace('.','/')+".cfm";
        return CFTag.getFileNames(config, dotPath.replace('.','/'));
    }
}