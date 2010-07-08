package railo.runtime.search.lucene2.docs;

import java.io.IOException;
import java.io.InputStream;

import org.apache.lucene.document.Document;
import org.textmining.text.extraction.WordExtractor;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.op.Caster;

/** A utility for making Lucene Documents from a File. */

public final class WordDocument {
    
    private static final int SUMMERY_SIZE=20;
    //private static final char FILE_SEPARATOR = System.getProperty("file.separator").charAt(0);
    
  /** Makes a document for a File.
    <p>
    The document has three fields:
    <ul>
    <li><code>path</code>--containing the pathname of the file, as a stored,
    tokenized field;
    <li><code>modified</code>--containing the last modified date of the file as
    a keyword field as encoded by <a
    href="lucene.document.DateField.html">DateField</a>; and
    <li><code>contents</code>--containing the full contents of the file, as a
    Reader field;
 * @param res
 * @return matching document
 * @throws IOException
    */
    public static Document getDocument(Resource res) throws IOException {
	 
    	// make a new, empty document
    	Document doc = new Document();    	
    	InputStream is =null;
    	try{
    		is=IOUtil.toBufferedInputStream(res.getInputStream());
    		addContent(null,doc,is);
    	}
    	finally{
    		IOUtil.closeEL(is);
    	}
  	    return doc;
  	}
    
    public static Document getDocument(StringBuffer content, InputStream is) throws IOException {
	 	Document doc = new Document();
    	addContent(content,doc,is);
    	return doc;
  	}
  
  

  	private static void addContent(StringBuffer content, Document doc, InputStream is) throws IOException {
    	FieldUtil.setMimeType(doc, "application/msword");
  		WordExtractor extractor = new WordExtractor();
  	    String contents;
		try {
			contents = extractor.extractText(is);
			if(content!=null)content.append(contents);
		} catch (Exception e) {
			if(e instanceof IOException) throw (IOException)e;
			throw new IOException(e.getMessage());
		}
  	    doc.add(FieldUtil.Text("size", Caster.toString(contents.length())));
  	    FieldUtil.setRaw(doc,contents);
  	    FieldUtil.setContent(doc, contents);
  	    //doc.add(FieldUtil.Text("contents", contents.toLowerCase()));
  	    FieldUtil.setSummary(doc, StringUtil.max(contents,SUMMERY_SIZE),false);
  	    //doc.add(FieldUtil.UnIndexed("summary",));
	}



private WordDocument() {}
}
    