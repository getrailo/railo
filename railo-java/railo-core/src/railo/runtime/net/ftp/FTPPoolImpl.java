package railo.runtime.net.ftp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import railo.commons.collections.HashTable;
import railo.runtime.exp.ApplicationException;

/**
 * Pool of FTP Client
 */
public final class FTPPoolImpl implements FTPPool {

    Map wraps=new HashTable();
    ArrayList arr=new ArrayList();

    /**
     * @see railo.runtime.net.ftp.FTPPool#get(railo.runtime.net.ftp.FTPConnection)
     */
    public FTPClient get(FTPConnection conn) throws IOException, ApplicationException {
        //return _get(conn).getClient();
        
        FTPClient client = _get(conn).getClient();
        if(client==null)throw new ApplicationException("can't connect to server ["+conn.getServer()+"]");
        return client;
    }

    /**
     * returns a client from given connection
     * @param conn
     * @return matching wrap
     * @throws IOException
     * @throws ApplicationException
     */
    protected FTPWrap _get(FTPConnection conn) throws IOException, ApplicationException {
        FTPWrap wrap=null;
        
        /* / get Existing Conn
        if(!conn.hasLoginData()) {
            if(!conn.hasName())
                throw new ApplicationException(
                        "invalid attribute constelation for the tag ftp",
                        "you must define the attribute connection or the attributes server, username and password");
            wrap=(FTPWrap) wraps.get(conn.getName());
            if(wrap==null) {
                //print.ln("_get(no existing conn)");
                throw new ApplicationException(
                        "invalid attribute constelation for the tag ftp",
                        "can't connect ftp server, missing connection ["+conn.getName()+"]");
            }
            else if(!wrap.getClient().isConnected()) {
                //print.ln("_get(reconnect)");
                wrap.reConnect();
            }
            return wrap;
        }*/
        
        if(!conn.hasLoginData()) {
        	wrap=(FTPWrap) wraps.get(conn.getName());
            if(wrap==null) {
                throw new ApplicationException("can't connect ftp server, missing connection ["+conn.getName()+"]");
            }
            else if(!wrap.getClient().isConnected()) {
                wrap.reConnect();
            }
            return wrap;
        }
        String name=conn.hasName()?conn.getName():"__noname__";
        
        wrap=(FTPWrap) wraps.get(name);
        if(wrap!=null) {
            if(conn.loginEquals(wrap.getConnection())) {
                return _get(new FTPConnectionImpl(name,null,null,null,conn.getPort(),conn.getTimeout(),conn.getTransferMode(),conn.isPassive(),
                		conn.getProxyServer(),conn.getProxyPort(),conn.getProxyUser(),conn.getProxyPassword()));
            }
            disconnect(wrap.getClient());
        }
                
        wrap=new FTPWrap(conn);
        wraps.put(name,wrap);
        
        if(conn.getTransferMode()==FTPConstant.TRANSFER_MODE_ASCCI) wrap.getClient().setFileType(FTP.ASCII_FILE_TYPE);
        else if(conn.getTransferMode()==FTPConstant.TRANSFER_MODE_BINARY) wrap.getClient().setFileType(FTP.BINARY_FILE_TYPE);
        
        return wrap;
    }

    /**
     * disconnect a client
     * @param client
     */
    private void disconnect(FTPClient client) {
        try {
            if(client!=null && client.isConnected()) {
    			client.quit();
                client.disconnect();
            }
        }
        catch(IOException ioe) {}
    }

    /**
     * @see railo.runtime.net.ftp.FTPPool#remove(railo.runtime.net.ftp.FTPConnection)
     */
    public FTPClient remove(FTPConnection conn) {
        return remove(conn.getName());
    }

    /**
     * @see railo.runtime.net.ftp.FTPPool#remove(java.lang.String)
     */
    public FTPClient remove(String name) {
        FTPWrap wrap=(FTPWrap) wraps.remove(name);
        if(wrap==null) return null;
        
        FTPClient client = wrap.getClient();
        disconnect(client);
        return client;
    }

    /**
     * @see railo.runtime.net.ftp.FTPPool#clear()
     */
    public void clear() {
        if(!wraps.isEmpty()) {
            Iterator it = wraps.entrySet().iterator();
            while(it.hasNext()) {
                try {
                    Map.Entry entry=(Map.Entry)it.next();
                    FTPWrap wrap=(FTPWrap)entry.getValue();
                    if(wrap!=null && wrap.getClient().isConnected())wrap.getClient().disconnect();
                } catch (IOException e) {}
            }
            wraps.clear();
        }
    }
}