package main;

import java.awt.Desktop;
import java.net.URI;

public class DesktopUtil {


	public static boolean LaunchBrowser( String uri ) {

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
}
