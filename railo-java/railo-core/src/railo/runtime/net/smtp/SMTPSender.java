package railo.runtime.net.smtp;

import javax.mail.Transport;

import railo.commons.io.SystemUtil;
import railo.runtime.net.smtp.SMTPClient.MimeMessageAndSession;


public final class SMTPSender extends Thread {

	private boolean hasSended=false;
	private Throwable throwable;
	private Object lock;
	private String host;
	private int port;
	private String user;
	private String pass;
	private MimeMessageAndSession mmas;
	
	public SMTPSender(Object lock, MimeMessageAndSession mmas, String host, int port, String user, String pass) {
		this.lock=lock;
		this.mmas=mmas;

		this.host=host;
		this.port=port;
		this.user=user;
		this.pass=pass;
	}
	
	@Override
	public void run() {
		Transport transport = null;
        try {
        	transport = mmas.session.transport;//SMTPConnectionPool.getTransport(session,host,port,user,pass);
        	if(user==null)pass=null;
        	// connect
    		if(!transport.isConnected())
    			transport.connect(host,port,user,pass);

        	
			mmas.message.saveChanges();  
			transport.sendMessage(mmas.message, mmas.message.getAllRecipients());
			hasSended=true;
		} 
		catch (Throwable t) {
			this.throwable=t;
		}
		finally {
			try {SMTPConnectionPool.releaseSessionAndTransport(mmas.session);}catch (Throwable t) {}
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
