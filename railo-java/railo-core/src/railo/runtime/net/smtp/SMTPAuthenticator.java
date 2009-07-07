package railo.runtime.net.smtp;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * This is a very simple authentication object that can be used for any
 * transport needing basic userName and password type authentication.
 *
 */
public final class SMTPAuthenticator extends Authenticator {
    /** Stores the login information for authentication */
    private PasswordAuthentication authentication;

    /**
     * Default constructor
     *
     * @param userName user name to use when authentication is requested
     * @param password password to use when authentication is requested
     *
     */
    public SMTPAuthenticator(String userName, String password){
        this.authentication = new PasswordAuthentication(userName, password);
    }

    /**
     * Gets the authentication object that will be used to login to the mail
     * server.
     *
     * @return A <code>PasswordAuthentication</code> object containing the
     *         login information.
     *
     */
    protected PasswordAuthentication getPasswordAuthentication() {
        return this.authentication;
    }
}
