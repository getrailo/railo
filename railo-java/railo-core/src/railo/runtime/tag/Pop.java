package railo.runtime.tag;

import railo.runtime.net.mail.MailClient;

/**
 * Retrieves and deletes e-mail messages from a POP mail server.
 */
public final class Pop extends _Mail {

	@Override
	protected int getDefaultPort() {
		return 110;
	}

	@Override
	protected String getTagName() {
		return "Pop";
	}

	@Override
	protected int getType() {
		return MailClient.TYPE_POP3;
	}
    
}