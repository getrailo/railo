package railo.runtime.search.lucene2.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import railo.commons.lang.ParserString;


/**
 * The simple query is the default query type and is appropriate for the vast majority of searches. 
 * When entering text on a search form, you perform a simple query by entering a word or comma-delimited strings, 
 * with optional wildcard characters. 
 * Verity treats each comma as a logical OR. If you omit the commas, Verity treats the expression as a phrase.
 */
public final class QueryParser {
    private static final String OR="or";
    private static final String AND="and";
    private static final String NOT="not";
    private static final char QUOTER='"';
    
	private static final String STAR = "*";
	private List list=new ArrayList();


	/**
     * parse given string query
     * @param criteria
     * @return matching Query
     */
    public String parse(String criteria) {
    	Op op = parseOp(criteria);
    	if(op==null) return STAR;
    	return op.toString();
    }
    public Op parseOp(String criteria) {
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
        	return null;
        }
        
        //StringBuffer str=new StringBuffer();
        ParserString ps=new ParserString(criteria);
        Op op=null;
        while(!ps.isAfterLast()) {
        	if(op==null)op=orOp(ps);
        	else op=new Concator(op,orOp(ps));
        }
        return op;
    }
    
    

    private Op orOp(ParserString ps) {
        Op op=andOp(ps);
        ps.removeSpace();
               
        // OR
        while(ps.isValidIndex() && (ps.forwardIfCurrent(OR) || ps.forwardIfCurrent(','))) {
            ps.removeSpace();
            if(ps.isAfterLast()) op=new Concator(op,new Literal("OR") );
            else op=new Or(op,andOp(ps));
        }
        return op;
    }

    private Op andOp(ParserString ps) {
        Op op = notOp(ps);
        ps.removeSpace();
        
        // AND
        while(ps.isValidIndex() && ps.forwardIfCurrent(AND)) {
            ps.removeSpace();
            if(ps.isAfterLast()) op=new Concator(op,new Literal("AND") );
            else op=new And(op,notOp(ps));
        }
        return op;
    }
    
    private Op notOp(ParserString ps) {
        Op op = spaceOp(ps);
        ps.removeSpace();
        
        // NOT
        while(ps.isValidIndex() && ps.forwardIfCurrent(NOT)) {
            ps.removeSpace();
            if(ps.isAfterLast()) op=new Concator(op,new Literal("NOT") );
            else {
            	Op r;
				op=new Not(op,r=clip(ps));
				this.list.remove(r);
            }
        }
        return op;
    }
    
    private Op spaceOp(ParserString ps) {
        Op op = clip(ps);
        //ps.removeSpace();
        
        // Concat
        while(ps.isValidIndex() && isSpace(ps.getCurrent()) && !(ps.isCurrentIgnoreSpace(OR) || ps.isCurrentIgnoreSpace(',') || ps.isCurrentIgnoreSpace(AND) || ps.isCurrentIgnoreSpace(NOT))) {
            ps.removeSpace();
            op=new Concator(op,clip(ps));
        }
        return op;
    }

    private Op clip(ParserString ps) {
        // ()
        if(ps.isValidIndex() && ps.forwardIfCurrent('(')) {
            Op op=orOp(ps);
            ps.removeSpace();
            ps.forwardIfCurrent(')');
            ps.removeSpace();
            return op;
        }
        return literal(ps);
    }
    
    private Op literal(ParserString ps) {
    	ps.removeSpace();
    	
    	if(ps.isCurrent(QUOTER)) return quotedLiteral(ps);
    	return notQuotedLiteral(ps);
    }
 

    private Op quotedLiteral(ParserString ps) {
    	StringBuffer str=new StringBuffer();
		ps.next();
		char c;
		while(!ps.isAfterLast()) {
			c=ps.getCurrent();
			if(c==QUOTER) {
				ps.next();
				if(ps.isCurrent(QUOTER))	str.append(QUOTER);
                else break;
            }
            else {
            	str.append(c);	
            }
			ps.next();
		}
		
		return register(new Literal(str.toString()));
    }
 
    private Op notQuotedLiteral(ParserString ps) {
    	
		StringBuffer str=new StringBuffer();
		ps.removeSpace();
		
		char c;
		
		while(!ps.isAfterLast()) {
			c=ps.getCurrent();
			if(isSpace(c) || c==',') break;
			str.append(c);
			ps.next();
		}
		return register(new Literal(str.toString()));
    	
    }
    
    private boolean isSpace(char c) {
		return c==' ' || c=='\t' || c=='\n' || c=='\b';
	}



	/*public static void main(String[] args) {
    	QueryParser qp = new QueryParser();
    	
    	qp.parseOp("aaa zzz not bbb and ccc");
		print.out(qp.getLiteralSearchedTerms());
    	if(true) return;
		print.out(qp.parse("\"abc\""));
		print.out(qp.parse("abc"));
		print.out(qp.parse("abc def"));
		print.out(qp.parse("abc def"));
		print.out(qp.parse("abc and def"));
		print.out(qp.parse("\"\"\"abc\"\"\""));
		print.out(qp.parse("\"abc\" susi or peter"));
		print.out(qp.parse("abc susi or peter"));
		print.out(qp.parse("abc or susi or peter"));
		print.out(qp.parse("*abc susi and peter or \"abc\"* , xxx,yy*"));
		print.out(qp.parse("xxx,y\"y*"));
		print.out(qp.parse("xxx y\"y*"));
		print.out(qp.parse(""));
		print.out(qp.parse("per or"));
		print.out(qp.parse("per and"));
		print.out(qp.parse("per not"));
		print.out(qp.parse("andi per not susi"));
		print.out(qp.parse("\"kinderhort test\""));
	}*/
	public Literal register(Literal literal) {
		list.add(literal);
		return literal;
	}

	public Literal[] getLiteralSearchedTerms() {
		return (Literal[]) list.toArray(new Literal[list.size()]);
	}
	
	public String[] getStringSearchedTerms() {
		Iterator it = list.iterator();
		String[] rtn=new String[list.size()];
		int i=0;
		while(it.hasNext()) {
			rtn[i++]=it.next().toString();
		}
		
		return rtn;
	}
    
}