package railo.runtime.functions.cache;

import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWebAdmin;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

/**
 * implements BIF CacheRegionNew.  This function only exists for compatibility with other CFML Engines and should be avoided where possible.
 * The preferred method to manipulate Cache connections is via the Administrator interface or in Application.
 */
public class CacheRegionNew implements Function {

    private final static String cacheClassName = "railo.runtime.cache.eh.EHCacheLite";


    public static String call( PageContext pc, String cacheName, Object arg2, Object arg3, String arg4 ) throws PageException {     // used Object for args 2 & 3 to match fld

        return _call( pc, cacheName, (Struct)arg2, (Boolean)arg3, arg4 );
    }


    public static String call( PageContext pc, String cacheName, Object properties, Object arg3 ) throws PageException {

        if ( arg3 instanceof Boolean )      // name, properties, throwOnError
            return _call( pc, cacheName, (Struct)properties, (Boolean)arg3, null );

        if ( arg3 instanceof String )       // name, properties, password
            return _call( pc, cacheName, (Struct)properties, true, (String)arg3 );

        throw new FunctionException( pc, "CacheRegionNew", 3, "throwOnError", "when calling this function with 3 arguments the 3rd argument must be either throwOnError (Boolean), or webAdminPassword (String)" );
    }


    public static String call( PageContext pc, String cacheName, Object arg2 ) throws PageException {

        if ( arg2 instanceof Struct )       // name, properties
            return _call( pc, cacheName, (Struct)arg2, true, null );

        if ( arg2 instanceof String )       // name, password
            return _call( pc, cacheName, new StructImpl(), true, (String)arg2 );

        throw new FunctionException( pc, "CacheRegionNew", 2, "properties", "when calling this function with 2 arguments the 2nd argument must be either properties (Struct), or webAdminPassword (String)" );
    }


    public static String call( PageContext pc, String cacheName ) throws PageException {

        return _call(pc, cacheName, new StructImpl(), true, null);      // name
    }


    static String _call( PageContext pc, String cacheName, Struct properties, Boolean throwOnError, String webAdminPassword ) throws PageException {

        webAdminPassword = Util.getPassword( pc, webAdminPassword );

        try {

            ConfigWebAdmin adminConfig = ConfigWebAdmin.newInstance( (ConfigWebImpl)pc.getConfig(), webAdminPassword );

            adminConfig.updateCacheConnection( cacheName, cacheClassName, Config.CACHE_DEFAULT_NONE, properties, false, false );

            adminConfig.store();

        } catch ( Exception e ) {

            if ( throwOnError )
                throw Caster.toPageException( e );
        }

        return null;
    }

}