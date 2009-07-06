package railo.transformer.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



/**
 * Class Hash produces a MessageDigest hash for a given string.
 */
public final class Hash {
	private String plainText;
	private String algorithm;


	/**
	 * Method Hash.
	 * @param plainText
	 * @param algorithm The algorithm to use like MD2, MD5, SHA-1, etc.
	 */
	public Hash(String plainText, String algorithm) {
		super();
		setPlainText(plainText);
		setAlgorithm(algorithm);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String hashText = null;

		try {
			hashText = Hash.getHashText(this.plainText, this.algorithm);
		}
		catch (NoSuchAlgorithmException nsae) {
			System.err.println(nsae.getLocalizedMessage());
		}

		return hashText;
	}

	/**
	 * Method getHashText.
	 * @param plainText
	 * @param algorithm The algorithm to use like MD2, MD5, SHA-1, etc.
	 * @return String
	 * @throws NoSuchAlgorithmException
	 */
	public static String getHashText(String plainText, String algorithm)
		throws NoSuchAlgorithmException {
		MessageDigest mdAlgorithm = MessageDigest.getInstance(algorithm);

		mdAlgorithm.update(plainText.getBytes());

		byte[] digest = mdAlgorithm.digest();
		StringBuffer hexString = new StringBuffer();

		for (int i = 0; i < digest.length; i++) {
			plainText = Integer.toHexString(0xFF & digest[i]);

			if (plainText.length() < 2) {
				plainText = "0" + plainText;
			}

			hexString.append(plainText);
		}

		return hexString.toString();
	}

	/**
	 * Returns the algorithm.
	 * @return String
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * Returns the plainText.
	 * @return String
	 */
	public String getPlainText() {
		return plainText;
	}

	/**
	 * Sets the algorithm.
	 * @param algorithm The algorithm to set
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Sets the plainText.
	 * @param plainText The plainText to set
	 */
	public void setPlainText(String plainText) {
		this.plainText = plainText;
	}

}