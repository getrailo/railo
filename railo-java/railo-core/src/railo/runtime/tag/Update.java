package railo.runtime.tag;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.config.ConfigImpl;
import railo.runtime.db.DataSource;
import railo.runtime.db.DataSourceManager;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.SQL;
import railo.runtime.db.SQLImpl;
import railo.runtime.db.SQLItem;
import railo.runtime.db.SQLItemImpl;
import railo.runtime.debug.DebuggerPro;
import railo.runtime.debug.DebuggerUtil;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.scope.Form;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.util.ListUtil;

/**
* Updates existing records in data sources.
*
*
*
**/
public final class Update extends TagImpl {

	/** If specified, password overrides the password value specified in the ODBC setup. */
	private String password;

	/** Name of the data source that contains a table. */
	private String datasource;

	/** If specified, username overrides the username value specified in the ODBC setup. */
	private String username;

	/** A comma-separated list of form fields to update. If this attribute is not specified, all fields 
	** 		in the form are included in the operation. */
	private String formfields;

	/** For data sources that support table ownership, for example, SQL Server, Oracle, and Sybase 
	** 		SQL Anywhere, use this field to specify the owner of the table. */
	private String tableowner;

	/** Name of the table you want to update. */
	private String tablename;

	/** For data sources that support table qualifiers, use this field to specify the qualifier for the 
	** 		table. The purpose of table qualifiers varies across drivers. For SQL Server and Oracle, the qualifier 
	** 		refers to the name of the database that contains the table. For the Intersolv dBase driver, the 
	** 		qualifier refers to the directory where the DBF files are located. */
	private String tablequalifier;

	@Override
	public void release()	{
		super.release();
		password=null;
		username=null;
		formfields=null;
		tableowner=null;
		tablequalifier=null;
		datasource=null;
	}

	/** set the value password
	*  If specified, password overrides the password value specified in the ODBC setup.
	* @param password value to set
	**/
	public void setPassword(String password)	{
		this.password=password;
	}

	/** set the value datasource
	*  Name of the data source that contains a table.
	* @param datasource value to set
	**/
	public void setDatasource(String datasource)	{
		this.datasource=datasource;
	}

	/** set the value username
	*  If specified, username overrides the username value specified in the ODBC setup.
	* @param username value to set
	**/
	public void setUsername(String username)	{
		this.username=username;
	}

	/** set the value formfields
	*  A comma-separated list of form fields to update. If this attribute is not specified, all fields 
	* 		in the form are included in the operation.
	* @param formfields value to set
	**/
	public void setFormfields(String formfields)	{
		this.formfields=formfields;
	}

	/** set the value tableowner
	*  For data sources that support table ownership, for example, SQL Server, Oracle, and Sybase 
	* 		SQL Anywhere, use this field to specify the owner of the table.
	* @param tableowner value to set
	**/
	public void setTableowner(String tableowner)	{
		this.tableowner=tableowner;
	}

	/** set the value tablename
	*  Name of the table you want to update.
	* @param tablename value to set
	**/
	public void setTablename(String tablename)	{
		this.tablename=tablename;
	}

	/** set the value tablequalifier
	*  For data sources that support table qualifiers, use this field to specify the qualifier for the 
	* 		table. The purpose of table qualifiers varies across drivers. For SQL Server and Oracle, the qualifier 
	* 		refers to the name of the database that contains the table. For the Intersolv dBase driver, the 
	* 		qualifier refers to the directory where the DBF files are located.
	* @param tablequalifier value to set
	**/
	public void setTablequalifier(String tablequalifier)	{
		this.tablequalifier=tablequalifier;
	}


