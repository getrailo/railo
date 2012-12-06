package railo.runtime.type.query;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import railo.commons.lang.StringUtil;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.PageContext;
import railo.runtime.db.DatasourceConnection;
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
import railo.runtime.op.Caster;
import railo.runtime.timer.Stopwatch;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.ArrayInt;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Objects;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.QueryColumnRef;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.CollectionIterator;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.ForEachQueryIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.QueryUtil;

public class SimpleQuery implements Query, ResultSet, Objects {
	
	static final Object DEFAULT_VALUE = new Object();
	private ResultSet res;
	private ResultSetMetaData meta;
	private Collection.Key[] columnNames;
	private Map<String,SimpleQueryColumn> columns=new LinkedHashMap<String, SimpleQueryColumn>();
	private int[] _types;
	
	private String name;
	private String template;
	private SQL sql;
	private int exeTime;
	private int recordcount;
	private ArrayInt arrCurrentRow=new ArrayInt();
	

	public SimpleQuery(DatasourceConnection dc,SQL sql,int maxrow, int fetchsize,int timeout, String name,String template) throws PageException {
		this.name=name;
		this.template=template;
        this.sql=sql;
		
        //ResultSet result=null;
		Statement stat=null;
		// check SQL Restrictions
		if(dc.getDatasource().hasSQLRestriction()) {
            QueryUtil.checkSQLRestriction(dc,sql);
        }
		
		Stopwatch stopwatch=new Stopwatch();
		stopwatch.start();
		boolean hasResult=false;
		try {	
			SQLItem[] items=sql.getItems();
			if(items.length==0) {
		    	stat=dc.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		        setAttributes(stat,maxrow,fetchsize,timeout);
		     // some driver do not support second argument
		        hasResult=stat.execute(sql.getSQLString());
	        }
	        else {
	        	// some driver do not support second argument
	        	PreparedStatement preStat = dc.getPreparedStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
	        	stat=preStat;
	            setAttributes(preStat,maxrow,fetchsize,timeout);
	            setItems(preStat,items);
		        hasResult=preStat.execute();    
	        }
			ResultSet res;
			
			do {
				if(hasResult) {
					res=stat.getResultSet();
					init(res);
					break;
				}
				throw new ApplicationException("Simple queries can only be used for queries returning a resultset");
			}
			while(true);
		} 
		catch (SQLException e) {
			throw new DatabaseException(e,sql,dc);
		} 
		catch (Throwable e) {
			throw Caster.toPageException(e);
		}
		exeTime=(int) stopwatch.time();
	}
	
	private void setAttributes(Statement stat,int maxrow, int fetchsize,int timeout) throws SQLException {
		if(maxrow>-1) stat.setMaxRows(maxrow);
        if(fetchsize>0)stat.setFetchSize(fetchsize);
        if(timeout>0)stat.setQueryTimeout(timeout);
	}
	private void setItems(PreparedStatement preStat, SQLItem[] items) throws DatabaseException, PageException, SQLException {
		for(int i=0;i<items.length;i++) {
            SQLCaster.setValue(preStat,i+1,items[i]);
        }
	}
	
	private void init(ResultSet res) throws SQLException{
		this.res=res;
		this.meta=res.getMetaData();
		
		// init columns
		int columncount = meta.getColumnCount();
		List<Key> tmpKeys=new ArrayList<Key>();
		//List<Integer> tmpTypes=new ArrayList<Integer>();
		//int count=0;
		Collection.Key key;
		String columnName;
		int type;
		for(int i=0;i<columncount;i++) {
			try {
				columnName=meta.getColumnName(i+1);
				type=meta.getColumnType(i+1);
			} catch (SQLException e) {
				throw toRuntimeExc(e);
			}
			if(StringUtil.isEmpty(columnName))columnName="column_"+i;
			key=KeyImpl.init(columnName);
			int index=tmpKeys.indexOf(key);
			if(index==-1) {
				//mappings.put(key.getLowerString(), Caster.toInteger(i+1));
				tmpKeys.add(key);
				//tmpTypes.add(type);
				columns.put(key.getLowerString(), new SimpleQueryColumn(this,res, key,type, i+1));
				
				//count++;
			}
			
		}
		columnNames=tmpKeys.toArray(new Key[tmpKeys.size()]);
		
		res.last();
		recordcount=res.getRow();
		res.beforeFirst();
		/*Iterator<Integer> it = tmpTypes.iterator();
		types=new int[tmpTypes.size()];
		int index=0;
		while(it.hasNext()){
			types[index++]=it.next();
		}*/
		
		
	}

	/**
	 * @see railo.runtime.type.QueryImpl#executionTime()
	 */
	
