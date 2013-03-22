package railo.runtime.net.ftp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;

import railo.commons.io.SystemUtil;
import railo.commons.lang.SystemOut;


/**
 * 
 */
public final class DebugFTPClient extends FTPClient {
    
    private static int count=0;
    
    /**
     * @see org.apache.commons.net.SocketClient#disconnect()
     */
    public void disconnect() throws IOException {
        SystemOut.printDate(SystemUtil.PRINTWRITER_OUT,"MyFTPClient.disconnect("+(--count)+")");
        super.disconnect();
    }
    /**
     * @see org.apache.commons.net.SocketClient#connect(java.net.InetAddress, int)
     */
    public void connect(InetAddress arg0, int arg1) throws SocketException,
            IOException {
        SystemOut.printDate(SystemUtil.PRINTWRITER_OUT,"MyFTPClient.connect("+(++count)+")");
        super.connect(arg0, arg1);
    }
}