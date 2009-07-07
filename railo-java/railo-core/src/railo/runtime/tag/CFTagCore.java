package railo.runtime.tag;

import railo.runtime.Mapping;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.PageException;

public class CFTagCore extends CFTag {

	private String name;
	private String filename;
	private boolean isweb;



	
	public void set__name(String name){
		this.name=name;
	}
	public void set__filename(String filename){
		this.filename=filename;
	}
	public void set__isweb(boolean isweb){
		this.isweb=isweb;
	}
	public InitFile initFile(PageContext pageContext) throws PageException {
    	ConfigWebImpl config = (ConfigWebImpl) pageContext.getConfig();
    	//print.out(this+"-"+filename);
        // config mappings
    	Mapping mapping=isweb?config.getTagMapping():config.getServerTagMapping();
    	
    	return new CFTag.InitFile(
    			mapping.getPageSource(filename),
    			filename,
    			filename.endsWith('.'+config.getCFCExtension()));
        
        //throw new ExpressionException("custom tag ["+name+"] is not defined in directory ["+mapping.getStrPhysical()+"]");
     
    }
}
