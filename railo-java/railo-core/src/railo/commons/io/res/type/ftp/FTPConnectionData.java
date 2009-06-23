

package railo.commons.io.res.type.ftp;

import railo.commons.lang.StringUtil;

public final class FTPConnectionData {

	public String username="";
	public String password="";
	public String host="localhost";
	public int port=0;

    private String proxyserver;
    private int proxyport;
    private String proxyuser;
    private String proxypassword;
	
	
	public String load(String path) {
		username="";
		password="";
		host=null;
		port=21;
		// TODO impl proxy
		
		int atIndex=path.indexOf('@');
		int slashIndex=path.indexOf('/');
		if(slashIndex==-1){
			slashIndex=path.length();
			path+="/";
		}
		int index;
		
		// username/password
		if(atIndex!=-1) {
			index=path.indexOf(':');
			if(index!=-1 && index<atIndex) {
				username=path.substring(0,index);
				password=path.substring(index+1,atIndex);
			}
			else username=path.substring(0,atIndex);
		}
		// host port
		if(slashIndex>atIndex+1) {
			index=path.indexOf(':',atIndex+1);
			if(index!=-1 && index>atIndex && index<slashIndex) {
				host=path.substring(atIndex+1,index);
				port=Integer.parseInt(path.substring(index+1,slashIndex));
			}
			else host=path.substring(atIndex+1,slashIndex);
		}
		//if(slashIndex==-1)return "/";
		return path.substring(slashIndex);
	}



	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "username:"+username+";password:"+password+";hostname:"+host+";port:"+port;
	}



	public String key() {
		if(StringUtil.isEmpty(username))
				return host+_port();
		return username+":"+password+"@"+host+_port();
	}



	private String _port() {
		if(port>0) return ":"+port;
		return "";
	}



	public boolean hasProxyData() {
		return getProxyserver()!=null;
	}



	/**
	 * @return the proxypassword
	 */
	public String getProxypassword() {
		return proxypassword;
	}



	/**
	 * @return the proxyport
	 */
	public int getProxyport() {
		return proxyport;
	}



	/**
	 * @return the proxyserver
	 */
	public String getProxyserver() {
		return proxyserver;
	}



	/**
	 * @return the proxyuser
	 */
	public String getProxyuser() {
		return proxyuser;
	}
	/**
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(this==obj)return true;
		if(!(obj instanceof FTPConnectionData)) return false;
		return key().equals(((FTPConnectionData)obj).key());
	}
}
