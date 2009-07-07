package railo.runtime.search.lucene2.net;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;

import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.HTMLUtil;
import railo.commons.lang.StringUtil;
import railo.commons.net.HTTPUtil;
import railo.runtime.search.lucene2.DocumentUtil;
import railo.runtime.tag.Index;
import railo.runtime.type.util.ArrayUtil;

/**
 * 
 */
public final class WebCrawler {
    
    private HTMLUtil htmlUtil=new HTMLUtil();
	
    
    
    public static void main(String[] args) throws MalformedURLException, IOException {
    	String[] ex=new String[]{".cfm",".pdf"};
    	
		WebCrawler wc = new WebCrawler();
		/*
		wc.parse(null, new URL("http://localhost:8888/susi"), ex,false);
		wc.parse(null, new URL("http://localhost:8888/susi/"), ex,false);
		wc.parse(null, new URL("http://localhost:8888/susi/index.cfm"), ex,false);
		wc.parse(null, new URL("http://localhost:8888/susi/index.cfm?a=1"), ex,false);
		wc.parse(null, new URL("http://localhost:8888/susi/?x=1"), ex,false);
		wc.parse(null, new URL("http://localhost:8888/susi/index.cfm;sss"), ex,false);
		wc.parse(null, new URL("http://localhost:8888/susi/index.cfm/aaa/ddd/sss"), ex,false);
		*/
		wc.parse(null, new URL("http://hcc.weblinedesigns.com/#"), ex,false);
		
	}
    
    public void parse(IndexWriter writer, URL current, String[] extensions, boolean recurse) throws IOException {
    	translateExtension(extensions);
    	if(ArrayUtil.isEmpty(extensions))extensions=Index.EXTENSIONS;
        _parse(writer,null,current,new ArrayList(), extensions,recurse,0);
    }
    
	private static URL translateURL(URL url) throws MalformedURLException {
		String path=url.getPath();
		int dotIndex = path.lastIndexOf('.');
		// no dot
		if(dotIndex==-1){
			if(path.endsWith("/")) return HTTPUtil.removeRef(url);
			return HTTPUtil.removeRef(new URL(
					url.getProtocol(),
					url.getHost(),
					url.getPort(),path+"/"+StringUtil.toStringEmptyIfNull(url.getQuery())));
		}
		return HTTPUtil.removeRef(url);
	}   
    

    private void translateExtension(String[] extensions) {
		for(int i=0;i<extensions.length;i++){
			if(extensions[i].startsWith("*."))extensions[i]=extensions[i].substring(2);
			else if(extensions[i].startsWith("."))extensions[i]=extensions[i].substring(1);
		}
	}




	/**
     * @param writer
     * @param current
     * @throws IOException
     * @throws IOException
     */
    private void _parse(IndexWriter writer, String root, URL current, List urlsDone, String[] extensions, boolean recurse,int deep) throws IOException {
    	current=translateURL(current);
    	if(urlsDone.contains(current.toExternalForm())) return;
    	
    	HttpMethod method = HTTPUtil.invoke(current, null, null, -1, null, "RailoBot", null, -1, null, null, null);
    	StringBuffer content=new StringBuffer();
    	Document doc = DocumentUtil.toDocument(content,root,current, method);
    	
        urlsDone.add(current.toExternalForm());
        if(doc==null) return;
        if(writer!=null)writer.addDocument(doc);
        
        
        if(recurse) {
            List urls = htmlUtil.getURLS(content.toString(),current);
	        int len=urls.size();
	        for(int i=0;i<len;i++) {
	            URL url=(URL) urls.get(i);
	            String protocol=url.getProtocol().toLowerCase();
	            String file=url.getPath();
	            if((protocol.equals("http") || protocol.equals("https")) && validExtension(extensions,file) &&
	               current.getHost().equalsIgnoreCase(url.getHost())) {
	            	
	                //if(!urlsDone.contains(url.toExternalForm())) {
	                    try {
	                    	_parse(writer,root,url,urlsDone,extensions,recurse,deep+1);
	                    }
	                    catch(Throwable t) {
	                    }
	                //}
	            }
	        }
	        urls.clear();
        }
    }

	private boolean validExtension(String[] extensions, String file) {
		
		String ext = ResourceUtil.getExtension(file);
		ext=railo.runtime.type.List.first(ext,"/");
		
		if(StringUtil.isEmpty(ext))return true;
		for(int i=0;i<extensions.length;i++){
			if(ext.equalsIgnoreCase(extensions[i]))return true;
		}
		return false;
	}
}