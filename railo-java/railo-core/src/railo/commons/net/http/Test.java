package railo.commons.net.http;

import java.io.IOException;
import java.net.URL;

import railo.print;

public class Test {

	public static void main(String[] args) throws IOException {
		URL url=new URL("http://www.google.ch"); 
		HTTPResponse rsp = HTTPEngine.get(url);

		print.e(rsp.getContentAsByteArray());
	}
}
