package railo.commons.net.http.httpclient3;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.methods.multipart.PartSource;

import railo.commons.io.res.Resource;

public final class ResourcePartSource implements PartSource {

    private final Resource res;
    private String fileName = null;
    
    /**
     * Constructor of the class
     * 
     * @param res the FilePart source File. 
     *
     * @throws FileNotFoundException if the file does not exist or 
     * cannot be read
     */
    public ResourcePartSource(Resource res) throws FileNotFoundException {
        this.res = res;
        if (res != null) {
            if (!res.isFile()) {
                throw new FileNotFoundException("File is not a normal file.");
            }
            if (!res.isReadable()) {
                throw new FileNotFoundException("File is not readable.");
            }
            this.fileName = res.getName();       
        }
    }

    /**
     * Constructor for FilePartSource.
     * 
     * @param fileName the file name of the FilePart
     * @param file the source File for the FilePart
     *
     * @throws FileNotFoundException if the file does not exist or 
     * cannot be read
     */
    public ResourcePartSource(String fileName, Resource file) 
      throws FileNotFoundException {
        this(file);
        if (fileName != null) {
            this.fileName = fileName;
        }
    }
    
    @Override
    public long getLength() {
        if (this.res != null) {
            return this.res.length();
        } 
        return 0;
    }

    @Override
    public String getFileName() {
    	return (fileName == null) ? "noname" : fileName;
    }

    @Override
    public InputStream createInputStream() throws IOException {
    	return res.getInputStream();
    }

    /**
	 * @return the res
	 */
	public Resource getResource() {
		return res;
	}
}