package railo.runtime.net.ftp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.net.ftp.FTPClient;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.ApplicationException;

/**
 * Pool of FTP Client
 */
public final class FTPPoolImpl implements FTPPool {

    Map<String,FTPWrap> wraps=new HashMap<String, FTPWrap>();
    //ArrayList arr=new ArrayList();

    @Override
    public FTPClient get(FTPConnection conn) throws IOException, ApplicationException {
        FTPClient client = _get(conn).getClient();
        if(client==null)throw new ApplicationException("can't connect to server ["+conn.getServer()+"]");
        
        FTPWrap.setConnectionSettings(client,conn);
        
        return client;
    }

    /**
     * returns a client from given connection
     * @param conn
     * @return 
     * @return matching wrap
     * @throws IOException
     * @throws ApplicationException
     */
    protected FTPWrap _get(FTPConnection conn) throws IOException, ApplicationException {
        FTPWrap wrap=null;
        
      
        
        if(!conn.hasLoginData()) {
        	if(StringUtil.isEmpty(conn.getName())){
        		throw new ApplicationException("can't connect ftp server, missing connection defintion");
        	}
        	
        	wrap=wraps.get(conn.getName());
            if(wrap==null) {
                throw new ApplicationException("can't connect ftp server, missing connection ["+conn.getName()+"]");
            }
            else if(!wrap.getClient().isConnected() || wrap.getConnection().getTransferMode()!=conn.getTransferMode()) {
                wrap.reConnect(conn.getTransferMode());
            }
            return wrap;
        }
        String name=conn.hasName()?conn.getName():"__noname__";
        
        wrap=wraps.get(name);
        if(wrap!=null) {
            if(conn.loginEquals(wrap.getConnection())) {
                return _get(new FTPConnectionImpl(name,null,null,null,conn.getPort(),conn.getTimeout(),conn.getTransferMode(),conn.isPassive(),
                		conn.getProxyServer(),conn.getProxyPort(),conn.getProxyUser(),conn.getProxyPassword()));
            }
            disconnect(wrap.getClient());
        }
                
        wrap=new FTPWrap(conn);
        wraps.put(name,wrap);
        
        
      
        	
        	
        	
        	
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

    @Override
    public FTPClient remove(FTPConnection conn) {
        return remove(conn.getName());
    }

    @Override
    public FTPClient remove(String name) {
        FTPWrap wrap=wraps.remove(name);
        if(wrap==null) return null;
        
        FTPClient client = wrap.getClient();
        disconnect(client);
        return client;
    }

    @Override
    public void clear() {
        if(!wraps.isEmpty()) {
            Iterator<Entry<String, FTPWrap>> it = wraps.entrySet().iterator();
            while(it.hasNext()) {
                try {
                    Entry<String, FTPWrap> entry = it.next();
                    FTPWrap wrap=entry.getValue();
                    if(wrap!=null && wrap.getClient().isConnected())wrap.getClient().disconnect();
                } catch (IOException e) {}
            }
            wraps.clear();
        }
    }
}