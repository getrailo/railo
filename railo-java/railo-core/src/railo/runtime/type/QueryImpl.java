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
import java.nio.charset.Charset;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import railo.commons.db.DBUtil;
import railo.commons.io.IOUtil;
import railo.commons.lang.StringUtil;
import railo.commons.sql.SQLUtil;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.PageContext;
import railo.runtime.config.NullSupportHelper;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.db.CFTypes;
import railo.runtime.db.DataSourceUtil;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.DatasourceConnectionImpl;
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
import railo.runtime.op.Duplicator;
import railo.runtime.op.ThreadLocalDuplication;
import railo.runtime.op.date.DateCaster;
import railo.runtime.query.caster.Cast;
import railo.runtime.type.comparator.NumberSortRegisterComparator;
import railo.runtime.type.comparator.SortRegister;
import railo.runtime.type.comparator.SortRegisterComparator;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.it.CollectionIterator;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.ForEachQueryIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.sql.BlobImpl;
import railo.runtime.type.sql.ClobImpl;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;
import railo.runtime.type.util.MemberUtil;
import railo.runtime.type.util.QueryUtil;

/**
 * implementation of the query interface
 */
/**
 * 
 */
public class QueryImpl implements Query,Objects {

	private static final long serialVersionUID = 1035795427320192551L;


	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	public static final Collection.Key GENERATED_KEYS = KeyImpl.intern("GENERATED_KEYS");
	public static final Collection.Key GENERATEDKEYS = KeyImpl.intern("GENERATEDKEYS");
	
	
	
	//private static int count=0;
	private QueryColumnImpl[] columns;
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
	
	
	@Override
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
    public QueryImpl(ResultSet result, int maxrow, String name, TimeZone tz) throws PageException {
    	this.name=name;
        //Stopwatch stopwatch=new Stopwatch();
		//stopwatch.start();
		long start=System.nanoTime();
    	try {
            fillResult(null,result,maxrow,false,false,tz);
        } catch (SQLException e) {
            throw new DatabaseException(e,null);
        } catch (IOException e) {
            throw Caster.toPageException(e);
        }
		exeTime=System.nanoTime()-start;
    }
    
