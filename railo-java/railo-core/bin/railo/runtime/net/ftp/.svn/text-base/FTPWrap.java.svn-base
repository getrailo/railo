package railo.runtime.net.ftp;

import java.io.IOException;
import java.net.InetAddress;

import org.apache.commons.net.ftp.FTPClient;

import railo.runtime.net.proxy.Proxy;


/**
 * Wrap a Client and a Connection
 */
public final class FTPWrap {

    private FTPConnection conn;
    private FTPClient client;
    private InetAddress address;
    private long lastAccess=0;

    /**
	 * @return the lastAccess
	 */
	public long getLastAccess() {
		return lastAccess;
	}



	/**
	 * @param lastAccess the lastAccess to set
	 */
	public void setLastAccess(long lastAccess) {
		this.lastAccess = lastAccess;
	}



	/**
     * 
     * @param connection
     * @throws IOException
     */
    public FTPWrap(FTPConnection connection) throws IOException {
        this.conn=connection;
        this.address = InetAddress.getByName(connection.getServer());
        connect();        
    }
    
    
    
    /**
     * @return Returns the connection.
     */
    public FTPConnection getConnection() {
        return conn;
    }

    /**
     * @return Returns the client.
     */
    public FTPClient getClient() {
        return client;
    }

    /**
     * @throws IOException
     * 
     */
    public void reConnect() throws IOException { 
        try {
            if(client!=null && client.isConnected())client.disconnect();
        }
        catch(IOException ioe) {}
        connect();
    }

    /**
     * connects the client
     * @throws IOException
     */
    private void connect() throws IOException { 
        
        client=new FTPClient();
        
        // timeout
        client.setDataTimeout(conn.getTimeout()*1000);
        
        // passive/active Mode
        if(conn.isPassive()) client.enterLocalPassiveMode();
        else client.enterLocalActiveMode();
        
        // Proxy
        /*if(connection.getProxyserver()!=null) {
            //print.ln(System.getProperties().get("socksProxyHost"));
            System.getProperties().put( "socksProxyPort", Caster.toString(connection.getPort()));
            System.getProperties().put( "socksProxyHost" ,connection.getProxyserver());
        }*/
        //Socket s;
        // Connect
        
        try {
        	Proxy.start(
            		conn.getProxyServer(), 
            		conn.getProxyPort(), 
            		conn.getProxyUser(), 
            		conn.getProxyPassword()
            );
        	client.connect(address,conn.getPort());
        	client.login(conn.getUsername(),conn.getPassword());
        }
        finally {
        	Proxy.end();
        }
    }
}