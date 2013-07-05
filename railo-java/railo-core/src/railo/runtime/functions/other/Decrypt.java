/**
 * Implements the CFML Function decrypt
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.coder.Coder;
import railo.runtime.crypt.CFMXCompat;
import railo.runtime.crypt.Cryptor;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;


public final class Decrypt implements Function {


    public synchronized static String call( PageContext pc, String input, String key ) throws PageException {

        return invoke(input, key, CFMXCompat.ALGORITHM_NAME, Cryptor.DEFAULT_ENCODING, null, 0);
    }


    public synchronized static String call( PageContext pc, String input, String key, String algorithm ) throws PageException {

        return invoke(input, key, algorithm, Cryptor.DEFAULT_ENCODING, null, 0);
    }


    public synchronized static String call( PageContext pc, String input, String key, String algorithm, String encoding ) throws PageException {

        return invoke( input, key, algorithm, encoding, null, 0 );
    }


    public synchronized static String call( PageContext pc, String input, String key, String algorithm, String encoding, Object ivOrSalt ) throws PageException {

        return invoke(input, key, algorithm, encoding, ivOrSalt, 0);
    }


    /**
     * call with all optional args
     */
    public synchronized static String call( PageContext pc, String input, String key, String algorithm, String encoding, Object ivOrSalt, double iterations ) throws PageException {

        return invoke( input, key, algorithm, encoding, ivOrSalt, Caster.toInteger( iterations ) );
    }


    public synchronized static String invoke( String input, String key, String algorithm, String encoding, Object ivOrSalt, int iterations ) throws PageException {

        try {

            if ( CFMXCompat.isCfmxCompat( algorithm ) )
                return new String( invoke( Coder.decode( encoding, input ), key, algorithm, null, 0 ), Cryptor.DEFAULT_CHARSET );

            byte[] baIVS = null;
            if ( ivOrSalt instanceof String )
                baIVS = ((String)ivOrSalt).getBytes( Cryptor.DEFAULT_CHARSET );
            else if ( ivOrSalt != null )
                baIVS = Caster.toBinary( ivOrSalt );

            return Cryptor.decrypt( input, key, algorithm, baIVS, iterations, encoding, Cryptor.DEFAULT_CHARSET  );
        }
        catch ( Throwable t ) {

            throw Caster.toPageException( t );
        }
    }


    public synchronized static byte[] invoke( byte[] input, String key, String algorithm, byte[] ivOrSalt, int iterations ) throws PageException {

        if ( CFMXCompat.isCfmxCompat( algorithm ) )
            return new CFMXCompat().transformString( key, input );

        return Cryptor.decrypt( input, key, algorithm, ivOrSalt, iterations );
    }
}