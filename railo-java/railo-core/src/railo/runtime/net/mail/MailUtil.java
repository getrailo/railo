package railo.runtime.net.mail;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;
import railo.runtime.type.Struct;
import railo.runtime.type.util.ListUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;

public final class MailUtil {

	public static String encode(String text,String encoding) throws UnsupportedEncodingException {
		//print.ln(StringUtil.changeCharset(text,encoding));
		return MimeUtility.encodeText(text,encoding,"Q");
	}
	
	public static String decode(String text) throws UnsupportedEncodingException {
		return MimeUtility.decodeText(text);
	}



    public static InternetAddress toInternetAddress(Object emails) throws MailException, AddressException, UnsupportedEncodingException, PageException {

        if ( emails instanceof String )
            return parseEmail( emails );

        InternetAddress[] addresses = toInternetAddresses( emails );
        if ( addresses != null && addresses.length > 0 )
            return addresses[0];

        return null;
    }


    public static InternetAddress[] toInternetAddresses(Object emails) throws MailException, AddressException, UnsupportedEncodingException, PageException {

        if ( emails instanceof String ){

            return fromList((String) emails);
        }
        else if ( Decision.isArray(emails) ) {

            return fromArray(Caster.toArray(emails));
        }
        else if ( Decision.isStruct(emails) ) {

            return new InternetAddress[]{ fromStruct(Caster.toStruct(emails)) };
        }
        else
            throw new MailException("e-mail defintions must be one of the following types [string,array,struct], not ["+emails.getClass().getName()+"]");
    }


    private static InternetAddress[] fromArray(Array array) throws MailException, PageException, UnsupportedEncodingException {

        Iterator it = array.valueIterator();
        Object el;
        ArrayList<InternetAddress> pairs = new ArrayList();

        while(it.hasNext()){
            el=it.next();
            if ( Decision.isStruct( el ) ) {

                pairs.add( fromStruct(Caster.toStruct(el)) );
            }
            else {

                InternetAddress addr = parseEmail( Caster.toString(el) );
                if ( addr != null )
                    pairs.add( addr );
            }
        }

        return pairs.toArray( new InternetAddress[ pairs.size() ] );
    }


    private static InternetAddress fromStruct( Struct sct ) throws MailException, UnsupportedEncodingException {

        String name = Caster.toString(sct.get("label",null),null);
        if ( name == null )
            name=Caster.toString(sct.get("name",null),null);

        String email = Caster.toString(sct.get("email",null),null);
        if ( email == null )
            email = Caster.toString(sct.get("e-mail",null),null);
        if ( email == null )
            email = Caster.toString(sct.get("mail",null),null);

        if( StringUtil.isEmpty(email) )
            throw new MailException("missing e-mail definition in struct");

        if(name==null) name="";

        return new InternetAddress( email, name );
    }


    private static InternetAddress[] fromList( String strEmails ) {

        if ( StringUtil.isEmpty( strEmails, true ) )
            return new InternetAddress[0];

        Array raw = ListUtil.listWithQuotesToArray(strEmails, ",;", "\"");

        Iterator<Object> it = raw.valueIterator();
        ArrayList<InternetAddress> al = new ArrayList();

        while( it.hasNext() ) {

            InternetAddress addr = parseEmail( it.next() );

            if( addr != null )
                al.add( addr );
        }

        return al.toArray( new InternetAddress[ al.size() ] );
    }


    /**
     * returns true if the passed value is a in valid email address format
     * @param value
     * @return
     */
    public static boolean isValidEmail( Object value ) {

        return ( parseEmail( value ) != null );
    }


    /**
     * returns an InternetAddress object or null if the parsing fails.  to be be used in multiple places.
     * @param value
     * @return
     */
    public static InternetAddress parseEmail( Object value ) {

        String str = Caster.toString( value, "" );

        if ( str.indexOf( '@' ) > -1 ) {

            try {

                return new InternetAddress( str );
            }
            catch ( AddressException ex ) {}
        }

        return null;
    }

}
