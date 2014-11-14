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
package main;

import java.awt.Desktop;
import java.net.Socket;
import java.net.URI;

public class DesktopUtil {


	public static boolean launchBrowser(String uri) {

		if ( Desktop.isDesktopSupported() ) {

			try {

				Desktop.getDesktop().browse( new URI( uri ) );

				return true;
			}
			catch (Exception ex) {

				System.out.println("Failed to launch browser to [" + uri + "]");
			}
		}

		return false;
	}


	public static boolean launchBrowser(String host, int port, boolean isSecure) {

		if ( !isServerListening( host, port ) )
			return false;

		String sPort = "";

		if ( (isSecure && port != 443) || (!isSecure && port != 80) )
			sPort = ":" + port;

		return launchBrowser("http" + (isSecure ? "s" : "") + "://" + host + sPort + "/");
	}


	public static boolean isServerListening(String host, int port) {

		try {

			Socket s = new Socket( host, port );

			return true;
		}
		catch (Exception ex) {}

		return false;
	}

}
