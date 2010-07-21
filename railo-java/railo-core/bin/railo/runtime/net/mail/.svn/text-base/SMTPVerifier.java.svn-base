package railo.runtime.net.mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

import org.apache.commons.mail.DefaultAuthenticator;

import railo.commons.lang.StringUtil;

/**
 * SMTP Server verifier
 */
public final class SMTPVerifier{       
       
    /**
     * verify mail server
     * @param host
     * @param username
     * @param password
     * @param port
     * @return are the setting ok
     * @throws SMTPException 
     */
    public static boolean verify(String host, String username,String password, int port) throws SMTPException  {
        try {
            return _verify(host,username,password,port);
        } 
        catch (MessagingException e) {
            
            // check user
            if(!StringUtil.isEmpty(username)) {
                try {
                    _verify(host,null,null,port);
                    throw new SMTPExceptionImpl("can't connect to mail server, authentication settings are invalid");
                } catch (MessagingException e1) {
                    
                }
            }
            // check port
            if(port>0 && port!=25) {
                try {
                    _verify(host,null,null,25);
                    throw new SMTPExceptionImpl("can't connect to mail server, port definition is invalid");
                } 
                catch (MessagingException e1) {}
            }
            
            throw new SMTPExceptionImpl("can't connect to mail server");
        }
    }
        
    private static boolean _verify(String host, String username,String password, int port) throws MessagingException {
        boolean hasAuth=!StringUtil.isEmpty(username);
        
        Properties props=new Properties();
        props.put("mail.smtp.host", host );  
        if(hasAuth)props.put("mail.smtp.auth", "true" );  
        if(hasAuth)props.put("mail.smtp.user", username ); 
        if(hasAuth)props.put("mail.transport.connect-timeout", "30" );  
        if(port>0)props.put("mail.smtp.port", String.valueOf(port) );
        
        
        Authenticator auth=null;
        if(hasAuth)auth=new DefaultAuthenticator(username,password);
        Session session = Session.getInstance( props, auth);      
        
        Transport transport = session.getTransport("smtp");                
        if(hasAuth)transport.connect( host , username ,password );                
        else transport.connect( );                
        transport.close();
        
        return true;
    }
}