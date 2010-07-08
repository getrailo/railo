package railo.runtime.search.lucene2.docs;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.document.Document;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.op.Caster;

/** A utility for making Lucene Documents from a File. */

public final class FileDocument {
    
    //private static final char FILE_SEPARATOR = System.getProperty("file.separator").charAt(0);
    private static final int SUMMERY_SIZE=200;
    
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
  public static Document getDocument(Resource res,String charset)
       throws IOException {
	 
    // make a new, empty document
    Document doc = new Document();
    doc.add(FieldUtil.UnIndexed("mime-type", "text/plain"));

    String content=IOUtil.toString(res,charset);
    FieldUtil.setRaw(doc,content);
    //doc.add(FieldUtil.UnIndexed("raw", content));
    doc.add(FieldUtil.Text("contents", content.toLowerCase()));
    doc.add(FieldUtil.UnIndexed("summary",StringUtil.max(content,SUMMERY_SIZE)));
    return doc;
  }
  

  public static Document getDocument(StringBuffer content, Reader r) throws IOException {
	 
    // make a new, empty document
    Document doc = new Document();
    FieldUtil.setMimeType(doc, "text/plain");
    //
    String contents=IOUtil.toString(r);
    if(content!=null)content.append(contents);
    doc.add(FieldUtil.UnIndexed("size", Caster.toString(contents.length())));
    FieldUtil.setContent(doc, contents);
    FieldUtil.setRaw(doc, contents);
    FieldUtil.setSummary(doc, StringUtil.max(contents,SUMMERY_SIZE),false);
    return doc;
  }

  private FileDocument() {}
}
    