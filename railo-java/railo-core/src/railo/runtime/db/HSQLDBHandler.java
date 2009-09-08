package railo.runtime.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import railo.print;
import railo.commons.db.DBUtil;
import railo.commons.lang.SerializableObject;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.ExceptionHandler;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.date.DateCaster;
import railo.runtime.sql.SQLParserException;
import railo.runtime.sql.SelectParser;
import railo.runtime.sql.Selects;
import railo.runtime.sql.old.ParseException;
import railo.runtime.timer.Stopwatch;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.QueryImpl;

/**
 * class to reexecute queries on the resultset object inside the cfml enviroment
 */
public final class HSQLDBHandler {

	private static final int STRING=0;
	private static final int INT=1;
	private static final int DOUBLE=2;
	private static final int DATE=3;
	private static final int TIME=4;
	private static final int TIMESTAMP=5;
	private static final int BINARY=6;
	
	
	private DatasourceConnection dc;
    private ArrayList usedTables=new ArrayList();
	Executer executer=new Executer();
	QoQ qoq=new QoQ();
	private static Object lock=new SerializableObject(); 

	/**
	 * constructor of the class
	 */
	public HSQLDBHandler() {

	}
	
	/**
	 * adds a table to the memory database
	 * @param pc
	 * @param name name of the new table
	 * @param query data source for table
	 * @throws SQLException 
	 * @throws PageException 
	 */
	private void addTable(PageContext pc,String name,Query query, boolean doSimpleTypes) throws SQLException, PageException {
      Statement stat;
		name=name.replace('.','_');
		usedTables.add(name);
			stat = dc.getConnection().createStatement();
			String[] keys=query.keysAsString();
			int[] types=query.getTypes();
			int[] innerTypes=toInnerTypes(types);
			
			// CREATE STATEMENT
				String comma="";
				StringBuffer create=new StringBuffer("CREATE TABLE "+name+" (");
				StringBuffer insert=new StringBuffer("INSERT INTO  "+name+" (");
				StringBuffer values=new StringBuffer("VALUES (");
				for(int i=0;i<keys.length;i++) {
					String key=keys[i];
					String type=(doSimpleTypes)?"VARCHAR_IGNORECASE":toUsableType(types[i]);
					
					
					create.append(comma+key);
					create.append(" ");
					create.append(type);
					insert.append(comma+key);			
					values.append(comma+"?");					
					comma=",";
				}
				create.append(")");
				insert.append(")");
				values.append(")");
                stat.execute(create.toString());
				PreparedStatement prepStat = dc.getConnection().prepareStatement(insert.toString()+values.toString());
				

			// INSERT STATEMENT
			//HashMap integerTypes=getIntegerTypes(types);
				
			int count=query.getRecordcount();
			QueryColumn[] columns=new QueryColumn[keys.length];
			for(int i=0;i<keys.length;i++) {
			    columns[i]=query.getColumn(keys[i]);
			}
			for(int y=0;y<count;y++) {
				for(int i=0;i<keys.length;i++) {
					int type=innerTypes[i];
					Object value=columns[i].get(y+1,null);
					
					//print.out("*** "+type+":"+Caster.toString(value));
					if(doSimpleTypes) {
						
						prepStat.setObject(i+1,Caster.toString(value));
					}
					else {
						if(value==null)
							prepStat.setNull(i+1,types[i]);
						else if(type==BINARY)
							prepStat.setBytes(i+1,Caster.toBinary(value));
						else if(type==DATE) {
							//print.out(new java.util.Date(new Date(DateCaster.toDateAdvanced(value,pc.getTimeZone()).getTime()).getTime()));

							prepStat.setTimestamp(i+1,(value==null || value.equals(""))?null:new Timestamp(DateCaster.toDateAdvanced(query.getAt(keys[i],y+1),pc.getTimeZone()).getTime()));
							//prepStat.setObject(i+1,Caster.toDate(value,null));
							//prepStat.setDate(i+1,(value==null || value.equals(""))?null:new Date(DateCaster.toDateAdvanced(value,pc.getTimeZone()).getTime()));
						}
						else if(type==TIME)
							prepStat.setTime(i+1,(value==null || value.equals(""))?null:new Time(DateCaster.toDateAdvanced(query.getAt(keys[i],y+1),pc.getTimeZone()).getTime()));
						else if(type==TIMESTAMP)
							prepStat.setTimestamp(i+1,(value==null || value.equals(""))?null:new Timestamp(DateCaster.toDateAdvanced(query.getAt(keys[i],y+1),pc.getTimeZone()).getTime()));
						else if(type==DOUBLE)
							prepStat.setDouble(i+1,(value==null || value.equals(""))?0:Caster.toDoubleValue(query.getAt(keys[i],y+1)));
						else if(type==INT)
							prepStat.setLong(i+1,(value==null || value.equals(""))?0:Caster.toIntValue(query.getAt(keys[i],y+1)));
						else if(type==STRING)
							prepStat.setObject(i+1,(value==null)?"":Caster.toString(value));
					}
					
				}
				prepStat.execute();			
			}

	}
	
	
	private int[] toInnerTypes(int[] types) {
		int[] innerTypes=new int[types.length];
		for(int i=0;i<types.length;i++) {
			int type=types[i];
			
			if(
					type==Types.BIGINT || 
					type==Types.BIT || 
					type==Types.INTEGER || 
					type==Types.SMALLINT || 
					type==Types.TINYINT)innerTypes[i]=INT;
			else if(
			        type==Types.DECIMAL || 
			        type==Types.DOUBLE || 
			        type==Types.NUMERIC || 
			        type==Types.REAL)innerTypes[i]=DOUBLE;
			else if(type==Types.DATE)innerTypes[i]=DATE;
			else if(type==Types.TIME)innerTypes[i]=TIME;
			else if(type==Types.TIMESTAMP)innerTypes[i]=TIMESTAMP;
			else if(
			        type==Types.BINARY || 
			        type==Types.LONGVARBINARY || 
			        type==Types.VARBINARY)innerTypes[i]=BINARY;
			else 
			    innerTypes[i]=STRING;
			
			
		}
		return innerTypes;
	}
	
	
	private String toUsableType(int type) {
	    if(type==Types.VARCHAR)return "VARCHAR_IGNORECASE";
	    if(type==Types.JAVA_OBJECT)return "VARCHAR_IGNORECASE";
	    //if(type==Types.DATE)return "DATETIME";
	    
	    
	    
		return QueryImpl.getColumTypeName(type);
		
	}
	
	
	/**
	 * remove a table from the memory database
	 * @param name
	 * @throws DatabaseException
	 */
	private void removeTable(String name) throws DatabaseException {
		name=name.replace('.','_');
		try {
			Statement stat = dc.getConnection().createStatement();
			stat.execute("DROP TABLE "+name);
		} catch (SQLException e) {
			throw new DatabaseException(e,dc);
		}
	} 
    
