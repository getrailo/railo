package railo.runtime.db;

import java.io.Serializable;

import railo.commons.lang.SizeOf;
import railo.runtime.type.Sizeable;




/**
 * represents a SQL Statement with his defined arguments for a prepared statement
 */
public final class SQLImpl implements SQL,Serializable,Sizeable {
    
    private String strSQL;
    private SQLItem[] items;
    private int position=0;

    /**
     * Constructor only with SQL String
     * @param strSQL SQL String
     */
    public SQLImpl(String strSQL) {
        this.strSQL=strSQL;
        this.items=new SQLItem[0];
    }
    
    /**
     * Constructor with SQL String and SQL Items
     * @param strSQL SQL String
     * @param items SQL Items
     */
    public SQLImpl(String strSQL, SQLItem[] items) {
        this.strSQL=strSQL;
        this.items=items;
    }
    
    public void addItems(SQLItem item) {
    	SQLItem[] tmp=new SQLItem[items.length+1];
    	for(int i=0;i<items.length;i++) {
    		tmp[i]=items[i];
    	}
    	tmp[items.length]=item;
    	items=tmp;
    }
    
    @Override
    public SQLItem[] getItems() {
        return items;
    }

    @Override
    public int getPosition() {
        return position;
    }
    
    @Override
    public void setPosition(int position) {
        this.position = position;
    }    
    

    @Override
    public String getSQLString() {
        return strSQL;
    }
    
    @Override
    public void setSQLString(String strSQL) {
        this.strSQL= strSQL;
    }

    @Override
    public String toString() {
        if(items.length==0) return strSQL;
        StringBuffer sb=new StringBuffer();
        int pos;
        int last=0;
        for(int i=0;i<items.length;i++) {
            pos=strSQL.indexOf('?',last);
            if(pos==-1) {
                sb.append(strSQL.substring(last));
                break;
            }
            sb.append(strSQL.substring(last,pos));
            if(items[i].isNulls()) sb.append("null");
            else sb.append(SQLCaster.toString(items[i]));
            last=pos+1;
        }
        sb.append(strSQL.substring(last));
        return sb.toString();
    }    
    
    @Override
    public String toHashString() {
        if(items.length==0) return strSQL;
        StringBuffer sb=new StringBuffer(strSQL);
        for(int i=0;i<items.length;i++) {
            sb.append(';').append(items[i].toString());
        }
        return sb.toString();
    }

	public long sizeOf() {
		return SizeOf.size(strSQL)+SizeOf.size(position)+SizeOf.size(items);
	}

	public static SQL duplicate(SQL sql) {
		if(!(sql instanceof SQLImpl)) return sql;
		return ((SQLImpl) sql).duplicate();
	}

	public SQL duplicate() {
		SQLImpl rtn=new SQLImpl(strSQL);
		rtn.position=position;
		rtn.items=new SQLItem[items.length];
		for(int i=0;i<items.length;i++){
			rtn.items[i]=SQLItemImpl.duplicate(items[i]);
		}
		
		return rtn;
	}

}