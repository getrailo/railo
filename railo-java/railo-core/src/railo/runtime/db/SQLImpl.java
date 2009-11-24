package railo.runtime.db;

import java.io.Serializable;




/**
 * represents a SQL Statement with his defined arguments for a prepared statement
 */
public final class SQLImpl implements SQL,Serializable {
    
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
    
    /**
     * @see railo.runtime.db.SQL#getItems()
     */
    public void addItems(SQLItem item) {
    	SQLItem[] tmp=new SQLItem[items.length+1];
    	for(int i=0;i<items.length;i++) {
    		tmp[i]=items[i];
    	}
    	tmp[items.length]=item;
    	items=tmp;
    }
    
    /**
     * @see railo.runtime.db.SQL#getItems()
     */
    public SQLItem[] getItems() {
        return items;
    }

    /**
     * @see railo.runtime.db.SQL#getPosition()
     */
    public int getPosition() {
        return position;
    }
    
    /**
     * @see railo.runtime.db.SQL#setPosition(int)
     */
    public void setPosition(int position) {
        this.position = position;
    }    
    

    /**
     * @see railo.runtime.db.SQL#getSQLString()
     */
    public String getSQLString() {
        return strSQL;
    }
    
    /**
     * @see railo.runtime.db.SQL#setSQLString(java.lang.String)
     */
    public void setSQLString(String strSQL) {
        this.strSQL= strSQL;
    }

    /**
     * @see java.lang.Object#toString()
     */
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
            sb.append(SQLCaster.toString(items[i]));
            last=pos+1;
        }
        sb.append(strSQL.substring(last));
        return sb.toString();
    }    
    
    /**
     * @see railo.runtime.db.SQL#toHashString()
     */
    public String toHashString() {
        if(items.length==0) return strSQL;
        StringBuffer sb=new StringBuffer(strSQL);
        for(int i=0;i<items.length;i++) {
            sb.append(items[i].toString());
        }
        return sb.toString();
    }

}