	/**
	 * remove all table inside the memory database
	 */
	private void removeAll() {
		int len=usedTables.size();
		for(int i=0;i<len;i++) {
			
			String tableName=usedTables.get(i).toString();
			//print.out("remove:"+tableName);
			try {
				removeTable(tableName);
			} catch (DatabaseException e) {
	               ExceptionHandler.printStackTrace(e);
	        }
		}
	}
	
    /**
     * executes a query on the queries inside the cld fusion enviroment
     * @param pc Page Context
     * @param sql
     * @param maxrows
     * @return result as Query
     * @throws PageException 
     * @throws PageException
     */
    public QueryImpl execute(PageContext pc, SQL sql, int maxrows, int fetchsize, int timeout) throws PageException {
        Stopwatch stopwatch=new Stopwatch();
		stopwatch.start();
		String prettySQL =null;
		Selects selects=null;
		
		// First Chance
				try {
					SelectParser parser=new SelectParser();
					selects = parser.parse(sql.getSQLString());
					
					QueryImpl q=qoq.execute(pc,sql,selects,maxrows);
					q.setExecutionTime(stopwatch.time());
					return q;
				} 
				catch (SQLParserException spe) {
					//sp
					//railo.print.out("sql parser crash at:");
					//railo.print.out("--------------------------------");
					//railo.print.out(sql.getSQLString().trim());
					//railo.print.out("--------------------------------");
					prettySQL = SQLPrettyfier.prettyfie(sql.getSQLString());
					try {
						QueryImpl query=executer.execute(pc,sql,prettySQL,maxrows);
						query.setExecutionTime(stopwatch.time());
						return query;
					} catch (PageException ex) {
						//railo.print.printST(ex);
						//railo.print.out("old executor/zql crash at:");
						//railo.print.out("--------------------------------");
						//railo.print.out(sql.getSQLString().trim());
						//railo.print.out("--------------------------------");
						
					}
					
				}
				catch (PageException e) {
					//throw e;
					//print.out("new executor crash at:");
					//print.out("--------------------------------");
					//print.out(sql.getSQLString().trim());
					//print.out("--------------------------------");
				}
				//if(true) throw new RuntimeException();
			
	// SECOND Chance with hsqldb
		try {
			boolean isUnion=false;
			Set tables=null;
			if(selects!=null) {
				HSQLUtil2 hsql2=new HSQLUtil2(selects);
				isUnion=hsql2.isUnion();
				tables=hsql2.getInvokedTables();
			}
			else {
				if(prettySQL==null)prettySQL = SQLPrettyfier.prettyfie(sql.getSQLString());
				HSQLUtil hsql=new HSQLUtil(prettySQL);
		    	tables=hsql.getInvokedTables();
				isUnion=hsql.isUnion();
			}
			try {
				return _execute(pc, sql, maxrows, fetchsize, timeout,stopwatch,tables,false);
			}
			catch(PageException pe) {
				if(isUnion)return _execute(pc, sql, maxrows, fetchsize, timeout,stopwatch,tables,true);

				//print.out("hsqldbhandler crash at:");
				//print.out("--------------------------------");
				//print.out(sql.getSQLString().trim());
				//print.out("--------------------------------");
				throw pe;
			}
		}
    	catch(ParseException e) {
    		throw  new DatabaseException(e.getMessage(),null,sql,null);
    	}
		
    }
    
