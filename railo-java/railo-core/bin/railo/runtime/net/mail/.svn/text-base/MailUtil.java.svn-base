package railo.runtime.net.mail;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.MimeUtility;

public final class MailUtil {

	public static String encode(String text,String encoding) throws UnsupportedEncodingException {
		//print.ln(StringUtil.changeCharset(text,encoding));
		return MimeUtility.encodeText(text,encoding,"Q");
	}
	
	public static String decode(String text) throws UnsupportedEncodingException {
		return MimeUtility.decodeText(text);
	}
}