	@Override
	public int doStartTag()	{
		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws PageException	{

		Object ds=Insert.getDatasource(pageContext,datasource);
		
		DataSourceManager manager = pageContext.getDataSourceManager();
	    DatasourceConnection dc=ds instanceof DataSource?
	    		manager.getConnection(pageContext,(DataSource)ds,username,password):
	    		manager.getConnection(pageContext,Caster.toString(ds),username,password);
		try {
			
			Struct meta =null;
	    	try {
	    		meta=Insert.getMeta(dc,tablequalifier,tableowner,tablename);
	    	}
	    	catch(SQLException se){
	    		meta=new StructImpl();
	    	}
			
		    String[] pKeys=getPrimaryKeys(dc);
			SQL sql=createSQL(dc,pKeys,meta);
			if(sql!=null) {
				railo.runtime.type.Query query = new QueryImpl(pageContext,dc,sql,-1,-1,-1,"query");
				
				if(pageContext.getConfig().debug()) {
					String dsn=ds instanceof DataSource?((DataSource)ds).getName():Caster.toString(ds);
					boolean logdb=((ConfigImpl)pageContext.getConfig()).hasDebugOptions(ConfigImpl.DEBUG_DATABASE);
					if(logdb){
						boolean debugUsage=DebuggerUtil.debugQueryUsage(pageContext,query);
						((DebuggerPro)pageContext.getDebugger()).addQuery(debugUsage?query:null,dsn,"",sql,query.getRecordcount(),pageContext.getCurrentPageSource(),query.getExecutionTime());
					}
				}
			}
			return EVAL_PAGE;
		}
		finally {
			manager.releaseConnection(pageContext,dc);
		}
	}

    private String[] getPrimaryKeys(DatasourceConnection dc) throws PageException {
    	railo.runtime.type.Query query = getPrimaryKeysAsQuery(dc);
		int recCount=query.getRecordcount();
        String[] pKeys=new String[recCount];
		
        if(recCount==0) throw new DatabaseException("can't find primary keys of table ["+tablename+"]",null,null,dc);
        
        for(int row=1;row<=recCount;row++) {
            pKeys[row-1]=Caster.toString(query.getAt("column_name",row));
        }

		return pKeys;
    }
    
    private railo.runtime.type.Query getPrimaryKeysAsQuery(DatasourceConnection dc) throws PageException {

        // Read Meta Data
        DatabaseMetaData meta;
        try {
            meta = dc.getConnection().getMetaData();
        } 
        catch (SQLException e) {
            throw new DatabaseException(e,dc);
        }
        
        try {
            return new QueryImpl(meta.getPrimaryKeys(tablequalifier, tableowner, tablename),-1,"query",pageContext.getTimeZone());
        } 
		catch (SQLException e) {
		    try {
		        return new QueryImpl(meta.getBestRowIdentifier(tablequalifier, tableowner, tablename, 0, false),-1,"query",pageContext.getTimeZone());
            } 
		    catch (SQLException sqle) {
                throw new DatabaseException("can't find primary keys of table ["+tablename+"] ("+ExceptionUtil.getMessage(sqle)+")",null,null,dc);
            }
        }
    }

    /**
     * @param keys primary Keys
     * @return return SQL String for update
     * @throws PageException
     */
    private SQL createSQL(DatasourceConnection dc,String[] keys, Struct meta) throws PageException {
        String[] fields=null; 
        Form form = pageContext.formScope();
        if(formfields!=null) fields=ListUtil.toStringArray(ListUtil.listToArrayRemoveEmpty(formfields,','));
        else fields=CollectionUtil.keysAsString(pageContext.formScope());
        
        StringBuffer set=new StringBuffer();
        StringBuffer where=new StringBuffer();
        ArrayList setItems=new ArrayList();
        ArrayList whereItems=new ArrayList();
        String field;
        for(int i=0;i<fields.length;i++) {
            field = StringUtil.trim(fields[i],null);
            if(StringUtil.startsWithIgnoreCase(field, "form."))
            	field=field.substring(5);
            
            if(!field.equalsIgnoreCase("fieldnames")) {
                if(ArrayUtil.indexOfIgnoreCase(keys,field)==-1) {
	                if(set.length()==0) set.append(" set ");
	                else set.append(",");
	                set.append(field);
	                set.append("=?");
	                ColumnInfo ci=(ColumnInfo) meta.get(field);
	                if(ci!=null)setItems.add(new SQLItemImpl(form.get(field,null),ci.getType())); 
	                else setItems.add(new SQLItemImpl(form.get(field,null))); 
                }
                else {
	                if(where.length()==0) where.append(" where ");
	                else where.append(" and ");
	                where.append(field);
	                where.append("=?");
	                whereItems.add(new SQLItemImpl(form.get(field,null))); 
                }
            }
        }
        if((setItems.size()+whereItems.size())==0) return null;
        
        if(whereItems.size()==0)throw new DatabaseException("can't find primary keys ["+ListUtil.arrayToList(keys,",")+"] of table ["+tablename+"] in form scope",null,null,dc);
        
        StringBuffer sql=new StringBuffer();
        sql.append("update ");
        if(tablequalifier!=null && tablequalifier.length()>0) {
            sql.append(tablequalifier);
            sql.append('.');
        }
        if(tableowner!=null && tableowner.length()>0) {
            sql.append(tableowner);
            sql.append('.');
        }
        sql.append(tablename);
        sql.append(set);
        sql.append(where);
        
        return new SQLImpl(sql.toString(),arrayMerge(setItems,whereItems));
    }

    private SQLItem[] arrayMerge(ArrayList setItems, ArrayList whereItems) {
        SQLItem[] items=new SQLItem[setItems.size()+whereItems.size()];

        int index=0;
        // Item
        int size=setItems.size();
        for(int i=0;i<size;i++) {
            items[index++]=(SQLItem) setItems.get(i);
        }
        // Where
        size=whereItems.size();
        for(int i=0;i<size;i++) {
            items[index++]=(SQLItem) whereItems.get(i);
        }
        return items;
    }
}
















