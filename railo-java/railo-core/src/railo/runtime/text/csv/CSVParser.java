package railo.runtime.text.csv;


import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class CSVParser {


    public static Query toQuery( String csv, char delimiter, char textQualifier, String[] headers, boolean firstRowIsHeaders ) throws CSVParserException, PageException {

        List<List<String>> allRows = parseLines( csv, delimiter, textQualifier, '\\', null );

        int numRows = allRows.size();

        if ( numRows == 0 )
            throw new CSVParserException( "No data found in CSV string" );

        List<String> row = allRows.get( 0 );
        int numCols = row.size();
        int curRow = 0;

        if ( firstRowIsHeaders ) {  // set first line to header

            curRow++;

            if ( headers == null )
                headers = makeUnique( row.toArray( new String[ numCols ] ) );
        }

        if( headers == null ) {

            headers = new String[ numCols ];

            for ( int i=0; i < numCols; i++ )
                headers[ i ] = "COLUMN_" + ( i + 1 );
        }

        Array[] arrays = new Array[ numCols ];  // create column Arrays
        for( int i=0; i < numCols; i++ )
            arrays[ i ] = new ArrayImpl();

        while ( curRow < numRows ) {

            row = allRows.get( curRow++ );

            if ( row.size() != numCols )
                throw new CSVParserException( "Invalid CSV line size, expected " + numCols + " columns but found " + row.size() + " instead", row.toString() );

            for ( int i=0; i < numCols; i++ ) {
                arrays[ i ].append( row.get( i ) );
            }
        }

        return new QueryImpl( headers, arrays, "query" );
    }

    
    public static List<List<String>> parseLines( String csv, char colDelim, char quote, char escape, String lineComment ) {

        List lines = new ArrayList();

        if ( StringUtil.isEmpty( csv ) )
            return lines;

        csv = csv.trim();

        boolean inQuotes = false;
        int pos = 0, lineStart = 0, len = csv.length();
        char last = 0, c;

        while ( pos < len ) {

            c = csv.charAt( pos );

            if ( c == quote ) {
                if ( last == quote || last != escape )  // two consecutive quotes or not escaped
                    inQuotes = !inQuotes;
            }

            if ( !inQuotes ) {

                if ( c == '\n' ) {

                    addLine( lines, csv.substring( lineStart, pos ), colDelim, quote, escape, lineComment );
                    lineStart = pos + 1;
                }
            }

            last = c;
            pos++;
        }

        if ( pos > lineStart )
            addLine( lines, csv.substring( lineStart, pos ), colDelim, quote, escape, lineComment );

        return lines;
    }


    private static void addLine( List<List<String>> lines, String line, char colDelim, char quote, char escape, String lineComment ) {

        line = line.trim();

        if ( line.isEmpty() )
            return;

        if ( !StringUtil.isEmpty( lineComment ) && line.startsWith( lineComment ) )
            return;

        List<String> tokens = new ArrayList();

        boolean inQuotes = false;
        int p = 0, tokenStart = 0, l = line.length();
        char last = 0, c;
        String token;

        while ( p < l ) {

            c = line.charAt( p );

            if ( c == quote ) {
                if ( last == quote || last != escape )  // two consecutive quotes or not escaped
                    inQuotes = !inQuotes;
            }

            if ( !inQuotes ) {

                if ( c == colDelim ) {

                    token = line.substring( tokenStart, p ).trim();
                    addToken( tokens, token, quote );

                    tokenStart = p + 1;
                }
            }

            last = c;
            p++;
        }

        if ( p > tokenStart ) {

            token = line.substring( tokenStart, p ).trim();
            addToken( tokens, token, quote );
        } else if ( line.charAt( l - 1 ) == colDelim ) {

            addToken( tokens, "", quote );
        }

        lines.add( tokens );
    }


    private static void addToken( List<String> tokens, String token, char quote ) {

        token = token.trim();

        if ( token.length() > 2 && token.charAt( 0 ) == quote && token.charAt( token.length() - 1 ) == quote )
            token = token.substring( 1, token.length() - 1 );

        tokens.add( token );
    }


    private static String[] makeUnique( String[] headers ) {

        int c = 1;
        Set set = new TreeSet( String.CASE_INSENSITIVE_ORDER );
        String header, orig;

        for (int i=0; i<headers.length; i++) {

            orig = header = headers[ i ];

            while ( set.contains( header ) )
                header = orig + "_" + ++c;

            set.add( header );

            if ( header != orig )       // ref comparison for performance
                headers[ i ] = header;
        }

        return headers;
    }

}
