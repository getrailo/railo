/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.runtime.text.csv;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;


public class CSVParser {


    public static Query toQuery( String csv, char delimiter, char textQualifier, String[] headers, boolean firstRowIsHeaders ) throws CSVParserException, PageException {

        List<List<String>> allRows = ( new CSVString( csv, delimiter ).parse() );

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
