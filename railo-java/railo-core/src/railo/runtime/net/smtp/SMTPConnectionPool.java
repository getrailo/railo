package railo.runtime.net.smtp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Stack;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

public class SMTPConnectionPool {
	
	private static final long MAX_AGE = 5*60*1000;
	private static Map<String,Stack<TransportWrap>> transports=new HashMap<String, Stack<TransportWrap>>();
	private static Map<String,Session> sessions=new HashMap<String, Session>();
	
	public synchronized static Transport getTransport(Session session, String host, int port, String user, String pass) throws MessagingException {
		String key=""+session.getProperties().hashCode();
		
		
		Stack<TransportWrap> stack = getStack(key);
		
		// create/get transport
		TransportWrap transportWrap=null;
		if(!stack.isEmpty()) transportWrap = stack.pop();
		if(transportWrap==null) transportWrap = new TransportWrap(session.getTransport("smtp"));
		
		// connect
		if(!transportWrap.transport.isConnected())
			transportWrap.transport.connect(host,port,user,pass);
		
		return transportWrap.transport;
	}



	public static void releaseTransport(Session session, Transport transport) {
		String key=""+session.getProperties().hashCode();
		Stack<TransportWrap> stack = getStack(key);
		
		stack.add(new TransportWrap(transport));
		//if(stack.isEmpty())
		//else disconnect(transport);
	}
	
	private static Stack<TransportWrap> getStack(String key) {
		Stack<TransportWrap> stack = transports.get(key);
		if(stack==null) {
			stack=new Stack<TransportWrap>();
			transports.put(key, stack);
		}
		return stack;
	}

	public static void closeTransports() {
		Iterator<Entry<String, Stack<TransportWrap>>> it = transports.entrySet().iterator();
		Stack<TransportWrap> stack;
		TransportWrap wrap;
		Entry<String, Stack<TransportWrap>> entry;
		while(it.hasNext()){
			entry = it.next();
			stack = entry.getValue();
			// only one Trans
			synchronized (stack) {
				while(stack.size()>1) {
					disconnect(stack.pop().transport);
				}
				if(!stack.isEmpty()) {
					wrap = stack.pop();
					if((wrap.stored+MAX_AGE)<System.currentTimeMillis()) {
						disconnect(wrap.transport);
					}
					else{
						stack.add(wrap);
					}
				}
			}
		}
	}
	
	public static void disconnect(Transport transport) {
		if(transport!=null && transport.isConnected()) {
			try {
				transport.close();
			} catch (MessagingException e) {}
		}
	}
	
	private static class TransportWrap {
		private Transport transport;
		private long stored;
		public TransportWrap(Transport transport) {
			this.transport=transport;
			this.stored=System.currentTimeMillis();
		}
	}

	public static Session getSession(Properties props, Authenticator auth) {
		String key=""+props.hashCode();
	    Session session=sessions.get(key);
	      
	    if(session==null)	{
	    	if(auth!=null)session=Session.getInstance(props,auth);
	    	else session=Session.getInstance(props);
	        sessions.put(key, session);
	     }
	     return session;
	}
}
