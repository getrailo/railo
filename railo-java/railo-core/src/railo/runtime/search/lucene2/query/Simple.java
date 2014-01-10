package railo.runtime.search.lucene2.query;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;

import railo.commons.lang.ParserString;

/**
 * @deprecated no longer in use
 * The simple query is the default query type and is appropriate for the vast majority of searches. 
 * When entering text on a search form, you perform a simple query by entering a word or comma-delimited strings, 
 * with optional wildcard characters. 
 * Verity treats each comma as a logical OR. If you omit the commas, Verity treats the expression as a phrase.
 */
public final class Simple {
    private String OR="or";
    private String AND="and";
    private String NOT="not";
    private char QUOTER='"';
    private String FIELD="contents";
    
    private static final short TYPE_TERMAL=0;
    private static final short TYPE_WILDCARD=1;
    private static final short TYPE_PREFIX=2;
    private static final short TYPE_FUZZY=3;
    private static final short TYPE_PHRASE=4;
    
    private Analyzer analyzer;
    
    private Map results=new WeakHashMap();
    
    /**
     * constructor of the class
     * @param analyzer
     */
    public Simple(Analyzer analyzer) {
        this.analyzer=analyzer;
        
    }
    /**
     * parse given string query
     * @param criteria
     * @return matching Query
     */
    public Query parse(String criteria) {
        Query qry=(Query) results.get(criteria);
        if(qry!=null) return qry;
        
        // remove operators at start
        if(criteria.length()>0) {
            char first=criteria.charAt(0);
            // start with operator
            while(first=='*' || first=='~' || first=='?') {
                criteria=criteria.substring(1);
                if(criteria.length()==0) break;
                first=criteria.charAt(0);
            } 
        }
        
        // make never foud query if quey is empty
        if(criteria.length()==0) {
            BooleanQuery bool = new BooleanQuery();
            bool.add(new TermQuery(new Term(FIELD, "dshnuiaslfspfhsadhfisd")), OccurUtil.toOccur(false, true));
            results.put(criteria,bool);
            return bool;
        }
        
        ParserString ps=new ParserString(criteria);
        qry= orOp(ps);
        results.put(criteria,qry);
        return qry;
    }
    
    

    private Query orOp(ParserString ps) {
        Query query=andOp(ps);
        ps.removeSpace();
               
        // OR
        while(ps.isValidIndex() && ps.forwardIfCurrent(OR) || ps.forwardIfCurrent(',')) {
            ps.removeSpace();
            BooleanQuery bool = new BooleanQuery();
            
            bool.add(query, OccurUtil.toOccur(false, false));
            //bool.add(query, false, false);
            bool.add(andOp(ps), OccurUtil.toOccur(false, false));
            query = bool;
        }
        return query;
    }

    private Query andOp(ParserString ps) {
        Query query = notOp(ps);
        ps.removeSpace();
        
        // AND
        while(ps.isValidIndex() && ps.forwardIfCurrent(AND)) {
            ps.removeSpace();
            BooleanQuery bool = new BooleanQuery();
            bool.add(query, OccurUtil.toOccur(true, false));
            bool.add(notOp(ps), OccurUtil.toOccur(true, false));
            query = bool;
        }
        return query;
    }
    private Query notOp(ParserString ps) {
        // NOT
        if(ps.isValidIndex() && ps.forwardIfCurrent(NOT)) {
            ps.removeSpace();
            BooleanQuery bool = new BooleanQuery();
            bool.add(clip(ps), OccurUtil.toOccur(false, true));
            return bool;
        }
        return clip(ps);
    }

    private Query clip(ParserString ps) {
        // ()
        if(ps.isValidIndex() && ps.forwardIfCurrent('(')) {
            Query query=orOp(ps);
            ps.removeSpace();
            ps.forwardIfCurrent(')');
            ps.removeSpace();
            return query;
        }
        return literal(ps);
    }

    private Query literal(ParserString ps) {
        _Term term=term(ps);
        ps.removeSpace();
        while(ps.isValidIndex() && !ps.isCurrent(',') && !ps.isCurrent(OR) && !ps.isCurrent(AND) && !ps.isCurrent(')')) {
            term.append(term(ps));
            ps.removeSpace();
        }
        return term.toQuery();
    }

    private _Term term(ParserString ps) {
        short type=TYPE_TERMAL;
        ps.removeSpace();
        StringBuffer sb=new StringBuffer();
        boolean inside=false;
        char c=0;
        while(ps.isValidIndex() && ((c=ps.getCurrentLower())!=' ' && c!=',' && c!=')' || inside)) {
            ps.next();
            if(c==QUOTER) {
                inside=!inside;
                type=TYPE_PHRASE;
                continue;
            }
            sb.append(c);
            if(!inside) {
	            if(type==TYPE_PREFIX)type=TYPE_WILDCARD;
	            if(type==TYPE_TERMAL && c=='*')type=TYPE_PREFIX;
	            if(c=='?')type=TYPE_WILDCARD;
	            if(type==TYPE_TERMAL && c=='~') {
	                type=TYPE_FUZZY;
	                break;
	            }
            }
        }
        return new _Term(type,sb.toString());

    }
    
    class _Term {
        private short type;
        private String content;
        
        private _Term(short type, String content) {
            this.type = type;
            this.content=content;
        }
        
        private void append(_Term term) {
            content+=' '+term.content;
            type=TYPE_PHRASE;
        }

        private Query toQuery() {
            if(type==TYPE_FUZZY) return toFuzzyQuery();
            else if(type==TYPE_WILDCARD) return new WildcardQuery(toTerm());
            else if(type==TYPE_PREFIX)return toPrefixQuery();
            else if(type==TYPE_PHRASE) return toPhraseQuery();
            return new TermQuery(toTerm());
        }
        
        private FuzzyQuery toFuzzyQuery() {
            String c=toContent();
            return new FuzzyQuery(new Term(FIELD,c.substring(0,c.length()-1)));
        }
        
        private PrefixQuery toPrefixQuery() {
            String c=toContent();
            return new PrefixQuery(new Term(FIELD,c.substring(0,c.length()-1)));
        }
        
        private PhraseQuery toPhraseQuery() {
            

            TokenStream source = analyzer.tokenStream(FIELD,new StringReader(content));
            Vector v = new Vector();
            org.apache.lucene.analysis.Token t;

            while (true) {
              try {
                t = source.next();
              }
              catch (IOException e) {
                t = null;
              }
              if (t == null)
                break;
              v.addElement(t.termText());
            }
            try {
              source.close();
            }
            catch (IOException e) {
              // ignore
            }

            PhraseQuery q = new PhraseQuery();
             q.setSlop(0);
             
             for (int i=0; i<v.size(); i++) {
                q.add(new Term(FIELD, (String) v.elementAt(i)));
              }
              return q;
            
        }

        private String toContent() {
            return content;
        }
        private Term toTerm() {
            return new Term(FIELD, toContent());
        }
        @Override
        public String toString() {
            return toContent();
        }
    }   
}