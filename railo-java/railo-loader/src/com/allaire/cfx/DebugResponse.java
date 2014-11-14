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
package com.allaire.cfx;

import java.util.Enumeration;
import java.util.Hashtable;

import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.cfx.QueryWrap;



/**
 * 
 */
public final class DebugResponse implements Response {
	
	private StringBuffer write=new StringBuffer();
	private StringBuffer writeDebug=new StringBuffer();
	private Hashtable variables=new Hashtable();
	private Hashtable queries=new Hashtable();
	
	
	/**
	 * @see com.allaire.cfx.Response#addQuery(java.lang.String, java.lang.String[])
	 */
	public Query addQuery(String name, String[] columns) {
		QueryWrap query = new QueryWrap(CFMLEngineFactory.getInstance().getCreationUtil().createQuery(columns,0,name),name.toLowerCase());
		queries.put(name.toLowerCase(),query);
		return query;
	}

    /**
	 * @see com.allaire.cfx.Response#setVariable(java.lang.String, java.lang.String)
	 */
	public void setVariable(String key, String value) {
		variables.put(key.toLowerCase(),value);
	}

	/**
	 * @see com.allaire.cfx.Response#write(java.lang.String)
	 */
	public void write(String str) {
		write.append(str);
	}

	/**
	 * @see com.allaire.cfx.Response#writeDebug(java.lang.String)
	 */
	public void writeDebug(String str) {
		writeDebug.append(str);
	}
	
	/**
	 *  print out the response
	 */
	public void printResults() {
		System.out.println("[ --- Railo Debug Response --- ]");
		System.out.println();
		
		System.out.println("----------------------------");
		System.out.println("|          Output          |");
		System.out.println("----------------------------");
		System.out.println(write);
		System.out.println();
		
		System.out.println("----------------------------");
		System.out.println("|       Debug Output       |");
		System.out.println("----------------------------");
		System.out.println(writeDebug);
		System.out.println();
		
		System.out.println("----------------------------");
		System.out.println("|        Variables         |");
		System.out.println("----------------------------");
		
		Enumeration e = variables.keys();
		while(e.hasMoreElements()) {
			Object key=e.nextElement();
			System.out.println("[Variable:"+key+"]");
			System.out.println(escapeString(variables.get(key).toString()));
		}
		System.out.println();
		
		e = queries.keys();
		while(e.hasMoreElements()) {
			Query query=(Query) queries.get(e.nextElement());
			printQuery(query);
			System.out.println();
		}
		
	}
	
	/**
	 * print out a query
	 * @param query
	 */
	public void printQuery(Query query) {
		if(query!=null) {
			String[] cols = query.getColumns();
			int rows = query.getRowCount();
			System.out.println("[Query:"+query.getName()+"]");
			for(int i=0;i<cols.length;i++) {
				if(i>0)System.out.print(", ");
				System.out.print(cols[i]);
			}
			System.out.println();
			
			for(int row=1;row<=rows;row++) {
				for(int col=1;col<=cols.length;col++) {
					if(col>1)System.out.print(", ");
					System.out.print(escapeString(query.getData(row,col)));
				}
				System.out.println();
			}
		}
	}
	
	private String escapeString(String string) {
		int len=string.length();
		StringBuffer sb=new StringBuffer(len);
		for(int i=0;i<len;i++) {
			char c=string.charAt(i);
			if(c=='\n')sb.append("\\n");
			else if(c=='\t')sb.append("\\t");
			else if(c=='\\')sb.append("\\\\");
			else if(c=='\b')sb.append("\\b");
			else if(c=='\r')sb.append("\\r");
			else if(c=='\"')sb.append("\\\"");
			else sb.append(c);
		}
		
		
		
		return "\""+sb.toString()+"\"";
	}

	
}