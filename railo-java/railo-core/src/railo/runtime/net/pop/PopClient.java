package railo.runtime.net.pop;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import railo.runtime.net.mail.MailClient;

import com.sun.mail.pop3.POP3Folder;

public final class PopClient extends MailClient {

	public PopClient(String server, int port, String username, String password) {
		super(server, port, username, password);
	}

	protected String getId(Folder folder,Message message) throws MessagingException {
		return ((POP3Folder)folder).getUID(message);
	}

	protected String getTypeAsString() {
		return "pop3";
	}

	protected int getType() {
		return TYPE_POP3;
	}
	
}