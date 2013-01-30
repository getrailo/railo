package railo.runtime.functions.cache;

import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWebAdmin;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

/**
 * implements BIF CacheRegionNew.  This function only exists for compatibility with other CFML Engines and should be avoided where possible.
 * The preferred method to manipulate Cache connections is via the Administrator interface or in Application.cfc
 */
public class CacheRegionNew implements Function {

    private final static String cacheClassName = "railo.runtime.cache.eh.EHCacheLite";    // TODO: this is the only supported type?


    public static void call( PageContext pc, String cacheName, Struct properties, Boolean throwOnError, String webAdminPassword ) throws PageException {

        webAdminPassword = Util.getPassword( pc, webAdminPassword );

        try {

            ConfigWebAdmin adminConfig = ConfigWebAdmin.newInstance( (ConfigWebImpl)pc.getConfig(), webAdminPassword );

            adminConfig.updateCacheConnection( cacheName, cacheClassName, Config.CACHE_DEFAULT_NONE, properties, false, false );

        } catch ( Exception e ) {

            if ( throwOnError )
                throw Caster.toPageException( e );
        }
    }


    public static void call( PageContext pc, String cacheName, Struct properties, String webAdminPassword ) throws PageException {

        CacheRegionNew.call( pc, cacheName, properties, true, webAdminPassword );
    }


    public static void call( PageContext pc, String cacheName, String webAdminPassword ) throws PageException {

        CacheRegionNew.call( pc, cacheName, new StructImpl(), true, webAdminPassword );     // TODO: pass empty struct?
    }

}