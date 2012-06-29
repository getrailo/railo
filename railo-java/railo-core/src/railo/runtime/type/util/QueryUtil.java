package railo.runtime.type.util;

import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.commons.sql.SQLUtil;
import railo.runtime.PageContext;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.SQL;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpRow;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.arrays.ArrayFind;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.QueryColumnImpl;
import railo.runtime.type.query.SimpleQuery;

public class QueryUtil {

	public static long sizeOf(QueryColumn column) {
		if(column instanceof QueryColumnImpl){
			return ((QueryColumnImpl)column).sizeOf();
		}
		int len = column.size();
		long size=0;
		for(int i=1;i<=len;i++){
			size+=SizeOf.size(column.get(i,null));
		}
		return size;
	}

	/**
	 * return column names as Key from a query
	 * 
	 * @param qry
	 * @return
	 */
	public static Key[] getColumnNames(Query qry) {
		Query qp = Caster.toQuery(qry,null);
		
		if(qp!=null) return qp.getColumnNames();
		String[] strNames = qry.getColumns();
		Key[] names=new Key[strNames.length];
		for(int i=0;i<names.length;i++){
			names[i]=KeyImpl.getInstance(strNames[i]);
		}
		return names;
	}

	public static String[] toStringArray(Collection.Key[] keys) {
		if(keys==null) return new String[0];
		String[] strKeys=new String[keys.length];
		for(int i=0	;i<keys.length;i++) {
			strKeys[i]=keys[i].getString();
		}
		return strKeys;
	}

	/**
     * check if there is a sql restriction
	 * @param ds
	 * @param sql
	 * @throws PageException 
	 */
	public static void checkSQLRestriction(DatasourceConnection dc, SQL sql) throws PageException {
        Array sqlparts = List.listToArrayRemoveEmpty(
        		SQLUtil.removeLiterals(sql.getSQLString())
        		," \t"+System.getProperty("line.separator"));
        
        
        
        //print.ln(List.toStringArray(sqlparts));
        DataSource ds = dc.getDatasource();
        if(!ds.hasAllow(DataSource.ALLOW_ALTER))    checkSQLRestriction(dc,"alter",sqlparts,sql);
        if(!ds.hasAllow(DataSource.ALLOW_CREATE))   checkSQLRestriction(dc,"create",sqlparts,sql);
        if(!ds.hasAllow(DataSource.ALLOW_DELETE))   checkSQLRestriction(dc,"delete",sqlparts,sql);
        if(!ds.hasAllow(DataSource.ALLOW_DROP))     checkSQLRestriction(dc,"drop",sqlparts,sql);
        if(!ds.hasAllow(DataSource.ALLOW_GRANT))    checkSQLRestriction(dc,"grant",sqlparts,sql);
        if(!ds.hasAllow(DataSource.ALLOW_INSERT))   checkSQLRestriction(dc,"insert",sqlparts,sql);
        if(!ds.hasAllow(DataSource.ALLOW_REVOKE))   checkSQLRestriction(dc,"revoke",sqlparts,sql);
        if(!ds.hasAllow(DataSource.ALLOW_SELECT))   checkSQLRestriction(dc,"select",sqlparts,sql);
        if(!ds.hasAllow(DataSource.ALLOW_UPDATE))   checkSQLRestriction(dc,"update",sqlparts,sql);        
        
    }
	
	private static void checkSQLRestriction(DatasourceConnection dc, String keyword, Array sqlparts, SQL sql) throws PageException {
        if(ArrayFind.find(sqlparts,keyword,false)>0) {
            throw new DatabaseException("access denied to execute \""+StringUtil.ucFirst(keyword)+"\" SQL statement for datasource "+dc.getDatasource().getName(),null,sql,dc);
        }
    }

