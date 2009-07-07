package railo.runtime.tag;

import railo.runtime.net.mail.MailClient;

/**
 * Retrieves and deletes e-mail messages from a POP mail server.
 */
public final class Pop extends _Mail {

	/**
	 * @see railo.runtime.tag._Mail#getDefaultPort()
	 */
	protected int getDefaultPort() {
		return 110;
	}

	/**
	 * @see railo.runtime.tag._Mail#getTagName()
	 */
	protected String getTagName() {
		return "Pop";
	}

	/**
	 * @see railo.runtime.tag._Mail#getType()
	 */
	protected int getType() {
		return MailClient.TYPE_POP3;
	}
    
}