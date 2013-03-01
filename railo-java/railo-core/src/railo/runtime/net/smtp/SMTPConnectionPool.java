package railo.runtime.net.smtp;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Stack;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;

public class SMTPConnectionPool {

	private static final long MAX_AGE = 5*60*1000;
	private static Map<String,Stack<SessionAndTransport>> sessions=new HashMap<String, Stack<SessionAndTransport>>();
	

	public static SessionAndTransport getSessionAndTransport(Properties props, String key, Authenticator auth) throws MessagingException{
		
	   // Session
		SessionAndTransport sat=null;
		Stack<SessionAndTransport> satStack = getSATStack(key);
		sat=pop(satStack);
		
		// when sat still valid return it
		if(sat!=null)	{
			if(sat.lastAccess+MAX_AGE>System.currentTimeMillis()) {
				return sat.touch();
			}
			disconnect(sat.transport);
		}
		
		return new SessionAndTransport(key, props, auth);
	}


	public static void releaseSessionAndTransport(SessionAndTransport sat) {

		getSATStack(sat.key).add(sat.touch());
	}
	
	public static String listSessions() {
		Iterator<Entry<String, Stack<SessionAndTransport>>> it = sessions.entrySet().iterator();
		Entry<String, Stack<SessionAndTransport>> entry;
		Stack<SessionAndTransport> stack;
		StringBuilder sb=new StringBuilder();
		while(it.hasNext()){
			entry = it.next();
			sb.append(entry.getKey()).append('\n');
			stack = entry.getValue();
			if(stack.isEmpty()) continue;
			listSessions(sb,stack);
		}
		return sb.toString();
	}
	
	private static void listSessions(StringBuilder sb, Stack<SessionAndTransport> stack) {
		Iterator<SessionAndTransport> it = stack.iterator();
		while(it.hasNext()){
			SessionAndTransport sat = it.next();
			sb.append("- "+sat.key+":"+new Date(sat.lastAccess)).append('\n');
		}
	}


	public static void closeSessions() {
		Iterator<Entry<String, Stack<SessionAndTransport>>> it = sessions.entrySet().iterator();
		Entry<String, Stack<SessionAndTransport>> entry;
		Stack<SessionAndTransport> oldStack;
		Stack<SessionAndTransport> newStack;
		while(it.hasNext()){
			entry = it.next();
			oldStack = entry.getValue();
			if(oldStack.isEmpty()) continue;
			newStack=new Stack<SMTPConnectionPool.SessionAndTransport>();
			entry.setValue(newStack);
			closeSessions(oldStack,newStack);
		}
	}
	
	private static void closeSessions(Stack<SessionAndTransport> oldStack,Stack<SessionAndTransport> newStack) {
		SessionAndTransport sat;
		while((sat=pop(oldStack))!=null){
			if(sat.lastAccess+MAX_AGE<System.currentTimeMillis()) {
				disconnect(sat.transport);
			}
			else
				newStack.add(sat);
		}	
	}
	
	private static void disconnect(Transport transport) {
		if(transport!=null && transport.isConnected()) {
			try {
				transport.close();
			} catch (MessagingException e) {}
		}
	}

	
	
	
	
	
	
	
	
	
	
	

	private static synchronized Stack<SessionAndTransport> getSATStack(String key) {
		Stack<SessionAndTransport> stack=sessions.get(key);
		if(stack==null) {
			stack=new Stack<SessionAndTransport>();
			sessions.put(key, stack);
		}
		return stack;
	}
	
	private static Session createSession(String key,Properties props, Authenticator auth) {
		if(auth!=null)return Session.getInstance(props,auth);
    	return Session.getInstance(props);
	}
	

	private static SessionAndTransport pop(Stack<SessionAndTransport> satStack) {
		try{
			return satStack.pop();
		}
		catch(Throwable t){}
		return null;
	}
	

	public static class SessionAndTransport {
		public final Session session;
		public final Transport transport;
		public final String key;
		private long lastAccess;
		
		private SessionAndTransport(String key, Properties props,Authenticator auth) throws NoSuchProviderException {
			this.key=key;
			this.session=createSession(key, props, auth);
			this.transport=session.getTransport("smtp");
			touch();
		}

		private SessionAndTransport touch() {
			this.lastAccess=System.currentTimeMillis();
			return this;
		}
	}
	
	
	
}
