package railo.runtime.search.lucene2.docs;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class FieldUtil {

	public static Field UnIndexed(String name, String value) {
		return new Field(name,value,Field.Store.YES,Field.Index.NO);
	}

	public static Field Text(String name, String value) {//print.out("text:"+name);
		return new Field(name,value,Field.Store.YES,Field.Index.TOKENIZED);
	}

	public static Field Text(String name, String value,boolean store) {
		return new Field(name,value,store?Field.Store.YES:Field.Store.NO,Field.Index.TOKENIZED);
	}

	public static Field Keyword(String name, String value) {
		return new Field(name,value,Field.Store.YES,Field.Index.TOKENIZED);
	}

	public static void addRaw(Document doc, String content) {
		doc.add(FieldUtil.UnIndexed("raw", content));
	}
	
}
