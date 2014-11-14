/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
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