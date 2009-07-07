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
        String custom1,String custom2,String custom3,String custom4) {
	 
    // make a new, empty document
    Document doc = new Document();
    doc.add(FieldUtil.UnIndexed("size", Caster.toString(content.length())));

    doc.add(FieldUtil.Text("key", key));
    doc.add(FieldUtil.UnIndexed("mime-type", "text/plain"));
    doc.add(FieldUtil.Text("contents", content.toLowerCase()));
    doc.add(FieldUtil.UnIndexed("summary",StringUtil.max(content,SUMMERY_SIZE)));
    doc.add(FieldUtil.UnIndexed("url", ""));
    if(title!=null)doc.add(FieldUtil.Text("title", title));
    if(custom1!=null)doc.add(FieldUtil.UnIndexed("custom1", custom1));
    if(custom2!=null)doc.add(FieldUtil.UnIndexed("custom2", custom2));
    if(custom3!=null)doc.add(FieldUtil.UnIndexed("custom3", custom3));
    if(custom4!=null)doc.add(FieldUtil.UnIndexed("custom4", custom4));
    return doc;
  }

  private CustomDocument() {}

}
    