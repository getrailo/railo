package railo.runtime.search.lucene2;


import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.op.Caster;
import railo.runtime.search.lucene2.docs.FieldUtil;
import railo.runtime.search.lucene2.docs.FileDocument;
import railo.runtime.search.lucene2.docs.HTMLDocument;
import railo.runtime.search.lucene2.docs.PDFDocument;
import railo.runtime.search.lucene2.docs.WordDocument;

/**
 * creates a matching Document Object to given File
 */
public final class DocumentUtil {

	public static Document toDocument(StringBuffer content,String root,URL url, HttpMethod method) throws IOException {
        if(method.getStatusCode()!=200)return null;
        
		// get type and charset
		Document doc=null;
		String type=getContentType(method);
		long len=getContentLength(method);
		String charset="iso-8859-1";
        if(!StringUtil.isEmpty(type)){
        	String[] types=type.split(";");
        	type=types[0];
        	if(types.length>1) {
                String tmp=types[types.length-1];
                int index=tmp.indexOf("charset=");
                if(index!=-1) {
                	charset=StringUtil.removeQuotes(tmp.substring(index+8),true);
                }
            }
        }
        Runtime rt = Runtime.getRuntime();
        if(len>rt.freeMemory()){
        	Runtime.getRuntime().gc();
        	if(len>rt.freeMemory()) return null;
        }
        	
        //print.err("url:"+url+";chr:"+charset+";type:"+type);
        
        if(type==null)  {}
        // HTML
        else if(type.indexOf("text/html")!=-1) {
        	Reader r=null;
        	try{
        		r = IOUtil.getReader(method.getResponseBodyAsStream(), charset);
        		doc= HTMLDocument.getDocument(content,r);
        	}
        	finally{
        		IOUtil.closeEL(r);
        	}
        }
        // PDF
        else if(type.indexOf("application/pdf")!=-1) {
        	InputStream is=null;
        	try{
        		is=IOUtil.toBufferedInputStream(method.getResponseBodyAsStream());
        		doc= PDFDocument.getDocument(content,is);
        	}
        	finally {
        		IOUtil.closeEL(is);
        	}
        }
        // DOC
        else if(type.equals("application/msword")) {
        	InputStream is=null;
        	try{
        		is=IOUtil.toBufferedInputStream(method.getResponseBodyAsStream());
        		doc= WordDocument.getDocument(content,is);
        	}
        	finally {
        		IOUtil.closeEL(is);
        	}
            
        }
        // Plain
        else if(type.indexOf("text/plain")!=-1) {
        	Reader r=null;
        	try{
        		r=IOUtil.toBufferedReader(IOUtil.getReader(method.getResponseBodyAsStream(), charset));
        		doc= FileDocument.getDocument(content,r);
        	}
        	finally {
        		IOUtil.closeEL(r);
        	}
        }
        
        if(doc!=null){
        	String strPath=url.toExternalForm();
    	   
    	    doc.add(FieldUtil.UnIndexed("url", strPath));
    	    doc.add(FieldUtil.UnIndexed("key", strPath));
    	    doc.add(FieldUtil.UnIndexed("path", strPath));
    	    //doc.add(FieldUtil.UnIndexed("size", Caster.toString(file.length())));
    	    //doc.add(FieldUtil.Keyword("modified",DateField.timeToString(file.lastModified())));
        }
        
        return doc;
        
    }

	private static String getContentType(HttpMethod method) {
		Header ct = method.getResponseHeader("Content-Type");
		if(!StringUtil.isEmpty(ct)) return ct.getValue().toLowerCase();
		Header[] headers = method.getResponseHeaders();
		for(int i=0;i<headers.length;i++){
			if("Content-Type".equalsIgnoreCase(headers[i].getName())){
				if(!StringUtil.isEmpty(headers[i].getValue()))return headers[i].getValue().toLowerCase();
				return null;
			}
		}
    	return null;
	}

	private static long getContentLength(HttpMethod method) {
		Header ct = method.getResponseHeader("Content-Length");
		if(!StringUtil.isEmpty(ct)) return Caster.toLongValue(ct.getValue(),-1);
		Header[] headers = method.getResponseHeaders();
		for(int i=0;i<headers.length;i++){
			if("Content-Length".equalsIgnoreCase(headers[i].getName())){
				if(!StringUtil.isEmpty(headers[i].getValue()))return Caster.toLongValue(headers[i].getValue(),-1);
				return -1;
			}
		}
    	return -1;
	}
	
	
	
	
    /**
     * translate the file to a Document Object
     * @param file
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public static Document toDocument(Resource file,String url,String charset) throws IOException {
        String ext = ResourceUtil.getExtension(file,null);
        Document doc=null;
        if(ext!=null) {
            ext=ext.toLowerCase();
            //String mimeType=new MimetypesFileTypeMap().getContentType(f);
            // HTML
            if(ext.equals("htm") || ext.equals("html") || ext.equals("cfm") || ext.equals("cfml") || ext.equals("php") || ext.equals("asp") || ext.equals("aspx")) {
                doc= HTMLDocument.getDocument(file);
            }
            // PDF
            else if(ext.equals("pdf")) {
                doc= PDFDocument.getDocument(file);
            }
            // DOC
            else if(ext.equals("doc")) {
                doc= WordDocument.getDocument(file);
            }
        }
        else {
            String type=ResourceUtil.getMymeType(file,"");
            if(type==null)  {}
            // HTML
            else if(type.equals("text/html")) {
                doc= HTMLDocument.getDocument(file);
            }
            // PDF
            else if(type.equals("application/pdf")) {
                doc= PDFDocument.getDocument(file);
            }
            // DOC
            else if(type.equals("application/msword")) {
                doc= WordDocument.getDocument(file);
            }
        }
        if(doc==null) doc= FileDocument.getDocument(file,charset);
        
        String strPath=file.getPath().replace('\\', '/');
	    String strName=strPath.substring(strPath.lastIndexOf('/'));
	    
	    
	    doc.add(FieldUtil.UnIndexed("url", strName));
	    
	    doc.add(FieldUtil.UnIndexed("key", strPath));
	    doc.add(FieldUtil.UnIndexed("path", file.getPath()));
	    doc.add(FieldUtil.UnIndexed("size", Caster.toString(file.length())));
	    doc.add(FieldUtil.UnIndexed("modified",DateField.timeToString(file.lastModified())));
        
        
        return doc;
    }
    
}