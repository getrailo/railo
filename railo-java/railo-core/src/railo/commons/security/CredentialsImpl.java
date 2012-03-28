package railo.commons.security;

import railo.commons.lang.StringUtil;

public class CredentialsImpl implements Credentials {

	private String username;
	private String password;

	private CredentialsImpl(String username,String password){
		this.username=username;
		this.password=password;
	}
	
	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}
	
	public static Credentials toCredentials(String username,String password){
		if(StringUtil.isEmpty(username,true)) return null;
		if(StringUtil.isEmpty(password,true)) password="";
		return new CredentialsImpl(username, password);
	}

}
