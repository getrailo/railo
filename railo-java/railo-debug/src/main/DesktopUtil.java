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
