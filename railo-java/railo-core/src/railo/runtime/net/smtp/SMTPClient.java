package railo.runtime.net.smtp;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import railo.commons.activation.ResourceDataSource;
import railo.commons.collections.HashTable;
import railo.commons.io.log.LogAndSource;
import railo.commons.io.log.LogUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.SerializableObject;
import railo.commons.lang.StringUtil;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.net.mail.EmailNamePair;
import railo.runtime.net.mail.MailException;
import railo.runtime.net.mail.MailUtil;
import railo.runtime.net.mail.Server;
import railo.runtime.net.mail.ServerImpl;
import railo.runtime.net.proxy.Proxy;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.net.proxy.ProxyDataImpl;
import railo.runtime.op.Caster;
import railo.runtime.spooler.mail.MailSpoolerTask;
import railo.runtime.type.List;
import railo.runtime.type.util.ArrayUtil;

import com.sun.mail.smtp.SMTPMessage;

public final class SMTPClient implements Serializable  {

	

	private static final int SPOOL_UNDEFINED=0;
	private static final int SPOOL_YES=1;
	private static final int SPOOL_NO=2;

	private static final int SSL_NONE=0;
	private static final int SSL_YES=1;
	private static final int SSL_NO=2;

	private static final int TLS_NONE=0;
	private static final int TLS_YES=1;
	private static final int TLS_NO=2;
	
	private static final String TEXT_HTML = "text/html";
	private static final String TEXT_PLAIN = "text/plain";
	private static final SerializableObject LOCK = new SerializableObject();
	//private static final int PORT = 25; 
	
	private int spool=SPOOL_UNDEFINED;
	
	private int timeout=-1;
	
	private String plainText;
	private String plainTextCharset;
	
	private String htmlText;
	private String htmlTextCharset;

	private Attachment[] attachmentz;

	private String[] host;
	private String charset="UTF-8";
	private InternetAddress from;
	private InternetAddress[] tos;
	private InternetAddress[] bccs;
	private InternetAddress[] ccs;
	private InternetAddress[] rts;
	private InternetAddress[] fts;
	private String subject="";
	private String xmailer="Railo Mail";
	private Map headers=new HashTable();
	private int port=-1;

	private String username;
	private String password="";


	
	
	private int ssl=SSL_NONE;
	private int tls=TLS_NONE;
	
	ProxyData proxyData=new ProxyDataImpl();
	
	public void setSpoolenable(boolean spoolenable) {
		spool=spoolenable?SPOOL_YES:SPOOL_NO;
	}

