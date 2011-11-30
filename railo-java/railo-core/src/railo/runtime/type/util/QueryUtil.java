package railo.runtime.type.util;

import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.commons.sql.SQLUtil;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.SQL;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.arrays.ArrayFind;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.QueryColumnPro;
import railo.runtime.type.QueryPro;

public class QueryUtil {

	public static long sizeOf(QueryColumn column) {
		if(column instanceof QueryColumnPro){
			return ((QueryColumnPro)column).sizeOf();
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
		QueryPro qp = Caster.toQueryPro(qry,null);
		
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
            throw new DatabaseException("access denied to execute \""+StringUtil.ucFirst(keyword)+"\" SQL statment for datasource "+dc.getDatasource().getName(),null,sql,dc);
        }
    }
}
