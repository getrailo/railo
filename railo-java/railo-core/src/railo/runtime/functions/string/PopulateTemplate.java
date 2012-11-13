package railo.runtime.functions.string;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Struct;


/**
 * populates a text template with values from a given struct.
 * 
 * this is a recursive, non-case-sensitive replaceAll of key/value pairs from a Struct, 
 * so it's a find Key, replace with Value 
 */
public class PopulateTemplate implements Function {

	
	public static String call( PageContext pc, String input, Struct map ) {
	
		if ( map == null || map.isEmpty() )
            return input;
        
        return replaceMap( input, map, true );
	}
	
    
    private static String replaceString( String input, String oldSub, String newSub, boolean isCaseSensitive ) {
        
        String in = input;
        
        if ( !isCaseSensitive ) {
            
            in      = in.toLowerCase();
            oldSub  = oldSub.toLowerCase();
        }
        
        StringBuilder sb = new StringBuilder();
        
        int start = 0;
        int pos;
        int subLen = oldSub.length();
        
        while ( (pos = in.indexOf( oldSub, start ) ) != -1 ) {
            
            sb.append( input.substring( start, pos ) );
            sb.append( newSub );
            
            start = pos + subLen;
        }
        
        sb.append( input.substring( start ) );

        return sb.toString();
    }
    
    
    private static String replaceMap( String input, Map map, boolean doResolveInternals ) {
        
        String result = input;
        
        if ( doResolveInternals )
            map = resolveInternals( map, 0 );
        
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        
        while ( it.hasNext() ) {
            
            Map.Entry<String, String> e = it.next();
            
            result = replaceString( result, e.getKey().toString(), e.getValue().toString(), false );
        }
        
        return result;
    }
    
    
    /**
     * resolves internal values within the map, so if the map has a key "{signature}" 
     * and its value is "Team {group}" and there's a key with the value {group} whose
     * value is "Railo", then {signature} will resolve to "Team Railo".
     * 
     *  {signature} = "Team {group}"
     *  {group}     = "Railo"
     * 
     * then signature will resolve to
     * 
     *  {signature} = "Team Railo"
     * 
     * @param map - key/value pairs for find key/replace with value
     * @param count - used internally as safety valve to ensure that we don't go into infinite loop if two values reference each-other
     * @return 
     */
    private static Map resolveInternals( Map map, int count ) {
        
        Map result = new HashMap();
        
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        
        boolean isModified = false;
        
        while ( it.hasNext() ) {
            
            Map.Entry<String, String> e = it.next();
            
            String k = e.getKey();
            String v = e.getValue().toString();
            String r = replaceMap( v, map, false );
            
            result.put( k, r );
            
            if ( !v.equalsIgnoreCase( r ) )
                isModified = true;
        }
                
        if ( isModified && count++ < map.size() )
            result = resolveInternals( result, count );
        
        return result;
    }
	
}
