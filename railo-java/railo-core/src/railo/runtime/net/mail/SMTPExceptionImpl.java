package railo.runtime.net.mail;

import railo.commons.collections.HashTable;

public final class SMTPExceptionImpl extends SMTPException {
    
    private static HashTable codes=new HashTable();
    
    static {
        codes.put("211","System status, or system help reply");
        codes.put("214"," Help message (Information on how to use the receiver or the meaning of a particular non-standard command; this reply is useful only to the human user)");
        codes.put("220","Service ready");
        codes.put("221","Service closing transmission channel");
        codes.put("250","Requested mail action okay, completed");
        codes.put("251","User not local; will forward to");
        codes.put("354","Start mail input; end with .");
        codes.put("421","Service not available, closing transmission channel (This may be a reply to any command if the service knows it must shut down) ");
        codes.put("450","Requested mail action not taken: mailbox unavailable (E.g., mailbox busy)");
        codes.put("451","Requested action aborted: local error in processing");
        codes.put("452","Requested action not taken: insufficient system storage");
        codes.put("500","Syntax error, command unrecognized (This may include errors such as command line too long)");
        codes.put("501","Syntax error in parameters or arguments");
        codes.put("502","Command not implemented");
        codes.put("503","Bad sequence of commands");
        codes.put("504","Command parameter not implemented");
        codes.put("550","Requested action not taken: mailbox unavailable (E.g., mailbox not found, no access)");
        codes.put("551","User not local; please try");
        codes.put("552","Requested mail action aborted: exceeded storage allocation");
        codes.put("553","Requested action not taken: mailbox name not allowed (E.g., mailbox syntax incorrect) ");
        codes.put("554","Transaction failed (Or, in the case of a connection-opening response, \"No SMTP service here\")");
        codes.put("252","Cannot VRFY user, but will accept message and attempt delivery");
    }
    
    public SMTPExceptionImpl(String message) {
        super(message);
    }
    public SMTPExceptionImpl(int code) {
        this(doMessage(code));
    }
    private static String doMessage(int code) {
        String message=(String) codes.get(String.valueOf(code));
        if(message==null) message="SMTP Code "+code;
        else message=code+" - "+message;
        return message;
    }
}
