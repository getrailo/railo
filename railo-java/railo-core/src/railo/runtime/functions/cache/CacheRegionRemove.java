package railo.runtime.functions.cache;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigWebAdmin;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * implements BIF CacheRegionRemove.  This function only exists for compatibility with other CFML Engines and should be avoided where possible.
 * The preferred method to manipulate Cache connections is via the Administrator interface or in Application.cfc
 */
public class CacheRegionRemove implements Function {

    public static void call( PageContext pc, String cacheName, String webAdminPassword ) throws PageException {

        webAdminPassword = Util.getPassword( pc, webAdminPassword );

        try {

            ConfigWebAdmin adminConfig = ConfigWebAdmin.newInstance( (ConfigWebImpl)pc.getConfig(), webAdminPassword );

            adminConfig.removeCacheConnection( cacheName );

        } catch ( Exception e ) {

            throw Caster.toPageException( e );
        }
    }


    public static void call( PageContext pc, String cacheName ) throws PageException {

        call( pc, cacheName, null );
    }

}