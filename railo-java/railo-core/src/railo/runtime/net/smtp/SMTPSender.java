package railo.runtime.net.smtp;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;


public final class SMTPSender extends Thread {

	private Transport transport;
	private boolean hasSended=false;
	private boolean hasError=false;
	private MimeMessage message;
	private MessagingException messageExpection;
	private Object lock;
	private String host;
	private int port;
	private String user;
	private String pass;
	
	public SMTPSender(Object lock, Transport transport, MimeMessage message, String host, int port, String user, String pass) {
		this.lock=lock;
		this.transport=transport;
		this.message=message;

		this.host=host;
		this.port=port;
		this.user=user;
		this.pass=pass;
	}

	/**
	 * @return is message sended
	 */
	public boolean hasSended() {
		return hasSended;
	}
	/**
	 * @return is error occurred
	 */
	public boolean hasError() {
		return hasError;
	}
	
	public void run() {
		try {
			//transport.connect();
			transport.connect(host,port,user,pass);
			message.saveChanges();  
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
	    	hasSended=true;
		} 
		catch (MessagingException me) {
			//print.ln("error			"+System.currentTimeMillis());
			hasError=true;
			this.messageExpection=me;
		}
		finally {
			//print.ln("done			"+System.currentTimeMillis());
			synchronized(lock) {
				lock.notify();
			}
		}
	}

	/**
	 * @return the messageExpection
	 */
	public MessagingException getMessageExpection() {
		return messageExpection;
	}

}
