package railo.commons.net.http.httpclient3;
 
 import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
  * In this class are only methods to copy a HttpMethod:
  * PUT, GET, POST,DELETE, TRACE, ...
  */
  
  public class HttpMethodCloner {
  
  private static void copyEntityEnclosingMethod(EntityEnclosingMethod m, EntityEnclosingMethod copy ) {
	  copy.setRequestEntity(m.getRequestEntity());
  }
  
  private static void copyHttpMethodBase(HttpMethodBase m, HttpMethodBase copy) {
	  if (m.getHostConfiguration() != null) {
		  copy.setHostConfiguration(new HostConfiguration(m.getHostConfiguration()));
	  }
	  try {
		  copy.setParams((HttpMethodParams)m.getParams().clone());
	  }
	  catch (CloneNotSupportedException e) {}
  }
  
  /**
  * Clones a HttpMethod. &ltbr>
  * &ltb&gtAttention:</b> You have to clone a method before it has
  * been executed, because the URI can change if followRedirects
  * is set to true.
  *
  * @param m the HttpMethod to clone
  *
  * @return the cloned HttpMethod, null if the HttpMethod could
  * not be instantiated
  *
  * @throws java.io.IOException if the request body couldn't be read
  */
  public static HttpMethod clone(HttpMethod m) {
	  HttpMethod copy = null;
	  try {
		  copy = m.getClass().newInstance();
	  } 
	  catch (InstantiationException iEx) {} 
	  catch (IllegalAccessException iaEx) {}
	  if ( copy == null ) {
		  return null;
	  }
	  copy.setDoAuthentication(m.getDoAuthentication());
	  copy.setFollowRedirects(m.getFollowRedirects());
	  copy.setPath( m.getPath() );
	  copy.setQueryString(m.getQueryString());
 
	  Header[] h = m.getRequestHeaders();
	  int size = (h == null) ? 0 : h.length;
 
	  for (int i = 0; i < size; i++) {
		  copy.setRequestHeader(new Header(h[i].getName(), h[i].getValue()));
	  }
	  copy.setStrictMode(m.isStrictMode());
	  if (m instanceof HttpMethodBase) {
		  copyHttpMethodBase((HttpMethodBase)m,(HttpMethodBase)copy);
	  }
	  if (m instanceof EntityEnclosingMethod) {
		  copyEntityEnclosingMethod((EntityEnclosingMethod)m,(EntityEnclosingMethod)copy);
	  }
	  return copy;
  }
} 