	/**
	 * set port of the mailserver
	 * @param port
	 */
	public void setPort(int port) {
		this.port=port;
	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	
	public static ServerImpl toServerImpl(String server,int port, String usr,String pwd) throws MailException {
		int index;
		
		// username/password
		index=server.indexOf('@');
		if(index!=-1) {
			usr=server.substring(0,index);
			server=server.substring(index+1);
			index=usr.indexOf(':');
			if(index!=-1) {
				pwd=usr.substring(index+1);
				usr=usr.substring(0,index);
			}
		}
		
		// port
		index=server.indexOf(':');
		if(index!=-1) {
			try {
				port=Caster.toIntValue(server.substring(index+1));
			} catch (ExpressionException e) {
				throw new MailException(e.getMessage());
			}
			server=server.substring(0,index);
		}
		
		
		ServerImpl srv = ServerImpl.getInstance(server, port, usr, pwd, false, false);
		return srv;
	}
	
	
	/**
	 * @throws PageException 
	 * @throws MailException 
	 * @see mail.Mail#setHost(java.lang.String)
	 */
	public void setHost(String host) throws PageException {
		this.host = List.toStringArray(List.listToArrayRemoveEmpty(host, ','));
	} 

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	

	public void addHeader(String name, String value) {
		headers.put(name, value);
	}

	/**
	 * @see mail.Mail#addTo(javax.mail.internet.InternetAddress)
	 */
	public void addTo(InternetAddress to) {
		tos=add(tos,to);
	}

	/**
	 * @throws MailException 
	 * @throws UnsupportedEncodingException 
	 * @throws AddressException 
	 * @see mail.Mail#addTo(java.lang.String)
	 */
	public void addTo(String to) throws AddressException, UnsupportedEncodingException, MailException {
		InternetAddress[] tmp = EmailNamePair.toInternetAddress(to);
		for(int i=0;i<tmp.length;i++) {
			addTo(tmp[i]);
		}
	}

	/**
	 * @see mail.Mail#setFrom(javax.mail.internet.InternetAddress)
	 */
	public void setFrom(InternetAddress from) {
		this.from=from;
	}

	/**
	 * @throws MailException 
	 * @throws UnsupportedEncodingException 
	 * @throws AddressException 
	 * @see mail.Mail#setFrom(java.lang.String)
	 */
	public void setFrom(String from) throws AddressException, UnsupportedEncodingException, MailException {
		setFrom(EmailNamePair.toInternetAddress(from)[0]);
	}
	/**
	 * @see mail.Mail#addBCC(javax.mail.internet.InternetAddress)
	 */
	public void addBCC(InternetAddress bcc) {
		bccs=add(bccs,bcc);
	}

	/**
	 * @throws MailException 
	 * @throws UnsupportedEncodingException 
	 * @throws AddressException 
	 * @see mail.Mail#addBCC(java.lang.String)
	 */
	public void addBCC(String bcc) throws AddressException, UnsupportedEncodingException, MailException {
		InternetAddress[] tmp = EmailNamePair.toInternetAddress(bcc);
		for(int i=0;i<tmp.length;i++) {
			addBCC(tmp[i]);
		}
	}

	/**
	 * @see mail.Mail#addCC(javax.mail.internet.InternetAddress)
	 */
	public void addCC(InternetAddress cc) {
		ccs=add(ccs,cc);
	}

	/**
	 * @throws MailException 
	 * @throws UnsupportedEncodingException 
	 * @throws AddressException 
	 * @see mail.Mail#addCC(java.lang.String)
	 */
	public void addCC(String cc) throws AddressException, UnsupportedEncodingException, MailException {
		InternetAddress[] tmp = EmailNamePair.toInternetAddress(cc);
		for(int i=0;i<tmp.length;i++) {
			addCC(tmp[i]);
		}
	}
	
	/**
	 * @see mail.Mail#addReplyTo(javax.mail.internet.InternetAddress)
	 */
	public void addReplyTo(InternetAddress rt) {
		rts=add(rts,rt);
	}

	/**
	 * @throws MailException 
	 * @throws UnsupportedEncodingException 
	 * @throws AddressException 
	 * @see mail.Mail#addReplyTo(java.lang.String)
	 */
	public void addReplyTo(String rt) throws AddressException, UnsupportedEncodingException, MailException {
		InternetAddress[] tmp = EmailNamePair.toInternetAddress(rt);
		for(int i=0;i<tmp.length;i++) {
			addReplyTo(tmp[i]);
		}
	}
	
	/**
	 * @see mail.Mail#addFailTo(javax.mail.internet.InternetAddress)
	 */
	public void addFailTo(InternetAddress ft) {
		fts=add(fts,ft);
	}

	/**
	 * @throws MailException 
	 * @throws UnsupportedEncodingException 
	 * @throws AddressException 
	 * @see mail.Mail#addFailTo(java.lang.String)
	 */
	public void addFailTo(String ft) throws AddressException, UnsupportedEncodingException, MailException {
		InternetAddress[] tmp = EmailNamePair.toInternetAddress(ft);
		for(int i=0;i<tmp.length;i++) {
			addFailTo(tmp[i]);
		}
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * @see mail.Mail#setSubject(java.lang.String)
	 */
	public void setSubject(String subject) {
		this.subject=subject;
	}
	
	/**
	 * @see mail.Mail#setXMailer(java.lang.String)
	 */
	public void setXMailer(String xmailer) {
		this.xmailer=xmailer;
	}

	/**
	 * creates a new expanded array and return it;
	 * @param oldArr
	 * @param newValue
	 * @return new expanded array
	 */
	protected static InternetAddress[] add(InternetAddress[] oldArr, InternetAddress newValue) {
		if(oldArr==null) return new InternetAddress[] {newValue};
		//else {
		InternetAddress[] tmp=new InternetAddress[oldArr.length+1];
		for(int i=0;i<oldArr.length;i++) {
			tmp[i]=oldArr[i];
		}
		tmp[oldArr.length]=newValue;	
		return tmp;
		//}
	}
	
	protected static Attachment[] add(Attachment[] oldArr, Attachment newValue) {
		if(oldArr==null) return new Attachment[] {newValue};
		//else {
		Attachment[] tmp=new Attachment[oldArr.length+1];
			for(int i=0;i<oldArr.length;i++) {
				tmp[i]=oldArr[i];
			}
			tmp[oldArr.length]=newValue;	
			return tmp;
		//}
	}

	private class MimeMessageAndSession {
		private MimeMessage message;
		private Session session;

		public MimeMessageAndSession(MimeMessage message,Session session){
			this.message=message;
			this.session=session;
		}
	}
	
	private MimeMessageAndSession createMimeMessage(railo.runtime.config.Config config,String hostName, int port, String username, String password,
			boolean tls,boolean ssl) throws MessagingException {
		
	      Properties props = System.getProperties();

	      props.put("mail.smtp.host", hostName);
	      props.put("mail.smtp.timeout", Caster.toString(timeout));
	      props.put("mail.smtp.connectiontimeout", Caster.toString(timeout));
	      if(port>0){
	    	  props.put("mail.smtp.port", Caster.toString(port));
	      }
	      if(ssl)	{
              props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	    	  props.put("mail.smtp.socketFactory.port", Caster.toString(port));
              props.put("mail.smtp.socketFactory.fallback", "false");
          }
          else {
        	  props.put("mail.smtp.socketFactory.class", "javax.net.SocketFactory");
	    	  props.remove("mail.smtp.socketFactory.port");
              props.remove("mail.smtp.socketFactory.fallback");
          }
	      Authenticator auth=null;
	      if(!StringUtil.isEmpty(username)) {
	    	  props.put("mail.smtp.auth", "true"); 
	    	  props.put("mail.smtp.starttls.enable",tls?"true":"false");
	    	  
	    	  props.put("mail.smtp.user", username);
	    	  props.put("mail.smtp.password", password);
	    	  props.put("password", password);
	    	  auth=new SMTPAuthenticator( username, password );
	      }
	      else {
	    	  props.put("mail.smtp.auth", "false"); 
	    	  props.remove("mail.smtp.starttls.enable");
	    	  
	    	  props.remove("mail.smtp.user");
	    	  props.remove("mail.smtp.password");
	    	  props.remove("password");
	      }
	      
	      
	      Session session = null;
	      /*try {
	    	  if(auth!=null)session=Session.getDefaultInstance(props, auth);
	    	  else session=Session.getDefaultInstance(props);
	      }catch(Throwable t){}
	      */
	      //if(session==null)	{
	          if(auth!=null)session=Session.getInstance(props,auth);
	    	  else session=Session.getInstance(props);
	      //}
	    	 
		  SMTPMessage msg = new SMTPMessage(session);
		  
		
	// Contacts
		if(from==null)throw new MessagingException("you have do define the from for the mail"); 
		if(tos==null)throw new MessagingException("you have do define the to for the mail"); 
		
		checkAddress(from,charset);
		checkAddress(tos,charset);
		
		msg.setFrom(from);
		msg.setRecipients(Message.RecipientType.TO, tos);
	    if(ccs!=null){
			checkAddress(ccs,charset);
	    	msg.setRecipients(Message.RecipientType.CC, ccs);
	    }
	    if(bccs!=null){
			checkAddress(bccs,charset);
	    	msg.setRecipients(Message.RecipientType.BCC, bccs);
	    }
	    if(rts!=null){
			checkAddress(rts,charset);
	    	msg.setReplyTo(rts);
	    }
	    if(fts!=null){
			checkAddress(fts,charset);
	    	msg.setEnvelopeFrom(fts[0].toString());
	    }
	    
	// Subject and headers
	    try {
			msg.setSubject(MailUtil.encode(subject, charset));
		} catch (UnsupportedEncodingException e) {
			throw new MessagingException("the encoding "+charset+" is not supported");
		}
	    msg.setHeader("X-Mailer", xmailer);
	    msg.setSentDate(new Date());
			
	// Headers
	    Iterator it = headers.keySet().iterator();
	    String key;
	    while(it.hasNext()) {
	    	key = (String)it.next();
	    	msg.setHeader(key, (String)headers.get(key));
	    }
	    MimeBodyPart content=null;
		
		// Only HTML
		if(plainText==null) {
			content=getHTMLText();
		}
		// only Plain
		else if(htmlText==null) {
			content=getPlainText();
		}
		// Plain and HTML
		else {
			Multipart mp=new MimeMultipart("alternative");
			mp.addBodyPart(getPlainText());
			mp.addBodyPart(getHTMLText());
			content=new MimeBodyPart();
			content.setContent(mp);
		}

		MimeMultipart mp = new MimeMultipart();
		mp.addBodyPart(content);
		// Attachments
		if(attachmentz!=null) {
			for(int i=0;i<attachmentz.length;i++) {
				mp.addBodyPart(toMimeBodyPart(config,attachmentz[i]));	
			}	
		}		
		msg.setContent(mp);
	    
		return new MimeMessageAndSession(msg,session);
	}
	
	
	private void checkAddress(InternetAddress[] ias,String charset) { // DIFF 23
		for(int i=0;i<ias.length;i++) {
			checkAddress(ias[i], charset);
		}
	}
	private void checkAddress(InternetAddress ia,String charset) { // DIFF 23
		try {
			if(!StringUtil.isEmpty(ia.getPersonal())) {
				String personal = MailUtil.encode(ia.getPersonal(), charset);
				if(!personal.equals(ia.getPersonal()))
					ia.setPersonal(personal);
			}
		} catch (UnsupportedEncodingException e) {}
	}

	/**
	 * @param plainText
	 */
	public void setPlainText(String plainText) {
		this.plainText=plainText;
		this.plainTextCharset=charset;
	}
	
	/**
	 * @param plainText
	 * @param plainTextCharset
	 */
	public void setPlainText(String plainText, String plainTextCharset) {
		this.plainText=plainText;
		this.plainTextCharset=plainTextCharset;
	}
	
	/**
	 * @param htmlText
	 */
	public void setHTMLText(String htmlText) {
		this.htmlText=htmlText;
		this.htmlTextCharset=charset;
	}



	public boolean hasHTMLText() {
		return htmlText!=null;
	}

	public boolean hasPlainText() {
		return plainText!=null;
	}
	
	/**
	 * @param htmlText
	 * @param htmlTextCharset
	 */
	public void setHTMLText(String htmlText, String htmlTextCharset) {
		this.htmlText=htmlText;
		this.htmlTextCharset=htmlTextCharset;
	}
	
	public void addAttachment(URL url) {
		Attachment mbp = new Attachment(url);
		attachmentz=add(attachmentz, mbp);
	}

	public void addAttachment(Resource resource, String type, String disposition, String contentID) {
		Attachment att = new Attachment(resource, type, disposition, contentID);
		attachmentz=add(attachmentz, att);
	}
	
	public MimeBodyPart toMimeBodyPart(railo.runtime.config.Config config,Attachment att) throws MessagingException  {
		
		MimeBodyPart mbp = new MimeBodyPart();
		
		// set Data Source
		String strRes = att.getResource();
		if(!StringUtil.isEmpty(strRes)){
			
			mbp.setDataHandler(new DataHandler(new ResourceDataSource(config.getResource(strRes))));
		}
		else mbp.setDataHandler(new DataHandler(new URLDataSource2(att.getURL())));
		
		mbp.setFileName(att.getFileName());
		if(!StringUtil.isEmpty(att.getType())) mbp.setHeader("Content-Type", att.getType());
		if(!StringUtil.isEmpty(att.getDisposition()))mbp.setDisposition(att.getDisposition());
		if(!StringUtil.isEmpty(att.getContentID()))mbp.setContentID(att.getContentID());
			
		return mbp;
	}
	
	/**
	 * @param is
	 * @throws MessagingException
	 * @throws FileNotFoundException 
	 */
	public void addAttachment(Resource file) throws MessagingException {
		addAttachment(file,null,null,null);
	}
	

	
	
	public void send(ConfigImpl config) throws MailException {
		if(ArrayUtil.isEmpty(config.getMailServers()) && ArrayUtil.isEmpty(host))
			throw new MailException("no SMTP Server defined");
		
		if(plainText==null && htmlText==null)
			throw new MailException("you must define plaintext or htmltext");
		
		if(timeout<1)timeout=config.getMailTimeout()*1000;
		
		if(spool==SPOOL_YES || (spool==SPOOL_UNDEFINED && config.isMailSpoolEnable())) {
        	config.getSpoolerEngine().add(new MailSpoolerTask(this));
        }
		else
			_send(config);
	}
	

	public void _send(railo.runtime.config.Config config) throws MailException {
		try {

        	Proxy.start(proxyData);
		
		LogAndSource log = config.getMailLogger();
		// Server
        Server[] servers = config.getMailServers();
        if(host!=null) {
        	int prt;
        	String usr,pwd;
        	ServerImpl[] nServers = new ServerImpl[host.length];
        	for(int i=0;i<host.length;i++) {
        		usr=null;pwd=null;
        		prt=ServerImpl.DEFAULT_PORT;
        		
        		if(port>0)prt=port;
        		if(!StringUtil.isEmpty(username))	{
        			usr=username;
        			pwd=password;
        		}
        		
        		nServers[i]=toServerImpl(host[i],prt,usr,pwd);
				if(ssl==SSL_YES) nServers[i].setSSL(true);
        		if(tls==TLS_YES) nServers[i].setTLS(true);
        			
        	}
        	servers=nServers;
        }
		if(servers.length==0) {
			return;
			//throw new MailException("no SMTP Server defined");
		}
		
		boolean _ssl,_tls;
		for(int i=0;i<servers.length;i++) {

			Server server = servers[i];
			String _username=null,_password="";
			//int _port;
			
			// username/password
			/*if(username!=null && username.length()>0) {
				_username=username;
				_password=password;
			}
			else */
			if(server.hasAuthentication()) {
				_username=server.getUsername();
				_password=server.getPassword();
			}
			
			/*
			if(port>0)_port=port;
			else if(server.getPort()>0)_port=server.getPort();
			else _port=PORT;
			*/
			// tls
			if(tls!=TLS_NONE)_tls=tls==TLS_YES;
			else _tls=((ServerImpl)server).isTLS();
	
			// ssl
			if(ssl!=SSL_NONE)_ssl=ssl==SSL_YES;
			else _ssl=((ServerImpl)server).isSSL();
			
			
			MimeMessageAndSession msgAsess;
			
			synchronized(LOCK) {
			
				try {
					msgAsess = createMimeMessage(config,server.getHostName(),server.getPort(),_username,_password,_tls,_ssl);
				} catch (MessagingException e) {
					log.error("mail",LogUtil.toMessage(e));
					throw new MailException(e.getMessage());
				}
				
	            try {
	            	Transport tr = msgAsess.session.getTransport("smtp");
            		SMTPSender sender=new SMTPSender(LOCK,tr,msgAsess.message,server.getHostName(),server.getPort(),_username,_password);
            		sender.start();
            		LOCK.wait(timeout);
                	if(!sender.hasSended()) {
                		if(sender.hasError()) throw sender.getMessageExpection();
                		throw new MessagingException("timeout occurred after "+(timeout/1000)+" seconds while sending mail message");
                	}
	            	log.info("mail","send mail");
					break;
				} 
	            catch (Exception me) {
					log.error("mail spooler",me.getMessage());
					if(i+1==servers.length) {
						String msg=me.getMessage();
						if(StringUtil.isEmpty(msg))msg=Caster.toClassName(me);
	                    throw new MailException(server.getHostName()+" "+msg);
	                }
				}
			}
		}
		}
		finally {
        	Proxy.end();
		}
	}

	private MimeBodyPart getHTMLText() throws MessagingException {
		MimeBodyPart html = new MimeBodyPart();
		html.setDataHandler(new DataHandler(new StringDataSource(htmlText,TEXT_HTML ,htmlTextCharset)));
		html.setHeader("Content-Transfer-Encoding", "quoted-printable");
		html.setHeader("Content-Type", TEXT_HTML+"; charset="+htmlTextCharset);
		return html;
	}

	private MimeBodyPart getPlainText() throws MessagingException {
		MimeBodyPart plain = new MimeBodyPart();
		plain.setDataHandler(new DataHandler(new StringDataSource(plainText,TEXT_PLAIN ,plainTextCharset)));
		plain.setHeader("Content-Transfer-Encoding", "quoted-printable");
		plain.setHeader("Content-Type", TEXT_PLAIN+"; charset="+plainTextCharset);
		return plain;
	}

	/**
	 * @return the proxyData
	 */
	public ProxyData getProxyData() {
		return proxyData;
	}

	/**
	 * @param proxyData the proxyData to set
	 */
	public void setProxyData(ProxyData proxyData) {
		this.proxyData = proxyData;
	}

	/**
	 * @param ssl the ssl to set
	 */
	public void setSSL(boolean ssl) {
		this.ssl = ssl?SSL_YES:SSL_NO;
	}

	/**
	 * @param tls the tls to set
	 */
	public void setTLS(boolean tls) {
		this.tls = tls?TLS_YES:TLS_NO;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @return the from
	 */
	public InternetAddress getFrom() {
		return from;
	}

	/**
	 * @return the tos
	 */
	public InternetAddress[] getTos() {
		return tos;
	}

	/**
	 * @return the bccs
	 */
	public InternetAddress[] getBccs() {
		return bccs;
	}

	/**
	 * @return the ccs
	 */
	public InternetAddress[] getCcs() {
		return ccs;
	}
}
