package railo.commons.net;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.RequestEntity;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;

/**
 * A RequestEntity that represents a Resource.
 */
public class ResourceRequestEntity implements RequestEntity {

    final Resource res;
    final String contentType;
    
    public ResourceRequestEntity(final Resource res, final String contentType) {
        this.res = res;
        this.contentType = contentType;
    }
    public long getContentLength() {
        return this.res.length();
    }

    public String getContentType() {
        return this.contentType;
    }

    public boolean isRepeatable() {
        return true;
    }

    public void writeRequest(final OutputStream out) throws IOException {
       IOUtil.copy(res.getInputStream(), out,true,false);
    }    
    
}