	public int executionTime() {
		return exeTime;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getUpdateCount()
	 */
	
	public int getUpdateCount() {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#size()
	 */
	public int size() {
		return columnNames.length;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#keys()
	 */
	
	public Key[] keys() {
		return columnNames;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeEL(railo.runtime.type.Collection.Key)
	 */
	
	public Object removeEL(Key key) {
		throw notSupported();
	}

	/* *
	 * @see railo.runtime.type.QueryImpl#remove(java.lang.String)
	 * /
	public synchronized Object remove (String key) {
		throw notSupported();
	}*/

	/**
	 * @see railo.runtime.type.QueryImpl#remove(railo.runtime.type.Collection.Key)
	 */
	
	public Object remove(Key key) throws PageException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#clear()
	 */
	
	public void clear() {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#get(Collection.Key, java.lang.Object)
	 */
	
	public Object get(Key key, Object defaultValue) {
		int pid = getPid();
		return getAt(key, getCurrentrow(pid),pid,defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	
	public Object get(String key, Object defaultValue) {
		return get(KeyImpl.init(key),defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#get(java.lang.String)
	 */
	
	public Object get(String key) throws PageException {
		return get(KeyImpl.init(key));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#get(railo.runtime.type.Collection.Key)
	 */
	
	public Object get(Key key) throws PageException {
		int pid = getPid();
		return getAt(key, getCurrentrow(pid),pid);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getAt(railo.runtime.type.Collection.Key, int, java.lang.Object)
	 */
	
	public Object getAt(Key key, int row, int pid, Object defaultValue) {
		char c=key.lowerCharAt(0);
    	if(c=='r') {
            if(key.equals(KeyConstants._RECORDCOUNT)) return new Double(getRecordcount());
        }
    	else if(c=='c') {
            if(key.equals(KeyConstants._CURRENTROW)) return new Double(getCurrentrow(pid));
            else if(key.equals(KeyConstants._COLUMNLIST)) return getColumnlist();
        }
        
        SimpleQueryColumn column = columns.get(key.getLowerString());
        if(column==null) return null;
		try {
			return column.get(row);
		} 
		catch (Throwable t) {
			return defaultValue;
		}
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getAt(railo.runtime.type.Collection.Key, int)
	 */
	
	public Object getAt(Key key, int row,int pid) throws PageException {
		Object res = getAt(key,row,pid,DEFAULT_VALUE);
		if(res!=DEFAULT_VALUE) return res;
		throw new DatabaseException("key ["+key+"] not found",null,null,null);
	}


	/**
	 * @see railo.runtime.type.QueryImpl#getAt(Key, int, java.lang.Object)
	 */
	
	public Object getAt(Key key, int row, Object defaultValue) {
		return getAt(key, row,getPid(),defaultValue);
	}
	
	
	public Object getAt(Key key, int row) throws PageException {
		Object res = getAt(key,row,getPid(),DEFAULT_VALUE);
		if(res!=DEFAULT_VALUE) return res;
		throw new DatabaseException("key ["+key+"] not found",null,null,null);
	}

	public Object getAt(String key, int row, Object defaultValue) {
		return getAt(KeyImpl.init(key), row,defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getAt(java.lang.String, int)
	 */
	
	public Object getAt(String key, int row) throws PageException {
		return getAt(KeyImpl.init(key), row);
	}

	
	/**
	 * @see railo.runtime.type.QueryImpl#removeRow(int)
	 */
	
	public synchronized int removeRow(int row) throws PageException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeRowEL(int)
	 */
	
	public int removeRowEL(int row) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeColumn(java.lang.String)
	 */
	
	public QueryColumn removeColumn(String key) throws DatabaseException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeColumn(railo.runtime.type.Collection.Key)
	 */
	
	public QueryColumn removeColumn(Key key) throws DatabaseException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeColumnEL(java.lang.String)
	 */
	
	public synchronized QueryColumn removeColumnEL(String key) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeColumnEL(railo.runtime.type.Collection.Key)
	 */
	
	public QueryColumn removeColumnEL(Key key) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setEL(java.lang.String, java.lang.Object)
	 */
	
	public Object setEL(String key, Object value) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	
	public Object setEL(Key key, Object value) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#set(java.lang.String, java.lang.Object)
	 */
	
	public Object set(String key, Object value) throws PageException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	
	public Object set(Key key, Object value) throws PageException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setAt(java.lang.String, int, java.lang.Object)
	 */
	
	public Object setAt(String key, int row, Object value) throws PageException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setAt(railo.runtime.type.Collection.Key, int, java.lang.Object)
	 */
	
	public Object setAt(Key key, int row, Object value) throws PageException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setAtEL(java.lang.String, int, java.lang.Object)
	 */
	
	public Object setAtEL(String key, int row, Object value) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setAtEL(railo.runtime.type.Collection.Key, int, java.lang.Object)
	 */
	
	public Object setAtEL(Key key, int row, Object value) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#next()
	 */
	
	public synchronized boolean next() {
		return next(getPid());
	}

	/**
	 * @see railo.runtime.type.QueryImpl#next(int)
	 */
	
	public synchronized boolean next(int pid) {
		if(recordcount>=(arrCurrentRow.set(pid,arrCurrentRow.get(pid,0)+1))) {
			return true;
		}
		arrCurrentRow.set(pid,0);
		return false;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#reset()
	 */
	
	public synchronized void reset() {
		reset(getPid());
	}

	/**
	 * @see railo.runtime.type.QueryImpl#reset(int)
	 */
	
	public synchronized void reset(int pid) {
		arrCurrentRow.set(pid,0);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getRecordcount()
	 */
	
	public int getRecordcount() {
		return recordcount;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getCurrentrow(int)
	 */
	
	public synchronized int getCurrentrow(int pid) {
		return arrCurrentRow.get(pid, 1);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumnlist(boolean)
	 */
	public String getColumnlist(boolean upperCase) {
		Key[] columnNames = keys();
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<columnNames.length;i++) {
			if(i>0)sb.append(',');
			sb.append(upperCase?columnNames[i].getUpperString():columnNames[i].getString());
		}
		return sb.toString();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumnlist()
	 */
	
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
	
	
	
	/*public synchronized boolean go(int index) {
		if(index==getCurrentrow()) return true;
		try {
			return res.absolute(index);
		} 
		catch (SQLException e) {
			throw toRuntimeExc(e);
		}
	}
	
	public boolean go(int index, int pid) {
		return go(index);
	}*/

	/**
	 * @see railo.runtime.type.QueryImpl#isEmpty()
	 */
	public boolean isEmpty() {
		return recordcount+columnNames.length==0;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return QueryUtil.toDumpData(this, pageContext, maxlevel, dp);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#sort(java.lang.String)
	 */
	
	public void sort(String column) throws PageException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#sort(railo.runtime.type.Collection.Key)
	 */
	
	public void sort(Key column) throws PageException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#sort(java.lang.String, int)
	 */
	
	public synchronized void sort(String strColumn, int order)
			throws PageException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#sort(railo.runtime.type.Collection.Key, int)
	 */
	
	public synchronized void sort(Key keyColumn, int order)
			throws PageException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addRow(int)
	 */
	
	public synchronized boolean addRow(int count) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addColumn(java.lang.String, railo.runtime.type.Array)
	 */
	
	public boolean addColumn(String columnName, railo.runtime.type.Array content)
			throws DatabaseException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addColumn(railo.runtime.type.Collection.Key, railo.runtime.type.Array)
	 */
	
	public boolean addColumn(Key columnName, railo.runtime.type.Array content)
			throws PageException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addColumn(java.lang.String, railo.runtime.type.Array, int)
	 */
	
	public synchronized boolean addColumn(String columnName,
			railo.runtime.type.Array content, int type)
			throws DatabaseException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addColumn(railo.runtime.type.Collection.Key, railo.runtime.type.Array, int)
	 */
	
	public boolean addColumn(Key columnName, railo.runtime.type.Array content,
			int type) throws DatabaseException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#clone()
	 */
	
	public Object clone() {
		return cloneQuery(true);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#duplicate(boolean)
	 */
	
	public Collection duplicate(boolean deepCopy) {
		return cloneQuery(deepCopy);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#cloneQuery(boolean)
	 */
	
	public QueryImpl cloneQuery(boolean deepCopy) {
		return QueryImpl.cloneQuery(this, deepCopy);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTypes()
	 */

	public synchronized int[] getTypes() {
		if(_types==null) {
			_types=new int[columns.size()];
			int i=0;
			Iterator<Entry<String, SimpleQueryColumn>> it = columns.entrySet().iterator();
			while(it.hasNext()){
				_types[i++]=it.next().getValue().getType();
			}
		}
		return _types;
	}
	/**
	 * @throws PageException 
	 * @see railo.runtime.type.QueryImpl#getTypesAsMap()
	 */
	
	public synchronized Map getTypesAsMap() {
		Map<String,String> map=new HashMap<String,String>();
		Iterator<SimpleQueryColumn> it = columns.values().iterator();
		SimpleQueryColumn c;
		while(it.hasNext()){
			c=it.next();
			map.put(c.getKeyAsString(), c.getTypeAsString());
		}
		return map;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumn(java.lang.String)
	 */
	
	public QueryColumn getColumn(String key) throws DatabaseException {
		return getColumn(KeyImpl.init(key));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumn(railo.runtime.type.Collection.Key)
	 */
	
	public QueryColumn getColumn(Key key) throws DatabaseException {
		QueryColumn rtn = getColumn(key,null);
		if(rtn!=null) return rtn;
        throw new DatabaseException("key ["+key.getString()+"] not found in query, columns are ["+getColumnlist(false)+"]",null,null,null);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumn(java.lang.String, railo.runtime.type.QueryColumn)
	 */
	public QueryColumn getColumn(String key, QueryColumn defaultValue) {
		return getColumn(KeyImpl.init(key),defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumn(railo.runtime.type.Collection.Key, railo.runtime.type.QueryColumn)
	 */
	
	public QueryColumn getColumn(Key key, QueryColumn defaultValue) {
		if(key.getString().length()>0) {
        	char c=key.lowerCharAt(0);
        	if(c=='r') {
	            if(key.equals(KeyConstants._RECORDCOUNT)) return new QueryColumnRef(this,key,Types.INTEGER);
	        }
        	else if(c=='c') {
	            if(key.equals(KeyConstants._CURRENTROW)) return new QueryColumnRef(this,key,Types.INTEGER);
	            else if(key.equals(KeyConstants._COLUMNLIST)) return new QueryColumnRef(this,key,Types.INTEGER);
	        }
	        SimpleQueryColumn col = columns.get(key.getLowerString());
	        if(col!=null) return col;
	        
		}
		return defaultValue;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#rename(railo.runtime.type.Collection.Key, railo.runtime.type.Collection.Key)
	 */
	
	public synchronized void rename(Key columnName, Key newColumnName)
			throws ExpressionException {
		throw notSupported();
		//Integer index=mappings.get(columnName);
		//if(index==null) throw new ExpressionException("invalid column name definitions");	
		// TODO implement
	}

	/**
	 * @see railo.runtime.type.QueryImpl#toString()
	 */
	public String toString() {
		return res.toString();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setExecutionTime(long)
	 */
	
	public void setExecutionTime(long exeTime) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#cutRowsTo(int)
	 */
	
	public synchronized boolean cutRowsTo(int maxrows) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setCached(boolean)
	 */
	
	public void setCached(boolean isCached) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isCached()
	 */
	
	public boolean isCached() {
		return false;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addRow()
	 */
	
	public int addRow() {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumnName(int)
	 */
	
	public Key getColumnName(int columnIndex) {
		Iterator<SimpleQueryColumn> it = columns.values().iterator();
		SimpleQueryColumn c;
		while(it.hasNext()){
			c = it.next();
			if(c.getIndex()==columnIndex) return c.getKey();
		}
		return null;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumnIndex(java.lang.String)
	 */
	
	public int getColumnIndex(String coulmnName) {
		SimpleQueryColumn col = columns.get(coulmnName.toLowerCase());
		if(col==null) return -1;
		return col.getIndex();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumns()
	 */
	
	public String[] getColumns() {
		return getColumnNamesAsString();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumnNames()
	 */
	
	public Key[] getColumnNames() {
		Key[] _columns=new Key[columnNames.length];
		for(int i=0;i<columnNames.length;i++){
			_columns[i]=columnNames[i];
		}
		return _columns;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setColumnNames(railo.runtime.type.Collection.Key[])
	 */
	
	public void setColumnNames(Key[] trg) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumnNamesAsString()
	 */
	
	public String[] getColumnNamesAsString() {
		String[] _columns=new String[columnNames.length];
		for(int i=0;i<columnNames.length;i++){
			_columns[i]=columnNames[i].getString();
		}
		return _columns;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getData(int, int)
	 */
	
	public synchronized String getData(int row, int col) throws IndexOutOfBoundsException {
		try{
			int rowBefore=res.getRow();
			try{
				res.absolute(row);
				if(col<1 || col>columnNames.length) {
					new IndexOutOfBoundsException("invalid column index to retrieve Data from query, valid index goes from 1 to "+columnNames.length);
				}
				return Caster.toString(get(columnNames[col]));
				
			}
			finally{
				res.absolute(rowBefore);
			}
		}
		catch(Throwable t){
			throw toRuntimeExc(t);
		}
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getName()
	 */
	
	public String getName() {
		return name;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getRowCount()
	 */
	
	public int getRowCount() {
		return getRecordcount();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setData(int, int, java.lang.String)
	 */
	
	public void setData(int row, int col, String value)
			throws IndexOutOfBoundsException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#containsKey(java.lang.String)
	 */
	
	public boolean containsKey(String key) {
		return columns.get(key.toLowerCase())!=null;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#containsKey(railo.runtime.type.Collection.Key)
	 */
	
	public boolean containsKey(Key key) {
		return containsKey(key.getString());
	}

	/**
	 * @see railo.runtime.type.QueryImpl#castToString()
	 */
	
	public String castToString() throws ExpressionException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#castToString(java.lang.String)
	 */
	
	public String castToString(String defaultValue) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#castToBooleanValue()
	 */
	
	public boolean castToBooleanValue() throws ExpressionException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#castToBoolean(java.lang.Boolean)
	 */
	
	public Boolean castToBoolean(Boolean defaultValue) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#castToDoubleValue()
	 */
	
	public double castToDoubleValue() throws ExpressionException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#castToDoubleValue(double)
	 */
	
	public double castToDoubleValue(double defaultValue) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#castToDateTime()
	 */
	
	public DateTime castToDateTime() throws ExpressionException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#castToDateTime(railo.runtime.type.dt.DateTime)
	 */
	
	public DateTime castToDateTime(DateTime defaultValue) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#compareTo(boolean)
	 */
	
	public int compareTo(boolean b) throws ExpressionException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#compareTo(railo.runtime.type.dt.DateTime)
	 */
	
	public int compareTo(DateTime dt) throws PageException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#compareTo(double)
	 */
	
	public int compareTo(double d) throws PageException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#compareTo(java.lang.String)
	 */
	
	public int compareTo(String str) throws PageException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getMetaDataSimple()
	 */
	public synchronized railo.runtime.type.Array getMetaDataSimple() {
			railo.runtime.type.Array cols=new ArrayImpl();
	    	SimpleQueryColumn sqc;
	    	Struct column;
	        Iterator<SimpleQueryColumn> it = columns.values().iterator();
	        while(it.hasNext()){
	        	sqc=it.next();
	        	column=new StructImpl();
	        	column.setEL(KeyConstants._name,sqc.getKey());
	        	column.setEL("isCaseSensitive",Boolean.FALSE);
	        	column.setEL("typeName",sqc.getTypeAsString());
	        	cols.appendEL(column);
	        }
	        return cols;
	    }
	
	/**
	 * @see railo.runtime.type.QueryImpl#getObject(java.lang.String)
	 */
	
	public Object getObject(String columnName) throws SQLException {
		return res.getObject(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getObject(int)
	 */
	
	public Object getObject(int columnIndex) throws SQLException {
		return res.getObject(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getString(int)
	 */
	
	public String getString(int columnIndex) throws SQLException {
		return res.getString(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getString(java.lang.String)
	 */
	
	public String getString(String columnName) throws SQLException {
		return res.getString(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBoolean(int)
	 */
	
	public boolean getBoolean(int columnIndex) throws SQLException {
		return res.getBoolean(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBoolean(java.lang.String)
	 */
	
	public boolean getBoolean(String columnName) throws SQLException {
		return res.getBoolean(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#call(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	
	public Object call(PageContext pc, Key methodName, Object[] arguments)
			throws PageException {
		throw notSupported();
	}


	/**
	 * @see railo.runtime.type.QueryImpl#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Collection.Key, railo.runtime.type.Struct)
	 */
	
	public Object callWithNamedValues(PageContext pc, Key methodName,
			Struct args) throws PageException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#get(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	
	public Object get(PageContext pc, String key, Object defaultValue) {
		return getAt(KeyImpl.init(key), getCurrentrow(pc.getId()), pc.getId(),defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return getAt(key, getCurrentrow(pc.getId()), pc.getId(),defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#get(railo.runtime.PageContext, java.lang.String)
	 */
	
	public Object get(PageContext pc, String key) throws PageException {
		return getAt(KeyImpl.init(key), getCurrentrow(pc.getId()), pc.getId());
	}

	/**
	 * @see railo.runtime.type.QueryImpl#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	
	public Object get(PageContext pc, Key key) throws PageException {
		return getAt(key, getCurrentrow(pc.getId()), pc.getId());
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isInitalized()
	 */
	
	public boolean isInitalized() {
		return true;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#set(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	
	public Object set(PageContext pc, Key propertyName, Object value)
			throws PageException {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setEL(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	
	public Object setEL(PageContext pc, String propertyName, Object value) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setEL(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	
	public Object setEL(PageContext pc, Key propertyName, Object value) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#wasNull()
	 */
	
	public boolean wasNull() {
		try {
			return res.wasNull();
		} catch (SQLException e) {
			throw toRuntimeExc(e);
		}
	}

	/**
	 * @see railo.runtime.type.QueryImpl#absolute(int)
	 */
	
	public synchronized boolean absolute(int row) throws SQLException {
		return res.absolute(row);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#afterLast()
	 */
	
	public synchronized void afterLast() throws SQLException {
		res.afterLast();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#beforeFirst()
	 */
	
	public synchronized void beforeFirst() throws SQLException {
		res.beforeFirst();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#cancelRowUpdates()
	 */
	
	public synchronized void cancelRowUpdates() throws SQLException {
		res.cancelRowUpdates();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#clearWarnings()
	 */
	
	public synchronized void clearWarnings() throws SQLException {
		res.clearWarnings();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#close()
	 */
	
	public synchronized void close() throws SQLException {
		res.close();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#deleteRow()
	 */
	
	public synchronized void deleteRow() throws SQLException {
		res.deleteRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#findColumn(java.lang.String)
	 */
	
	public int findColumn(String columnName) throws SQLException {
		return res.findColumn(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#first()
	 */
	
	public synchronized boolean first() throws SQLException {
		return res.first();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getArray(int)
	 */
	
	public Array getArray(int i) throws SQLException {
		return res.getArray(i);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getArray(java.lang.String)
	 */
	
	public Array getArray(String colName) throws SQLException {
		return res.getArray(toIndex(colName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getAsciiStream(int)
	 */
	
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return res.getAsciiStream(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getAsciiStream(java.lang.String)
	 */
	
	public InputStream getAsciiStream(String columnName) throws SQLException {
		return res.getAsciiStream(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBigDecimal(int)
	 */
	
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return res.getBigDecimal(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBigDecimal(java.lang.String)
	 */
	
	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		return res.getBigDecimal(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBigDecimal(int, int)
	 */
	
	public BigDecimal getBigDecimal(int columnIndex, int scale)
			throws SQLException {
		return res.getBigDecimal(columnIndex, scale);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBigDecimal(java.lang.String, int)
	 */
	
	public BigDecimal getBigDecimal(String columnName, int scale)
			throws SQLException {
		return res.getBigDecimal(toIndex(columnName), scale);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBinaryStream(int)
	 */
	
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return res.getBinaryStream(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBinaryStream(java.lang.String)
	 */
	
	public InputStream getBinaryStream(String columnName) throws SQLException {
		return res.getBinaryStream(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBlob(int)
	 */
	
	public Blob getBlob(int i) throws SQLException {
		return res.getBlob(i);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBlob(java.lang.String)
	 */
	
	public Blob getBlob(String colName) throws SQLException {
		return res.getBlob(toIndex(colName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getByte(int)
	 */
	
	public byte getByte(int columnIndex) throws SQLException {
		return res.getByte(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getByte(java.lang.String)
	 */
	
	public byte getByte(String columnName) throws SQLException {
		return res.getByte(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBytes(int)
	 */
	
	public byte[] getBytes(int columnIndex) throws SQLException {
		return res.getBytes(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBytes(java.lang.String)
	 */
	
	public byte[] getBytes(String columnName) throws SQLException {
		return res.getBytes(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getCharacterStream(int)
	 */
	
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		return res.getCharacterStream(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getCharacterStream(java.lang.String)
	 */
	
	public Reader getCharacterStream(String columnName) throws SQLException {
		return res.getCharacterStream(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getClob(int)
	 */
	
	public Clob getClob(int i) throws SQLException {
		return res.getClob(i);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getClob(java.lang.String)
	 */
	
	public Clob getClob(String colName) throws SQLException {
		return res.getClob(toIndex(colName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getConcurrency()
	 */
	
	public int getConcurrency() throws SQLException {
		return res.getConcurrency();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getCursorName()
	 */
	
	public String getCursorName() throws SQLException {
		return res.getCursorName();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDate(int)
	 */
	
	public Date getDate(int columnIndex) throws SQLException {
		return res.getDate(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDate(java.lang.String)
	 */
	
	public Date getDate(String columnName) throws SQLException {
		return res.getDate(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDate(int, java.util.Calendar)
	 */
	
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return res.getDate(columnIndex, cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDate(java.lang.String, java.util.Calendar)
	 */
	
	public Date getDate(String columnName, Calendar cal) throws SQLException {
		return res.getDate(toIndex(columnName), cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDouble(int)
	 */
	
	public double getDouble(int columnIndex) throws SQLException {
		return res.getDouble(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDouble(java.lang.String)
	 */
	
	public double getDouble(String columnName) throws SQLException {
		return res.getDouble(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getFetchDirection()
	 */
	
	public int getFetchDirection() throws SQLException {
		return res.getFetchDirection();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getFetchSize()
	 */
	
	public int getFetchSize() throws SQLException {
		return res.getFetchSize();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getFloat(int)
	 */
	
	public float getFloat(int columnIndex) throws SQLException {
		return res.getFloat(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getFloat(java.lang.String)
	 */
	
	public float getFloat(String columnName) throws SQLException {
		return res.getFloat(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getInt(int)
	 */
	
	public int getInt(int columnIndex) throws SQLException {
		return res.getInt(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getInt(java.lang.String)
	 */
	
	public int getInt(String columnName) throws SQLException {
		return res.getInt(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getLong(int)
	 */
	
	public long getLong(int columnIndex) throws SQLException {
		return res.getLong(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getLong(java.lang.String)
	 */
	
	public long getLong(String columnName) throws SQLException {
		return res.getLong(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getObject(int, java.util.Map)
	 */
	
	public Object getObject(int i, Map map) throws SQLException {
		return res.getObject(i, map);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getObject(java.lang.String, java.util.Map)
	 */
	
	public Object getObject(String colName, Map map) throws SQLException {
		return res.getObject(toIndex(colName), map);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getRef(int)
	 */
	
	public Ref getRef(int i) throws SQLException {
		return res.getRef(i);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getRef(java.lang.String)
	 */
	
	public Ref getRef(String colName) throws SQLException {
		return res.getRef(toIndex(colName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getRow()
	 */
	
	public int getRow() throws SQLException {
		return res.getRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getShort(int)
	 */
	
	public short getShort(int columnIndex) throws SQLException {
		return res.getShort(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getShort(java.lang.String)
	 */
	
	public short getShort(String columnName) throws SQLException {
		return res.getShort(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getStatement()
	 */
	
	public Statement getStatement() throws SQLException {
		return res.getStatement();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTime(int)
	 */
	
	public Time getTime(int columnIndex) throws SQLException {
		return res.getTime(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTime(java.lang.String)
	 */
	
	public Time getTime(String columnName) throws SQLException {
		return res.getTime(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTime(int, java.util.Calendar)
	 */
	
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return res.getTime(columnIndex, cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTime(java.lang.String, java.util.Calendar)
	 */
	
	public Time getTime(String columnName, Calendar cal) throws SQLException {
		return res.getTime(toIndex(columnName), cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTimestamp(int)
	 */
	
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return res.getTimestamp(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTimestamp(java.lang.String)
	 */
	
	public Timestamp getTimestamp(String columnName) throws SQLException {
		return res.getTimestamp(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTimestamp(int, java.util.Calendar)
	 */
	
	public Timestamp getTimestamp(int columnIndex, Calendar cal)
			throws SQLException {
		return res.getTimestamp(columnIndex, cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTimestamp(java.lang.String, java.util.Calendar)
	 */
	
	public Timestamp getTimestamp(String columnName, Calendar cal)
			throws SQLException {
		return res.getTimestamp(toIndex(columnName), cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getType()
	 */
	
	public int getType() throws SQLException {
		return res.getType();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getURL(int)
	 */
	
	public URL getURL(int columnIndex) throws SQLException {
		return res.getURL(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getURL(java.lang.String)
	 */
	
	public URL getURL(String columnName) throws SQLException {
		return res.getURL(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getUnicodeStream(int)
	 */
	
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return res.getUnicodeStream(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getUnicodeStream(java.lang.String)
	 */
	
	public InputStream getUnicodeStream(String columnName) throws SQLException {
		return res.getUnicodeStream(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getWarnings()
	 */
	
	public SQLWarning getWarnings() throws SQLException {
		return res.getWarnings();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#insertRow()
	 */
	
	public void insertRow() throws SQLException {
		res.insertRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isAfterLast()
	 */
	
	public boolean isAfterLast() throws SQLException {
		return res.isAfterLast();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isBeforeFirst()
	 */
	
	public boolean isBeforeFirst() throws SQLException {
		return res.isBeforeFirst();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isFirst()
	 */
	
	public boolean isFirst() throws SQLException {
		return res.isFirst();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isLast()
	 */
	
	public boolean isLast() throws SQLException {
		return res.isLast();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#last()
	 */
	
	public boolean last() throws SQLException {
		return res.last();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#moveToCurrentRow()
	 */
	
	public void moveToCurrentRow() throws SQLException {
		res.moveToCurrentRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#moveToInsertRow()
	 */
	
	public void moveToInsertRow() throws SQLException {
		res.moveToInsertRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#previous()
	 */
	
	public boolean previous() {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#previous(int)
	 */
	
	public boolean previous(int pid) {
		throw notSupported();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#refreshRow()
	 */
	
	public void refreshRow() throws SQLException {
		res.refreshRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#relative(int)
	 */
	
	public boolean relative(int rows) throws SQLException {
		return res.relative(rows);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#rowDeleted()
	 */
	
	public boolean rowDeleted() throws SQLException {
		return res.rowDeleted();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#rowInserted()
	 */
	
	public boolean rowInserted() throws SQLException {
		return res.rowInserted();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#rowUpdated()
	 */
	
	public boolean rowUpdated() throws SQLException {
		return res.rowUpdated();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setFetchDirection(int)
	 */
	
	public void setFetchDirection(int direction) throws SQLException {
		res.setFetchDirection(direction);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setFetchSize(int)
	 */
	
	public void setFetchSize(int rows) throws SQLException {
		res.setFetchSize(rows);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateArray(int, java.sql.Array)
	 */
	
	public void updateArray(int columnIndex, Array x) throws SQLException {
		res.updateArray(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateArray(java.lang.String, java.sql.Array)
	 */
	
	public void updateArray(String columnName, Array x) throws SQLException {
		res.updateArray(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateAsciiStream(int, java.io.InputStream, int)
	 */
	
	public void updateAsciiStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		res.updateAsciiStream(columnIndex, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateAsciiStream(java.lang.String, java.io.InputStream, int)
	 */
	
	public void updateAsciiStream(String columnName, InputStream x, int length)
			throws SQLException {
		res.updateAsciiStream(toIndex(columnName), x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBigDecimal(int, java.math.BigDecimal)
	 */
	
	public void updateBigDecimal(int columnIndex, BigDecimal x)
			throws SQLException {
		res.updateBigDecimal(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	
	public void updateBigDecimal(String columnName, BigDecimal x)
			throws SQLException {
		res.updateBigDecimal(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBinaryStream(int, java.io.InputStream, int)
	 */
	
	public void updateBinaryStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		res.updateBinaryStream(columnIndex, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBinaryStream(java.lang.String, java.io.InputStream, int)
	 */
	
	public void updateBinaryStream(String columnName, InputStream x, int length)
			throws SQLException {
		res.updateBinaryStream(toIndex(columnName), x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBlob(int, java.sql.Blob)
	 */
	
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		res.updateBlob(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBlob(java.lang.String, java.sql.Blob)
	 */
	
	public void updateBlob(String columnName, Blob x) throws SQLException {
		res.updateBlob(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBoolean(int, boolean)
	 */
	
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		res.updateBoolean(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBoolean(java.lang.String, boolean)
	 */
	
	public void updateBoolean(String columnName, boolean x) throws SQLException {
		res.updateBoolean(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateByte(int, byte)
	 */
	
	public void updateByte(int columnIndex, byte x) throws SQLException {
		res.updateByte(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateByte(java.lang.String, byte)
	 */
	
	public void updateByte(String columnName, byte x) throws SQLException {
		res.updateByte(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBytes(int, byte[])
	 */
	
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		res.updateBytes(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBytes(java.lang.String, byte[])
	 */
	
	public void updateBytes(String columnName, byte[] x) throws SQLException {
		res.updateBytes(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateCharacterStream(int, java.io.Reader, int)
	 */
	
	public void updateCharacterStream(int columnIndex, Reader reader, int length)
			throws SQLException {
		res.updateCharacterStream(columnIndex, reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateCharacterStream(java.lang.String, java.io.Reader, int)
	 */
	
	public void updateCharacterStream(String columnName, Reader reader,
			int length) throws SQLException {
		res.updateCharacterStream(toIndex(columnName), reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateClob(int, java.sql.Clob)
	 */
	
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		res.updateClob(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateClob(java.lang.String, java.sql.Clob)
	 */
	
	public void updateClob(String columnName, Clob x) throws SQLException {
		res.updateClob(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateDate(int, java.sql.Date)
	 */
	
	public void updateDate(int columnIndex, Date x) throws SQLException {
		res.updateDate(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateDate(java.lang.String, java.sql.Date)
	 */
	
	public void updateDate(String columnName, Date x) throws SQLException {
		res.updateDate(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateDouble(int, double)
	 */
	
	public void updateDouble(int columnIndex, double x) throws SQLException {
		res.updateDouble(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateDouble(java.lang.String, double)
	 */
	
	public void updateDouble(String columnName, double x) throws SQLException {
		res.updateDouble(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateFloat(int, float)
	 */
	
	public void updateFloat(int columnIndex, float x) throws SQLException {
		res.updateFloat(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateFloat(java.lang.String, float)
	 */
	
	public void updateFloat(String columnName, float x) throws SQLException {
		res.updateFloat(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateInt(int, int)
	 */
	
	public void updateInt(int columnIndex, int x) throws SQLException {
		res.updateInt(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateInt(java.lang.String, int)
	 */
	
	public void updateInt(String columnName, int x) throws SQLException {
		res.updateInt(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateLong(int, long)
	 */
	
	public void updateLong(int columnIndex, long x) throws SQLException {
		res.updateLong(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateLong(java.lang.String, long)
	 */
	
	public void updateLong(String columnName, long x) throws SQLException {
		res.updateLong(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNull(int)
	 */
	
	public void updateNull(int columnIndex) throws SQLException {
		res.updateNull(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNull(java.lang.String)
	 */
	
	public void updateNull(String columnName) throws SQLException {
		res.updateNull(toIndex(columnName));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateObject(int, java.lang.Object)
	 */
	
	public void updateObject(int columnIndex, Object x) throws SQLException {
		res.updateObject(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateObject(java.lang.String, java.lang.Object)
	 */
	
	public void updateObject(String columnName, Object x) throws SQLException {
		res.updateObject(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateObject(int, java.lang.Object, int)
	 */
	
	public void updateObject(int columnIndex, Object x, int scale)
			throws SQLException {
		res.updateObject(columnIndex, x, scale);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateObject(java.lang.String, java.lang.Object, int)
	 */
	
	public void updateObject(String columnName, Object x, int scale)
			throws SQLException {
		res.updateObject(toIndex(columnName), x, scale);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateRef(int, java.sql.Ref)
	 */
	
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		res.updateRef(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateRef(java.lang.String, java.sql.Ref)
	 */
	
	public void updateRef(String columnName, Ref x) throws SQLException {
		res.updateRef(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateRow()
	 */
	
	public void updateRow() throws SQLException {
		res.updateRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateShort(int, short)
	 */
	
	public void updateShort(int columnIndex, short x) throws SQLException {
		res.updateShort(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateShort(java.lang.String, short)
	 */
	
	public void updateShort(String columnName, short x) throws SQLException {
		res.updateShort(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateString(int, java.lang.String)
	 */
	
	public void updateString(int columnIndex, String x) throws SQLException {
		res.updateString(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateString(java.lang.String, java.lang.String)
	 */
	
	public void updateString(String columnName, String x) throws SQLException {
		res.updateString(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateTime(int, java.sql.Time)
	 */
	
	public void updateTime(int columnIndex, Time x) throws SQLException {
		res.updateTime(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateTime(java.lang.String, java.sql.Time)
	 */
	
	public void updateTime(String columnName, Time x) throws SQLException {
		res.updateTime(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateTimestamp(int, java.sql.Timestamp)
	 */
	
	public void updateTimestamp(int columnIndex, Timestamp x)
			throws SQLException {
		res.updateTimestamp(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	
	public void updateTimestamp(String columnName, Timestamp x)
			throws SQLException {
		res.updateTimestamp(toIndex(columnName), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getMetaData()
	 */
	
	public ResultSetMetaData getMetaData() throws SQLException {
		return res.getMetaData();
	}

	  /**
	 * @see railo.runtime.type.Collection#keyIterator()
	 */
	public Iterator<Collection.Key> keyIterator() {
		return new KeyIterator(keys());
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return new StringIterator(keys());
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new EntryIterator(this,keys());
	}
	
	/**
	 * @see railo.runtime.type.Iteratorable#valueIterator()
	 */
	public Iterator<Object> valueIterator() {
		return new CollectionIterator(keys(),this);
	}
	
	/**
	 * @see railo.runtime.type.QueryImpl#equals(java.lang.Object)
	 */
	
	public boolean equals(Object obj) {
		return res.equals(obj);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getHoldability()
	 */
	
	public int getHoldability() throws SQLException {
		return res.getHoldability();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isClosed()
	 */
	
	public boolean isClosed() throws SQLException {
		return res.isClosed();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNString(int, java.lang.String)
	 */
	
	public void updateNString(int columnIndex, String nString)
			throws SQLException {
		res.updateNString(columnIndex, nString);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNString(java.lang.String, java.lang.String)
	 */
	
	public void updateNString(String columnLabel, String nString)
			throws SQLException {
		res.updateNString(toIndex(columnLabel), nString);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getNString(int)
	 */
	
	public String getNString(int columnIndex) throws SQLException {
		return res.getNString(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getNString(java.lang.String)
	 */
	
	public String getNString(String columnLabel) throws SQLException {
		return res.getNString(toIndex(columnLabel));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getNCharacterStream(int)
	 */
	
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return res.getNCharacterStream(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getNCharacterStream(java.lang.String)
	 */
	
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		return res.getNCharacterStream(toIndex(columnLabel));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNCharacterStream(int, java.io.Reader, long)
	 */
	
	public void updateNCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		res.updateNCharacterStream(columnIndex, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNCharacterStream(java.lang.String, java.io.Reader, long)
	 */
	
	public void updateNCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		res.updateNCharacterStream(toIndex(columnLabel), reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateAsciiStream(int, java.io.InputStream, long)
	 */
	
	public void updateAsciiStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		res.updateAsciiStream(columnIndex, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBinaryStream(int, java.io.InputStream, long)
	 */
	
	public void updateBinaryStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		res.updateBinaryStream(columnIndex, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateCharacterStream(int, java.io.Reader, long)
	 */
	
	public void updateCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		res.updateCharacterStream(columnIndex, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateAsciiStream(java.lang.String, java.io.InputStream, long)
	 */
	
	public void updateAsciiStream(String columnLabel, InputStream x, long length)
			throws SQLException {
		res.updateAsciiStream(toIndex(columnLabel), x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBinaryStream(java.lang.String, java.io.InputStream, long)
	 */
	
	public void updateBinaryStream(String columnLabel, InputStream x,
			long length) throws SQLException {
		res.updateBinaryStream(toIndex(columnLabel), x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateCharacterStream(java.lang.String, java.io.Reader, long)
	 */
	
	public void updateCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		res.updateCharacterStream(toIndex(columnLabel), reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBlob(int, java.io.InputStream, long)
	 */
	
	public void updateBlob(int columnIndex, InputStream inputStream, long length)
			throws SQLException {
		res.updateBlob(columnIndex, inputStream, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBlob(java.lang.String, java.io.InputStream, long)
	 */
	
	public void updateBlob(String columnLabel, InputStream inputStream,
			long length) throws SQLException {
		res.updateBlob(toIndex(columnLabel), inputStream, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateClob(int, java.io.Reader, long)
	 */
	
	public void updateClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		res.updateClob(columnIndex, reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateClob(java.lang.String, java.io.Reader, long)
	 */
	
	public void updateClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		res.updateClob(toIndex(columnLabel), reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNClob(int, java.io.Reader, long)
	 */
	
	public void updateNClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		res.updateNClob(columnIndex, reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNClob(java.lang.String, java.io.Reader, long)
	 */
	
	public void updateNClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		res.updateNClob(toIndex(columnLabel), reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNCharacterStream(int, java.io.Reader)
	 */
	
	public void updateNCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		res.updateNCharacterStream(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNCharacterStream(java.lang.String, java.io.Reader)
	 */
	
	public void updateNCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		res.updateNCharacterStream(toIndex(columnLabel), reader);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateAsciiStream(int, java.io.InputStream)
	 */
	
	public void updateAsciiStream(int columnIndex, InputStream x)
			throws SQLException {
		res.updateAsciiStream(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBinaryStream(int, java.io.InputStream)
	 */
	
	public void updateBinaryStream(int columnIndex, InputStream x)
			throws SQLException {
		res.updateBinaryStream(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateCharacterStream(int, java.io.Reader)
	 */
	
	public void updateCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		res.updateCharacterStream(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateAsciiStream(java.lang.String, java.io.InputStream)
	 */
	
	public void updateAsciiStream(String columnLabel, InputStream x)
			throws SQLException {
		res.updateAsciiStream(toIndex(columnLabel), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBinaryStream(java.lang.String, java.io.InputStream)
	 */
	
	public void updateBinaryStream(String columnLabel, InputStream x)
			throws SQLException {
		res.updateBinaryStream(columnLabel, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateCharacterStream(java.lang.String, java.io.Reader)
	 */
	
	public void updateCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		res.updateCharacterStream(toIndex(columnLabel), reader);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBlob(int, java.io.InputStream)
	 */
	
	public void updateBlob(int columnIndex, InputStream inputStream)
			throws SQLException {
		res.updateBlob(columnIndex, inputStream);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBlob(java.lang.String, java.io.InputStream)
	 */
	
	public void updateBlob(String columnLabel, InputStream inputStream)
			throws SQLException {
		res.updateBlob(toIndex(columnLabel), inputStream);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateClob(int, java.io.Reader)
	 */
	
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		res.updateClob(columnIndex, reader);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateClob(java.lang.String, java.io.Reader)
	 */
	
	public void updateClob(String columnLabel, Reader reader)
			throws SQLException {
		res.updateClob(toIndex(columnLabel), reader);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNClob(int, java.io.Reader)
	 */
	
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		res.updateNClob(columnIndex, reader);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNClob(java.lang.String, java.io.Reader)
	 */
	
	public void updateNClob(String columnLabel, Reader reader)
			throws SQLException {
		res.updateNClob(toIndex(columnLabel), reader);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#unwrap(java.lang.Class)
	 */
	
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return res.unwrap(iface);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isWrapperFor(java.lang.Class)
	 */
	
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return res.isWrapperFor(iface);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNClob(int, java.sql.NClob)
	 */
	
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		res.updateNClob(columnIndex, nClob);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNClob(java.lang.String, java.sql.NClob)
	 */
	
	public void updateNClob(String columnLabel, NClob nClob)
			throws SQLException {
		res.updateNClob(toIndex(columnLabel), nClob);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getNClob(int)
	 */
	
	public NClob getNClob(int columnIndex) throws SQLException {
		return res.getNClob(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getNClob(java.lang.String)
	 */
	
	public NClob getNClob(String columnLabel) throws SQLException {
		return res.getNClob(toIndex(columnLabel));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getSQLXML(int)
	 */
	
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		return res.getSQLXML(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getSQLXML(java.lang.String)
	 */
	
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		return res.getSQLXML(toIndex(columnLabel));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateSQLXML(int, java.sql.SQLXML)
	 */
	
	public void updateSQLXML(int columnIndex, SQLXML xmlObject)
			throws SQLException {
		res.updateSQLXML(columnIndex, xmlObject);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateSQLXML(java.lang.String, java.sql.SQLXML)
	 */
	
	public void updateSQLXML(String columnLabel, SQLXML xmlObject)
			throws SQLException {
		res.updateSQLXML(toIndex(columnLabel), xmlObject);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getRowId(int)
	 */
	
	public RowId getRowId(int columnIndex) throws SQLException {
		return res.getRowId(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getRowId(java.lang.String)
	 */
	
	public RowId getRowId(String columnLabel) throws SQLException {
		return res.getRowId(toIndex(columnLabel));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateRowId(int, java.sql.RowId)
	 */
	
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		res.updateRowId(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateRowId(java.lang.String, java.sql.RowId)
	 */
	
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		res.updateRowId(toIndex(columnLabel), x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#enableShowQueryUsage()
	 */
	
	public synchronized void enableShowQueryUsage() {
		throw notSupported();
	}


	
	public static PageRuntimeException notSupported() {
		return new PageRuntimeException(new ApplicationException("not supported"));
	}

	public static PageRuntimeException toRuntimeExc(Throwable t) {
		return new PageRuntimeException(Caster.toPageException(t));
	}

	public static PageException toPageExc(Throwable t) {
		return Caster.toPageException(t);
	}

	private int toIndex(String columnName) throws SQLException {
		SimpleQueryColumn col = columns.get(columnName.toLowerCase());
		if(col==null) throw new SQLException("There is no column with name ["+columnName+"], available columns are ["+getColumnlist()+"]");
		return col.getIndex();
	}
	
	int getPid() {
		
		PageContext pc = ThreadLocalPageContext.get();
		if(pc==null) {
			pc=CFMLEngineFactory.getInstance().getThreadPageContext();
			if(pc==null)throw new RuntimeException("cannot get pid for current thread");
		}
		return pc.getId();
	}

	@Override
	public Query getGeneratedKeys() {
		return null;
	}

	@Override
	public SQL getSql() {
		return sql;
	}

	@Override
	public String getTemplate() {
		return template;
	}

	@Override
	public long getExecutionTime() {
		return exeTime;
	}
	
	@Override
	public java.util.Iterator getIterator() {
		return new ForEachQueryIterator(this, ThreadLocalPageContext.get().getId());
    }

}
