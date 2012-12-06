package railo.runtime.user;


/**
 * a simple implementation of a Credentials
 */
public final class SimpleCredentials implements Credentials {
    
    private String username;
    private String password;

    public SimpleCredentials(String username, String password) {
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

}