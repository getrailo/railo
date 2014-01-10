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
    
    @Override
    public void disconnect() throws IOException {
        SystemOut.printDate(SystemUtil.getPrintWriter(SystemUtil.OUT),"MyFTPClient.disconnect("+(--count)+")");
        super.disconnect();
    }
    @Override
    public void connect(InetAddress arg0, int arg1) throws SocketException,
            IOException {
        SystemOut.printDate(SystemUtil.getPrintWriter(SystemUtil.OUT),"MyFTPClient.connect("+(++count)+")");
        super.connect(arg0, arg1);
    }
}