package railo.runtime.net.smtp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.activation.DataSource;

import railo.commons.io.IOUtil;

public final class URLDataSource2 implements DataSource {

    private URL url;
    private final static String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    private byte[] barr;

    /**
     * Creates a URLDataSource from a URL object
     */
    public URLDataSource2(URL url) {
        this.url = url;
    }

    /**
     * Returns the value of the URL content-type header field
     * 
     */
    public String getContentType() {
        URLConnection connection = null;
        try {
            connection = url.openConnection();
        } catch (IOException e) {
        }
        if (connection == null)
            return DEFAULT_CONTENT_TYPE;

        return connection.getContentType();

    }

    /**
     * Returns the file name of the URL object
     */
    public String getName() {
        return url.getFile();
    }

    /**
     * Returns an InputStream obtained from the data source
     */
    public InputStream getInputStream() throws IOException {
    	if(barr==null) {
    		barr=IOUtil.toBytes(url.openStream());
    	}
        return new ByteArrayInputStream(barr);
    }

    /**
     * Returns an OutputStream obtained from the data source
     */
    public OutputStream getOutputStream() throws IOException {

        URLConnection connection = url.openConnection();
        if (connection == null)
            return null;

        connection.setDoOutput(true); //is it necessary?
        return connection.getOutputStream();
    }

    /**
     * Returns the URL of the data source
     */
    public URL getURL() {
        return url;
    }
}