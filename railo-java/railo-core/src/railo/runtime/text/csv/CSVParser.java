
package railo.runtime.text.csv;

import java.util.ArrayList;

import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.transformer.util.CFMLString;


/**
 * Class to parse a CVS File 
 */
public final class CSVParser {
	
	/**
	 * parse a CVS File
	 * @param csv
	 * @param delimiter
	 * @param textQualifier
	 * @param headers
	 * @param firstrowasheaders
	 * @return parse CVS as Query
	 * @throws DatabaseException
	 * @throws CSVParserException
	 * @throws PageException
	 */
	public Query parse(String csv,char delimiter, char textQualifier,String[] headers, boolean firstrowasheaders) throws DatabaseException, CSVParserException, PageException {
		//print.ln("delimiter:"+delimiter);
		//print.ln("textQualifier:"+textQualifier);
		String[] first=null;
		
		CFMLString cfmlStr = new CFMLString(csv,"UTF-8");
		//print.ln(cfmlStr.toString());
	// no predefined Header
		if(headers==null) {
			// read first line
			first=readFirstLine(cfmlStr,delimiter,textQualifier);
			cfmlStr.removeSpace();
			
			// set first line to header
			if(firstrowasheaders) {
				headers=first;
				first=null;
			}
			// create auto header
			else {
				headers=new String[first.length];
				for(int i=0;i<first.length;i++) {
					headers[i]="COLUMN_"+(i+1);
				}
			}
		}
		// remove first line when header is defined and firstrowasheaders is true
		else if(!cfmlStr.isAfterLast() && firstrowasheaders){
			readFirstLine(cfmlStr,delimiter,textQualifier);
		}
		
		// create column Array
		Array[] arrays=new Array[headers.length];
		for(int i=0;i<arrays.length;i++) {
			arrays[i]=new ArrayImpl();
		}
		// fill first row to data array, when not empty
		if(first!=null) {
			for(int i=0;i<arrays.length;i++) {
				arrays[i].append(first[i]);
			}	
		}
		
				
		// read Body
        //int count=0;
		while(!cfmlStr.isAfterLast()) {
            readLine(cfmlStr,delimiter,textQualifier,arrays);
			cfmlStr.removeSpace();
		}
		
		cfmlStr.removeSpace();
		
		return new QueryImpl(headers,arrays,"query");
		
	}
	
	
	
	
	/**
	 * @param cfmlStr
	 * @param delimiter
	 * @param textQualifier
	 * @return read the first Line of the CVS
	 * @throws CSVParserException
	 */
	private String[] readFirstLine(CFMLString cfmlStr, char delimiter, char textQualifier) throws CSVParserException {
		ArrayList list=new ArrayList();
		
		do {
			String value=readValue(cfmlStr,delimiter,textQualifier);
			list.add(value);
			if(delimiter!=' ')cfmlStr.removeSpace();
			if(!cfmlStr.isAfterLast() && cfmlStr.getCurrent()==delimiter) {
                cfmlStr.next();
                if(cfmlStr.getCurrent()=='\n')break;
            }
			else break;
		}
		while(!cfmlStr.isAfterLast());
		
		return (String[])list.toArray(new String[list.size()]);
	}	

	/**
	 * read a Line of the CVS File
	 * @param cfmlStr
	 * @param delimiter
	 * @param textQualifier
	 * @param arrays
	 * @throws CSVParserException
	 * @throws PageException
	 */
	private void readLine(CFMLString cfmlStr, char delimiter, char textQualifier, Array[] arrays) throws CSVParserException, PageException {
        //print.ln("-----------------");
        //String[] arr=new String[len];
		int index=0;
		do {
			if(index>=arrays.length)throw new CSVParserException("invalid column count ("+index+"), only "+arrays.length+" columns are allowed");
            
            String value = readValue(cfmlStr,delimiter,textQualifier);
            //print.ln("["+value+"]"+delimiter+":"+textQualifier);
            arrays[index++].append(value);
			//arr[index++]=readValue(cfmlStr,delimiter,textQualifier);
			if(delimiter!=' ')cfmlStr.removeSpace();
			if(!cfmlStr.isAfterLast() && cfmlStr.getCurrent()==delimiter) {
                cfmlStr.next();
                if(cfmlStr.getCurrent()=='\n' || cfmlStr.getCurrent()=='\r')break;
                //if(cfmlStr.getCurrent()=='\n')break;
            }
			else break;
			
		}
		while(!cfmlStr.isAfterLast());
		
		if(arrays.length!=index)
			throw new CSVParserException("invalid column count, at least "+arrays.length+" columns must be defined");
	}




	/**
	 * Reads a Single value from a CSV File
	 * @param cfmlStr CFML String containig csv
	 * @param delimiter delimiter splits values
	 * @param textQualifier text qualifier of the value
	 * @return parsed value
	 * @throws CSVParserException
	 */
	private String readValue(CFMLString cfmlStr,char delimiter,char textQualifier) throws CSVParserException {
		StringBuffer sb=new StringBuffer();
		cfmlStr.removeSpace();
		// Quoted String
		if(cfmlStr.forwardIfCurrent(textQualifier)) {
			while(true) {
				if(cfmlStr.isAfterLast())
					throw new CSVParserException("invalid CSV File, missing end Qualifier ["+textQualifier+"]");
				
				if(cfmlStr.isCurrent(textQualifier)) {
					cfmlStr.next();
					if(cfmlStr.isCurrent(textQualifier)) {
						sb.append(textQualifier);
					}
					else {
						break;
					}
				}
				else sb.append(cfmlStr.getCurrent());
				cfmlStr.next();
			}
			return sb.toString();
		}
		// Pure String	
		while(!cfmlStr.isAfterLast()) {
			if(cfmlStr.isCurrent(delimiter) || cfmlStr.getCurrent()=='\n') {
				break;
			}
			sb.append(cfmlStr.getCurrent());
			cfmlStr.next();
		}
		return sb.toString().trim();

	}

/*
	public static void main(String[] args) throws Exception {
		Query query=new CSVParser().parse("aaa , \"abc\" , \"bcd\" ,aac\n11,22,33,4\n\"bcd\",1,2,3",',','"',null,false);
		print.ln(query.toString());
		
		//query=new CSVParser().parse("aaa , \"a\"\"b,c\" , \"bcd\" ,c\n11,22,33,4\n\"bcd\",1,2,3",',','"',new String[]{"aaa","bbb","ccc","ddd"});
		//print.ln(query.toString());
	}
	*/
}