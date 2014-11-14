/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.runtime.tag;

import railo.commons.lang.StringUtil;
import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWeb;
import railo.runtime.customtag.CustomTagUtil;
import railo.runtime.customtag.InitFile;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.MissingIncludeException;
import railo.runtime.type.util.KeyConstants;

/**
* Invokes a custom tag for use in CFML application pages.
**/
public final class Module extends CFTag {

	@Override
	public void initFile() throws MissingIncludeException, ExpressionException {
		ConfigWeb config = pageContext.getConfig();
        // MUSTMUST cache like ct
		//String[] filenames=getFileNames(config,getAppendix());// = appendix+'.'+config.getCFMLExtension();
        
	    Object objTemplate =attributesScope.get(KeyConstants._template,null);
	    Object objName =attributesScope.get(KeyConstants._name,null);
	    source=null;
	    if(objTemplate!=null) {
			attributesScope.removeEL(KeyConstants._template);
		    String template=objTemplate.toString();

            if(StringUtil.startsWith(template,'/'))  {
            	PageSource[] sources = ((PageContextImpl)pageContext).getPageSources(template);
            	PageSource ps = MappingImpl.isOK(sources);
            	
            	if(ps==null)
					throw new MissingIncludeException(sources[0],"could not find template ["+template+"], file ["+sources[0].getDisplayPath()+"] doesn't exist");
            	source=new InitFile(ps,template,template.endsWith('.'+pageContext.getConfig().getCFCExtension()));
            }
            else {
            	source=new InitFile(pageContext.getCurrentPageSource().getRealPage(template),template,StringUtil.endsWithIgnoreCase(template,'.'+pageContext.getConfig().getCFCExtension()));
            	if(!MappingImpl.isOK(source.getPageSource())){
					throw new MissingIncludeException(source.getPageSource(),"could not find template ["+template+"], file ["+source.getPageSource().getDisplayPath()+"] doesn't exist");
            	}
            }
    		
            //attributesScope.removeEL(TEMPLATE);
            setAppendix(source.getPageSource());
	    }
	    else if(objName!=null) {
			attributesScope.removeEL(KeyConstants._name);
	        String[] filenames = toRelPath(config,objName.toString());
	        boolean exist=false;
	        
	        // appcontext mappings
	        Mapping[] ctms = pageContext.getApplicationContext().getCustomTagMappings(); 
	        if(ctms!=null) {
	        	outer:for(int f=0;f<filenames.length;f++){
		        	for(int i=0;i<ctms.length;i++){
		            	source=new InitFile(ctms[i].getPageSource(filenames[f]),filenames[f],filenames[f].endsWith('.'+config.getCFCExtension()));
		            	if(MappingImpl.isOK(source.getPageSource())) {
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
		            	if(MappingImpl.isOK(source.getPageSource())) {
		            		exist=true;
		            		break outer;
		            	}
		            }
	        	}
	        }
            
			if(!exist)
				throw new ExpressionException("custom tag ("+CustomTagUtil.getDisplayName(config, objName.toString())+") is not defined in custom tag directory ["+(ctms.length==0?"no custom tag directory defined":CustomTagUtil.toString(ctms))+"]");
			
			setAppendix(source.getPageSource());
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
	 * translate a dot-notation path to a relpath
     * @param dotPath
     * @return relpath
	 * @throws ExpressionException 
     */
    private static String[] toRelPath(Config config ,String dotPath) throws ExpressionException {
        dotPath=dotPath.trim();
        
        while(dotPath.indexOf('.')==0) {
            dotPath=dotPath.substring(1);
        }
        int len=-1;
        while((len=dotPath.length())>0 && dotPath.lastIndexOf('.')==len-1) {
            dotPath=dotPath.substring(0,len-2);
        }
        //dotPath.replace('.','/')+".cfm";
        return CustomTagUtil.getFileNames(config, dotPath.replace('.','/'));
    }
}