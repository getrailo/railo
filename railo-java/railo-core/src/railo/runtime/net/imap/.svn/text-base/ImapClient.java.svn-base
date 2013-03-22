package railo.runtime.net.imap;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import railo.runtime.net.mail.MailClient;
import railo.runtime.op.Caster;

import com.sun.mail.imap.IMAPFolder;

public final class ImapClient extends MailClient {

	public ImapClient(String server, int port, String username, String password) {
		super(server, port, username, password);
	}

	protected String getId(Folder folder,Message message) throws MessagingException {
		return Caster.toString(((IMAPFolder)folder).getUID(message));
	}

	protected String getTypeAsString() {
		return "imap";
	}

	protected int getType() {
		return TYPE_IMAP;
	}
	
}