package railo.runtime.search.lucene2.docs;

import org.apache.lucene.document.Document;

import railo.commons.lang.StringUtil;
import railo.runtime.op.Caster;


/** A utility for making Lucene Documents from a File. */

public final class CustomDocument {
    
    private static final int SUMMERY_SIZE=200;
    

  /**
 * @param title
 * @param key
 * @param content
 * @param custom1 
 * @param custom2 
 * @param custom3 
 * @param custom4 
 * @return Document
 */
public static Document getDocument(String title, String key, String content,
		String urlpath,String custom1,String custom2,String custom3,String custom4) {
	 
    // make a new, empty document
    Document doc = new Document();
    doc.add(FieldUtil.UnIndexed("size", Caster.toString(content.length())));
    
    doc.add(FieldUtil.Text("key", key));
    FieldUtil.setMimeType(doc, "text/plain");
    FieldUtil.setRaw(doc,content);
    FieldUtil.setContent(doc, content);
    FieldUtil.setSummary(doc, StringUtil.max(content,SUMMERY_SIZE),false);
    
    FieldUtil.setTitle(doc, title);
    FieldUtil.setURL(doc, urlpath);
    FieldUtil.setCustom(doc, custom1, 1);
    FieldUtil.setCustom(doc, custom2, 2);
    FieldUtil.setCustom(doc, custom3, 3);
    FieldUtil.setCustom(doc, custom4, 4);
    return doc;
  }

  private CustomDocument() {}

}
    