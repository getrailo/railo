package railo.runtime.net.proxy;



import java.util.Properties;

import railo.commons.lang.StringUtil;
import railo.commons.lang.lock.KeyLock;
import railo.commons.lang.lock.KeyLockListener;

public final class Proxy {
	
	//private static Map map=new HashTable();
	private static KeyLock kl=new KeyLock();
	public static void start(ProxyData proxyData) {
		start(proxyData.getServer(),proxyData.getPort(),proxyData.getUsername(),proxyData.getPassword());
	}
	
	public static void start(String server, int port, String user, String password) {
		String key=StringUtil.toString(server, "")+":"+StringUtil.toString(port+"", "")+":"+StringUtil.toString(user, "")+":"+StringUtil.toString(password, "");
		kl.setListener(new ProxyListener(server,port,user,password));
		kl.start(key);
	}
	
	public static void end() {
		kl.end();
	}
	
	
	

	/*public static void main(String[] args) throws Exception {
	
		new ProxyThread(1,"203.144.160.247",8080,null,null).start();
		new ProxyThread(2,"203.144.160.247",8080,null,null).start();
		new ProxyThread(3,"202.144.160.247",8080,null,null).start();
		new ProxyThread(4,"202.144.160.247",8080,null,null).start();
		new ProxyThread(5,"204.144.160.247",8080,null,null).start();
	}*/
}

class ProxyListener implements KeyLockListener {

	private String server;
	private int port;
	private String user;
	private String password;
	
	public ProxyListener(String server, int port, String user, String password) {
		this.server=server;
		this.port=port;
		this.user=user;
		this.password=password;
	}

	public void onStart(String key,boolean isFirst) {
		//print.ln(" start:"+key+" _ "+isFirst);	
		if(!isFirst) return;
		
		Properties props = System.getProperties();
		if(!StringUtil.isEmpty(server)) {
			// Server
			props.setProperty("socksProxyHost",	server);
			props.setProperty("http.proxyHost",	server);
			props.setProperty("https.proxyHost",server);
			props.setProperty("ftp.proxyHost", 	server);
			props.setProperty("smtp.proxyHost", server);
			
			// Port
			if(port>0) {
				String strPort=	String.valueOf(port);
				props.setProperty("socksProxyPort",strPort);
				props.setProperty("http.proxyPort",strPort);
				props.setProperty("https.proxyPort",strPort);
				props.setProperty("ftp.proxyPort",strPort);
				props.setProperty("smtp.proxyPort",strPort);
			}
			else removePort(props);
			
			if(!StringUtil.isEmpty(user)) {
				props.setProperty("socksProxyUser",user);
				props.setProperty("java.net.socks.username",user);
				props.setProperty("http.proxyUser",user);
				props.setProperty("https.proxyUser",user);
				props.setProperty("ftp.proxyUser",user);
				props.setProperty("smtp.proxyUser",user);
				
				if(password==null)password="";
				props.setProperty("socksProxyPassword",user);
				props.setProperty("java.net.socks.password",user);
				props.setProperty("http.proxyPassword",user);
				props.setProperty("https.proxyPassword",user);
				props.setProperty("ftp.proxyPassword",user);
				props.setProperty("smtp.proxyPassword",user);
			}
			else removeUserPass(props);
		}
		else {
			removeAll(props);
		}
	}

	public void onEnd(String key,boolean isLast) {
		//print.ln(" end:"+key+key+" _ "+isLast);
		if(!isLast) return;
		removeAll(System.getProperties());
	}

	private void removeAll(Properties props) {
		removeHost(props);
		removePort(props);
		removeUserPass(props);
        
	}
	private void removeHost(Properties props) {
		remove(props,"socksProxyHost");
        
        remove(props,"http.proxyHost");
        remove(props,"https.proxyHost");
        remove(props,"ftp.proxyHost");
        remove(props,"smtp.proxyHost");
        
	}
	private void removePort(Properties props) {
		remove(props,"socksProxyPort");
        remove(props,"http.proxyPort");
        remove(props,"https.proxyPort");
        remove(props,"ftp.proxyPort");
        remove(props,"smtp.proxyPort");
	}
	private void removeUserPass(Properties props) {
		remove(props,"socksProxyUser");
        remove(props,"socksProxyPassword");
        
        remove(props,"java.net.socks.username");
        remove(props,"java.net.socks.password");
        
        remove(props,"http.proxyUser");
        remove(props,"http.proxyPassword");
        
        remove(props,"https.proxyUser");
        remove(props,"https.proxyPassword");

        remove(props,"ftp.proxyUser");
        remove(props,"ftp.proxyPassword");
        
        remove(props,"smtp.proxyUser");
        remove(props,"smtp.proxyPassword");
        
	}
	
	private static void remove(Properties props, String key) {
		if(props.containsKey(key))
			props.remove(key);
	}
	
}


/*class ProxyThread extends Thread {
	private String s;
	private int po;
	private int id;
	private String u;
	private String p;
	
	public ProxyThread(int id,String s, int po, String u, String p) {
		this.s=s;
		this.id=id;
		this.po=po;
		this.u=u;
		this.p=p;
	}
	public void run() {
		try {
			_run();
		} catch (Exception e) {
			
		}
	}
	public void _run() throws Exception {
		//print.ln("start("+Thread.currentThread().getName()+"):"+s+":"+po+":"+u+":"+p);
		Proxy.start(id,s, po, u, p);
		sleep(1000);
		Proxy.end(id);
	}
}*/