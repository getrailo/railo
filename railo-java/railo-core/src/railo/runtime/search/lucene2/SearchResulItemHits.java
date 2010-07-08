package railo.runtime.search.lucene2;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;

import railo.runtime.search.SearchResultItemPro;
import railo.runtime.search.lucene2.highlight.Highlight;

public class SearchResulItemHits implements SearchResultItemPro {

	
	
	private Hits hits;
	private int index;
	private Object highlighter;
	private Analyzer analyzer;
	private String id;
	private String categoryTree;
	private String category;
	private int maxNumFragments;
	private int maxLength;
	private Document doc;

	public SearchResulItemHits(Hits hits, int index, Object highlighter,Analyzer analyzer,
			String id, String categoryTree, String category,int maxNumFragments, int maxLength) {
		this.hits=hits;
		this.index=index;
		this.highlighter=highlighter;
		this.analyzer=analyzer;
		this.id=id;
		this.categoryTree=categoryTree;
		this.category=category;
		this.maxNumFragments=maxNumFragments;
		this.maxLength=maxLength;
	}

	/**
	 * @see railo.runtime.search.coreDuplicate.SearchResulItem#getAuthor()
	 */
	public String getAuthor() {
		return doc("author");
	}

	/**
	 * @see railo.runtime.search.coreDuplicate.SearchResulItem#getCategory()
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @see railo.runtime.search.coreDuplicate.SearchResulItem#getCategoryTree()
	 */
	public String getCategoryTree() {
		return categoryTree;
	}

	/**
	 * @see railo.runtime.search.coreDuplicate.SearchResulItem#getCustom1()
	 */
	public String getCustom1() {
		return doc("custom1");
	}

	/**
	 * @see railo.runtime.search.coreDuplicate.SearchResulItem#getCustom2()
	 */
	public String getCustom2() {
		return doc("custom2");
	}

	/**
	 * @see railo.runtime.search.coreDuplicate.SearchResulItem#getCustom3()
	 */
	public String getCustom3() {
		return doc("custom3");
	}

	/**
	 * @see railo.runtime.search.coreDuplicate.SearchResulItem#getCustom4()
	 */
	public String getCustom4() {
		return doc("custom4");
	}

	/**
	 * @see railo.runtime.search.coreDuplicate.SearchResulItem#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * @see railo.runtime.search.coreDuplicate.SearchResulItem#getKey()
	 */
	public String getKey() {
		return doc("key");
	}

	/**
	 * @see railo.runtime.search.coreDuplicate.SearchResulItem#getMimeType()
	 */
	public String getMimeType() {
		return doc("mime-type");
	}

	public int getRecordsSearched() {
		// TODO Auto-generated method stub
		return 0;
	}
    

	/**
	 * @see railo.runtime.search.coreDuplicate.SearchResulItem#getScore()
	 */
	public float getScore() {
		try {
			return hits.score(index);
		} catch (IOException e) {
			return 0;
		}
	}

	/**
	 * @see railo.runtime.search.coreDuplicate.SearchResulItem#getSize()
	 */
	public String getSize() {
		return doc("size");
	}

	/**
	 * @see railo.runtime.search.coreDuplicate.SearchResulItem#getSummary()
	 */
	public String getSummary() {
		return doc("summary");
	}

	/**
	 * @see railo.runtime.search.coreDuplicate.SearchResulItem#getTitle()
	 */
	public String getTitle() {
		return doc("title");
	}

	/**
	 * @see railo.runtime.search.coreDuplicate.SearchResulItem#getUrl()
	 */
	public String getUrl() {
		return doc("url");
	}
	
	/** FUTURE add to interface
	 * @return the contextSummary
	 */
	public String getContextSummary() {
		String contextSummary="";
		if(maxNumFragments>0){
			contextSummary=Highlight.createContextSummary(highlighter,analyzer,doc("contents"),maxNumFragments,maxLength,"");
		}
		return contextSummary;
	}

	private String doc(String field) {
		if(doc==null){
			try {
				doc=hits.doc(index);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return doc.get(field);
	}
}
