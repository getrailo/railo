package railo.runtime.net.smtp;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import railo.commons.io.SystemUtil;


public final class SMTPSender extends Thread {

	private boolean hasSended=false;
	private MimeMessage message;
	private Throwable throwable;
	private Object lock;
	private String host;
	private int port;
	private String user;
	private String pass;
	private Session session;
	
	public SMTPSender(Object lock, MimeMessage message, Session session, String host, int port, String user, String pass) {
		this.lock=lock;
		this.message=message;
		this.session=session;

		this.host=host;
		this.port=port;
		this.user=user;
		this.pass=pass;
	}
	
	/**
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		Transport transport = null;
        try {
        	transport = SMTPConnectionPool.getTransport(session,host,port,user,pass);
			message.saveChanges();  
			transport.sendMessage(message, message.getAllRecipients());
			hasSended=true;
		} 
		catch (Throwable t) {
			this.throwable=t;
		}
		finally {
			try {SMTPConnectionPool.releaseTransport(session,transport);}catch (Throwable t) {}
			SystemUtil.notify(lock);
		}
	}

	/**
	 * @return the messageExpection
	 */
	public Throwable getThrowable() {
		return throwable;
	}

	/**
	 * @return is message sended
	 */
	public boolean hasSended() {
		return hasSended;
	}

}