    /**
     * Constructor of the class
     * only for internal usage (cloning/deserialize)
     */
    public QueryImpl() {
    }
    
    
    public QueryImpl(ResultSet result, String name,TimeZone tz) throws PageException {
		this.name=name;
        
		try {	
		    fillResult(null,result,-1,true,false,tz);
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
    public QueryImpl(PageContext pc, DatasourceConnection dc,SQL sql,int maxrow, int fetchsize,int timeout, String name) throws PageException {
    	this(pc,dc, sql, maxrow, fetchsize, timeout, name,null,false,true);
    }
    

    public QueryImpl(PageContext pc, DatasourceConnection dc,SQL sql,int maxrow, int fetchsize,int timeout, String name,String template) throws PageException {
    	this(pc,dc, sql, maxrow, fetchsize, timeout, name,template,false,true);
    }
    
	public QueryImpl(PageContext pc, DatasourceConnection dc,SQL sql,int maxrow, int fetchsize,int timeout, String name,String template,boolean createUpdateData, boolean allowToCachePreperadeStatement) throws PageException {
		this.name=name;
		this.template=template;
        this.sql=sql;
		TimeZone tz = ThreadLocalPageContext.getTimeZone(pc);
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

        
		//Stopwatch stopwatch=new Stopwatch();
        long start=System.nanoTime();
		//stopwatch.start();
		boolean hasResult=false;
		//boolean closeStatement=true;
		try {	
			SQLItem[] items=sql.getItems();
			if(items.length==0) {
		    	stat=dc.getConnection().createStatement();
		        setAttributes(stat,maxrow,fetchsize,timeout);
		     // some driver do not support second argument
		        //hasResult=createGeneratedKeys?stat.execute(sql.getSQLString(),Statement.RETURN_GENERATED_KEYS):stat.execute(sql.getSQLString());
		        hasResult=QueryUtil.execute(pc,stat,createGeneratedKeys,sql);
	        }
	        else {
	        	// some driver do not support second argument
	        	PreparedStatement preStat = dc.getPreparedStatement(sql, createGeneratedKeys,allowToCachePreperadeStatement);
	        	//closeStatement=false;
	        	stat=preStat;
	            setAttributes(preStat,maxrow,fetchsize,timeout);
	            setItems(ThreadLocalPageContext.getTimeZone(pc),preStat,items);
		        hasResult=QueryUtil.execute(pc,preStat);    
	        }
			int uc;
			ResultSet res;
			do {
				if(hasResult) {
					res=stat.getResultSet();
					if(fillResult(dc,res, maxrow, true,createGeneratedKeys,tz))break;
				}
				else if((uc=setUpdateCount(stat))!=-1){
					if(uc>0 && createGeneratedKeys)setGeneratedKeys(dc, stat,tz);
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
        	//if(closeStatement)
        		DBUtil.closeEL(stat);
        }  
		exeTime=System.nanoTime()-start;

		if(columncount==0) {
			if(columnNames==null) columnNames=new Collection.Key[0];
			if(columns==null) columns=new QueryColumnImpl[0];
		}
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
	
	private boolean setGeneratedKeys(DatasourceConnection dc,Statement stat, TimeZone tz)  {
		try{
			ResultSet rs = stat.getGeneratedKeys();
			setGeneratedKeys(dc, rs,tz);
			return true;
		}
		catch(Throwable t) {t.printStackTrace();
			return false;
		}
	}
	
	private void setGeneratedKeys(DatasourceConnection dc,ResultSet rs, TimeZone tz) throws PageException  {
		generatedKeys=new QueryImpl(rs,"",tz);
		
		// ACF compatibility action 
		if(generatedKeys.getColumnCount()==1 && DataSourceUtil.isMSSQL(dc)) {
			generatedKeys.renameEL(GENERATED_KEYS,KeyConstants._IDENTITYCOL);
			generatedKeys.renameEL(GENERATEDKEYS,KeyConstants._IDENTITYCOL);
			generatedKeys.renameEL(KeyConstants._ID,KeyConstants._IDENTITYCOL);
		}
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


	private void setItems(TimeZone tz,PreparedStatement preStat, SQLItem[] items) throws DatabaseException, PageException, SQLException {
		for(int i=0;i<items.length;i++) {
            SQLCaster.setValue(tz,preStat,i+1,items[i]);
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

    private boolean fillResult(DatasourceConnection dc, ResultSet result, int maxrow, boolean closeResult,boolean createGeneratedKeys, TimeZone tz) throws SQLException, IOException, PageException {
    	if(result==null) return false;
    	try {
	    	recordcount=0;
			ResultSetMetaData meta = result.getMetaData();
			columncount=meta.getColumnCount();
			
		// set header arrays
			Collection.Key[] tmpColumnNames = new Collection.Key[columncount];
			int count=0;
			Collection.Key key;
			String columnName;
			for(int i=0;i<columncount;i++) {
				columnName=QueryUtil.getColumnName(meta,i+1);
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
			columns=new QueryColumnImpl[columncount];
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
	            else if(types[i]==Types.BIGINT)	casts[i]=Cast.BIGINT;
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
				setGeneratedKeys(dc, result,tz);
				return false;
			}
			
	
		// fill data
			//Object o;
			while(result.next()) {
				if(maxrow>-1 && recordcount>=maxrow) {
					break;
				}
				for(int i=0;i<usedColumns.length;i++) {
				    columns[i].add(casts[i].toCFType(tz,types[i], result, usedColumns[i]+1));
				}
				++recordcount;
			}
    	}
    	finally {
    		if(closeResult)IOUtil.closeEL(result);
    	}
		
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
	 * @deprecated use instead <code>QueryImpl(Collection.Key[] columnKeys, int rowNumber,String name)</code>
	 */
	public QueryImpl(String[] strColumns, int rowNumber,String name) {
        this.name=name;
        columncount=strColumns.length;
		recordcount=rowNumber;
		columnNames=new Collection.Key[columncount];
		columns=new QueryColumnImpl[columncount];
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
	public QueryImpl(Collection.Key[] columnKeys, int rowNumber,String name) throws DatabaseException {
		this.name=name;
        columncount=columnKeys.length;
		recordcount=rowNumber;
		columnNames=new Collection.Key[columncount];
		columns=new QueryColumnImpl[columncount];
		for(int i=0;i<columnKeys.length;i++) {
			columnNames[i]=columnKeys[i];
			columns[i]=new QueryColumnImpl(this,columnNames[i],Types.OTHER,recordcount);
		}
		validateColumnNames(columnNames);
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
		columns=new QueryColumnImpl[columncount];
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
		columns=new QueryColumnImpl[columncount];
		for(int i=0;i<columnNames.length;i++) {
			columns[i]=new QueryColumnImpl(this,columnNames[i],SQLCaster.toIntType(strTypes[i]),recordcount);
		}
		validateColumnNames(columnNames);
	}
	
	/**
	 * constructor of the class, to generate a empty resultset (no database execution)
	 * @param arrColumns columns for the resultset
	 * @param rowNumber count of rows to generate (empty fields)
	 * @param name 
	 * @throws DatabaseException 
	 */
	public QueryImpl(Array arrColumns, int rowNumber, String name) throws DatabaseException {
        this.name=name;
        columncount=arrColumns.size();
		recordcount=rowNumber;
		columnNames=new Collection.Key[columncount];
		columns=new QueryColumnImpl[columncount];
		for(int i=0;i<columncount;i++) {
			columnNames[i]=KeyImpl.init(arrColumns.get(i+1,"").toString().trim());
			columns[i]=new QueryColumnImpl(this,columnNames[i],Types.OTHER,recordcount);
		}
		validateColumnNames(columnNames);
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
		columns=new QueryColumnImpl[columncount];
		for(int i=0;i<columncount;i++) {
			columnNames[i]=KeyImpl.init(arrColumns.get(i+1,"").toString().trim());
			columns[i]=new QueryColumnImpl(this,columnNames[i],SQLCaster.toIntType(Caster.toString(arrTypes.get(i+1,""))),recordcount);
		}
		validateColumnNames(columnNames);
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
	
	private static void validateColumnNames(Key[] columnNames) throws DatabaseException {
		Set<String> testMap=new HashSet<String>();
		for(int i=0	;i<columnNames.length;i++) {
			
			// Only allow column names that are valid variable name
			//if(!Decision.isSimpleVariableName(columnNames[i]))
			//	throw new DatabaseException("invalid column name ["+columnNames[i]+"] for query", "column names must start with a letter and can be followed by letters numbers and underscores [_]. RegExp:[a-zA-Z][a-zA-Z0-9_]*",null,null,null);
			
			if(testMap.contains(columnNames[i].getLowerString()))
				throw new DatabaseException("invalid parameter for query, ambiguous column name "+columnNames[i],"columnNames: "+ListUtil.arrayToListTrim( _toStringKeys(columnNames),","),null,null);
			testMap.add(columnNames[i].getLowerString());
		}
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
			throw new DatabaseException("invalid parameter for query, not the same count from names and columns","names:"+columnNames.length+";columns:"+arrColumns.length,null,null);
		int len=0;
		columns=new QueryColumnImpl[arrColumns.length];
		if(arrColumns.length>0) {
		// test columns
			len=arrColumns[0].size();
			for(int i=0;i<arrColumns.length;i++) {
				if(arrColumns[i].size()!=len)
					throw new DatabaseException("invalid parameter for query, all columns must have the same size","column[1]:"+len+"<>column["+(i+1)+"]:"+arrColumns[i].size(),null,null);
				//columns[i]=new QueryColumnTypeFlex(arrColumns[i]);
				columns[i]=new QueryColumnImpl(this,columnNames[i],arrColumns[i],Types.OTHER);
			}
		// test keys
			validateColumnNames(columnNames);
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
     * @throws DatabaseException 
     */
    public QueryImpl(String[] strColumnList, Object[][] data,String name) throws DatabaseException {
    	
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

	@Override
	public int size() {
		return columncount;
	}

	@Override
	public Collection.Key[] keys() {
		return columnNames;
	}

	@Override
	public Object removeEL(Collection.Key key) {
		return setEL(key,null);
	}

	@Override
	public Object remove(Collection.Key key) throws PageException {
		return set(key,null);
	}

	@Override
	public void clear() {
		for(int i=0;i<columns.length;i++) {
			columns[i].clear();
        }
        recordcount=0;
	}

	@Override
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

	@Override
	public Object get(Collection.Key key, Object defaultValue) {
		return getAt(key,
				arrCurrentRow.get(getPid(), 1),
				defaultValue);
	}

	@Override
	public Object get(String key) throws PageException {
		return getAt(key,
				arrCurrentRow.get(getPid(), 1)
				);
	}

	@Override
	public Object get(Collection.Key key) throws PageException {
		return getAt(key,
				arrCurrentRow.get(getPid(), 1)
				);
	}

	@Override
	public Object getAt(String key, int row, Object defaultValue) {
		return getAt(KeyImpl.init(key), row, defaultValue);
	}

	public Object getAt(Collection.Key key, int row, Object defaultValue) {
		int index=getIndexFromKey(key);
		if(index!=-1) {
			return columns[index].get(row,defaultValue);
		}
		if(key.length()>=10) {
	        if(key.equals(KeyConstants._RECORDCOUNT)) return new Double(getRecordcount());
	        if(key.equals(KeyConstants._CURRENTROW)) return new Double(row);
	        if(key.equals(KeyConstants._COLUMNLIST)) return getColumnlist(true);
		}
        return defaultValue;
	}
	
	@Override
	public Object getAt(String key, int row) throws PageException {
		return getAt(KeyImpl.init(key), row);
	}

	@Override
	public Object getAt(Collection.Key key, int row) throws PageException {
		int index=getIndexFromKey(key);
		if(index!=-1) {
			return columns[index].get(row, NullSupportHelper.empty());
		}
		if(key.length()>=10) {
        	if(key.equals(KeyConstants._RECORDCOUNT)) return new Double(getRecordcount());
        	if(key.equals(KeyConstants._CURRENTROW)) return new Double(row);
			if(key.equals(KeyConstants._COLUMNLIST)) return getColumnlist(true);
        }
		throw new DatabaseException("column ["+key+"] not found in query, columns are ["+getColumnlist(false)+"]",null,sql,null);
	}
	

    @Override
    public synchronized int removeRow(int row) throws PageException {
        //disconnectCache();
        
        for(int i=0;i<columns.length;i++) {
            columns[i].removeRow(row);
        }
        return --recordcount;
    }

    @Override
    public int removeRowEL(int row) {
        //disconnectCache();
        
        try {
            return removeRow(row);
        } catch (PageException e) {
            return recordcount;
        }
    }
    
    @Override
    public QueryColumn removeColumn(String key) throws DatabaseException {
        return removeColumn(KeyImpl.init(key));
    }

	@Override
	public QueryColumn removeColumn(Collection.Key key) throws DatabaseException {
		//disconnectCache();
        
        QueryColumn removed = removeColumnEL(key);
        if(removed==null) {
            if(key.equals(KeyConstants._RECORDCOUNT) || 
            		key.equals(KeyConstants._CURRENTROW) || 
            		key.equals(KeyConstants._COLUMNLIST))
                throw new DatabaseException("can't remove "+key+" this is not a row","existing rows are ["+getColumnlist(false)+"]",null,null);
            throw new DatabaseException("can't remove row ["+key+"], this row doesn't exist",
                    "existing rows are ["+getColumnlist(false)+"]",null,null);
        }
        return removed;
	}

    @Override
    public synchronized QueryColumn removeColumnEL(String key) {
        return removeColumnEL(KeyImpl.init(key));
    }

	public QueryColumn removeColumnEL(Collection.Key key) {
		//disconnectCache();
        
        int index=getIndexFromKey(key);
        if(index!=-1) {
            int current=0;
            QueryColumn removed=null;
            Collection.Key[] newColumnNames=new Collection.Key[columnNames.length-1];
            QueryColumnImpl[] newColumns=new QueryColumnImpl[columns.length-1];
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

	@Override
	public Object setEL(String key, Object value) {	
		return setEL(KeyImpl.init(key),value);	
	}

	@Override
	public Object setEL(Collection.Key key, Object value) {
		return setAtEL(key,
				arrCurrentRow.get(getPid(), 1),
				value);
	}
	
	@Override
	public Object set(String key, Object value) throws PageException {
		return set(KeyImpl.init(key),value);
	}

	@Override
	public Object set(Collection.Key key, Object value) throws PageException {
		return setAt(key,
				arrCurrentRow.get(getPid(), 1),
				value);
	}
    
    @Override
    public Object setAt(String key,int row, Object value) throws PageException {
    	return setAt(KeyImpl.init(key), row, value);
    }

    public Object setAt(Collection.Key key, int row, Object value) throws PageException {
		int index=getIndexFromKey(key);
        if(index!=-1) {
            return columns[index].set(row,value);
        }
        throw new DatabaseException("column ["+key+"] does not exist","columns are ["+getColumnlist(false)+"]",sql,null);
	}
    
    @Override
    public Object setAtEL(String key,int row, Object value) {
        return setAtEL(KeyImpl.init(key), row, value);
    }

	public Object setAtEL(Collection.Key key, int row, Object value) {
		int index=getIndexFromKey(key);
        if(index!=-1) {
            return columns[index].setEL(row,value);
        }
        return null;
	}

	@Override
	public boolean next() {
		return next(getPid());
	}

	@Override
	public boolean next(int pid) {
		if(recordcount>=(arrCurrentRow.set(pid,arrCurrentRow.get(pid,0)+1))) {
			return true;
		}
		arrCurrentRow.set(pid,0);
		return false;
	}

	@Override
	public void reset() {
		reset(getPid());
	}
	
	public void reset(int pid) {
		arrCurrentRow.set(pid,0);
	}

	@Override
	public int getRecordcount() {
		return recordcount;
	}
	
	@Override
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
			sb.append(upperCase?columnNames[i].getUpperString():columnNames[i].getString());
		}
		return sb.toString();
	}
	public String getColumnlist() {
		return getColumnlist(true);
	}

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

	@Override
	public boolean isEmpty() {
		return recordcount+columncount==0;
	}

	@Override
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

	@Override
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
	
	@Override
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
			QueryColumnImpl newCol=new QueryColumnImpl(this,columnNames[i],columns[i].getType(),len);
			for(int y=1;y<=len;y++) {
				newCol.set(y,column.get(arr[y-1].getOldPosition()+1,null));
			}
			columns[i]=newCol;
		}
	}

	@Override
	 public synchronized boolean addRow(int count) {		
         //disconnectCache();
            
         for(int i=0;i<columns.length;i++) {
        	 QueryColumnPro column = columns[i];
		 	column.addRow(count);
		 }
		 recordcount+=count;
		 return true;
	 }
	 
	 @Override
	public boolean addColumn(String columnName, Array content) throws DatabaseException {
		return addColumn(columnName,content,Types.OTHER);
	 }

	public boolean addColumn(Collection.Key columnName, Array content) throws PageException {
		return addColumn(columnName,content,Types.OTHER);
	}
	
    @Override
    public synchronized boolean addColumn(String columnName, Array content, int type) throws DatabaseException {
		return addColumn(KeyImpl.init(columnName.trim()), content, type);
    }

	@Override
	public boolean addColumn(Collection.Key columnName, Array content, int type) throws DatabaseException {
		//disconnectCache();
        // TODO Meta type
		content=(Array) Duplicator.duplicate(content,false);
		
	 	if(getIndexFromKey(columnName)!=-1)
	 		throw new DatabaseException("column name ["+columnName.getString()+"] already exist",null,sql,null);
	 	if(content.size()!=getRecordcount()) {
	 		//throw new DatabaseException("array for the new column has not the same size like the query (arrayLen!=query.recordcount)");
	 		if(content.size()>getRecordcount()) addRow(content.size()-getRecordcount());
	 		else content.setEL(getRecordcount(),"");
	 	}
	 	QueryColumnImpl[] newColumns=new QueryColumnImpl[columns.length+1];
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

	
	@Override
    public Object clone() {
        return cloneQuery(true);
    }
    
    @Override
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
		        newResult.columns=new QueryColumnImpl[columnNames.length];
		        for(int i=0;i<columnNames.length;i++) {
		        	newResult.columnNames[i]=columnNames[i];
		        	newResult.columns[i]=columns[i].cloneColumnImpl(deepCopy);
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
        	// ThreadLocalDuplication.remove(this); removed "remove" to catch sisters and brothers
        }
    }

	@Override
	public synchronized int[] getTypes() {
		int[] types=new int[columns.length];
		for(int i=0;i<columns.length;i++) {
		    types[i]=columns[i].getType();
		}
		return types;
	}
	
	@Override
	public synchronized Map<Collection.Key,String> getTypesAsMap() {
		
		Map<Collection.Key,String> map=new HashMap<Collection.Key,String>();
		for(int i=0;i<columns.length;i++) {
			map.put(columnNames[i],columns[i].getTypeAsString());
		}
		return map;
	}

	@Override
	public QueryColumn getColumn(String key) throws DatabaseException {
		return getColumn(KeyImpl.init(key.trim()));
	}

	@Override
	public QueryColumn getColumn(Collection.Key key) throws DatabaseException {
		int index=getIndexFromKey(key);
		if(index!=-1) return columns[index];
        
		if(key.length()>=10) {
        	if(key.equals(KeyConstants._RECORDCOUNT)) return new QueryColumnRef(this,key,Types.INTEGER);
	        if(key.equals(KeyConstants._CURRENTROW)) return new QueryColumnRef(this,key,Types.INTEGER);
	        if(key.equals(KeyConstants._COLUMNLIST)) return new QueryColumnRef(this,key,Types.INTEGER);
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
	

	@Override
	public QueryColumn getColumn(String key, QueryColumn defaultValue) {
		return getColumn(KeyImpl.init(key.trim()),defaultValue);
	}

	@Override
	public QueryColumn getColumn(Collection.Key key, QueryColumn defaultValue) {
        int index=getIndexFromKey(key);
		if(index!=-1) return columns[index];
        if(key.length()>=10) {
        	if(key.equals(KeyConstants._RECORDCOUNT)) return new QueryColumnRef(this,key,Types.INTEGER);
	        if(key.equals(KeyConstants._CURRENTROW)) return new QueryColumnRef(this,key,Types.INTEGER);
	        if(key.equals(KeyConstants._COLUMNLIST)) return new QueryColumnRef(this,key,Types.INTEGER);
        }
        return defaultValue;
	}
	
	@Override
	public String toString() {
		Collection.Key[] keys=keys();
		
		StringBuffer sb=new StringBuffer();

		sb.append("Query\n");
		sb.append("---------------------------------------------------\n");
		
		if(sql!=null) {
			sb.append(sql+"\n");
			sb.append("---------------------------------------------------\n");
		}

		if(exeTime>0)	{
			sb.append("Execution Time (ns): "+exeTime+"\n");
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
			sb.append(getToStringField(keys[i].getString()));
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
			case Types.SQLXML: return "SQLXML";
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
	
	@Override
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

    @Override
    public void setCached(boolean isCached) {
        this.isCached=isCached; 
    }

    @Override
    public boolean isCached() {
        return isCached;
    }



    @Override
    public int addRow() {
		addRow(1);
		return getRecordcount();
    }


    public Key getColumnName(int columnIndex) {
    	Key[] keys = keys();
		if(columnIndex<1 || columnIndex>keys.length) return null;
		return keys[columnIndex-1];
    }

    @Override
    public int getColumnIndex(String coulmnName) {
        Collection.Key[] keys = keys();
		for(int i=0;i<keys.length;i++) {
			if(keys[i].getString().equalsIgnoreCase(coulmnName)) return i+1;
		}
		return -1;
    }



    @Override
    public String[] getColumns() {
        return getColumnNamesAsString();
    }
    
    @Override
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
    		QueryColumnImpl[] tmp=new QueryColumnImpl[trg.length];
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
    


	@Override
	public String[] getColumnNamesAsString() {
		return CollectionUtil.keysAsString(this);
	}
	
	public int getColumnCount() {
		return columncount;
	}

    @Override
    public String getData(int row, int col) throws IndexOutOfBoundsException {
        Collection.Key[] keys = keys();
		if(col<1 || col>keys.length) {
			new IndexOutOfBoundsException("invalid column index to retrieve Data from query, valid index goes from 1 to "+keys.length);
		}
		
		Object o=getAt(keys[col-1],row,NullSupportHelper.NULL());
		if(o==NullSupportHelper.NULL())
			throw new IndexOutOfBoundsException("invalid row index to retrieve Data from query, valid index goes from 1 to "+getRecordcount());
		return Caster.toString( o,NullSupportHelper.full()?null:"");
    }



    @Override
    public String getName() {
        return this.name;
    }



    @Override
    public int getRowCount() {
        return getRecordcount();
    }



    @Override
    public void setData(int row, int col, String value) throws IndexOutOfBoundsException {
        Collection.Key[] keys = keys();
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

    @Override
    public boolean containsKey(String key) {
        return getColumn(key,null)!=null;
    }	

	@Override
	public boolean containsKey(Collection.Key key) {
        return getColumn(key,null)!=null;
	}
	
    @Override
    public String castToString() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Query to String",
          "Use Built-In-Function \"serialize(Query):String\" to create a String from Query");
    }

	@Override
	public String castToString(String defaultValue) {
		return defaultValue;
	}


    @Override
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Query to a boolean value");
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }


    @Override
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Query to a number value");
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }


    @Override
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Query to a Date");
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	@Override
	public int compareTo(boolean b) throws ExpressionException {
		throw new ExpressionException("can't compare Complex Object Type Query with a boolean value");
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Query with a DateTime Object");
	}

	@Override
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Query with a numeric value");
	}

	@Override
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Query with a String");
	}

    public synchronized Array getMetaDataSimple() {
    	Array cols=new ArrayImpl();
    	Struct column;
        for(int i=0;i<columns.length;i++) {
        	column=new StructImpl();
        	column.setEL(KeyConstants._name,columnNames[i].getString());
        	column.setEL("isCaseSensitive",Boolean.FALSE);
        	column.setEL("typeName",columns[i].getTypeAsString());
        	cols.appendEL(column);
        }
        return cols;
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


	@Override
	public Object getObject(String columnName) throws SQLException {
		int currentrow;
		if((currentrow=arrCurrentRow.get(getPid(),0))==0) return null;
		return getAt(columnName,currentrow,null);
	}
	
	@Override
	public Object getObject(int columnIndex) throws SQLException {
		if(columnIndex>0 && columnIndex<=columncount) return  getObject(this.columnNames[columnIndex-1].getString());
		return null;
	}
	
	@Override
	public String getString(int columnIndex) throws SQLException {
		Object rtn = getObject(columnIndex);
		if(rtn==null)return null;
		if(Decision.isCastableToString(rtn)) return Caster.toString(rtn,null);
		throw new SQLException("can't cast value to string");
	}
	
	@Override
	public String getString(String columnName) throws SQLException {
		Object rtn = getObject(columnName);
		if(rtn==null)return null;
		if(Decision.isCastableToString(rtn)) return Caster.toString(rtn,null);
		throw new SQLException("can't cast value to string");
	}
	
	@Override
	public boolean getBoolean(int columnIndex) throws SQLException {
		Object rtn = getObject(columnIndex);
		if(rtn==null)return false;
		if(Decision.isCastableToBoolean(rtn)) return Caster.toBooleanValue(rtn,false);
		throw new SQLException("can't cast value to boolean");
	}
	
	@Override
	public boolean getBoolean(String columnName) throws SQLException {
		Object rtn = getObject(columnName);
		if(rtn==null)return false;
		if(Decision.isCastableToBoolean(rtn)) return Caster.toBooleanValue(rtn,false);
		throw new SQLException("can't cast value to boolean");
	}
	
	
	// ---------------------------------------

	@Override
	public Object call(PageContext pc, Key methodName, Object[] arguments) throws PageException {
		return MemberUtil.call(pc, this, methodName, arguments, railo.commons.lang.CFTypes.TYPE_QUERY, "query");
		//return Reflector.callMethod(this,methodName,arguments);
	}

	@Override
	public Object callWithNamedValues(PageContext pc, Key methodName, Struct args) throws PageException {	
		return MemberUtil.callWithNamedValues(pc, this, methodName, args,railo.commons.lang.CFTypes.TYPE_QUERY, "query");
	}

	@Override
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return getAt(key,arrCurrentRow.get(
				pc.getId(),1),defaultValue);
	}

	@Override
	public Object get(PageContext pc, Key key) throws PageException {
		return getAt(key,arrCurrentRow.get(pc.getId(),1));
	}

	public boolean isInitalized() {
		return true;
	}

	@Override
	public Object set(PageContext pc, Key propertyName, Object value) throws PageException {
		return setAt(propertyName,arrCurrentRow.get(pc.getId(),1),value);
	}

	@Override
	public Object setEL(PageContext pc, Key propertyName, Object value) {
		return setAtEL(propertyName,arrCurrentRow.get(pc.getId(),1),value);
	}
	
	@Override
	public boolean wasNull() {
		throw new PageRuntimeException(new ApplicationException("method [wasNull] is not supported"));
	}

	@Override
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

	@Override
	public void afterLast() throws SQLException {
		arrCurrentRow.set(getPid(),recordcount+1);
	}

	@Override
	public void beforeFirst() throws SQLException {
		arrCurrentRow.set(getPid(),0);
	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		// ignored
	}

	@Override
	public void clearWarnings() throws SQLException {
		// ignored
	}

	@Override
	public void close() throws SQLException {
		// ignored
	}

	@Override
	public void deleteRow() throws SQLException {
		try {
			removeRow(arrCurrentRow.get(getPid()));
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public int findColumn(String columnName) throws SQLException {
		int index= getColumnIndex(columnName);
		if(index==-1) throw new SQLException("invald column definitions ["+columnName+"]");
		return index;
	}

	@Override
	public boolean first() throws SQLException {
		return absolute(1);
	}

	public java.sql.Array getArray(int i) throws SQLException {
		throw new SQLException("method is not implemented");
	}

	public java.sql.Array getArray(String colName) throws SQLException {
		throw new SQLException("method is not implemented");
	}

	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		String res = getString(columnIndex);
		if(res==null)return null;
		return new ByteArrayInputStream(res.getBytes());
	}

	@Override
	public InputStream getAsciiStream(String columnName) throws SQLException {
		String res = getString(columnName);
		if(res==null)return null;
		return new ByteArrayInputStream(res.getBytes());
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return new BigDecimal(getDouble(columnIndex));
	}

	@Override
	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		return new BigDecimal(getDouble(columnName));
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return new BigDecimal(getDouble(columnIndex));
	}

	@Override
	public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
		return new BigDecimal(getDouble(columnName));
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		Object obj = getObject(columnIndex);
		if(obj==null)return null;
		try {
			return Caster.toInputStream(obj,(Charset)null);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public InputStream getBinaryStream(String columnName) throws SQLException {
		Object obj = getObject(columnName);
		if(obj==null)return null;
		try {
			return Caster.toInputStream(obj,(Charset)null);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
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

	@Override
	public Blob getBlob(String colName) throws SQLException {
		byte[] bytes = getBytes(colName);
		if(bytes==null) return null;
		try {
			return BlobImpl.toBlob(bytes);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {
		Object obj = getObject(columnIndex);
		if(obj==null) return (byte)0;
		try {
			return Caster.toByteValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public byte getByte(String columnName) throws SQLException {
		Object obj = getObject(columnName);
		if(obj==null) return (byte)0;
		try {
			return Caster.toByteValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		Object obj = getObject(columnIndex);
		if(obj==null) return null;
		try {
			return Caster.toBytes(obj,(Charset)null);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public byte[] getBytes(String columnName) throws SQLException {
		Object obj = getObject(columnName);
		if(obj==null) return null;
		try {
			return Caster.toBytes(obj,(Charset)null);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		String str=getString(columnIndex);
		if(str==null) return null;
		return new StringReader(str);
	}

	@Override
	public Reader getCharacterStream(String columnName) throws SQLException {
		String str=getString(columnName);
		if(str==null) return null;
		return new StringReader(str);
	}

	@Override
	public Clob getClob(int i) throws SQLException {
		String str=getString(i);
		if(str==null) return null;
		return ClobImpl.toClob(str);
	}

	@Override
	public Clob getClob(String colName) throws SQLException {
		String str=getString(colName);
		if(str==null) return null;
		return ClobImpl.toClob(str);
	}

	@Override
	public int getConcurrency() throws SQLException {
		return 0;
	}

	@Override
	public String getCursorName() throws SQLException {
		return null;
	}

	@Override
	public java.sql.Date getDate(int columnIndex) throws SQLException {
		Object obj=getObject(columnIndex);
		if(obj==null) return null;
		try {
			return new java.sql.Date(Caster.toDate(obj, false, null).getTime());
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public java.sql.Date getDate(String columnName) throws SQLException {
		Object obj=getObject(columnName);
		if(obj==null) return null;
		try {
			return new java.sql.Date(Caster.toDate(obj, false, null).getTime());
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public java.sql.Date getDate(int columnIndex, Calendar cal)throws SQLException {
		return getDate(columnIndex); // TODO impl
	}

	@Override
	public java.sql.Date getDate(String columnName, Calendar cal) throws SQLException {
		return getDate(columnName);// TODO impl
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		Object obj=getObject(columnIndex);
		if(obj==null) return 0;
		try {
			return Caster.toDoubleValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public double getDouble(String columnName) throws SQLException {
		Object obj=getObject(columnName);
		if(obj==null) return 0;
		try {
			return Caster.toDoubleValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return 1000;
	}

	@Override
	public int getFetchSize() throws SQLException {
		return 0;
	}

	@Override
	public float getFloat(int columnIndex) throws SQLException {
		Object obj=getObject(columnIndex);
		if(obj==null) return 0;
		try {
			return Caster.toFloatValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public float getFloat(String columnName) throws SQLException {
		Object obj=getObject(columnName);
		if(obj==null) return 0;
		try {
			return Caster.toFloatValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public int getInt(int columnIndex) throws SQLException {
		Object obj=getObject(columnIndex);
		if(obj==null) return 0;
		try {
			return Caster.toIntValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public int getInt(String columnName) throws SQLException {
		Object obj=getObject(columnName);
		if(obj==null) return 0;
		try {
			return Caster.toIntValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public long getLong(int columnIndex) throws SQLException {
		Object obj=getObject(columnIndex);
		if(obj==null) return 0;
		try {
			return Caster.toLongValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public long getLong(String columnName) throws SQLException {
		Object obj=getObject(columnName);
		if(obj==null) return 0;
		try {
			return Caster.toLongValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public Object getObject(int i, Map map) throws SQLException {
		throw new SQLException("method is not implemented");
	}

	@Override
	public Object getObject(String colName, Map map) throws SQLException {
		throw new SQLException("method is not implemented");
	}

	// used only with java 7, do not set @Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		return (T) QueryUtil.getObject(this,columnIndex, type);
	}

	// used only with java 7, do not set @Override
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		return (T) QueryUtil.getObject(this,columnLabel, type);
	}

	@Override
	public Ref getRef(int i) throws SQLException {
		throw new SQLException("method is not implemented");
	}

	@Override
	public Ref getRef(String colName) throws SQLException {
		throw new SQLException("method is not implemented");
	}

	@Override
	public int getRow() throws SQLException {
		return arrCurrentRow.get(getPid(),0);
	}

	@Override
	public short getShort(int columnIndex) throws SQLException {
		Object obj=getObject(columnIndex);
		if(obj==null) return 0;
		try {
			return Caster.toShortValue(obj);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
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

	@Override
	public Time getTime(int columnIndex) throws SQLException {
		Object obj=getObject(columnIndex);
		if(obj==null) return null;
		try {
			return new Time(DateCaster.toTime(null, obj).getTime());
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public Time getTime(String columnName) throws SQLException {
		Object obj=getObject(columnName);
		if(obj==null) return null;
		try {
			return new Time(DateCaster.toTime(null, obj).getTime());
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return getTime(columnIndex);// TODO impl
	}

	@Override
	public Time getTime(String columnName, Calendar cal) throws SQLException {
		return getTime(columnName);// TODO impl
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		Object obj=getObject(columnIndex);
		if(obj==null) return null;
		try {
			return new Timestamp(DateCaster.toTime(null, obj).getTime());
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public Timestamp getTimestamp(String columnName) throws SQLException {
		Object obj=getObject(columnName);
		if(obj==null) return null;
		try {
			return new Timestamp(DateCaster.toTime(null, obj).getTime());
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return getTimestamp(columnIndex);// TODO impl
	}

	@Override
	public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
		return getTimestamp(columnName);// TODO impl
	}

	@Override
	public int getType() throws SQLException {
		return 0;
	}

	@Override
	public URL getURL(int columnIndex) throws SQLException {
		throw new SQLException("method is not implemented");
	}

	@Override
	public URL getURL(String columnName) throws SQLException {
		throw new SQLException("method is not implemented");
	}

	@Override
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

	@Override
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

	@Override
	public SQLWarning getWarnings() throws SQLException {
		throw new SQLException("method is not implemented");
	}

	@Override
	public void insertRow() throws SQLException {
		throw new SQLException("method is not implemented");
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		return getCurrentrow(ThreadLocalPageContext.get().getId())>recordcount;
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		return arrCurrentRow.get(getPid(),0)==0;
	}

	@Override
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

	@Override
	public boolean relative(int rows) throws SQLException {
		return absolute(getRow()+rows);
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		return false;
	}

	@Override
	public boolean rowInserted() throws SQLException {
		return false;
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		return false;
	}

	public void setFetchDirection(int direction) throws SQLException {
		// ignore
	}

	public void setFetchSize(int rows) throws SQLException {
		// ignore
	}

	@Override
	public void updateArray(int columnIndex, java.sql.Array x)throws SQLException {
		updateObject(columnIndex, x.getArray());
	}

	@Override
	public void updateArray(String columnName, java.sql.Array x)throws SQLException {
		updateObject(columnName, x.getArray());
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, int length)throws SQLException {
		updateBinaryStream(columnIndex, x, length);
	}

	@Override
	public void updateAsciiStream(String columnName, InputStream x, int length)throws SQLException {
		updateBinaryStream(columnName, x, length);
	}

	@Override
	public void updateBigDecimal(int columnIndex, BigDecimal x)throws SQLException {
		updateObject(columnIndex, x.toString());
	}

	@Override
	public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
		updateObject(columnName, x.toString());
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		try {
			updateObject(columnIndex, IOUtil.toBytesMax(x, length));
		} catch (IOException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
		try {
			updateObject(columnName, IOUtil.toBytesMax(x, length));
		} catch (IOException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		try {
			updateObject(columnIndex, toBytes(x));
		} catch (IOException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public void updateBlob(String columnName, Blob x) throws SQLException {
		try {
			updateObject(columnName, toBytes(x));
		} catch (IOException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		updateObject(columnIndex, Caster.toBoolean(x));
	}

	@Override
	public void updateBoolean(String columnName, boolean x) throws SQLException {
		updateObject(columnName, Caster.toBoolean(x));
	}

	@Override
	public void updateByte(int columnIndex, byte x) throws SQLException {
		updateObject(columnIndex, new Byte(x));
	}

	@Override
	public void updateByte(String columnName, byte x) throws SQLException {
		updateObject(columnName, new Byte(x));
	}

	@Override
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		updateObject(columnIndex, x);
	}

	@Override
	public void updateBytes(String columnName, byte[] x) throws SQLException {
		updateObject(columnName, x);
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader reader, int length)throws SQLException {
		try {
			updateObject(columnIndex, IOUtil.toString(reader));
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public void updateCharacterStream(String columnName, Reader reader,int length) throws SQLException {
		try {
			updateObject(columnName, IOUtil.toString(reader));
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		try {
			updateObject(columnIndex, toString(x));
		} catch (IOException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public void updateClob(String columnName, Clob x) throws SQLException {
		try {
			updateObject(columnName, toString(x));
		} catch (IOException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public void updateDate(int columnIndex, java.sql.Date x)throws SQLException {
		updateObject(columnIndex, Caster.toDate(x, false, null, null));
	}

	@Override
	public void updateDate(String columnName, java.sql.Date x)throws SQLException {
		updateObject(columnName, Caster.toDate(x, false, null, null));
	}

	@Override
	public void updateDouble(int columnIndex, double x) throws SQLException {
		updateObject(columnIndex, Caster.toDouble(x));
	}

	@Override
	public void updateDouble(String columnName, double x) throws SQLException {
		updateObject(columnName, Caster.toDouble(x));
	}

	@Override
	public void updateFloat(int columnIndex, float x) throws SQLException {
		updateObject(columnIndex, Caster.toDouble(x));
	}

	@Override
	public void updateFloat(String columnName, float x) throws SQLException {
		updateObject(columnName, Caster.toDouble(x));
	}

	@Override
	public void updateInt(int columnIndex, int x) throws SQLException {
		updateObject(columnIndex, Caster.toDouble(x));
	}

	@Override
	public void updateInt(String columnName, int x) throws SQLException {
		updateObject(columnName, Caster.toDouble(x));
	}

	@Override
	public void updateLong(int columnIndex, long x) throws SQLException {
		updateObject(columnIndex, Caster.toDouble(x));
	}

	@Override
	public void updateLong(String columnName, long x) throws SQLException {
		updateObject(columnName, Caster.toDouble(x));
	}

	@Override
	public void updateNull(int columnIndex) throws SQLException {
		updateObject(columnIndex, null);
	}

	@Override
	public void updateNull(String columnName) throws SQLException {
		updateObject(columnName, null);
	}

	@Override
	public void updateObject(int columnIndex, Object x) throws SQLException {
		try {
			set(getColumnName(columnIndex), x);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public void updateObject(String columnName, Object x) throws SQLException {
		try {
			set(KeyImpl.init(columnName), x);
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public void updateObject(int columnIndex, Object x, int scale)throws SQLException {
		updateObject(columnIndex, x);
	}

	@Override
	public void updateObject(String columnName, Object x, int scale)throws SQLException {
		updateObject(columnName, x);
	}

	@Override
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		updateObject(columnIndex, x.getObject());
	}

	@Override
	public void updateRef(String columnName, Ref x) throws SQLException {
		updateObject(columnName, x.getObject());
	}

	public void updateRow() throws SQLException {
		throw new SQLException("method is not implemented");
	}

	@Override
	public void updateShort(int columnIndex, short x) throws SQLException {
		updateObject(columnIndex, Caster.toDouble(x));
	}

	@Override
	public void updateShort(String columnName, short x) throws SQLException {
		updateObject(columnName, Caster.toDouble(x));
	}

	@Override
	public void updateString(int columnIndex, String x) throws SQLException {
		updateObject(columnIndex, x);
	}

	@Override
	public void updateString(String columnName, String x) throws SQLException {
		updateObject(columnName, x);
	}

	@Override
	public void updateTime(int columnIndex, Time x) throws SQLException {
		updateObject(columnIndex, new DateTimeImpl(x.getTime(),false));
	}

	@Override
	public void updateTime(String columnName, Time x) throws SQLException {
		updateObject(columnName, new DateTimeImpl(x.getTime(),false));
	}

	@Override
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		updateObject(columnIndex, new DateTimeImpl(x.getTime(),false));
	}

	@Override
	public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
		updateObject(columnName, new DateTimeImpl(x.getTime(),false));
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		throw new SQLException("method is not implemented");
	}

	@Override
	public Iterator<Collection.Key> keyIterator() {
		return new KeyIterator(keys());
	}
    
	@Override
	public Iterator<String> keysAsStringIterator() {
    	return new StringIterator(keys());
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new EntryIterator(this, keys());
	}
	
	public Iterator<Object> valueIterator() {
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

	public synchronized void enableShowQueryUsage() {
		if(columns!=null)for(int i=0;i<columns.length;i++){
			columns[i]=columns[i]._toDebugColumn();
		}
	}

	@Override
	public long getExecutionTime() {
		return exeTime;
	}
	
	public static QueryImpl cloneQuery(Query qry,boolean deepCopy) {
        QueryImpl newResult=new QueryImpl();
        ThreadLocalDuplication.set(qry, newResult);
        try{
	        newResult.columnNames=qry.getColumnNames();
	        newResult.columns=new QueryColumnImpl[newResult.columnNames.length];
	        QueryColumn col;
	        for(int i=0;i<newResult.columnNames.length;i++) {
	        	col =  qry.getColumn(newResult.columnNames[i],null);
	        	newResult.columns[i]=QueryUtil.duplicate2QueryColumnImpl(newResult,col,deepCopy);
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
        	// ThreadLocalDuplication.remove(qry); removed "remove" to catch sisters and brothers
        }
    }
	
	@Override
	public java.util.Iterator getIterator() {
		return new ForEachQueryIterator(this, ThreadLocalPageContext.get().getId());
    }
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Collection)) return false;
		return CollectionUtil.equals(this,(Collection)obj);
	}
	
	/*@Override
	public int hashCode() {
		return CollectionUtil.hashCode(this);
	}*/
}