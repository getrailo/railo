package railo.commons.io.res.type.http;

import java.io.IOException;
import java.nio.charset.Charset;

import railo.aprint;
import railo.commons.io.IOUtil;

public class Test {
	public static void main(String[] args) throws IOException {
		HTTPResourceProvider p=(HTTPResourceProvider) new HTTPResourceProvider().init("https", null);
		HTTPResource res = (HTTPResource) p.getResource("https://www.google.com/reader/public/atom/user%2F11740182374692118732%2Flabel%2Fweb-rat.com");
		aprint.out("1");
		aprint.out(res.length());
		aprint.out("2");
		aprint.out(res.exists());
		aprint.out("3");
		aprint.out(res.isFile());
		aprint.out("4");
		aprint.out(IOUtil.toString(res,(Charset)null).length());
		
	}
}
