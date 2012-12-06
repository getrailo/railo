package railo.runtime.net.mail;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.ExpressionException;
import railo.runtime.op.Caster;


/**
 * 
 */
public final class ServerImpl implements Server {
	
	private String hostName;
	private String username;
	private String password;
	private int port=DEFAULT_PORT;
	private boolean readOnly=false;
	private boolean tls;
	private boolean ssl;
	//private static Pattern[] patterns=new Pattern[3];
    
	//[user:password@]server[:port],[
	/*static {
		patterns[0]=Pattern.compile("^([^:\\s)]+)\\s*:\\s*([^@\\s)]+)\\s*@\\s*([^:\\s)]+)\\s*:\\s*(.+)$");
		patterns[1]=Pattern.compile("^([^:\\s)]+)\\s*:\\s*([^@\\s)]+)\\s*@\\s*(.+)$");
		patterns[2]=Pattern.compile("^([^:\\s)]+)\\s*:\\s*(.+)$");
		
	}*/
	
	public static ServerImpl getInstance(String host, int defaultPort,String defaultUsername,String defaultPassword, boolean defaultTls, boolean defaultSsl) throws MailException {
		
		String userpass,user=defaultUsername,pass=defaultPassword,tmp;
		int port=defaultPort;
		
		// [user:password@]server[:port]
		int index=host.indexOf('@');
			
		// username:password
		if(index!=-1) {
			userpass=host.substring(0,index);
			host=host.substring(index+1);
			
			index=userpass.indexOf(':');
			if(index!=-1) {
				user=userpass.substring(0,index).trim();
				pass=userpass.substring(index+1).trim();
			}
			else user=userpass.trim();
	}

		// server:port
		index=host.indexOf(':');
		if(index!=-1) {
			tmp=host.substring(index+1).trim();
			if(!StringUtil.isEmpty(tmp)){
				try {
					port=Caster.toIntValue(tmp);
				} catch (ExpressionException e) {
					throw new MailException("port definition is invalid ["+tmp+"]");
				}
			}
			host=host.substring(0,index).trim();
		}
		else host=host.trim();

			
		return new ServerImpl(host,port,user,pass,defaultTls,defaultSsl);
	}
	

	/*public ServerImpl(String server,int port) {
		this.hostName=server;
		this.port=port;
	}*/
	
	public ServerImpl(String hostName,int port,String username,String password, boolean tls, boolean ssl) {
		this.hostName=hostName;
		this.username=username;
		this.password=password;
		this.port=port;
		this.tls=tls;
		this.ssl=ssl;
	}
	
	/*public ServerImpl(String strServer) throws MailException {
		strServer=strServer.trim();
		boolean hasMatch=false;
		outer:for(int i=0;i<patterns.length;i++) {
			Pattern p = patterns[i];
			Matcher m = p.matcher(strServer);
			
			if(m.matches()) {
				try {
					switch(m.groupCount()) {
						case 2:
							hostName=m.group(1).trim();
							port=Caster.toIntValue(m.group(2).trim());
						break;
						case 4:
							username=m.group(1).trim();
							password=m.group(2).trim();
							hostName=m.group(3).trim();
							port=Caster.toIntValue(m.group(4).trim());
						break;
					}
				}
				catch(ExpressionException e) {
					throw new MailException(e.getMessage());
				}
				hasMatch=true;
				break outer;
			}
		}
		if(!hasMatch) hostName=strServer;
	}*/
	
	/*public static Server[] factory(String strServers) throws MailException {
		StringTokenizer tokens=new StringTokenizer(strServers,",;");
		ArrayList list=new ArrayList();
		
		while(tokens.hasMoreTokens()) {
			list.add(new ServerImpl(tokens.nextToken()));
		}
		Server[] pairs=(Server[])list.toArray(new Server[list.size()]);
		return pairs;
		
		
	}*/

	@Override
	public String getPassword() {
		if(password==null && hasAuthentication()) return "";
		return password;
	}
	@Override
	public int getPort() {
		return port;
	}
	@Override
	public String getHostName() {
		return hostName;
	}
	@Override
	public String getUsername() {
		return username;
	}
	@Override
	public boolean hasAuthentication() {
		return username!=null && username.length()>0;
	}
	
	@Override
	public String toString() {
		if(username!=null) {
			return username+":"+password+"@"+hostName+":"+port;
		}
		return hostName+":"+port;
	}

    @Override
    public Server cloneReadOnly() {
        ServerImpl s = new ServerImpl(hostName, port,username, password,tls,ssl);
        s.readOnly=true;
        return s;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public boolean verify() throws SMTPException {
        return SMTPVerifier.verify(hostName,username,password,port);
    }

	public boolean isTLS() {
		return tls;
	}

	public boolean isSSL() {
		return ssl;
	}

	public void setSSL(boolean ssl) {
		this.ssl=ssl;
	}

	public void setTLS(boolean tls) {
		this.tls=tls;
	}
}