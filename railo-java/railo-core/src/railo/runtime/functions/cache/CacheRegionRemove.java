package railo.runtime.functions.cache;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigWebAdmin;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * implements BIF CacheRegionRemove.  This function only exists for compatibility with other CFML Engines and should be avoided where possible.
 * The preferred method to manipulate Cache connections is via the Administrator interface or in Application.
 */
public class CacheRegionRemove implements Function {


    public static String call( PageContext pc, String cacheName, String webAdminPassword ) throws PageException {

        return _call( pc, cacheName, webAdminPassword );
    }


    public static String call( PageContext pc, String cacheName ) throws PageException {

        return _call( pc, cacheName, null );
    }


    static String _call( PageContext pc, String cacheName, String webAdminPassword ) throws PageException {

        webAdminPassword = Util.getPassword( pc, webAdminPassword );

        try {

            ConfigWebAdmin adminConfig = ConfigWebAdmin.newInstance( (ConfigWebImpl)pc.getConfig(), webAdminPassword );

            adminConfig.removeCacheConnection( cacheName );

            adminConfig.store();

        } catch ( Exception e ) {

            throw Caster.toPageException( e );
        }

        return null;
    }
}