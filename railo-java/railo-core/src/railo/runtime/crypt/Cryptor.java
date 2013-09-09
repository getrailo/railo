package railo.runtime.crypt;

import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import railo.commons.lang.StringUtil;
import railo.runtime.coder.Coder;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

/**
 *
 */
public class Cryptor {

    public final static String DEFAULT_CHARSET  = "UTF-8";
    public final static String DEFAULT_ENCODING = "UU";
    public final static int DEFAULT_ITERATIONS  = 1000;                                                                 // minimum recommended per NIST

    private final static SecureRandom secureRandom = new SecureRandom();


    /**
     * @param input - the clear-text input to be encrypted, or the encrypted input to be decrypted
     * @param key - the encryption key
     * @param algorithm - algorithm in JCE scheme
     * @param ivOrSalt - Initialization Vector for algorithms with Feedback Mode that is not ECB, or Salt for Password Based Encryption algorithms
     * @param iterations - number of Iterations for Password Based Encryption algorithms (recommended minimum value is 1000)
     * @param doDecrypt - the Operation Type, pass false for Encrypt or true for Decrypt
     * @return
     * @throws PageException
     */
    static byte[] crypt( byte[] input, String key, String algorithm, byte[] ivOrSalt, int iterations, boolean doDecrypt ) throws PageException {

        byte[] result = null;
        Key secretKey = null;
        AlgorithmParameterSpec params = null;

        String  algo  = algorithm;
        boolean isFBM = false, isPBE = StringUtil.startsWithIgnoreCase( algo, "PBE" );
        int ivsLen = 0, algoDelimPos = algorithm.indexOf( '/' );

        if ( algoDelimPos > -1 ) {

            algo  = algorithm.substring( 0, algoDelimPos );
            isFBM = !StringUtil.startsWithIgnoreCase( algorithm.substring( algoDelimPos + 1 ), "ECB" );
        }

        try {

            Cipher cipher = Cipher.getInstance( algorithm );

            if ( ivOrSalt == null ) {

                if ( isPBE || isFBM ) {

                    ivsLen   = cipher.getBlockSize();
                    ivOrSalt = new byte[ ivsLen ];

                    if ( doDecrypt )
                        System.arraycopy( input, 0, ivOrSalt, 0, ivsLen );
                    else
                        secureRandom.nextBytes( ivOrSalt );
                }
            }

            if ( isPBE ) {

                secretKey  = SecretKeyFactory.getInstance( algorithm ).generateSecret( new PBEKeySpec( key.toCharArray() ) );
                params     = new PBEParameterSpec( ivOrSalt, iterations > 0 ? iterations : DEFAULT_ITERATIONS );        // set Salt and Iterations for PasswordBasedEncryption
            }
            else {

                secretKey  = new SecretKeySpec( Coder.decode( Coder.ENCODING_BASE64, key ), algo );
                if ( isFBM )
                    params = new IvParameterSpec( ivOrSalt );                                                           // set Initialization Vector for non-ECB Feedback Mode
            }

            if ( doDecrypt ) {

                cipher.init( Cipher.DECRYPT_MODE, secretKey, params );

                result = cipher.doFinal( input, ivsLen, input.length - ivsLen );
            }
            else {

                cipher.init( Cipher.ENCRYPT_MODE, secretKey, params );

                result = new byte[ ivsLen + cipher.getOutputSize( input.length ) ];

                if ( ivsLen > 0 )
                    System.arraycopy( ivOrSalt, 0, result, 0, ivsLen );

                cipher.doFinal( input, 0, input.length, result, ivsLen );
            }

            return result;
        }
        catch ( Throwable t ) {

            throw Caster.toPageException( t );
        }
    }


    /**
     * an encrypt method that takes a byte-array for input and returns an encrypted byte-array
     */
    public static byte[] encrypt(byte[] input, String key, String algorithm, byte[] ivOrSalt, int iterations) throws PageException {

        return crypt( input, key, algorithm, ivOrSalt, iterations, false );
    }


    /**
     * an encrypt method that takes a clear-text String for input and returns an encrypted, encoded, String
     */
    public static String encrypt(String input, String key, String algorithm, byte[] ivOrSalt, int iterations, String encoding, String charset) throws PageException {

        try {

            if ( charset  == null )     charset  = DEFAULT_CHARSET;
            if ( encoding == null )     encoding = DEFAULT_ENCODING;

            byte[] baInput = input.getBytes( charset );
            byte[] encrypted = encrypt( baInput, key, algorithm, ivOrSalt, iterations );

            return Coder.encode( encoding, encrypted );
        }
        catch ( Throwable t ) {

            throw Caster.toPageException( t );
        }
    }


    /**
     * a decrypt method that takes an encrypted byte-array for input and returns an unencrypted byte-array
     */
    public static byte[] decrypt(byte[] input, String key, String algorithm, byte[] ivOrSalt, int iterations) throws PageException {

        return crypt( input, key, algorithm, ivOrSalt, iterations, true );
    }


    /**
     * a decrypt method that takes an encrypted, encoded, String for input and returns a clear-text String
     */
    public static String decrypt(String input, String key, String algorithm, byte[] ivOrSalt, int iterations, String encoding, String charset) throws PageException {

        try {

            if ( charset  == null )     charset  = DEFAULT_CHARSET;
            if ( encoding == null )     encoding = DEFAULT_ENCODING;

            byte[] baInput = Coder.decode( encoding, input );
            byte[] decrypted = decrypt( baInput, key, algorithm, ivOrSalt, iterations );

            return new String( decrypted, charset );
        }
        catch ( Throwable t ) {

            throw Caster.toPageException( t );
        }
    }
}
