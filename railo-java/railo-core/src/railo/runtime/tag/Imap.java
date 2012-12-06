package railo.runtime.tag;

import railo.runtime.net.mail.MailClient;

public class Imap extends _Mail {

	@Override
	protected int getDefaultPort() {
		return 143;
	}

	@Override
	protected String getTagName() {
		return "Imap";
	}

	@Override
	protected int getType() {
		return MailClient.TYPE_IMAP;
	}

}
