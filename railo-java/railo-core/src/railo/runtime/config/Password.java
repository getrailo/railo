package railo.runtime.config;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import railo.commons.digest.Hash;
import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.crypt.BlowfishEasy;
import railo.runtime.exp.PageException;

public class Password {

	public static final int HASHED=1;
	public static final int HASHED_SALTED=2;

	public static final int ORIGIN_ENCRYPTED=3;
	public static final int ORIGIN_HASHED=4;
	public static final int ORIGIN_HASHED_SALTED=5;
	public static final int ORIGIN_UNKNOW=6;

	private final String rawPassword;
	public final String password;
	public final String salt; 
	public final int type;
	public final int origin;


	private Password(int origin,String password, String salt, int type) {
		this.rawPassword=null;
		this.password=password;
		this.salt=salt;
		this.type=type;
		this.origin=origin;
	}
	
	private Password(int origin,String rawPassword, String salt) {
		this.rawPassword=rawPassword;
		this.password=hash(rawPassword, salt);
		this.salt=salt;
		this.type=StringUtil.isEmpty(salt)?HASHED:HASHED_SALTED;
		this.origin=origin;
	}

	
	private static String hash(String str, String salt) {
		try {
			return Hash.hash(StringUtil.isEmpty(salt,true)?str:str+":"+salt,Hash.ALGORITHM_SHA_256,5,Hash.ENCODING_HEX);
		}
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	/*public Password isEqual(Config config,String other, boolean hashIfNecessary) {
		Password b = _isEqual(config, other, hashIfNecessary);
		if(b==null) {
			print.e("+++++++++++++++++++++++++");
			print.e(type);
			print.e(salt);
			print.e(other);
			print.e(this.password);
			print.e((hash(other,null)));
			print.e((hash(other,salt)));
	    	print.ds();
		}
		return b;
	}*/
	public Password isEqual(Config config,String other, boolean hashIfNecessary) {
		if(password.equals(other)) return this;
    	
		//String salt=((ConfigImpl)config).getSalt(); // this hash is used!
		if(!hashIfNecessary) return null;
		
    	
    	// current password is only hashed
    	if(type==HASHED) return this.password.equals(hash(other,null))?this:null;
    	// current password is hashed and salted
    	
    	
    	return this.password.equals(hash(other,salt))?this:null;
	}

	public static Password getInstance(Element el, String salt, boolean isDefault) {
		String prefix=isDefault?"default-":"";
		
		// first we look for the hashed and salted password
		String pw=el.getAttribute(prefix+"hspw");
		if(!StringUtil.isEmpty(pw,true)) {
			// password is only of use when there is a salt as well
			if(salt==null) return null;
			return new Password(ORIGIN_HASHED_SALTED,pw,salt,HASHED_SALTED);
		}
		
		// fall back to password that is hashed but not salted
		pw=el.getAttribute(prefix+"pw");
		if(!StringUtil.isEmpty(pw,true)) {
			return new Password(ORIGIN_HASHED,pw,null,HASHED);
		}
		
		// fall back to encrypted password
		String pwEnc = el.getAttribute(prefix+"password"); 
		if (!StringUtil.isEmpty(pwEnc,true)) {
			String rawPassword = new BlowfishEasy("tpwisgh").decryptString(pwEnc);
			return new Password(ORIGIN_ENCRYPTED,rawPassword,salt);
		}
		return null;
	}
	

	public static Password getInstanceFromRawPassword(String rawPassword, String salt) {
		return new Password(ORIGIN_UNKNOW,rawPassword,salt);
	}
	
	public static Password hashAndStore(Element el,String password, boolean isDefault) {
		// salt
		String salt=el.getAttribute("salt");
		if(StringUtil.isEmpty(salt,true)) throw new RuntimeException("missing salt!");// this should never happen
		salt=salt.trim();
		
		Password pw = new Password(ORIGIN_UNKNOW,hash(password, salt), salt, StringUtil.isEmpty(salt)?HASHED:HASHED_SALTED);
		store(el,pw,isDefault);
        return pw;
	}


	public static void store(Element el,Password pw, boolean isDefault) {
		String prefix=isDefault?"default-":"";
		if(pw==null) {
			if(el.hasAttribute(prefix+"hspw")) el.removeAttribute(prefix+"hspw");
			if(el.hasAttribute(prefix+"pw")) el.removeAttribute(prefix+"pw");
			if(el.hasAttribute(prefix+"password")) el.removeAttribute(prefix+"password");
		}
		else {
			// also set older password type, needed when someone downgrade Railo
			if(pw.rawPassword!=null) {
				if(el.hasAttribute(prefix+"pw")) el.setAttribute(prefix+"pw",hash(pw.rawPassword, null));
				String encoded = new BlowfishEasy("tpwisgh").encryptString(pw.rawPassword);
				if(el.hasAttribute(prefix+"password")) el.setAttribute(prefix+"password",encoded);
			}
			
			el.setAttribute(prefix+"hspw",pw.password);
		}
		
		// remove older passwords
		// xxx when update the older needed to be updated as well or removed
	}


	public static void remove(Element root, boolean isDefault) { 
		store(root, null, isDefault);
	}
	
	
	public static Password updatePasswordIfNecessary(ConfigImpl config, Password passwordOld, String strPasswordNew) {
		
		try {
			// is the server context default password used
			boolean defPass=false;
			if(config instanceof ConfigWebImpl)
				defPass=((ConfigWebImpl)config).isDefaultPassword();
			
			
			int origin=config.getPasswordOrigin();
	
			// is old style password!
			if((origin==Password.ORIGIN_HASHED || origin==Password.ORIGIN_ENCRYPTED) && !defPass) {
				// is passord valid!
				if(config.isPasswordEqual(strPasswordNew, true)!=null) {
					// new salt
			    	String saltn=config.getSalt(); // get salt from context, not from old password that can be different when default password
	
			    	// new password
			    	Password passwordNew=null;
			    	if(!StringUtil.isEmpty(strPasswordNew,true))
			    		passwordNew = Password.getInstanceFromRawPassword(strPasswordNew, saltn);
			    	
			    	updatePassword(config, passwordOld, passwordNew);
			    	return passwordNew;
				}
			}
		}
		catch (Throwable t) {
			// Optinal functionality, ignore failures
			t.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * @param config Config of the context (ConfigServer to set a server level password)
	 * @param strPasswordOld the old password to replace or null if there is no password set yet
	 * @param strPasswordNew the new password
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws PageException 
	 */
	public static void updatePassword(ConfigImpl config, String strPasswordOld, String strPasswordNew) throws SAXException, IOException, PageException {
		
		// old salt
    	int pwType=config.getPasswordType(); // get type from password
    	String salto=config.getPasswordSalt(); // get salt from password
    	if(pwType==Password.HASHED)salto=null; // if old password does not use a salt, we do not use a salt to hash
    	
    	// new salt
    	String saltn=config.getSalt(); // get salt from context, not from old password that can be different when default password
    	
    	
    	// old password
    	Password passwordOld=null;
    	if(!StringUtil.isEmpty(strPasswordOld,true))
    		passwordOld=Password.getInstanceFromRawPassword(strPasswordOld, salto);

    	// new password
    	Password passwordNew=null;
    	if(!StringUtil.isEmpty(strPasswordNew,true))
    		passwordNew = Password.getInstanceFromRawPassword(strPasswordNew, saltn);
    	
    	updatePassword(config, passwordOld, passwordNew);
		
		
		
	}
	
	public static void updatePassword(ConfigImpl config, Password passwordOld, Password passwordNew) throws SAXException, IOException, PageException {
		if(!config.hasPassword()) { 
	    	config.setPassword(passwordNew);
	        ConfigWebAdmin admin = ConfigWebAdmin.newInstance(config,passwordNew.password);
	        admin.setPassword(passwordNew);
	        admin.store();
	    }
	    else {
	    	ConfigWebUtil.checkPassword(config,"write",passwordOld.password);
	    	ConfigWebUtil.checkGeneralWriteAccess(config,passwordOld.password);
	    	ConfigWebAdmin admin = ConfigWebAdmin.newInstance(config,passwordOld.password);
	        admin.setPassword(passwordNew);
	        admin.store();
	    }
	}

	public static Password hashPassword(ConfigWeb cw, boolean server, String rawPassword) {
		ConfigWebImpl cwi=(ConfigWebImpl) cw;
		int pwType;
    	String pwSalt;
		if(server) {
			pwType=cwi.getServerPasswordType();
			pwSalt=cwi.getServerPasswordSalt();
		}
		else {
			pwType=cwi.getPasswordType();
	    	pwSalt=cwi.getPasswordSalt();
		}
		
    	
    	// if the internal password is not using the salt yet, this hash should eigther
    	String salt=pwType==Password.HASHED?null:pwSalt;
    	
    	return Password.getInstanceFromRawPassword(rawPassword,salt);
	}

	

}