	public static DumpData toDumpData(Query query,PageContext pageContext, int maxlevel, DumpProperties dp) {
		maxlevel--;
		Collection.Key[] keys=CollectionUtil.keys(query);
		DumpData[] heads=new DumpData[keys.length+1];
		//int tmp=1;
		heads[0]=new SimpleDumpData("");
		for(int i=0;i<keys.length;i++) {
			heads[i+1]=new SimpleDumpData(keys[i].getString());
		}
		
		StringBuilder comment=new StringBuilder(); 
		
		//table.appendRow(1, new SimpleDumpData("SQL"), new SimpleDumpData(sql.toString()));
		String template=query.getTemplate();
		if(!StringUtil.isEmpty(template))
			comment.append("Template:").append(template).append("\n");
		//table.appendRow(1, new SimpleDumpData("Template"), new SimpleDumpData(template));
		
		comment.append("Execution Time (ms):").append(Caster.toString(query.getExecutionTime())).append("\n");
		comment.append("Recordcount:").append(Caster.toString(query.getRecordcount())).append("\n");
		comment.append("Cached:").append(query.isCached()?"Yes\n":"No\n");
		comment.append("Lazy:").append(query instanceof SimpleQuery?"Yes\n":"No\n");
		
		SQL sql=query.getSql();
		if(sql!=null)
			comment.append("SQL:").append("\n").append(StringUtil.suppressWhiteSpace(sql.toString().trim())).append("\n");
		
		//table.appendRow(1, new SimpleDumpData("Execution Time (ms)"), new SimpleDumpData(exeTime));
		//table.appendRow(1, new SimpleDumpData("recordcount"), new SimpleDumpData(getRecordcount()));
		//table.appendRow(1, new SimpleDumpData("cached"), new SimpleDumpData(isCached()?"Yes":"No"));
		
		
		
		DumpTable recs=new DumpTable("query","#cc99cc","#ffccff","#000000");
		recs.setTitle("Query");
		if(dp.getMetainfo())recs.setComment(comment.toString());
		recs.appendRow(new DumpRow(-1,heads));
		
		// body
		DumpData[] items;
		int recordcount=query.getRecordcount();
		int columncount=query.getColumnNames().length;
		for(int i=0;i<recordcount;i++) {
			items=new DumpData[columncount+1];
			items[0]=new SimpleDumpData(i+1);
			for(int y=0;y<keys.length;y++) {
				try {
					Object o=query.getAt(keys[y],i+1);
					if(o instanceof String)items[y+1]=new SimpleDumpData(o.toString());
                    else if(o instanceof Number) items[y+1]=new SimpleDumpData(Caster.toString(((Number)o).doubleValue()));
                    else if(o instanceof Boolean) items[y+1]=new SimpleDumpData(((Boolean)o).booleanValue());
                    else if(o instanceof Date) items[y+1]=new SimpleDumpData(Caster.toString(o));
                    else if(o instanceof Clob) items[y+1]=new SimpleDumpData(Caster.toString(o));								
					else items[y+1]=DumpUtil.toDumpData(o, pageContext,maxlevel,dp);
				} catch (PageException e) {
					items[y+1]=new SimpleDumpData("[empty]");
				}
			}
			recs.appendRow(new DumpRow(1,items));
		}
		if(!dp.getMetainfo()) return recs;
		
		//table.appendRow(1, new SimpleDumpData("result"), recs);
		return recs;
	}

	public static void removeRows(Query query, int index, int count) throws PageException {
		if(query.getRecordcount()==0) 
			throw new DatabaseException("cannot remove rows, query is empty",null,null,null);
		if(index<0 || index>=query.getRecordcount()) 
			throw new DatabaseException("invalid index ["+index+"], index must be between 0 and "+(query.getRecordcount()-1),null,null,null);
		if(index+count>query.getRecordcount()) 
			throw new DatabaseException("invalid count ["+count+"], count+index ["+(count+index)+"] must less or equal to "+(query.getRecordcount()),null,null,null);
		
		for(int row=count;row>=1;row--){
			query.removeRow(index+row);
		}
	}

	public static boolean execute(Statement stat, boolean createGeneratedKeys, SQL sql) throws SQLException {
		return createGeneratedKeys?stat.execute(sql.getSQLString(),Statement.RETURN_GENERATED_KEYS):stat.execute(sql.getSQLString());
	}
}