    public QueryImpl _execute(PageContext pc, SQL sql, int maxrows, int fetchsize, int timeout,Stopwatch stopwatch, Set tables, boolean doSimpleTypes) throws PageException {

		synchronized(lock) {
		    	
			QueryImpl nqr=null;
			ConfigImpl config = (ConfigImpl)pc.getConfig();
			DatasourceConnectionPool pool = config.getDatasourceConnectionPool();
	    	dc=pool.getDatasourceConnection(config.getDataSource("_queryofquerydb"),"sa","");
	    	Connection conn = dc.getConnection();
	    	try {
	    		DBUtil.setAutoCommitEL(conn,false);
	    		
		    	
	    		
	    		
	        	//sql.setSQLString(HSQLUtil.sqlToZQL(sql.getSQLString(),false));
		        try {
	    			Iterator it = tables.iterator();
		    		//int len=tables.size();
	                while(it.hasNext()) {
		    			String tableName=it.next().toString();//tables.get(i).toString();
		    			String modTableName=tableName.replace('.','_');
	                    String modSql=StringUtil.replace(sql.getSQLString(),tableName,modTableName,false);
		    			sql.setSQLString(modSql);
		    			addTable(pc,tableName,Caster.toQuery(pc.getVariable(tableName)),doSimpleTypes);
		    		}
	                DBUtil.setReadOnlyEL(conn,true);
	                try {
	                	nqr =new QueryImpl(dc,sql,maxrows,fetchsize,timeout,"query");
	                }
	                finally {
						DBUtil.setReadOnlyEL(conn,false);
		                DBUtil.commitEL(conn);
		                DBUtil.setAutoCommitEL(conn,true);
	                }
		    		
				}  
	            catch (SQLException e) {
	                DatabaseException de = new DatabaseException("there is a problem to execute sql statement on query",null,sql,null);
	                de.setDetail(e.getMessage());
	                throw de;
	            }
	
	    	}
	    	finally {
                DBUtil.setAutoCommitEL(conn,true);
	    		removeAll();
	    		pool.releaseDatasourceConnection(dc);
	    		
	    		//manager.releaseConnection(dc);
	    	}
	        nqr.setExecutionTime(stopwatch.time());
			return nqr;
		}
    }
}