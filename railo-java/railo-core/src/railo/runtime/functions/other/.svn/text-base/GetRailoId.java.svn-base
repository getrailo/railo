
package railo.runtime.functions.other;


import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

/**
 * Implements the Cold Fusion Function createGuid
 */
public final class GetRailoId implements Function {

    private static final Collection.Key SECURITY_KEY = KeyImpl.getInstance("securityKey");
    private static final Collection.Key WEB = KeyImpl.getInstance("web");
    private static final Collection.Key SERVER = KeyImpl.getInstance("server");
    private static final Collection.Key ID = KeyImpl.getInstance("id");
    private static final Collection.Key REQUEST = KeyImpl.getInstance("request");

	public static Struct call(PageContext pc ) throws PageException {
		Struct sct=new StructImpl();
	    Struct web=new StructImpl();
	    Struct server=new StructImpl();
	    ConfigWeb config = pc.getConfig();
    	
		web.set(SECURITY_KEY, ((ConfigImpl)config).getSecurityKey());
		web.set(ID, config.getId());
		sct.set(WEB, web);
    	
    	if(config instanceof ConfigWebImpl){
    		ConfigWebImpl cwi = (ConfigWebImpl)config;
    		server.set(SECURITY_KEY, cwi.getServerSecurityKey());
    		server.set(ID, cwi.getServerId());
    		sct.set(SERVER, server);
    	}
    	
    	sct.set(REQUEST, Caster.toString(pc.getId()));
    	return  sct;
    }
    
}