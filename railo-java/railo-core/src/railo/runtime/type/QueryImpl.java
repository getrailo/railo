package railo.runtime.type;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import railo.commons.db.DBUtil;
import railo.commons.io.IOUtil;
import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.commons.sql.SQLUtil;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.PageContext;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.db.CFTypes;
import railo.runtime.db.DataSourceUtil;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.DatasourceConnectionImpl;
import railo.runtime.db.DatasourceConnectionPro;
import railo.runtime.db.SQL;
import railo.runtime.db.SQLCaster;
import railo.runtime.db.SQLItem;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.interpreter.CFMLExpressionInterpreter;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.op.ThreadLocalDuplication;
import railo.runtime.op.date.DateCaster;
import railo.runtime.query.caster.Cast;
import railo.runtime.reflection.Reflector;
import railo.runtime.timer.Stopwatch;
import railo.runtime.type.comparator.NumberSortRegisterComparator;
import railo.runtime.type.comparator.SortRegister;
import railo.runtime.type.comparator.SortRegisterComparator;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.it.CollectionIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.sql.BlobImpl;
import railo.runtime.type.sql.ClobImpl;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.util.QueryUtil;

/**
 * implementation of the query interface
 */
/**
 * 
 */
public class QueryImpl implements QueryPro,Objects,Sizeable {

	private static final long serialVersionUID = 1035795427320192551L;


	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	public static final Collection.Key COLUMNS = KeyImpl.intern("COLUMNS");
	public static final Collection.Key SQL = KeyImpl.intern("SQL");
	public static final Collection.Key EXECUTION_TIME = KeyImpl.intern("executionTime");
	public static final Collection.Key RECORDCOUNT = KeyImpl.intern("RECORDCOUNT");
	public static final Collection.Key CACHED = KeyImpl.intern("cached");
	public static final Collection.Key COLUMNLIST = KeyImpl.intern("COLUMNLIST");
	public static final Collection.Key CURRENTROW = KeyImpl.intern("CURRENTROW");
	public static final Collection.Key IDENTITYCOL =  KeyImpl.intern("IDENTITYCOL");
	public static final Collection.Key GENERATED_KEYS = KeyImpl.intern("GENERATED_KEYS");
	
	
	
	//private static int count=0;
	private QueryColumnPro[] columns;
	private Collection.Key[] columnNames;
	private SQL sql;
	private ArrayInt arrCurrentRow=new ArrayInt();
	private int recordcount=0;
	private int columncount;
	private long exeTime=0;
    private boolean isCached=false;
    private String name;
	private int updateCount;
    private QueryImpl generatedKeys;
	private String template;
	
	
	/**
	 * @return return execution time to get query
	 */
	public int executionTime() {
		return (int) exeTime;
	}

	/**
	 * create a QueryImpl from a SQL Resultset
     * @param result SQL Resultset
	 * @param maxrow
	 * @param name 
	 * @throws PageException
     */
    public QueryImpl(ResultSet result, int maxrow, String name) throws PageException {
    	this.name=name;
        Stopwatch stopwatch=new Stopwatch();
		stopwatch.start();
		try {
            fillResult(null,result,maxrow,false,false);
        } catch (SQLException e) {
            throw new DatabaseException(e,null);
        } catch (IOException e) {
            throw Caster.toPageException(e);
        }
		exeTime=stopwatch.time();
    }
    
    /**
     * Constructor of the class
     * only for internal usage (cloning/deserialize)
     */
    public QueryImpl() {
    }
    
    
    public QueryImpl(ResultSet result, String name) throws PageException {
		this.name=name;
        
		try {	
		    fillResult(null,result,-1,true,false);
		} 
		catch (SQLException e) {
			throw new DatabaseException(e,null);
		} 
		catch (Exception e) {
		    throw Caster.toPageException(e);
		} 
	}
	
	/**
	 * constructor of the class, to generate a resultset from a sql query
	 * @param dc Connection to a database
	 * @param name 
	 * @param sql sql to execute
	 * @param maxrow maxrow for the resultset
	 * @throws PageException
	 */	
    public QueryImpl(DatasourceConnection dc,SQL sql,int maxrow, int fetchsize,int timeout, String name) throws PageException {
    	this(dc, sql, maxrow, fetchsize, timeout, name,null,false,true);
    }
    

    public QueryImpl(DatasourceConnection dc,SQL sql,int maxrow, int fetchsize,int timeout, String name,String template) throws PageException {
    	this(dc, sql, maxrow, fetchsize, timeout, name,template,false,true);
    }
    
	public QueryImpl(DatasourceConnection dc,SQL sql,int maxrow, int fetchsize,int timeout, String name,String template,boolean createUpdateData, boolean allowToCachePreperadeStatement) throws PageException {
		this.name=name;
		this.template=template;
        this.sql=sql;
		
        //ResultSet result=null;
		Statement stat=null;
		// check SQL Restrictions
		if(dc.getDatasource().hasSQLRestriction()) {
			QueryUtil.checkSQLRestriction(dc,sql);
        }
		// check if datasource support Generated Keys
		boolean createGeneratedKeys=createUpdateData;
        if(createUpdateData){
        	DatasourceConnectionImpl dci=(DatasourceConnectionImpl) dc;
        	if(!dci.supportsGetGeneratedKeys())createGeneratedKeys=false;
        }
		
		Stopwatch stopwatch=new Stopwatch();
		stopwatch.start();
		boolean hasResult=false;
		boolean closeStatement=true;
		try {	
			SQLItem[] items=sql.getItems();
			if(items.length==0) {
		    	stat=dc.getConnection().createStatement();
		        setAttributes(stat,maxrow,fetchsize,timeout);
		     // some driver do not support second argument
		        hasResult=createGeneratedKeys?stat.execute(sql.getSQLString(),Statement.RETURN_GENERATED_KEYS):stat.execute(sql.getSQLString());
	        }
	        else {
	        	// some driver do not support second argument
	        	PreparedStatement preStat = ((DatasourceConnectionPro)dc).getPreparedStatement(sql, createGeneratedKeys,allowToCachePreperadeStatement);
	        	closeStatement=false;
	        	stat=preStat;
	            setAttributes(preStat,maxrow,fetchsize,timeout);
	            setItems(preStat,items);
		        hasResult=preStat.execute();    
	        }
			int uc;
			ResultSet res;
			
			do {
				if(hasResult) {
					res=stat.getResultSet();
					if(fillResult(dc,res, maxrow, true,createGeneratedKeys))break;
				}
				else if((uc=setUpdateCount(stat))!=-1){
					if(uc>0 && createGeneratedKeys)setGeneratedKeys(dc, stat);
				}
				else break;
				try{
					hasResult=stat.getMoreResults(Statement.CLOSE_CURRENT_RESULT);
				}
				catch(Throwable t){
					break;
				}
			}
			while(true);
		} 
		catch (SQLException e) {
			throw new DatabaseException(e,sql,dc);
		} 
		catch (Throwable e) {
			throw Caster.toPageException(e);
		}
        finally {
        	if(closeStatement)DBUtil.closeEL(stat);
        }  
		exeTime=stopwatch.time();
	}
	
	private int setUpdateCount(Statement stat)  {
		try{
			int uc=stat.getUpdateCount();
			if(uc>-1){
				updateCount+=uc;
				return uc;
			}
		}
		catch(Throwable t){}
		return -1;
	}
	
	private boolean setGeneratedKeys(DatasourceConnection dc,Statement stat)  {
		try{
			ResultSet rs = stat.getGeneratedKeys();
			setGeneratedKeys(dc, rs);
			return true;
		}
		catch(Throwable t) {
			return false;
		}
	}
	
	private void setGeneratedKeys(DatasourceConnection dc,ResultSet rs) throws PageException  {
		generatedKeys=new QueryImpl(rs,"");
		if(DataSourceUtil.isMSSQL(dc)) generatedKeys.renameEL(GENERATED_KEYS,IDENTITYCOL);
	}
	
	/*private void setUpdateData(Statement stat, boolean createGeneratedKeys, boolean createUpdateCount)  {
		
		// update Count
		if(createUpdateCount){
			try{
				updateCount=stat.getUpdateCount();
			}
			catch(Throwable t){
				t.printStackTrace();
			}
		}
		// generated keys
		if(createGeneratedKeys){
			try{
				ResultSet rs = stat.getGeneratedKeys();
				generatedKeys=new QueryImpl(rs,"");
			}
			catch(Throwable t){
				t.printStackTrace();
			}
		}
	}*/


	private void setItems(PreparedStatement preStat, SQLItem[] items) throws DatabaseException, PageException, SQLException {
		
		for(int i=0;i<items.length;i++) {
            SQLCaster.setValue(preStat,i+1,items[i]);
        }
	}

	public int getUpdateCount() {
		return updateCount;
	}
	public Query getGeneratedKeys() {
		return generatedKeys;
	}

	private void setAttributes(Statement stat,int maxrow, int fetchsize,int timeout) throws SQLException {
		if(maxrow>-1) stat.setMaxRows(maxrow);
        if(fetchsize>0)stat.setFetchSize(fetchsize);
        if(timeout>0)stat.setQueryTimeout(timeout);
	}

	private ResultSet getResultSetEL(Statement stat) {
		try {
			return stat.getResultSet();
		}
		catch(SQLException sqle) {
			return null;
		}
	}

	

    

    private boolean fillResult(DatasourceConnection dc, ResultSet result, int maxrow, boolean closeResult,boolean createGeneratedKeys) throws SQLException, IOException, PageException {
    	if(result==null) return false;
    	recordcount=0;
		ResultSetMetaData meta = result.getMetaData();
		columncount=meta.getColumnCount();
		
	// set header arrays
		Collection.Key[] tmpColumnNames = new Collection.Key[columncount];
		int count=0;
		Collection.Key key;
		String columnName;
		for(int i=0;i<columncount;i++) {
			columnName=meta.getColumnName(i+1);
			if(StringUtil.isEmpty(columnName))columnName="column_"+i;
			key=KeyImpl.init(columnName);
			int index=getIndexFrom(tmpColumnNames,key,0,i);
			if(index==-1) {
				tmpColumnNames[i]=key;
				count++;
			}
		}
		

		columncount=count;
		columnNames=new Collection.Key[columncount];
		columns=new QueryColumnPro[columncount];
		Cast[] casts = new Cast[columncount];
		
	// get all used ints
		int[] usedColumns=new int[columncount];
		count=0;
		for(int i=0;i<tmpColumnNames.length;i++) {
			if(tmpColumnNames[i]!=null) {
				usedColumns[count++]=i;
			}
		}	
					
	// set used column names
		int[] types=new int[columns.length];
		for(int i=0;i<usedColumns.length;i++) {
            columnNames[i]=tmpColumnNames[usedColumns[i]];
            columns[i]=new QueryColumnImpl(this,columnNames[i],types[i]=meta.getColumnType(usedColumns[i]+1));
            
            if(types[i]==Types.TIMESTAMP)	casts[i]=Cast.TIMESTAMP;
            else if(types[i]==Types.TIME)	casts[i]=Cast.TIME;
            else if(types[i]==Types.DATE)	casts[i]=Cast.DATE;
            else if(types[i]==Types.CLOB)	casts[i]=Cast.CLOB;
            else if(types[i]==Types.BLOB)	casts[i]=Cast.BLOB;
            else if(types[i]==Types.BIT)	casts[i]=Cast.BIT;
            else if(types[i]==Types.ARRAY)	casts[i]=Cast.ARRAY;
            //else if(types[i]==Types.TINYINT)	casts[i]=Cast.ARRAY;
            
            else if(types[i]==CFTypes.OPAQUE){
            	if(SQLUtil.isOracle(result.getStatement().getConnection()))
            		casts[i]=Cast.ORACLE_OPAQUE;
            	else 
            		casts[i]=Cast.OTHER;
				
            }
            else casts[i]=Cast.OTHER;
		}
		
		if(createGeneratedKeys && columncount==1 && columnNames[0].equals(GENERATED_KEYS) && dc!=null && DataSourceUtil.isMSSQLDriver(dc)) {
			columncount=0;
			columnNames=null;
			columns=null;
			setGeneratedKeys(dc, result);
			return false;
		}
		

	// fill data
		//Object o;
		while(result.next()) {
			if(maxrow>-1 && recordcount>=maxrow) {
				break;
			}
			for(int i=0;i<usedColumns.length;i++) {
			    columns[i].add(casts[i].toCFType(types[i], result, usedColumns[i]+1));
			}
			++recordcount;
		}
		if(closeResult)result.close();
		return true;
	}

