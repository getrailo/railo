package railo.runtime.search.lucene2;

import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.el.GreekAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.th.ThaiAnalyzer;

import railo.commons.collection.MapFactory;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.search.SearchException;
import railo.runtime.search.lucene2.analyzer.DanishAnalyzer;
import railo.runtime.search.lucene2.analyzer.ItalianAnalyzer;
import railo.runtime.search.lucene2.analyzer.NorwegianAnalyzer;
import railo.runtime.search.lucene2.analyzer.PortugueseAnalyzer;
import railo.runtime.search.lucene2.analyzer.SpanishAnalyzer;
public final class SearchUtil {

	private static Map<String,Analyzer> analyzers=MapFactory.<String,Analyzer>getConcurrentMap();
	
	public static Analyzer getAnalyzer(String language) throws SearchException {
        if(language==null)language="english";
        else language=language.toLowerCase().trim();
        language=railo.runtime.search.SearchUtil.translateLanguage(language);
		
        Analyzer analyzer=analyzers.get(language);
        if(analyzer!=null) return analyzer;
        
        
        if(language.equals("english"))
        	analyzer= new StandardAnalyzer();
        else if(language.equals("german")) 		
        	analyzer= new GermanAnalyzer();
        else if(language.equals("russian")) 	
        	analyzer= new RussianAnalyzer();
        else if(language.equals("dutch")) 		
        	analyzer= new DutchAnalyzer();
        else if(language.equals("french")) 		
        	analyzer= new FrenchAnalyzer();
        else if(language.equals("norwegian")) 	
        	analyzer= new NorwegianAnalyzer();
        else if(language.equals("portuguese")) 	
        	analyzer= new PortugueseAnalyzer();
        else if(language.equals("spanish")) 	
        	analyzer= new SpanishAnalyzer();
        else if(language.equals("brazilian")) 	
        	analyzer= new BrazilianAnalyzer();
        else if(language.equals("chinese")) 	
        	analyzer= new ChineseAnalyzer();
        else if(language.startsWith("czech")) 	
        	analyzer= new CzechAnalyzer();
        else if(language.equals("greek")) 		
        	analyzer= new GreekAnalyzer();
        else if(language.equals("thai"))
        	analyzer= new ThaiAnalyzer();
        else if(language.equals("japanese"))
        	analyzer= new CJKAnalyzer();
        else if(language.equals("korean"))
        	analyzer= new CJKAnalyzer();

        else if(language.equals("italian"))
        	analyzer= new ItalianAnalyzer();
        else if(language.equals("danish"))
        	analyzer= new DanishAnalyzer();
        else if(language.equals("norwegian"))
        	analyzer= new NorwegianAnalyzer();
        else if(language.equals("finnish"))
        	analyzer= new SnowballAnalyzer( "Finnish" );
        else if(language.equals("swedish"))
        	analyzer= new SnowballAnalyzer( "Swedish" );
        
        
        else {
        	String clazzName="org.apache.lucene.analysis.el."+StringUtil.ucFirst(language.trim().toLowerCase())+"Analyzer;";
        	Object o=ClassUtil.loadInstance(clazzName,(Object)null);
            if(o==null){
            	clazzName="railo.runtime.search.lucene2.analyzer."+StringUtil.ucFirst(language.trim().toLowerCase())+"Analyzer";
            	o=ClassUtil.loadInstance(clazzName,(Object)null);//Class.orName(clazzName).newInstance();
        	}
            if(o instanceof Analyzer) analyzer=(Analyzer) o;
            else if(o==null) 
            	 throw new SearchException("can't create Language Analyzer for Lanuage "+language+", make Analyzer ["+clazzName+"] available");
            else 
                throw new SearchException( "can't create Language Analyzer for Lanuage "+language+", Analyzer ["+clazzName+"] is of invalid type");
        }        
        analyzers.put(language, analyzer);
        return analyzer;
    }
}
