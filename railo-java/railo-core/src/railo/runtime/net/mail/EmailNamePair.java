package railo.runtime.net.mail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;
import railo.runtime.type.Struct;
import railo.runtime.type.util.ListUtil;


/**
 * represent a email name pair
 */
public final class EmailNamePair {
	
	private static final short EMAIL=0;
	private static final short EMAIL_NAME=1;
	private static final short NAME_EMAIL=2;
	
	private String email="";
	private String name="";
	private static Pattern[] patterns=new Pattern[8];
	private static short[] pairType=new short[8];
	
	static {
		String email="([\\w\\._\\-\\%\\+\\'\\!\\#\\$\\%\\&]*@[\\w\\._\\-]*)";
		patterns[0]=Pattern.compile("^"+email+"\\s*\\(([^\\)]+)\\)$");
		patterns[1]=Pattern.compile("^"+email+"\\s*:\\s*(.+)$");
		patterns[2]=Pattern.compile("^([^:]+)\\s*:\\s*"+email+"$");
		patterns[3]=Pattern.compile("^\"([^\"]+)\"[\\s]<[\\s]*"+email+"[\\s]*>$");
		patterns[4]=Pattern.compile("^'([^']+)'[\\s]<[\\s]*"+email+"[\\s]*>$");
		patterns[5]=Pattern.compile("^([^<]+)<[\\s]*"+email+"[\\s]*>$");
		patterns[6]=Pattern.compile("^<[\\s]*"+email+"[\\s]*>$");
		patterns[7]=Pattern.compile("^"+email+"$");

		
		
		
		pairType[0]=EMAIL_NAME;
		pairType[1]=EMAIL_NAME;
		pairType[2]=NAME_EMAIL;
		pairType[3]=NAME_EMAIL;
		pairType[4]=NAME_EMAIL;
		pairType[5]=NAME_EMAIL;
		pairType[6]=EMAIL;
		pairType[7]=EMAIL;
		
	}
	
	private EmailNamePair(String email,String name) {
		this.name=name;
		this.email=email;
	}
	
	private EmailNamePair(String strEmail) throws MailException {
		strEmail=strEmail.trim();
		boolean hasMatch=false;
		outer:for(int i=0;i<patterns.length;i++) {
			Pattern p = patterns[i];
			Matcher m = p.matcher(strEmail);
			
			if(m.matches()) {
				switch(pairType[i]) {
					case EMAIL:
						email=clean(m.group(1));
					break;
					case EMAIL_NAME:
						email=clean(m.group(1));
						name=clean(m.group(2));
					break;
					case NAME_EMAIL:
						name=clean(m.group(1));
						email=clean(m.group(2));
					break;
				}
				hasMatch=true;
				break outer;
			}
		}
		if(!hasMatch) throw new MailException("Invalid E-Mail Address definition ("+strEmail+")");
	}
		
	private static String clean(String str) {
		if(str==null) return"";
		
		str=str.trim();
		if((str.startsWith("'") && str.endsWith("'")) || (str.startsWith("\"") && str.endsWith("\""))) 
			return str.substring(1,str.length()-1).trim();
		return str;
	}


	public static InternetAddress toInternetAddress(Object emails) throws MailException, AddressException, UnsupportedEncodingException, PageException {
		if(emails instanceof String){
			EmailNamePair pair = _factoryMail((String)emails);
			if(!StringUtil.isEmpty(pair.getName()))return new InternetAddress(pair.getEmail(),pair.getName());
			return new InternetAddress(pair.getEmail());
		}
		InternetAddress[] addresses = toInternetAddresses(emails);
		if(addresses!=null && addresses.length>0) return addresses[0];
		return null;
	}
	
	
	
	public static InternetAddress[] toInternetAddresses(Object emails) throws MailException, AddressException, UnsupportedEncodingException, PageException {
		EmailNamePair[] pairs=null;
		if(emails instanceof String){
			pairs = _factoryMailList((String)emails);
		}
		else if(Decision.isArray(emails)) {
			pairs = _factory(Caster.toArray(emails));
		}
		else if(Decision.isStruct(emails)) {
			pairs = new EmailNamePair[]{_factory(Caster.toStruct(emails))};
		}
		else 
			throw new MailException("e-mail defintions must be one of the following types [string,array,struct], not ["+emails.getClass().getName()+"]");

	
		InternetAddress[] addresses=new InternetAddress[pairs.length];
		EmailNamePair pair;
		for(int i=0;i<pairs.length;i++) {
			pair=pairs[i];
			if(!StringUtil.isEmpty(pair.getName()))addresses[i]=new InternetAddress(pair.getEmail(),pair.getName());
			else addresses[i]=new InternetAddress(pair.getEmail());
		}
		return addresses;
	}
	

	private static EmailNamePair[] _factory(Array array) throws MailException, PageException {
		Iterator it = array.valueIterator();
		Object el;
		ArrayList<EmailNamePair> pairs=new ArrayList<EmailNamePair>();
		while(it.hasNext()){
			el=it.next();
			if(Decision.isStruct(el))
				pairs.add(_factory(Caster.toStruct(el)));
			else
				pairs.add(new EmailNamePair(Caster.toString(el)));
		}
		return pairs.toArray(new EmailNamePair[pairs.size()]);
	}
	
	private static EmailNamePair _factory(Struct sct) throws MailException {
		String name=Caster.toString(sct.get("label",null),null);
		if(name==null)name=Caster.toString(sct.get("name",null),null);
		
		String email=Caster.toString(sct.get("email",null),null);
		if(email==null)email=Caster.toString(sct.get("e-mail",null),null);
		if(email==null)email=Caster.toString(sct.get("mail",null),null);
		
		if(name==null) name="";
		if(StringUtil.isEmpty(email)) throw new MailException("missing e-mail defintion in struct");
		
		return new EmailNamePair(email,name);
	}
	
	/**
	 * parse a string with email name pairs and return it as array
	 * @param strEmails email name pairs a string
	 * @return parsed email n ame pairs
	 * @throws MailException
	 */
	private static EmailNamePair[] _factoryMailList(String strEmails) throws MailException {
		if(StringUtil.isEmpty(strEmails,true)) return new EmailNamePair[0];
		Array raw = ListUtil.listWithQuotesToArray(strEmails,",;","\"");
		
		Iterator<Object> it = raw.valueIterator();
		ArrayList<EmailNamePair> pairs=new ArrayList<EmailNamePair>();
		String address;
		while(it.hasNext()) {
			address=Caster.toString(it.next(),null);
			if(StringUtil.isEmpty(address,true))continue;
			pairs.add(new EmailNamePair(address));
		}
		return pairs.toArray(new EmailNamePair[pairs.size()]);
	}
	

	private static EmailNamePair _factoryMail(String strEmail) throws MailException {
		if(StringUtil.isEmpty(strEmail,true)) return null;
		return new EmailNamePair(strEmail);
	}
	

	
	
	

	/**
	 * @return return if in the email name pair also a name is defined
	 */
	public boolean hasName() {
		return name.length()>0;
	}
	/**
	 * @return returns the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return returns the email
	 */
	public String getEmail() {
		return email;
	}
	
	@Override
	public String toString() {
		if(name.length()==0) return email;
		return email+"("+name+")";
	}
	
}