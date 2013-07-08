/**
 * Implements the CFML Function encrypt
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.crypt.CFMXCompat;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;


public final class EncryptBinary implements Function {


    public synchronized static Object call( PageContext pc , Object oBytes, String key ) throws PageException {

        return call( pc, oBytes, key, CFMXCompat.ALGORITHM_NAME );
    }


    public synchronized static Object call( PageContext pc , Object oBytes, String key, String algorithm ) throws PageException {

        return Encrypt.invoke( Caster.toBinary( oBytes ), key, algorithm, null, 0 );
    }


    public synchronized static Object call( PageContext pc , Object oBytes, String key, String algorithm, Object ivOrSalt ) throws PageException {

        return Encrypt.invoke( Caster.toBinary( oBytes ), key, algorithm, Caster.toBinary( ivOrSalt ), 0 );
    }


    public synchronized static Object call( PageContext pc , Object oBytes, String key, String algorithm, Object ivOrSalt, double iterations ) throws PageException {

        return Encrypt.invoke( Caster.toBinary( oBytes ), key, algorithm, Caster.toBinary( ivOrSalt ), Caster.toInteger( iterations ) );
    }
}