    private Object toBytes(Blob blob) throws IOException, SQLException {
		return IOUtil.toBytes((blob).getBinaryStream());
	}

	private static Object toString(Clob clob) throws IOException, SQLException {
		return IOUtil.toString(clob.getCharacterStream());
	}

    private static int getIndexFrom(Collection.Key[] tmpColumnNames, Collection.Key key, int from, int to) {
		for(int i=from;i<to;i++) {
			if(tmpColumnNames[i]!=null && tmpColumnNames[i].equalsIgnoreCase(key))return i;
		}
		return -1;
	}

	/**
	 * constructor of the class, to generate a empty resultset (no database execution)
	 * @param strColumns columns for the resultset
	 * @param rowNumber count of rows to generate (empty fields)
	 * @param name 
	 */
	public QueryImpl(String[] strColumns, int rowNumber,String name) {
        this.name=name;
        columncount=strColumns.length;
		recordcount=rowNumber;
		columnNames=new Collection.Key[columncount];
		columns=new QueryColumnPro[columncount];
		for(int i=0;i<strColumns.length;i++) {
			columnNames[i]=KeyImpl.init(strColumns[i].trim());
			columns[i]=new QueryColumnImpl(this,columnNames[i],Types.OTHER,recordcount);
		}
	}

	/**
	 * constructor of the class, to generate a empty resultset (no database execution)
	 * @param strColumns columns for the resultset
	 * @param rowNumber count of rows to generate (empty fields)
	 * @param name 
	 */
	public QueryImpl(Collection.Key[] columnKeys, int rowNumber,String name) {
		this.name=name;
        columncount=columnKeys.length;
		recordcount=rowNumber;
		columnNames=new Collection.Key[columncount];
		columns=new QueryColumnPro[columncount];
		for(int i=0;i<columnKeys.length;i++) {
			columnNames[i]=columnKeys[i];
			columns[i]=new QueryColumnImpl(this,columnNames[i],Types.OTHER,recordcount);
		}
	}
	
	/**
	 * constructor of the class, to generate a empty resultset (no database execution)
	 * @param strColumns columns for the resultset
	 * @param strTypes array of the types
	 * @param rowNumber count of rows to generate (empty fields)
	 * @param name 
	 * @throws DatabaseException 
	 */
	public QueryImpl(String[] strColumns, String[] strTypes, int rowNumber, String name) throws DatabaseException {
        this.name=name;
        columncount=strColumns.length;
		if(strTypes.length!=columncount) throw new DatabaseException("columns and types has not the same count",null,null,null);
		recordcount=rowNumber;
		columnNames=new Collection.Key[columncount];
		columns=new QueryColumnPro[columncount];
		for(int i=0;i<strColumns.length;i++) {
			columnNames[i]=KeyImpl.init(strColumns[i].trim());
			columns[i]=new QueryColumnImpl(this,columnNames[i],SQLCaster.toIntType(strTypes[i]),recordcount);
		}
	}
	
	/**
	 * constructor of the class, to generate a empty resultset (no database execution)
	 * @param strColumns columns for the resultset
	 * @param strTypes array of the types
	 * @param rowNumber count of rows to generate (empty fields)
	 * @param name 
	 * @throws DatabaseException 
	 */
	public QueryImpl(Collection.Key[] columnNames, String[] strTypes, int rowNumber, String name) throws DatabaseException {
        this.name=name;
        this.columnNames=columnNames;
        columncount=columnNames.length;
		if(strTypes.length!=columncount) throw new DatabaseException("columns and types has not the same count",null,null,null);
		recordcount=rowNumber;
		columns=new QueryColumnPro[columncount];
		for(int i=0;i<columnNames.length;i++) {
			columns[i]=new QueryColumnImpl(this,columnNames[i],SQLCaster.toIntType(strTypes[i]),recordcount);
		}
	}
	
	/**
	 * constructor of the class, to generate a empty resultset (no database execution)
	 * @param arrColumns columns for the resultset
	 * @param rowNumber count of rows to generate (empty fields)
	 * @param name 
	 */
	public QueryImpl(Array arrColumns, int rowNumber, String name) {
        this.name=name;
        columncount=arrColumns.size();
		recordcount=rowNumber;
		columnNames=new Collection.Key[columncount];
		columns=new QueryColumnPro[columncount];
		for(int i=0;i<columncount;i++) {
			columnNames[i]=KeyImpl.init(arrColumns.get(i+1,"").toString().trim());
			columns[i]=new QueryColumnImpl(this,columnNames[i],Types.OTHER,recordcount);
		}
	}
	
	/**
	 * constructor of the class, to generate a empty resultset (no database execution)
	 * @param arrColumns columns for the resultset
	 * @param arrTypes type of the columns
	 * @param rowNumber count of rows to generate (empty fields)
	 * @param name 
	 * @throws PageException
	 */
	public QueryImpl(Array arrColumns, Array arrTypes, int rowNumber, String name) throws PageException {
        this.name=name;
        columncount=arrColumns.size();
		if(arrTypes.size()!=columncount) throw new DatabaseException("columns and types has not the same count",null,null,null);
		recordcount=rowNumber;
		columnNames=new Collection.Key[columncount];
		columns=new QueryColumnPro[columncount];
		for(int i=0;i<columncount;i++) {
			columnNames[i]=KeyImpl.init(arrColumns.get(i+1,"").toString().trim());
			columns[i]=new QueryColumnImpl(this,columnNames[i],SQLCaster.toIntType(Caster.toString(arrTypes.get(i+1,""))),recordcount);
		}
	}

	/**
	 * constructor of the class
	 * @param columnNames columns definition as String Array
	 * @param arrColumns values
	 * @param name 
	 * @throws DatabaseException
	 */

	public QueryImpl(String[] strColumnNames, Array[] arrColumns, String name) throws DatabaseException {
		this(_toKeys(strColumnNames),arrColumns,name);		
		
	}
	

	private static Collection.Key[] _toKeys(String[] strColumnNames) {
		Collection.Key[] columnNames=new Collection.Key[strColumnNames.length];
		for(int i=0	;i<columnNames.length;i++) {
			columnNames[i]=KeyImpl.init(strColumnNames[i].trim());
		}
		return columnNames;
	}
	private static String[] _toStringKeys(Collection.Key[] columnNames) {
		String[] strColumnNames=new String[columnNames.length];
		for(int i=0	;i<strColumnNames.length;i++) {
			strColumnNames[i]=columnNames[i].getString();
		}
		return strColumnNames;
	}
	
	/*public QueryImpl(Collection.Key[] columnNames, QueryColumn[] columns, String name,long exeTime, boolean isCached,SQL sql) throws DatabaseException {
		this.columnNames=columnNames;
		this.columns=columns;
		this.exeTime=exeTime;
		this.isCached=isCached;
		this.name=name;
		this.columncount=columnNames.length;
		this.recordcount=columns.length==0?0:columns[0].size();
		this.sql=sql;
		
	}*/

	public QueryImpl(Collection.Key[] columnNames, Array[] arrColumns, String name) throws DatabaseException {
        this.name=name;
        
        if(columnNames.length!=arrColumns.length)
			throw new DatabaseException("invalid parameter for query, not the same count from names and columns","names:"+columnNames.length+";columns:"+arrColumns.length,null,null,null);
		int len=0;
		columns=new QueryColumnPro[arrColumns.length];
		if(arrColumns.length>0) {
		// test columns
			len=arrColumns[0].size();
			for(int i=0;i<arrColumns.length;i++) {
				if(arrColumns[i].size()!=len)
					throw new DatabaseException("invalid parameter for query, all columns must have the same size","column[1]:"+len+"<>column["+(i+1)+"]:"+arrColumns[i].size(),null,null,null);
				//columns[i]=new QueryColumnTypeFlex(arrColumns[i]);
				columns[i]=new QueryColumnImpl(this,columnNames[i],arrColumns[i],Types.OTHER);
			}
		// test keys
			Map testMap=new HashMap();
			for(int i=0	;i<columnNames.length;i++) {
				
				if(!Decision.isSimpleVariableName(columnNames[i]))
					throw new DatabaseException("invalid column name ["+columnNames[i]+"] for query", "column names must start with a letter and can be followed by letters numbers and underscores [_]. RegExp:[a-zA-Z][a-zA-Z0-9_]*",null,null,null);
				if(testMap.containsKey(columnNames[i].getLowerString()))
					throw new DatabaseException("invalid parameter for query, ambiguous column name "+columnNames[i],"columnNames: "+List.arrayToListTrim( _toStringKeys(columnNames),","),null,null,null);
				testMap.put(columnNames[i].getLowerString(),"set");
			}
		}
		
		columncount=columns.length;
		recordcount=len;
		this.columnNames=columnNames;
	}
	
    /**
     * constructor of the class
     * @param columnList
     * @param data
     * @param name 
     */
    public QueryImpl(String[] strColumnList, Object[][] data,String name) {
    	
        this(toCollKeyArr(strColumnList),data.length,name);
        
        for(int iRow=0;iRow<data.length;iRow++) {
            Object[] row=data[iRow];
            for(int iCol=0;iCol<row.length;iCol++) {
                //print.ln(columnList[iCol]+":"+iRow+"="+row[iCol]);
                setAtEL(columnNames[iCol],iRow+1,row[iCol]);
            }
        }
    }

