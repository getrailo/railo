package railo.runtime.net.mail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import railo.commons.lang.StringUtil;


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
		
		//^((?>[a-zA-Z\d!#$%&'*+\-/=?^_`{|}~]+\x20*|"((?=[\x01-\x7f])[^"\\]|\\[\x01-\x7f])*"\x20*)*(?<angle><))?((?!\.)(?>\.?[a-zA-Z\d!#$%&'*+\-/=?^_`{|}~]+)+|"((?=[\x01-\x7f])[^"\\]|\\[\x01-\x7f])*")@(((?!-)[a-zA-Z\d\-]+(?<!-)\.)+[a-zA-Z]{2,}|\[(((?(?<!\[)\.)(25[0-5]|2[0-4]\d|[01]?\d?\d)){4}|[a-zA-Z\d\-]*[a-zA-Z\d]:((?=[\x01-\x7f])[^\\\[\]]|\\[\x01-\x7f])+)\])(?(angle)>)$
		
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
	
	private EmailNamePair(String strEmail) throws MailException {
		strEmail=strEmail.trim();
		boolean hasMatch=false;
		outer:for(int i=0;i<patterns.length;i++) {
			Pattern p = patterns[i];
			Matcher m = p.matcher(strEmail);
			
			if(m.matches()) {
				switch(pairType[i]) {
					case EMAIL:
						email=m.group(1).trim();
					break;
					case EMAIL_NAME:
						email=m.group(1).trim();
						name=m.group(2).trim();
					break;
					case NAME_EMAIL:
						name=m.group(1).trim();
						email=m.group(2).trim();
					break;
				}
				hasMatch=true;
				break outer;
			}
		}
		if(!hasMatch) throw new MailException("Invalid E-Mail Address definition ("+strEmail+")");
	}
		
	/**
	 * parse a string with email name pairs and return it as array
	 * @param strEmails email name pairs a string
	 * @return parsed email n ame pairs
	 * @throws MailException
	 */
	private static EmailNamePair[] _factory(String strEmails) throws MailException {
		StringTokenizer tokens=new StringTokenizer(strEmails,",;");
		ArrayList list=new ArrayList();
		
		while(tokens.hasMoreTokens()) {
			list.add(new EmailNamePair(tokens.nextToken()));
		}
		EmailNamePair[] pairs=(EmailNamePair[])list.toArray(new EmailNamePair[list.size()]);
		return pairs;
	}
	

	public static InternetAddress[] toInternetAddress(String strEmails) throws MailException, AddressException, UnsupportedEncodingException {
		EmailNamePair[] pairs = _factory(strEmails);
		InternetAddress[] addresses=new InternetAddress[pairs.length];
		EmailNamePair pair;
		for(int i=0;i<pairs.length;i++) {
			pair=pairs[i];
			if(!StringUtil.isEmpty(pair.getName()))addresses[i]=new InternetAddress(pair.getEmail(),pair.getName());
			else addresses[i]=new InternetAddress(pair.getEmail());
		}
		return addresses;
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
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if(name.length()==0) return email;
		return email+"("+name+")";
	}
	
}