package railo.runtime.crypt;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import railo.runtime.coder.Coder;
import railo.runtime.coder.CoderException;

   /**
   * This program generates a AES key, retrieves its raw bytes, and
   * then reinstantiates a AES key from the key bytes.
   * The reinstantiated key is used to initialize a AES cipher for
   * encryption and decryption.
   */

   public class Cryptor {

   	public static byte[] encrypt(String type, String key, String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
   		return encrypt(type, key, message.getBytes());
   	}
  	public static byte[] encrypt(String type, String key, byte[] message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
  		type=type.toUpperCase();
  		Key k = new SecretKeySpec( decodeKey(key), type );
        Cipher c = Cipher.getInstance(type);
        c.init( Cipher.ENCRYPT_MODE, k );
        return c.doFinal(message);
  	}



	public static byte[] decrypt(String type, String key, String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return decrypt(type, key, message.getBytes());
 	}
 	public static byte[] decrypt(String type, String key, byte[] message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
 		type=type.toUpperCase();
 		
 		Cipher c = Cipher.getInstance(type);
        Key k = new SecretKeySpec( decodeKey(key), type );
        c.init( Cipher.DECRYPT_MODE, k );
        return c.doFinal(message);
 	}
 	


 	private static byte[] decodeKey(String key) {
 		try {
			return Coder.decode(Coder.ENCODING_BASE64, key);
		}
  		catch (CoderException e) {
  			return key.getBytes();
		}
	}
}