    private static Collection.Key[] toCollKeyArr(String[] strColumnList) {
    	Collection.Key[] columnList=new Collection.Key[strColumnList.length];
		for(int i=0	;i<columnList.length;i++) {
			columnList[i]=KeyImpl.init(strColumnList[i].trim());
		}
		return columnList;
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return columncount;
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Collection.Key[] keys() {
		return columnNames;
	}

	/**
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		return QueryUtil.toStringArray(columnNames);
	}

	

	/**
	 * @see railo.runtime.type.Collection#removeEL(java.lang.String)
	 */
	public synchronized Object removeEL(String key) {
		return setEL(key,null);
        /*int index=getIndexFromKey(key);
		if(index!=-1) _removeEL(index);
		return null;*/
	}

	/**
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Collection.Key key) {
		return setEL(key,null);
	}

	/**
	 * @see railo.runtime.type.Collection#remove(java.lang.String)
	 */
	public synchronized Object remove(String key) throws PageException {
		return set(key,null);
	}

	
	
	/**
	 * @throws PageException 
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key key) throws PageException {
		return set(key,null);
	}

	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		for(int i=0;i<columns.length;i++) {
            columns[i].clear();
        }
        recordcount=0;
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(java.lang.String, java.lang.Object)
	 */
	public Object get(String key, Object defaultValue) {
		return getAt(key,
				arrCurrentRow.get(getPid(), 1),
				defaultValue);
	}

	//private static int pidc=0;
	private int getPid() {
		
		PageContext pc = ThreadLocalPageContext.get();
		if(pc==null) {
			pc=CFMLEngineFactory.getInstance().getThreadPageContext();
			if(pc==null)throw new RuntimeException("cannot get pid for current thread");
		}
		return pc.getId();
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
		return getAt(key,
				arrCurrentRow.get(getPid(), 1),
				defaultValue);
	}

	/**
	 * @see railo.runtime.type.Collection#get(java.lang.String)
	 */
	public Object get(String key) throws PageException {
		return getAt(key,
				arrCurrentRow.get(getPid(), 1)
				);
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Collection.Key key) throws PageException {
		return getAt(key,
				arrCurrentRow.get(getPid(), 1)
				);
	}

	/**
	 *
	 * @see railo.runtime.type.Query#getAt(java.lang.String, int, java.lang.Object)
	 */
	public Object getAt(String key, int row, Object defaultValue) {
		int index=getIndexFromKey(key);
		if(index!=-1) {
			return columns[index].get(row,defaultValue);
		}
		if(key.length()>0) {
	        char c=key.charAt(0);
	        if(c=='r' || c=='R') {
	            if(key.equalsIgnoreCase("recordcount")) return new Double(getRecordcount());
	        }
	        if(c=='c' || c=='C') {
	            if(key.equalsIgnoreCase("currentrow")) return new Double(row);
	            else if(key.equalsIgnoreCase("columnlist")) return getColumnlist(true);
	        }
		}
        return null;
	}

	public Object getAt(Collection.Key key, int row, Object defaultValue) {
		int index=getIndexFromKey(key);
		if(index!=-1) {
			return columns[index].get(row,defaultValue);
		}
		if(key.getString().length()>0) {
	        char c=key.lowerCharAt(0);
	        if(c=='r') {
	            if(key.equals(RECORDCOUNT)) return new Double(getRecordcount());
	        }
	        else if(c=='c') {
	            if(key.equals(CURRENTROW)) return new Double(row);
	            else if(key.equals(COLUMNLIST)) return getColumnlist(true);
	        }
		}
        return null;
	}
	
	/**
	 * @see railo.runtime.type.Query#getAt(java.lang.String, int)
	 */
	public Object getAt(String key, int row) throws PageException {
		//print.err("str:"+key);
        int index=getIndexFromKey(key);
		if(index!=-1) {
			return columns[index].get(row);
		}
		if(key.length()>0){
	        char c=key.charAt(0);
	        if(c=='r' || c=='R') {
	            if(key.equalsIgnoreCase("recordcount")) return new Double(getRecordcount());
			}
	        else if(c=='c' || c=='C') {
			    if(key.equalsIgnoreCase("currentrow")) return new Double(row);
			    else if(key.equalsIgnoreCase("columnlist")) return getColumnlist(true);
			}
		}
		throw new DatabaseException("column ["+key+"] not found in query, columns are ["+getColumnlist(false)+"]",null,sql,null);
	}

	/**
	 * @see railo.runtime.type.Query#getAt(railo.runtime.type.Collection.Key, int)
	 */
	public Object getAt(Collection.Key key, int row) throws PageException {
		int index=getIndexFromKey(key);
		if(index!=-1) {
			return columns[index].get(row);
		}
        if(key.getString().length()>0) {
        	char c=key.lowerCharAt(0);
	        if(c=='r') {
	            if(key.equals(RECORDCOUNT)) return new Double(getRecordcount());
			}
	        else if(c=='c') {
			    if(key.equals(CURRENTROW)) return new Double(row);
			    else if(key.equals(COLUMNLIST)) return getColumnlist(true);
			}
        }
		throw new DatabaseException("column ["+key+"] not found in query, columns are ["+getColumnlist(false)+"]",null,sql,null);
	}

    /**
     * @see railo.runtime.type.Query#removeRow(int)
     */
    public synchronized int removeRow(int row) throws PageException {
        //disconnectCache();
        
        for(int i=0;i<columns.length;i++) {
            columns[i].removeRow(row);
        }
        return --recordcount;
    }

    /**
     * @see railo.runtime.type.Query#removeRowEL(int)
     */
    public int removeRowEL(int row) {
        //disconnectCache();
        
        try {
            return removeRow(row);
        } catch (PageException e) {
            return recordcount;
        }
    }
    
    /**
     * @see railo.runtime.type.Query#removeColumn(java.lang.String)
     */
    public QueryColumn removeColumn(String key) throws DatabaseException {
        //disconnectCache();
        
        QueryColumn removed = removeColumnEL(key);
        if(removed==null) {
            if(key.equalsIgnoreCase("recordcount") || key.equalsIgnoreCase("currentrow") || key.equalsIgnoreCase("columnlist"))
                throw new DatabaseException("can't remove "+key+" this is not a row","existing rows are ["+getColumnlist(false)+"]",null,null,null);
            throw new DatabaseException("can't remove row ["+key+"], this row doesn't exist",
                    "existing rows are ["+getColumnlist(false)+"]",null,null,null);
        }
        return removed;
    }

	/**
	 * @see railo.runtime.type.Query#removeColumn(railo.runtime.type.Collection.Key)
	 */
	public QueryColumn removeColumn(Collection.Key key) throws DatabaseException {
		//disconnectCache();
        
        QueryColumn removed = removeColumnEL(key);
        if(removed==null) {
            if(key.equals(RECORDCOUNT) || 
            		key.equals(CURRENTROW) || 
            		key.equals(COLUMNLIST))
                throw new DatabaseException("can't remove "+key+" this is not a row","existing rows are ["+getColumnlist(false)+"]",null,null,null);
            throw new DatabaseException("can't remove row ["+key+"], this row doesn't exist",
                    "existing rows are ["+getColumnlist(false)+"]",null,null,null);
        }
        return removed;
	}

    /**
     * @see railo.runtime.type.Query#removeColumnEL(java.lang.String)
     */
    public synchronized QueryColumn removeColumnEL(String key) {
        //disconnectCache();
        
        int index=getIndexFromKey(key);
        if(index!=-1) {
            int current=0;
            QueryColumn removed=null;
            Collection.Key[] newColumnNames=new Collection.Key[columnNames.length-1];
            QueryColumnPro[] newColumns=new QueryColumnPro[columns.length-1];
            for(int i=0;i<columns.length;i++) {
                if(i==index) {
                    removed=columns[i];
                }
                else {
                    newColumnNames[current]=columnNames[i];
                    newColumns[current++]=columns[i];
                }
            }
            columnNames=newColumnNames;
            columns=newColumns;
            columncount--;
            return removed;
        }
        return null;
    }

	public QueryColumn removeColumnEL(Collection.Key key) {
		//disconnectCache();
        
        int index=getIndexFromKey(key);
        if(index!=-1) {
            int current=0;
            QueryColumn removed=null;
            Collection.Key[] newColumnNames=new Collection.Key[columnNames.length-1];
            QueryColumnPro[] newColumns=new QueryColumnPro[columns.length-1];
            for(int i=0;i<columns.length;i++) {
                if(i==index) {
                    removed=columns[i];
                }
                else {
                    newColumnNames[current]=columnNames[i];
                    newColumns[current++]=columns[i];
                }
            }
            columnNames=newColumnNames;
            columns=newColumns;
            columncount--;
            return removed;
        }
        return null;
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(java.lang.String, java.lang.Object)
	 */
	public Object setEL(String key, Object value) {	
		return setAtEL(key,
				arrCurrentRow.get(getPid(), 1),
				value);	
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Collection.Key key, Object value) {
		return setAtEL(key,
				arrCurrentRow.get(getPid(), 1),
				value);
	}
	
	/**
	 * @see railo.runtime.type.Collection#set(java.lang.String, java.lang.Object)
	 */
	public Object set(String key, Object value) throws PageException {
		return setAt(key,
				arrCurrentRow.get(getPid(), 1),
				value);
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws PageException {
		return setAt(key,
				arrCurrentRow.get(getPid(), 1),
				value);
	}
    
    /**
     * @see railo.runtime.type.Query#setAt(java.lang.String, int, java.lang.Object)
     */
    public Object setAt(String key,int row, Object value) throws PageException {
        //disconnectCache();
        
        int index=getIndexFromKey(key);
        if(index!=-1) {
            return columns[index].set(row,value);
        }
        throw new DatabaseException("column ["+key+"] does not exist","columns are ["+getColumnlist(false)+"]",null,sql,null);
    }

    public Object setAt(Collection.Key key, int row, Object value) throws PageException {
		//disconnectCache();
        
        int index=getIndexFromKey(key);
        if(index!=-1) {
            return columns[index].set(row,value);
        }
        throw new DatabaseException("column ["+key+"] does not exist","columns are ["+getColumnlist(false)+"]",null,sql,null);
	}
    
    /**
     * @see railo.runtime.type.Query#setAtEL(java.lang.String, int, java.lang.Object)
     */
    public Object setAtEL(String key,int row, Object value) {
        //disconnectCache();
        
        int index=getIndexFromKey(key);
        if(index!=-1) {
            return columns[index].setEL(row,value);
        }
        return null;
    }

	public Object setAtEL(Collection.Key key, int row, Object value) {
		//disconnectCache();
        
        int index=getIndexFromKey(key);
        if(index!=-1) {
            return columns[index].setEL(row,value);
        }
        return null;
	}

	/**
	 * @see railo.runtime.type.Iterator#next()
	 */
	public boolean next() {
		return next(getPid());
	}

	/**
	 * @see railo.runtime.type.Iterator#next(int)
	 */
	public boolean next(int pid) {
		if(recordcount>=(arrCurrentRow.set(pid,arrCurrentRow.get(pid,0)+1))) {
			return true;
		}
		arrCurrentRow.set(pid,0);
		return false;
	}

	/**
	 * @see railo.runtime.type.Iterator#reset()
	 */
	public void reset() {
		reset(getPid());
	}
	
	public void reset(int pid) {
		arrCurrentRow.set(pid,0);
	}

	/**
	 * @see railo.runtime.type.Iterator#getRecordcount()
	 */
	public int getRecordcount() {
		return recordcount;
	}

	/**
	 * @see railo.runtime.type.Iterator#getCurrentrow()
	 * FUTURE set this to deprectaed
	 */
	public int getCurrentrow() {
		return getCurrentrow(getPid());
	}
	
	/**
	 * @see railo.runtime.type.QueryPro#getCurrentrow(int)
	 */
	public int getCurrentrow(int pid) {
		return arrCurrentRow.get(pid,1);
	}


	/**
	 * return a string list of all columns
	 * @return string list
	 */
	public String getColumnlist(boolean upperCase) {
		
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<columnNames.length;i++) {
			if(i>0)sb.append(',');
			sb.append(upperCase?columnNames[i].getString().toUpperCase():columnNames[i].getString());// FUTURE getUpperString
		}
		return sb.toString();
	}
	public String getColumnlist() {
		return getColumnlist(true);
	}

	/**
	 * @deprecated use instead go(int,int)
	 * @see railo.runtime.type.Iterator#go(int)
	 */
	public boolean go(int index) {
		return go(index,getPid());
	}
	
	public boolean go(int index, int pid) {
		if(index>0 && index<=recordcount) {
			arrCurrentRow.set(pid, index);
			return true;
		}
		arrCurrentRow.set(pid, 0);
		return false;
	}

	/**
	 * @see railo.runtime.type.Iterator#isEmpty()
	 */
	public boolean isEmpty() {
		return recordcount+columncount==0;
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return QueryUtil.toDumpData(this, pageContext, maxlevel, dp);
	}
	
	/**
	 * sorts a query by a column
	 * @param column colun to sort
	 * @throws PageException
	 */
	public void sort(String column) throws PageException {
		sort(column,Query.ORDER_ASC);
	}

	/**
	 * @see railo.runtime.type.Query#sort(railo.runtime.type.Collection.Key)
	 */
	public void sort(Collection.Key column) throws PageException {
		sort(column,Query.ORDER_ASC);
	}

	/**
	 * sorts a query by a column 
	 * @param strColumn column to sort
	 * @param order sort type (Query.ORDER_ASC or Query.ORDER_DESC)
	 * @throws PageException
	 */
	public synchronized void sort(String strColumn, int order) throws PageException {
		//disconnectCache();
        sort(getColumn(strColumn),order);
	}
	
	/**
	 * @see railo.runtime.type.Query#sort(railo.runtime.type.Collection.Key, int)
	 */
	public synchronized void sort(Collection.Key keyColumn, int order) throws PageException {
		//disconnectCache();
        sort(getColumn(keyColumn),order);
	}
	
	private void sort(QueryColumn column, int order) throws PageException {
        int type = column.getType();
		
		SortRegister[] arr= ArrayUtil.toSortRegisterArray(column);
		
		Arrays.sort(arr,
		(
				type==Types.BIGINT || 
				type==Types.BIT || 
				type==Types.INTEGER || 
				type==Types.SMALLINT || 
				type==Types.TINYINT	||
		        type==Types.DECIMAL || 
		        type==Types.DOUBLE || 
		        type==Types.NUMERIC || 
		        type==Types.REAL)?
		
		(Comparator)new NumberSortRegisterComparator(order==ORDER_ASC):(Comparator)new SortRegisterComparator(order==ORDER_ASC,true)
		);
		
		for(int i=0;i<columns.length;i++) {
			column=columns[i];
			int len=column.size();
			QueryColumnPro newCol=new QueryColumnImpl(this,columnNames[i],columns[i].getType(),len);
			for(int y=1;y<=len;y++) {
				newCol.set(y,column.get(arr[y-1].getOldPosition()+1));
			}
			columns[i]=newCol;
		}
	}

	/**
	 * @see railo.runtime.type.Query#addRow(int)
	 */
	 public synchronized boolean addRow(int count) {		
         //disconnectCache();
            
         for(int i=0;i<columns.length;i++) {
        	 QueryColumnPro column = columns[i];
		 	column.addRow(count);
		 }
		 recordcount+=count;
		 return true;
	 }
	 
	 /**
	 * @see railo.runtime.type.Query#addColumn(java.lang.String, railo.runtime.type.Array)
	 */
	public boolean addColumn(String columnName, Array content) throws DatabaseException {
		return addColumn(columnName,content,Types.OTHER);
	 }

	public boolean addColumn(Collection.Key columnName, Array content) throws PageException {
		return addColumn(columnName,content,Types.OTHER);
	}
	
    /**
     * @throws DatabaseException
     * @see railo.runtime.type.Query#addColumn(java.lang.String, railo.runtime.type.Array, int)
     */
    public synchronized boolean addColumn(String columnName, Array content, int type) throws DatabaseException {
		return addColumn(KeyImpl.init(columnName.trim()), content, type);
    }

	/**
	 * @see railo.runtime.type.Query#addColumn(railo.runtime.type.Collection.Key, railo.runtime.type.Array, int)
	 */
	public boolean addColumn(Collection.Key columnName, Array content, int type) throws DatabaseException {
		//disconnectCache();
        // TODO Meta type
		content=(Array) content.duplicate(false);
		
	 	if(getIndexFromKey(columnName)!=-1)
	 		throw new DatabaseException("column name ["+columnName.getString()+"] already exist",null,sql,null);
	 	if(content.size()!=getRecordcount()) {
	 		//throw new DatabaseException("array for the new column has not the same size like the query (arrayLen!=query.recordcount)");
	 		if(content.size()>getRecordcount()) addRow(content.size()-getRecordcount());
	 		else content.setEL(getRecordcount(),"");
	 	}
	 	QueryColumnPro[] newColumns=new QueryColumnPro[columns.length+1];
	 	Collection.Key[] newColumnNames=new Collection.Key[columns.length+1];
	 	boolean logUsage=false;
	 	for(int i=0;i<columns.length;i++) {
	 		newColumns[i]=columns[i];
	 		newColumnNames[i]=columnNames[i];
	 		if(!logUsage && columns[i] instanceof DebugQueryColumn) logUsage=true;
	 	}
	 	newColumns[columns.length]=new QueryColumnImpl(this,columnName,content,type);
	 	newColumnNames[columns.length]=columnName;
	 	columns=newColumns;
	 	columnNames=newColumnNames;
	 	
	 	columncount++;
	 	
	 	if(logUsage)enableShowQueryUsage();
	 	
		return true;
	}
	

	/* *
	 * if this query is still connected with cache (same query also in cache)
     * it will disconnetd from cache (clone object and add clone to cache)
	 */
	//protected void disconnectCache() {}

	
	/**
	 * @see railo.runtime.type.Query#clone()
	 */
    public Object clone() {
        return cloneQuery(true);
    }
    
    /**
     * @see railo.runtime.type.Collection#duplicate(boolean)
     */
    public Collection duplicate(boolean deepCopy) {
        return cloneQuery(true);
    }
    

	
    
    /**
     * @return clones the query object
     */
    public QueryImpl cloneQuery(boolean deepCopy) {
        QueryImpl newResult=new QueryImpl();
        ThreadLocalDuplication.set(this, newResult);
        try{
	        if(columnNames!=null){
		        newResult.columnNames=new Collection.Key[columnNames.length];
		        newResult.columns=new QueryColumnPro[columnNames.length];
		        for(int i=0;i<columnNames.length;i++) {
		        	newResult.columnNames[i]=columnNames[i];
		        	newResult.columns[i]=columns[i].cloneColumn(newResult,deepCopy);
		        }
	        }
	        newResult.sql=sql;
	        newResult.template=template;
	        newResult.recordcount=recordcount;
	        newResult.columncount=columncount;
	        newResult.isCached=isCached;
	        newResult.name=name;
	        newResult.exeTime=exeTime;
	        newResult.updateCount=updateCount;
	        if(generatedKeys!=null)newResult.generatedKeys=generatedKeys.cloneQuery(false);
	        return newResult;
        }
        finally {
        	ThreadLocalDuplication.remove(this);
        }
    }

	/**
	 * @see railo.runtime.type.Query#getTypes()
	 */
	public synchronized int[] getTypes() {
		int[] types=new int[columns.length];
		for(int i=0;i<columns.length;i++) {
		    types[i]=columns[i].getType();
		}
		return types;
	}
	
	/**
	 * @see railo.runtime.type.Query#getTypesAsMap()
	 */
	public synchronized Map getTypesAsMap() {
		
		Map map=new HashMap();
		for(int i=0;i<columns.length;i++) {
			map.put(columnNames[i],columns[i].getTypeAsString());
		}
		return map;
	}

	/**
	 * @see railo.runtime.type.Query#getColumn(java.lang.String)
	 */
	public QueryColumn getColumn(String key) throws DatabaseException {
		return getColumn(KeyImpl.init(key.trim()));
	}

	/**
	 * @see railo.runtime.type.Query#getColumn(railo.runtime.type.Collection.Key)
	 */
	public QueryColumn getColumn(Collection.Key key) throws DatabaseException {
		int index=getIndexFromKey(key);
		if(index!=-1) return columns[index];
        
		if(key.getString().length()>0) {
        	char c=key.lowerCharAt(0);
	        if(c=='r') {
	            if(key.equals(RECORDCOUNT)) return new QueryColumnRef(this,key,Types.INTEGER);
	        }
	        if(c=='c') {
	            if(key.equals(CURRENTROW)) return new QueryColumnRef(this,key,Types.INTEGER);
	            else if(key.equals(COLUMNLIST)) return new QueryColumnRef(this,key,Types.INTEGER);
	        }
		}
        throw new DatabaseException("key ["+key.getString()+"] not found in query, columns are ["+getColumnlist(false)+"]",null,sql,null);
	}
	

	private void renameEL(Collection.Key src, Collection.Key trg) {
		int index=getIndexFromKey(src);
		if(index!=-1){
			columnNames[index]=trg;
			columns[index].setKey(trg);
		}
	}
	
	public synchronized void rename(Collection.Key columnName,Collection.Key newColumnName) throws ExpressionException {
		int index=getIndexFromKey(columnName);
		if(index==-1) throw new ExpressionException("invalid column name definitions");
		columnNames[index]=newColumnName;
		columns[index].setKey(newColumnName);
	}
	

	/**
	 *
	 * @see railo.runtime.type.Query#getColumn(java.lang.String, railo.runtime.type.QueryColumn)
	 */
	public QueryColumn getColumn(String key, QueryColumn defaultValue) {
		return getColumn(KeyImpl.init(key.trim()),defaultValue);
	}

	/**
	 * @see railo.runtime.type.Query#getColumn(railo.runtime.type.Collection.Key, railo.runtime.type.QueryColumn)
	 */
	public QueryColumn getColumn(Collection.Key key, QueryColumn defaultValue) {
        int index=getIndexFromKey(key);
		if(index!=-1) return columns[index];
        if(key.getString().length()>0) {//FUTURE add length method to Key Interface
        	char c=key.lowerCharAt(0);
	        if(c=='r') {
	            if(key.equals(RECORDCOUNT)) return new QueryColumnRef(this,key,Types.INTEGER);
	        }
	        if(c=='c') {
	            if(key.equals(CURRENTROW)) return new QueryColumnRef(this,key,Types.INTEGER);
	            else if(key.equals(COLUMNLIST)) return new QueryColumnRef(this,key,Types.INTEGER);
	        }
        }
        return defaultValue;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String[] keys=keysAsString();
		
		StringBuffer sb=new StringBuffer();

		sb.append("Query\n");
		sb.append("---------------------------------------------------\n");
		
		if(sql!=null) {
			sb.append(sql+"\n");
			sb.append("---------------------------------------------------\n");
		}

		if(exeTime>0)	{
			sb.append("Execution Time: "+exeTime+"\n");
			sb.append("---------------------------------------------------\n");
		}
		
		sb.append("Recordcount: "+getRecordcount()+"\n");
		sb.append("---------------------------------------------------\n");
		String trenner="";
		for(int i=0;i<keys.length;i++) {
			trenner+="+---------------------";
		}
		trenner+="+\n";
		sb.append(trenner);
	
	// Header
		for(int i=0;i<keys.length;i++) {
			sb.append(getToStringField(keys[i]));
		}
		sb.append("|\n");
		sb.append(trenner);
		sb.append(trenner);
		
	// body
		for(int i=0;i<recordcount;i++) {
			for(int y=0;y<keys.length;y++) {
				try {
					Object o=getAt(keys[y],i+1);
					if(o instanceof String)sb.append(getToStringField(o.toString()));
					else if(o instanceof Number) sb.append(getToStringField(Caster.toString(((Number)o).doubleValue())));
					else if(o instanceof Clob) sb.append(getToStringField(Caster.toString(o)));							
					else sb.append(getToStringField(o.toString()));
				} catch (PageException e) {
					sb.append(getToStringField("[empty]"));
				}
			}
			sb.append("|\n");
			sb.append(trenner);
		}
		return sb.toString();
	}

	private String getToStringField(String str) {
		if(str==null) return "|                    ";
		else if(str.length()<21) {
			String s="|"+str;
			for(int i=str.length();i<21;i++)s+=" ";
			return s;
		}
		else if(str.length()==21) return "|"+str;
		else  return "|"+str.substring(0,18)+"...";
	}
	
	/**
	 * 
	 * @param type
	 * @return return String represetation of a Type from int type
	 */
	public static String getColumTypeName(int type) {
		switch(type) {
			case Types.ARRAY: return "OBJECT";
			case Types.BIGINT: return "BIGINT";
			case Types.BINARY: return "BINARY";
			case Types.BIT: return "BIT";
			case Types.BLOB: return "OBJECT";
			case Types.BOOLEAN: return "BOOLEAN";
			case Types.CHAR: return "CHAR";
			case Types.NCHAR: return "NCHAR";
			case Types.CLOB: return "OBJECT";
			case Types.NCLOB: return "OBJECT";
			case Types.DATALINK: return "OBJECT";
			case Types.DATE: return "DATE";
			case Types.DECIMAL: return "DECIMAL";
			case Types.DISTINCT: return "OBJECT";
			case Types.DOUBLE: return "DOUBLE";
			case Types.FLOAT: return "DOUBLE";
			case Types.INTEGER: return "INTEGER";
			case Types.JAVA_OBJECT: return "OBJECT";
			case Types.LONGVARBINARY: return "LONGVARBINARY";
			case Types.LONGVARCHAR: return "LONGVARCHAR";
			case Types.NULL: return "OBJECT";
			case Types.NUMERIC: return "NUMERIC";
			case Types.OTHER: return "OBJECT";
			case Types.REAL: return "REAL";
			case Types.REF: return "OBJECT";
			case Types.SMALLINT: return "SMALLINT";
			case Types.STRUCT: return "OBJECT";
			case Types.TIME: return "TIME";
			case Types.TIMESTAMP: return "TIMESTAMP";
			case Types.TINYINT: return "TINYINT";
			case Types.VARBINARY: return "VARBINARY";
			case Types.NVARCHAR: return "NVARCHAR";
			case Types.VARCHAR: return "VARCHAR";
			default : return "VARCHAR";
		}
	}

	private int getIndexFromKey(String key) {
		String lc = StringUtil.toLowerCase(key);
		for(int i=0;i<columnNames.length;i++) {
			if(columnNames[i].getLowerString().equals(lc)) return i;
		}
		return -1;
	}
	
	private int getIndexFromKey(Collection.Key key) {
		
		for(int i=0;i<columnNames.length;i++) {
			if(columnNames[i].equalsIgnoreCase(key)) return i;
		}
		return -1;
	}
	

	/**
	 * @see railo.runtime.type.Query#setExecutionTime(long)
	 */
	public void setExecutionTime(long exeTime) {
		this.exeTime=exeTime;
	}

    /**
     * @param maxrows
     * @return has cutted or not
     */
    public synchronized boolean cutRowsTo(int maxrows) {
        //disconnectCache();
        
        if(maxrows>-1 && maxrows<getRecordcount()) {
			 for(int i=0;i<columns.length;i++) {
			 	QueryColumn column = columns[i];
			 	column.cutRowsTo(maxrows);
			 }
			 recordcount=maxrows;
			 return true;
        }
        return false;
    }

    /**
     * @see railo.runtime.type.Query#setCached(boolean)
     */
    public void setCached(boolean isCached) {
        this.isCached=isCached; 
    }

    /**
     * @see railo.runtime.type.Query#isCached()
     */
    public boolean isCached() {
        return isCached;
    }



    /**
     * @see com.allaire.cfx.Query#addRow()
     */
    public int addRow() {
		addRow(1);
		return getRecordcount();
    }


    public Key getColumnName(int columnIndex) {
    	Key[] keys = keys();
		if(columnIndex<1 || columnIndex>keys.length) return null;
		return keys[columnIndex-1];
    }

    /**
     * @see com.allaire.cfx.Query#getColumnIndex(java.lang.String)
     */
    public int getColumnIndex(String coulmnName) {
        String[] keys = keysAsString();
		for(int i=0;i<keys.length;i++) {
			if(keys[i].equalsIgnoreCase(coulmnName)) return i+1;
		}
		return -1;
    }



    /**
     * @see com.allaire.cfx.Query#getColumns()
     */
    public String[] getColumns() {
        return getColumnNamesAsString();
    }
    
    /**
     * @see railo.runtime.type.QueryPro#getColumnNames()
     */
    public Collection.Key[] getColumnNames() {
    	Collection.Key[] keys = keys();
    	Collection.Key[] rtn=new Collection.Key[keys.length];
		System.arraycopy(keys,0,rtn,0,keys.length);
		return rtn;
    }
    
    public void setColumnNames(Collection.Key[] trg) throws PageException {
    	columncount=trg.length;
    	Collection.Key[] src = keys();
    	
    	// target < source
    	if(trg.length<src.length){
    		this.columnNames=new Collection.Key[trg.length];
    		QueryColumnPro[] tmp=new QueryColumnPro[trg.length];
    		for(int i=0;i<trg.length;i++){
    			this.columnNames[i]=trg[i];
    			tmp[i]=this.columns[i];
    			tmp[i].setKey(trg[i]);
        	}
    		this.columns=tmp;
    		return;
    	}
    	
    	if(trg.length>src.length){
    		int recordcount=getRecordcount();
    		for(int i=src.length;i<trg.length;i++){
    			
    			Array arr=new ArrayImpl();
    			for(int r=1;r<=recordcount;r++){
    				arr.setE(i,"");
    			}
    			addColumn(trg[i], arr);
    		}
    		src = keys();
    	}
    	
		for(int i=0;i<trg.length;i++){
			this.columnNames[i]=trg[i];
			this.columns[i].setKey(trg[i]);
    	}
    }
    


	/**
	 * @see railo.runtime.type.QueryPro#getColumnNamesAsString()
	 */
	public String[] getColumnNamesAsString() {
		String[] keys = keysAsString();
		String[] rtn=new String[keys.length];
		System.arraycopy(keys,0,rtn,0,keys.length);
		return rtn;
	}

    /**
     * @see com.allaire.cfx.Query#getData(int, int)
     */
    public String getData(int row, int col) throws IndexOutOfBoundsException {
        String[] keys = keysAsString();
		if(col<1 || col>keys.length) {
			new IndexOutOfBoundsException("invalid column index to retrieve Data from query, valid index goes from 1 to "+keys.length);
		}
		
		Object o=getAt(keys[col-1],row,null);
		if(o==null)
			throw new IndexOutOfBoundsException("invalid row index to retrieve Data from query, valid index goes from 1 to "+getRecordcount());
		return Caster.toString( o,"" );
    }



    /**
     * @see com.allaire.cfx.Query#getName()
     */
    public String getName() {
        return this.name;
    }



    /**
     * @see com.allaire.cfx.Query#getRowCount()
     */
    public int getRowCount() {
        return getRecordcount();
    }



    /**
     * @see com.allaire.cfx.Query#setData(int, int, java.lang.String)
     */
    public void setData(int row, int col, String value) throws IndexOutOfBoundsException {
        String[] keys = keysAsString();
		if(col<1 || col>keys.length) {
			new IndexOutOfBoundsException("invalid column index to retrieve Data from query, valid index goes from 1 to "+keys.length);
		}
		try {
			setAt(keys[col-1],row,value);
		} 
		catch (PageException e) {
			throw new IndexOutOfBoundsException("invalid row index to retrieve Data from query, valid index goes from 1 to "+getRecordcount());
		}
    }

    /**
     * @see railo.runtime.type.Collection#containsKey(java.lang.String)
     */
    public boolean containsKey(String key) {
        return getColumn(key,null)!=null;
    }	


	/**
	 *
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Collection.Key key) {
        return getColumn(key,null)!=null;
	}
    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Query to String",
          "Use Build-In-Function \"serialize(Query):String\" to create a String from Query");
    }

	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return defaultValue;
	}


    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Query to a boolean value");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }


    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Query to a number value");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }


    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Query to a Date");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws ExpressionException {
		throw new ExpressionException("can't compare Complex Object Type Query with a boolean value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Query with a DateTime Object");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Query with a numeric value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Query with a String");
	}

    public synchronized Array getMetaDataSimple() {
    	Array cols=new ArrayImpl();
    	Struct column;
        for(int i=0;i<columns.length;i++) {
        	column=new StructImpl();
        	column.setEL(KeyImpl.NAME,columnNames[i].getString());
        	column.setEL("isCaseSensitive",Boolean.FALSE);
        	column.setEL("typeName",columns[i].getTypeAsString());
        	cols.appendEL(column);
        }
        return cols;
    }

    public synchronized Struct _getMetaData() {
    	
        Struct cols=new StructImpl();
        for(int i=0;i<columns.length;i++) {
            cols.setEL(columnNames[i],columns[i].getTypeAsString());
        }
        
        Struct sct=new StructImpl();
        sct.setEL(KeyImpl.NAME_UC,getName());
        sct.setEL(COLUMNS,cols);
        sct.setEL(SQL,sql==null?"":sql.toString());
        sct.setEL(EXECUTION_TIME,new Double(exeTime));
        sct.setEL(RECORDCOUNT,new Double(getRowCount()));
        sct.setEL(CACHED,Caster.toBoolean(isCached()));
        return sct;
        
    }

	/**
	 * @return the sql
	 */
	public SQL getSql() {
		return sql;
	}

	/**
	 * @param sql the sql to set
	 */
	public void setSql(SQL sql) {
		this.sql = sql;
	}


	/**
	 * @see java.sql.ResultSet#getObject(java.lang.String)
	 */
	public Object getObject(String columnName) throws SQLException {
		int currentrow;
		if((currentrow=arrCurrentRow.get(getPid(),0))==0) return null;
		return getAt(columnName,currentrow,null);
	}
	
	/**
	 * @see java.sql.ResultSet#getObject(int)
	 */
	public Object getObject(int columnIndex) throws SQLException {
		if(columnIndex>0 && columnIndex<=columncount) return  getObject(this.columnNames[columnIndex-1].getString());
		return null;
	}
	
	/**
	 * @see java.sql.ResultSet#getString(int)
	 */
	public String getString(int columnIndex) throws SQLException {
		Object rtn = getObject(columnIndex);
		if(rtn==null)return null;
		if(Decision.isCastableToString(rtn)) return Caster.toString(rtn,null);
		throw new SQLException("can't cast value to string");
	}
	
	/**
	 * @see java.sql.ResultSet#getString(java.lang.String)
	 */
	public String getString(String columnName) throws SQLException {
		Object rtn = getObject(columnName);
		if(rtn==null)return null;
		if(Decision.isCastableToString(rtn)) return Caster.toString(rtn,null);
		throw new SQLException("can't cast value to string");
	}
	
	/**
	 * @see java.sql.ResultSet#getBoolean(int)
	 */
	public boolean getBoolean(int columnIndex) throws SQLException {
		Object rtn = getObject(columnIndex);
		if(rtn==null)return false;
		if(rtn!=null && Decision.isCastableToBoolean(rtn)) return Caster.toBooleanValue(rtn,false);
		throw new SQLException("can't cast value to boolean");
	}
	
	/**
	 * @see java.sql.ResultSet#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String columnName) throws SQLException {
		Object rtn = getObject(columnName);
		if(rtn==null)return false;
		if(rtn!=null && Decision.isCastableToBoolean(rtn)) return Caster.toBooleanValue(rtn,false);
		throw new SQLException("can't cast value to boolean");
	}
	
	
	// ---------------------------------------

	/**
	 *
	 * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, java.lang.String, java.lang.Object[])
	 */
	public Object call(PageContext pc, String methodName, Object[] arguments) throws PageException {
		return Reflector.callMethod(this,methodName,arguments);
	}

	/**
	 *
	 * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	public Object call(PageContext pc, Key methodName, Object[] arguments) throws PageException {
		return Reflector.callMethod(this,methodName,arguments);
	}

	/**
	 *
	 * @see railo.runtime.type.Objects#callWithNamedValues(railo.runtime.PageContext, java.lang.String, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, String methodName,Struct args) throws PageException {		
		throw new ExpressionException("No matching Method/Function ["+methodName+"] for call with named arguments found");
	}

	/**
	 * @see railo.runtime.type.Objects#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Collection.Key, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, Key methodName, Struct args) throws PageException {	
		throw new ExpressionException("No matching Method/Function ["+methodName.getString()+"] for call with named arguments found");
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object get(PageContext pc, String key, Object defaultValue) {
		return getAt(key,arrCurrentRow.get(pc.getId(),1),defaultValue);
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return getAt(key,arrCurrentRow.get(
				pc.getId(),1),defaultValue);
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String)
	 */
	public Object get(PageContext pc, String key) throws PageException {
		return getAt(key,arrCurrentRow.get(pc.getId(),1));
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public Object get(PageContext pc, Key key) throws PageException {
		return getAt(key,arrCurrentRow.get(pc.getId(),1));
	}

	/**
	 * @see railo.runtime.type.Objects#isInitalized()
	 */
	public boolean isInitalized() {
		return true;
	}

	/**
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object set(PageContext pc, String propertyName, Object value) throws PageException {
		return setAt(propertyName,arrCurrentRow.get(pc.getId(),1),value);
	}

	/**
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(PageContext pc, Key propertyName, Object value) throws PageException {
		return setAt(propertyName,arrCurrentRow.get(pc.getId(),1),value);
	}

	/**
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object setEL(PageContext pc, String propertyName, Object value) {
		return setAtEL(propertyName,arrCurrentRow.get(pc.getId(),1),value);
	}

	/**
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(PageContext pc, Key propertyName, Object value) {
		return setAtEL(propertyName,arrCurrentRow.get(pc.getId(),1),value);
	}
	
	/**
	 * @see java.sql.ResultSet#wasNull()
	 */
	public boolean wasNull() {
		throw new PageRuntimeException(new ApplicationException("method [wasNull] is not supported"));
	}

	/**
	 * @see java.sql.ResultSet#absolute(int)
	 */
	public boolean absolute(int row) throws SQLException {
		if(recordcount==0) {
			if(row!=0) throw new SQLException("invalid row ["+row+"], query is Empty");
			return false;
		}
		//row=row%recordcount;
		
		if(row>0) arrCurrentRow.set(getPid(),row);
		else arrCurrentRow.set(getPid(),(recordcount+1)+row);
		return true;
	}

	/**
	 * @see java.sql.ResultSet#afterLast()
	 */
	public void afterLast() throws SQLException {
		arrCurrentRow.set(getPid(),recordcount+1);
	}

	/**
	 * @see java.sql.ResultSet#beforeFirst()
	 */
	public void beforeFirst() throws SQLException {
		arrCurrentRow.set(getPid(),0);
	}

	/**
	 * @see java.sql.ResultSet#cancelRowUpdates()
	 */
	public void cancelRowUpdates() throws SQLException {
		// ignored
	}

	/**
	 * @see java.sql.ResultSet#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		// ignored
	}

	/**
	 * @see java.sql.ResultSet#close()
	 */
	public void close() throws SQLException {
		// ignored
	}

	/**
	 * @see java.sql.ResultSet#deleteRow()
	 */
	public void deleteRow() throws SQLException {
		try {
			removeRow(arrCurrentRow.get(getPid()));
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#findColumn(java.lang.String)
	 */
	public int findColumn(String columnName) throws SQLException {
		int index= getColumnIndex(columnName);
		if(index==-1) throw new SQLException("invald column definitions ["+columnName+"]");
		return index;
	}

	/**
	 * @see java.sql.ResultSet#first()
	 */
	public boolean first() throws SQLException {
		return absolute(1);
	}

	public java.sql.Array getArray(int i) throws SQLException {
		throw new SQLException("method is not implemented");
	}

	public java.sql.Array getArray(String colName) throws SQLException {
		throw new SQLException("method is not implemented");
	}

	/**
	 * @see java.sql.ResultSet#getAsciiStream(int)
	 */
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		String res = getString(columnIndex);
		if(res==null)return null;
		return new ByteArrayInputStream(res.getBytes());
	}

	/**
	 * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
	 */
	public InputStream getAsciiStream(String columnName) throws SQLException {
		String res = getString(columnName);
		if(res==null)return null;
		return new ByteArrayInputStream(res.getBytes());
	}

	/**
	 * @see java.sql.ResultSet#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return new BigDecimal(getDouble(columnIndex));
	}

	/**
	 * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		return new BigDecimal(getDouble(columnName));
	}

	/**
	 * @see java.sql.ResultSet#getBigDecimal(int, int)
	 */
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return new BigDecimal(getDouble(columnIndex));
	}

	/**
	 * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
	 */
	public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
		return new BigDecimal(getDouble(columnName));
	}

	/**
	 * @see java.sql.ResultSet#getBinaryStream(int)
	 */
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		Object obj = getObject(columnIndex);
		if(obj==null)return null;
		try {
			return Caster.toBinaryStream(obj);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getBinaryStream(java.lang.String)
	 */
	public InputStream getBinaryStream(String columnName) throws SQLException {
		Object obj = getObject(columnName);
		if(obj==null)return null;
		try {
			return Caster.toBinaryStream(obj);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getBlob(int)
	 */
	public Blob getBlob(int i) throws SQLException {
		byte[] bytes = getBytes(i);
		if(bytes==null) return null;
		try {
			return BlobImpl.toBlob(bytes);
		} 
		catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getBlob(java.lang.String)
	 */
	public Blob getBlob(String colName) throws SQLException {
		byte[] bytes = getBytes(colName);
		if(bytes==null) return null;
		try {
			return BlobImpl.toBlob(bytes);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getByte(int)
	 */
	public byte getByte(int columnIndex) throws SQLException {
		Object obj = getObject(columnIndex);
		if(obj==null) return (byte)0;
		try {
			return Caster.toByteValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getByte(java.lang.String)
	 */
	public byte getByte(String columnName) throws SQLException {
		Object obj = getObject(columnName);
		if(obj==null) return (byte)0;
		try {
			return Caster.toByteValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getBytes(int)
	 */
	public byte[] getBytes(int columnIndex) throws SQLException {
		Object obj = getObject(columnIndex);
		if(obj==null) return null;
		try {
			return Caster.toBytes(obj);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getBytes(java.lang.String)
	 */
	public byte[] getBytes(String columnName) throws SQLException {
		Object obj = getObject(columnName);
		if(obj==null) return null;
		try {
			return Caster.toBytes(obj);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getCharacterStream(int)
	 */
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		String str=getString(columnIndex);
		if(str==null) return null;
		return new StringReader(str);
	}

	/**
	 * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
	 */
	public Reader getCharacterStream(String columnName) throws SQLException {
		String str=getString(columnName);
		if(str==null) return null;
		return new StringReader(str);
	}

	/**
	 * @see java.sql.ResultSet#getClob(int)
	 */
	public Clob getClob(int i) throws SQLException {
		String str=getString(i);
		if(str==null) return null;
		return ClobImpl.toClob(str);
	}

	/**
	 * @see java.sql.ResultSet#getClob(java.lang.String)
	 */
	public Clob getClob(String colName) throws SQLException {
		String str=getString(colName);
		if(str==null) return null;
		return ClobImpl.toClob(str);
	}

	/**
	 * @see java.sql.ResultSet#getConcurrency()
	 */
	public int getConcurrency() throws SQLException {
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getCursorName()
	 */
	public String getCursorName() throws SQLException {
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getDate(int)
	 */
	public java.sql.Date getDate(int columnIndex) throws SQLException {
		Object obj=getObject(columnIndex);
		if(obj==null) return null;
		try {
			return new java.sql.Date(Caster.toDate(obj, false, null).getTime());
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getDate(java.lang.String)
	 */
	public java.sql.Date getDate(String columnName) throws SQLException {
		Object obj=getObject(columnName);
		if(obj==null) return null;
		try {
			return new java.sql.Date(Caster.toDate(obj, false, null).getTime());
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
	 */
	public java.sql.Date getDate(int columnIndex, Calendar cal)throws SQLException {
		return getDate(columnIndex); // TODO impl
	}

	/**
	 * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
	 */
	public java.sql.Date getDate(String columnName, Calendar cal) throws SQLException {
		return getDate(columnName);// TODO impl
	}

	/**
	 * @see java.sql.ResultSet#getDouble(int)
	 */
	public double getDouble(int columnIndex) throws SQLException {
		Object obj=getObject(columnIndex);
		if(obj==null) return 0;
		try {
			return Caster.toDoubleValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getDouble(java.lang.String)
	 */
	public double getDouble(String columnName) throws SQLException {
		Object obj=getObject(columnName);
		if(obj==null) return 0;
		try {
			return Caster.toDoubleValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {
		return 1000;
	}

	/**
	 * @see java.sql.ResultSet#getFetchSize()
	 */
	public int getFetchSize() throws SQLException {
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getFloat(int)
	 */
	public float getFloat(int columnIndex) throws SQLException {
		Object obj=getObject(columnIndex);
		if(obj==null) return 0;
		try {
			return Caster.toFloatValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getFloat(java.lang.String)
	 */
	public float getFloat(String columnName) throws SQLException {
		Object obj=getObject(columnName);
		if(obj==null) return 0;
		try {
			return Caster.toFloatValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getInt(int)
	 */
	public int getInt(int columnIndex) throws SQLException {
		Object obj=getObject(columnIndex);
		if(obj==null) return 0;
		try {
			return Caster.toIntValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getInt(java.lang.String)
	 */
	public int getInt(String columnName) throws SQLException {
		Object obj=getObject(columnName);
		if(obj==null) return 0;
		try {
			return Caster.toIntValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getLong(int)
	 */
	public long getLong(int columnIndex) throws SQLException {
		Object obj=getObject(columnIndex);
		if(obj==null) return 0;
		try {
			return Caster.toLongValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getLong(java.lang.String)
	 */
	public long getLong(String columnName) throws SQLException {
		Object obj=getObject(columnName);
		if(obj==null) return 0;
		try {
			return Caster.toLongValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getObject(int, java.util.Map)
	 */
	public Object getObject(int i, Map map) throws SQLException {
		throw new SQLException("method is not implemented");
	}

	/**
	 * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map)
	 */
	public Object getObject(String colName, Map map) throws SQLException {
		throw new SQLException("method is not implemented");
	}

	/**
	 * @see java.sql.ResultSet#getRef(int)
	 */
	public Ref getRef(int i) throws SQLException {
		throw new SQLException("method is not implemented");
	}

	/**
	 * @see java.sql.ResultSet#getRef(java.lang.String)
	 */
	public Ref getRef(String colName) throws SQLException {
		throw new SQLException("method is not implemented");
	}

	/**
	 * @see java.sql.ResultSet#getRow()
	 */
	public int getRow() throws SQLException {
		return arrCurrentRow.get(getPid(),0);
	}

	/**
	 * @see java.sql.ResultSet#getShort(int)
	 */
	public short getShort(int columnIndex) throws SQLException {
		Object obj=getObject(columnIndex);
		if(obj==null) return 0;
		try {
			return Caster.toShortValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getShort(java.lang.String)
	 */
	public short getShort(String columnName) throws SQLException {
		Object obj=getObject(columnName);
		if(obj==null) return 0;
		try {
			return Caster.toShortValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	public Statement getStatement() throws SQLException {
		throw new SQLException("method is not implemented");
	}

	/**
	 * @see java.sql.ResultSet#getTime(int)
	 */
	public Time getTime(int columnIndex) throws SQLException {
		Object obj=getObject(columnIndex);
		if(obj==null) return null;
		try {
			return new Time(DateCaster.toTime(null, obj).getTime());
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getTime(java.lang.String)
	 */
	public Time getTime(String columnName) throws SQLException {
		Object obj=getObject(columnName);
		if(obj==null) return null;
		try {
			return new Time(DateCaster.toTime(null, obj).getTime());
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
	 */
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return getTime(columnIndex);// TODO impl
	}

	/**
	 * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
	 */
	public Time getTime(String columnName, Calendar cal) throws SQLException {
		return getTime(columnName);// TODO impl
	}

	/**
	 * @see java.sql.ResultSet#getTimestamp(int)
	 */
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		Object obj=getObject(columnIndex);
		if(obj==null) return null;
		try {
			return new Timestamp(DateCaster.toTime(null, obj).getTime());
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getTimestamp(java.lang.String)
	 */
	public Timestamp getTimestamp(String columnName) throws SQLException {
		Object obj=getObject(columnName);
		if(obj==null) return null;
		try {
			return new Timestamp(DateCaster.toTime(null, obj).getTime());
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
	 */
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return getTimestamp(columnIndex);// TODO impl
	}

	/**
	 * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
	 */
	public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
		return getTimestamp(columnName);// TODO impl
	}

	/**
	 * @see java.sql.ResultSet#getType()
	 */
	public int getType() throws SQLException {
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getURL(int)
	 */
	public URL getURL(int columnIndex) throws SQLException {
		throw new SQLException("method is not implemented");
	}

	/**
	 * @see java.sql.ResultSet#getURL(java.lang.String)
	 */
	public URL getURL(String columnName) throws SQLException {
		throw new SQLException("method is not implemented");
	}

	/**
	 * @see java.sql.ResultSet#getUnicodeStream(int)
	 */
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		String str=getString(columnIndex);
		if(str==null) return null;
		try {
			return new ByteArrayInputStream(str.getBytes("UTF-8"));
		} 
		catch (UnsupportedEncodingException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
	 */
	public InputStream getUnicodeStream(String columnName) throws SQLException {
		String str=getString(columnName);
		if(str==null) return null;
		try {
			return new ByteArrayInputStream(str.getBytes("UTF-8"));
		} 
		catch (UnsupportedEncodingException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		throw new SQLException("method is not implemented");
	}

	/**
	 * @see java.sql.ResultSet#insertRow()
	 */
	public void insertRow() throws SQLException {
		throw new SQLException("method is not implemented");
	}

	/**
	 * @see java.sql.ResultSet#isAfterLast()
	 */
	public boolean isAfterLast() throws SQLException {
		return getCurrentrow()>recordcount;
	}

	/**
	 * @see java.sql.ResultSet#isBeforeFirst()
	 */
	public boolean isBeforeFirst() throws SQLException {
		return arrCurrentRow.get(getPid(),0)==0;
	}

	/**
	 * @see java.sql.ResultSet#isFirst()
	 */
	public boolean isFirst() throws SQLException {
		return arrCurrentRow.get(getPid(),0)==1;
	}

	public boolean isLast() throws SQLException {
		return arrCurrentRow.get(getPid(),0)==recordcount;
	}

	public boolean last() throws SQLException {
		return absolute(recordcount);
	}

	public void moveToCurrentRow() throws SQLException {
		// ignore
	}

	public void moveToInsertRow() throws SQLException {
		// ignore
	}


	public boolean previous() {
		return previous(getPid());
	}
	
	public boolean previous(int pid) {
		if(0<(arrCurrentRow.set(pid,arrCurrentRow.get(pid,0)-1))) {
			return true;
		}
		arrCurrentRow.set(pid,0);
		return false;
	}

	public void refreshRow() throws SQLException {
		// ignore
		
	}

	/**
	 * @see java.sql.ResultSet#relative(int)
	 */
	public boolean relative(int rows) throws SQLException {
		return absolute(getRow()+rows);
	}

	/**
	 * @see java.sql.ResultSet#rowDeleted()
	 */
	public boolean rowDeleted() throws SQLException {
		return false;
	}

	/**
	 * @see java.sql.ResultSet#rowInserted()
	 */
	public boolean rowInserted() throws SQLException {
		return false;
	}

	/**
	 * @see java.sql.ResultSet#rowUpdated()
	 */
	public boolean rowUpdated() throws SQLException {
		return false;
	}

	public void setFetchDirection(int direction) throws SQLException {
		// ignore
	}

	public void setFetchSize(int rows) throws SQLException {
		// ignore
	}

	/**
	 * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
	 */
	public void updateArray(int columnIndex, java.sql.Array x)throws SQLException {
		updateObject(columnIndex, x.getArray());
	}

	/**
	 * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
	 */
	public void updateArray(String columnName, java.sql.Array x)throws SQLException {
		updateObject(columnName, x.getArray());
	}

	/**
	 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
	 */
	public void updateAsciiStream(int columnIndex, InputStream x, int length)throws SQLException {
		updateBinaryStream(columnIndex, x, length);
	}

	/**
	 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, int)
	 */
	public void updateAsciiStream(String columnName, InputStream x, int length)throws SQLException {
		updateBinaryStream(columnName, x, length);
	}

	/**
	 * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
	 */
	public void updateBigDecimal(int columnIndex, BigDecimal x)throws SQLException {
		updateObject(columnIndex, x.toString());
	}

	/**
	 * @see java.sql.ResultSet#updateBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
		updateObject(columnName, x.toString());
	}

	/**
	 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
	 */
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		try {
			updateObject(columnIndex, IOUtil.toBytesMax(x, length));
		} catch (IOException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, int)
	 */
	public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
		try {
			updateObject(columnName, IOUtil.toBytesMax(x, length));
		} catch (IOException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
	 */
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		try {
			updateObject(columnIndex, toBytes(x));
		} catch (IOException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
	 */
	public void updateBlob(String columnName, Blob x) throws SQLException {
		try {
			updateObject(columnName, toBytes(x));
		} catch (IOException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#updateBoolean(int, boolean)
	 */
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		updateObject(columnIndex, Caster.toBoolean(x));
	}

	/**
	 * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
	 */
	public void updateBoolean(String columnName, boolean x) throws SQLException {
		updateObject(columnName, Caster.toBoolean(x));
	}

	/**
	 * @see java.sql.ResultSet#updateByte(int, byte)
	 */
	public void updateByte(int columnIndex, byte x) throws SQLException {
		updateObject(columnIndex, new Byte(x));
	}

	/**
	 * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
	 */
	public void updateByte(String columnName, byte x) throws SQLException {
		updateObject(columnName, new Byte(x));
	}

	/**
	 * @see java.sql.ResultSet#updateBytes(int, byte[])
	 */
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		updateObject(columnIndex, x);
	}

	/**
	 * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
	 */
	public void updateBytes(String columnName, byte[] x) throws SQLException {
		updateObject(columnName, x);
	}

	/**
	 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
	 */
	public void updateCharacterStream(int columnIndex, Reader reader, int length)throws SQLException {
		try {
			updateObject(columnIndex, IOUtil.toString(reader));
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, int)
	 */
	public void updateCharacterStream(String columnName, Reader reader,int length) throws SQLException {
		try {
			updateObject(columnName, IOUtil.toString(reader));
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
	 */
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		try {
			updateObject(columnIndex, toString(x));
		} catch (IOException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
	 */
	public void updateClob(String columnName, Clob x) throws SQLException {
		try {
			updateObject(columnName, toString(x));
		} catch (IOException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
	 */
	public void updateDate(int columnIndex, java.sql.Date x)throws SQLException {
		updateObject(columnIndex, Caster.toDate(x, false, null, null));
	}

	/**
	 * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
	 */
	public void updateDate(String columnName, java.sql.Date x)throws SQLException {
		updateObject(columnName, Caster.toDate(x, false, null, null));
	}

	/**
	 * @see java.sql.ResultSet#updateDouble(int, double)
	 */
	public void updateDouble(int columnIndex, double x) throws SQLException {
		updateObject(columnIndex, Caster.toDouble(x));
	}

	/**
	 * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
	 */
	public void updateDouble(String columnName, double x) throws SQLException {
		updateObject(columnName, Caster.toDouble(x));
	}

	/**
	 * @see java.sql.ResultSet#updateFloat(int, float)
	 */
	public void updateFloat(int columnIndex, float x) throws SQLException {
		updateObject(columnIndex, Caster.toDouble(x));
	}

	/**
	 * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
	 */
	public void updateFloat(String columnName, float x) throws SQLException {
		updateObject(columnName, Caster.toDouble(x));
	}

	/**
	 * @see java.sql.ResultSet#updateInt(int, int)
	 */
	public void updateInt(int columnIndex, int x) throws SQLException {
		updateObject(columnIndex, Caster.toDouble(x));
	}

	/**
	 * @see java.sql.ResultSet#updateInt(java.lang.String, int)
	 */
	public void updateInt(String columnName, int x) throws SQLException {
		updateObject(columnName, Caster.toDouble(x));
	}

	/**
	 * @see java.sql.ResultSet#updateLong(int, long)
	 */
	public void updateLong(int columnIndex, long x) throws SQLException {
		updateObject(columnIndex, Caster.toDouble(x));
	}

	/**
	 * @see java.sql.ResultSet#updateLong(java.lang.String, long)
	 */
	public void updateLong(String columnName, long x) throws SQLException {
		updateObject(columnName, Caster.toDouble(x));
	}

	/**
	 * @see java.sql.ResultSet#updateNull(int)
	 */
	public void updateNull(int columnIndex) throws SQLException {
		updateObject(columnIndex, null);
	}

	/**
	 * @see java.sql.ResultSet#updateNull(java.lang.String)
	 */
	public void updateNull(String columnName) throws SQLException {
		updateObject(columnName, null);
	}

	/**
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
	 */
	public void updateObject(int columnIndex, Object x) throws SQLException {
		try {
			set(getColumnName(columnIndex), x);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
	 */
	public void updateObject(String columnName, Object x) throws SQLException {
		try {
			set(KeyImpl.init(columnName), x);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
	 */
	public void updateObject(int columnIndex, Object x, int scale)throws SQLException {
		updateObject(columnIndex, x);
	}

	/**
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object, int)
	 */
	public void updateObject(String columnName, Object x, int scale)throws SQLException {
		updateObject(columnName, x);
	}

	/**
	 * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
	 */
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		updateObject(columnIndex, x.getObject());
	}

	/**
	 * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
	 */
	public void updateRef(String columnName, Ref x) throws SQLException {
		updateObject(columnName, x.getObject());
	}

	public void updateRow() throws SQLException {
		throw new SQLException("method is not implemented");
	}

	/**
	 * @see java.sql.ResultSet#updateShort(int, short)
	 */
	public void updateShort(int columnIndex, short x) throws SQLException {
		updateObject(columnIndex, Caster.toDouble(x));
	}

	/**
	 * @see java.sql.ResultSet#updateShort(java.lang.String, short)
	 */
	public void updateShort(String columnName, short x) throws SQLException {
		updateObject(columnName, Caster.toDouble(x));
	}

	/**
	 * @see java.sql.ResultSet#updateString(int, java.lang.String)
	 */
	public void updateString(int columnIndex, String x) throws SQLException {
		updateObject(columnIndex, x);
	}

	/**
	 * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
	 */
	public void updateString(String columnName, String x) throws SQLException {
		updateObject(columnName, x);
	}

	/**
	 * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
	 */
	public void updateTime(int columnIndex, Time x) throws SQLException {
		updateObject(columnIndex, new DateTimeImpl(x.getTime(),false));
	}

	/**
	 * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
	 */
	public void updateTime(String columnName, Time x) throws SQLException {
		updateObject(columnName, new DateTimeImpl(x.getTime(),false));
	}

	/**
	 * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
	 */
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		updateObject(columnIndex, new DateTimeImpl(x.getTime(),false));
	}

	/**
	 * @see java.sql.ResultSet#updateTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
		updateObject(columnName, new DateTimeImpl(x.getTime(),false));
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		
		throw new SQLException("method is not implemented");
	}


    /**
	 * @see railo.runtime.type.Collection#keyIterator()
	 */
	public Iterator keyIterator() {
		return new KeyIterator(keys());
	}
	

	/**
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
	public Iterator iterator() {
		return keyIterator();
	}
	
	public Iterator valueIterator() {
		return new CollectionIterator(keys(),this);
	}

	public void readExternal(ObjectInput in) throws IOException {
		try {
			QueryImpl other=(QueryImpl) new CFMLExpressionInterpreter().interpret(ThreadLocalPageContext.get(),in.readUTF());
			this.arrCurrentRow=other.arrCurrentRow;
			this.columncount=other.columncount;
			this.columnNames=other.columnNames;
			this.columns=other.columns;
			this.exeTime=other.exeTime;
			this.generatedKeys=other.generatedKeys;
			this.isCached=other.isCached;
			this.name=other.name;
			this.recordcount=other.recordcount;
			this.sql=other.sql;
			this.updateCount=other.updateCount;
			
		} catch (PageException e) {
			throw new IOException(e.getMessage());
		}
	}

	public void writeExternal(ObjectOutput out) {
		try {
			out.writeUTF(new ScriptConverter().serialize(this));
		} 
		catch (Throwable t) {}
	}
	
	/**
	 * @see railo.runtime.type.Sizeable#sizeOf()
	 */
	public long sizeOf(){
		long size=SizeOf.size(this.exeTime)+
		SizeOf.size(this.isCached)+
		SizeOf.size(this.arrCurrentRow)+
		SizeOf.size(this.columncount)+
		SizeOf.size(this.generatedKeys)+
		SizeOf.size(this.name)+
		SizeOf.size(this.recordcount)+
		SizeOf.size(this.sql)+
		SizeOf.size(this.template)+
		SizeOf.size(this.updateCount);
		
		for(int i=0;i<columns.length;i++){
			size+=this.columns[i].sizeOf();
		}
		return size;
	}
	

	public boolean equals(Object obj){
		if(!(obj instanceof Collection)) return false;
		return CollectionUtil.equals(this,(Collection)obj);
	}

	public int getHoldability() throws SQLException {
		throw notSupported();
	}

	public boolean isClosed() throws SQLException {
		return false;
	}

	public void updateNString(int columnIndex, String nString)throws SQLException {
		updateString(columnIndex, nString);
	}

	public void updateNString(String columnLabel, String nString)throws SQLException {
		updateString(columnLabel, nString);
	}

	

	public String getNString(int columnIndex) throws SQLException {
		return getString(columnIndex);
	}

	public String getNString(String columnLabel) throws SQLException {
		return getString(columnLabel);
	}

	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return getCharacterStream(columnIndex);
	}

	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		return getCharacterStream(columnLabel);
	}

	public void updateNCharacterStream(int columnIndex, Reader x, long length)throws SQLException {
		updateCharacterStream(columnIndex, x, length);
	}

	public void updateNCharacterStream(String columnLabel, Reader reader,long length) throws SQLException {
		throw notSupported();
	}

	public void updateAsciiStream(int columnIndex, InputStream x, long length)throws SQLException {
		throw notSupported();
	}

	public void updateBinaryStream(int columnIndex, InputStream x, long length)throws SQLException {
		throw notSupported();
	}

	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		throw notSupported();
	}

	public void updateAsciiStream(String columnLabel, InputStream x, long length)throws SQLException {
		throw notSupported();
	}

	public void updateBinaryStream(String columnLabel, InputStream x,long length) throws SQLException {
		throw notSupported();
	}

	public void updateCharacterStream(String columnLabel, Reader reader,long length) throws SQLException {
		throw notSupported();
	}

	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		throw notSupported();
	}

	public void updateBlob(String columnLabel, InputStream inputStream,long length) throws SQLException {
		throw notSupported();
	}

	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		throw notSupported();
	}

	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		throw notSupported();
	}

	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		updateClob(columnIndex, reader, length);
	}

	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		updateClob(columnLabel, reader,length);
	}

	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		updateCharacterStream(columnIndex, x);
	}

	public void updateNCharacterStream(String columnLabel, Reader reader)throws SQLException {
		throw notSupported();
	}

	public void updateAsciiStream(int columnIndex, InputStream x)throws SQLException {
		throw notSupported();
	}

	public void updateBinaryStream(int columnIndex, InputStream x)throws SQLException {
		throw notSupported();
	}

	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		throw notSupported();
	}

	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		throw notSupported();
	}

	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		throw notSupported();
	}

	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		throw notSupported();
	}

	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		throw notSupported();
	}

	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		throw notSupported();
	}

	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		throw notSupported();
	}

	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		throw notSupported();
	}

	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		updateClob(columnIndex, reader);
	}

	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		updateClob(columnLabel, reader);
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw notSupported();
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw notSupported();
	}
	

	
	//JDK6: uncomment this for compiling with JDK6 
	 
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		throw notSupported();
	}

	public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		throw notSupported();
	}

	public NClob getNClob(int columnIndex) throws SQLException {
		throw notSupported();
	}

	public NClob getNClob(String columnLabel) throws SQLException {
		throw notSupported();
	}

	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		throw notSupported();
	}

	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		throw notSupported();
	}

	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		throw notSupported();
	}

	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		throw notSupported();
	}
	
	public RowId getRowId(int columnIndex) throws SQLException {
		throw notSupported();
	}

	public RowId getRowId(String columnLabel) throws SQLException {
		throw notSupported();
	}

	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		throw notSupported();
	}

	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		throw notSupported();
	}
	
	public void removeRows(int index, int count) throws PageException {
		QueryUtil.removeRows(this,index,count);
	}
	

	private SQLException notSupported() {
		return new SQLException("this feature is not supported");
	}
	private RuntimeException notSupportedEL() {
		return new RuntimeException(new SQLException("this feature is not supported"));
	}

	public synchronized void enableShowQueryUsage() {
		if(columns!=null)for(int i=0;i<columns.length;i++){
			columns[i]=columns[i].toDebugColumn();
		}
	}

	public long getExecutionTime() {
		return exeTime;
	}
	
	public static QueryImpl cloneQuery(QueryPro qry,boolean deepCopy) {
        QueryImpl newResult=new QueryImpl();
        ThreadLocalDuplication.set(qry, newResult);
        try{
	        
    	    newResult.columnNames=qry.getColumnNames();
	        newResult.columns=new QueryColumnPro[newResult.columnNames.length];
	        QueryColumnPro col;
	        for(int i=0;i<newResult.columnNames.length;i++) {
	        	col = (QueryColumnPro) qry.getColumn(newResult.columnNames[i],null);
	        	newResult.columns[i]=col.cloneColumn(newResult,deepCopy);
	        }
	        
		        
		    newResult.sql=qry.getSql();
	        newResult.template=qry.getTemplate();
	        newResult.recordcount=qry.getRecordcount();
	        newResult.columncount=newResult.columnNames.length;
	        newResult.isCached=qry.isCached();
	        newResult.name=qry.getName();
	        newResult.exeTime=qry.getExecutionTime();
	        newResult.updateCount=qry.getUpdateCount();
	        if(qry.getGeneratedKeys()!=null)newResult.generatedKeys=((QueryImpl)qry.getGeneratedKeys()).cloneQuery(false);
	        return newResult;
        }
        finally {
        	ThreadLocalDuplication.remove(qry);
        }
    }
}