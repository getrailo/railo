package railo.runtime.net.mail;

/**
 * SMTP Exception
 */
public abstract class SMTPException extends Exception {
    
    /**
     * constructor of the class
     * @param message
     */
    public SMTPException(String message) {
        super(message);
    }
}