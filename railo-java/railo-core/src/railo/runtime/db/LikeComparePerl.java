package railo.runtime.db;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

/**
 * Wildcard Filter
 */
class LikeComparePerl  {
    
    private static final String specials="{}[]().+\\^$";
    private static Perl5Matcher matcher=new Perl5Matcher();
    private static Map patterns=new WeakHashMap();
    static {
    	matcher.setMultiline(true);
    }
    
    
    private static Pattern createPattern(SQL sql, String wildcard, String escape) throws PageException {
    	Pattern pattern=(Pattern) patterns.get(wildcard+escape);
        if(pattern!=null) return pattern;
        char esc=0;
        if(!StringUtil.isEmpty(escape)){
        	esc=escape.charAt(0);
        	if(escape.length()>1)throw new DatabaseException("Invalid escape character ["+escape+"] has been specified in a LIKE conditional",null,sql,null);
        }
        
    	StringBuffer sb = new StringBuffer(wildcard.length());
        int len=wildcard.length();
        //boolean isEscape=false;
        char c;
        for(int i=0;i<len;i++) {
            c = wildcard.charAt(i);
            if(c == esc){
            	if(i+1==len)throw new DatabaseException("Invalid Escape Sequence. Valid sequence pairs for this escape character are: ["+esc+"%] or ["+esc+"_]",null,sql,null);
            	c = wildcard.charAt(++i);
            	if(c == '%')sb.append(c);
            	else if(c == '_') sb.append(c);
            	else throw new DatabaseException("Invalid Escape Sequence ["+esc+""+c+"]. Valid sequence pairs for this escape character are: ["+esc+"%] or ["+esc+"_]",null,sql,null);
            }
            else {
            	if(c == '%')sb.append(".*");
                else if(c == '_') sb.append('.');
                else if(specials.indexOf(c)!=-1)sb.append('\\').append(c);
                else sb.append(c);
            }
            
        }    
        try {
        	patterns.put(wildcard+escape,pattern=new Perl5Compiler().compile(sb.toString(),Perl5Compiler.MULTILINE_MASK));
		} 
        catch (MalformedPatternException e) {
        	throw Caster.toPageException(e);
        }
        return pattern;
    }
    
    public static boolean like(SQL sql, String haystack, String needle) throws PageException {
    	return like(sql, haystack, needle, null);
    }
    
    public static boolean like(SQL sql, String haystack, String needle,String escape) throws PageException {
    	return matcher.matches(StringUtil.toLowerCase(haystack), createPattern(sql,StringUtil.toLowerCase(needle),escape==null?null:StringUtil.toLowerCase(escape)));
    }
    
}