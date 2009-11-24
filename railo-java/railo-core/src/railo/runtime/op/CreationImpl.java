package railo.runtime.op;

import java.io.InputStream;
import java.io.Serializable;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import railo.commons.date.DateTimeUtil;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.config.RemoteClient;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.SQL;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.spooler.ExecutionPlan;
import railo.runtime.spooler.SpoolerTask;
import railo.runtime.spooler.remote.RemoteClientTask;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.dt.Date;
import railo.runtime.type.dt.DateImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.dt.Time;
import railo.runtime.type.dt.TimeImpl;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.dt.TimeSpanImpl;
import railo.runtime.type.scope.ClusterEntry;
import railo.runtime.type.scope.ClusterEntryImpl;
import railo.runtime.util.Creation;

/**
 * implemention of the ctration object
 */
public final class CreationImpl implements Creation {

    private static CreationImpl singelton;

    /**
     * @return singleton instance
     */
    public static Creation getInstance() {
        if(singelton==null)singelton=new CreationImpl();
        return singelton;
    }

    /**
     * @see railo.runtime.util.Creation#createArray()
     */
    public Array createArray() {
        return new ArrayImpl();
    }

	/**
	 * @see railo.runtime.util.Creation#createArray(java.lang.String, java.lang.String, boolean, boolean)
	 */
	public Array createArray(String list, String delimeter,boolean removeEmptyItem, boolean trim) {
		if(removeEmptyItem)return List.listToArrayRemoveEmpty(list, delimeter);
		if(trim)return List.listToArrayTrim(list, delimeter);
		return List.listToArray(list, delimeter);
	}
	
    /**
     * @see railo.runtime.util.Creation#createArray(int)
     */
    public Array createArray(int dimension) throws PageException {
        return new ArrayImpl(dimension);
    }

    /**
     * @see railo.runtime.util.Creation#createStruct()
     */
    public Struct createStruct() {
        return new StructImpl();
    }

    /**
     * @see railo.runtime.util.Creation#createStruct(int)
     */
    public Struct createStruct(int type) {
        return new StructImpl(type);
    }

    /**
     * @see railo.runtime.util.Creation#createQuery(java.lang.String[], int, java.lang.String)
     */
    public Query createQuery(String[] columns, int rows, String name) {
        return new QueryImpl(columns,rows,name);
    }
    
    /**
     *
     * @see railo.runtime.util.Creation#createQuery(railo.runtime.db.DatasourceConnection, railo.runtime.db.SQL, int, java.lang.String)
     */
    public Query createQuery(DatasourceConnection dc, SQL sql, int maxrow, String name) throws PageException {
		return new QueryImpl(dc,sql,maxrow,-1,-1,name);
	}
    
    public Query createQuery(DatasourceConnection dc, SQL sql, int maxrow, int fetchsize, int timeout, String name) throws PageException {
		return new QueryImpl(dc,sql,maxrow,fetchsize,timeout,name);
	}
    
    /**
     * @see railo.runtime.util.Creation#createDateTime(long)
     */
    public DateTime createDateTime(long time) {
        return new DateTimeImpl(time,false);
    }

    /**
     * @see railo.runtime.util.Creation#createTimeSpan(int, int, int, int)
     */
    public TimeSpan createTimeSpan(int day,int hour,int minute,int second) {
        return new TimeSpanImpl(day,hour,minute,second);
    }

    /**
     * @see railo.runtime.util.Creation#createDate(long)
     */
    public Date createDate(long time) {
        return new DateImpl(time);
    }

    /**
     * @see railo.runtime.util.Creation#createTime(long)
     */
    public Time createTime(long time) {
        return new TimeImpl(time,false);
    }

    /**
     * @see railo.runtime.util.Creation#createDateTime(int, int, int, int, int, int, int)
     */
    public DateTime createDateTime(int year, int month, int day, int hour, int minute, int second, int millis) throws ExpressionException {
        return DateTimeUtil.getInstance().toDateTime(ThreadLocalPageContext.getTimeZone(),year,month,day,hour,minute,second,millis);
    }

    /**
     * @see railo.runtime.util.Creation#createDate(int, int, int)
     */
    public Date createDate(int year, int month, int day) throws ExpressionException {
        return new DateImpl(DateTimeUtil.getInstance().toDateTime(null,year,month,day, 0, 0, 0,0));
    }

    /**
     * @see railo.runtime.util.Creation#createTime(int, int, int, int)
     */
    public Time createTime(int hour, int minute, int second, int millis) {
        return new TimeImpl(
        		DateTimeUtil.getInstance().toTime(null,1899,12,30,hour,minute,second,millis,0),false);
    }

    /**
     * @see railo.runtime.util.Creation#createDocument()
     */
    public Document createDocument() throws PageException {
        try {
            return XMLUtil.newDocument();
        } catch (Exception e) {
            throw Caster.toPageException(e);
        }
    }

    /**
     * @see railo.runtime.util.Creation#createDocument(railo.commons.io.res.Resource, boolean)
     */
    public Document createDocument(Resource res, boolean isHTML) throws PageException {
        InputStream is=null;
    	try {
            return XMLUtil.parse(new InputSource(is=res.getInputStream()),null,isHTML);
        } catch (Exception e) {
            throw Caster.toPageException(e);
        }
        finally {
        	IOUtil.closeEL(is);
        }
    }

    /**
     * @see railo.runtime.util.Creation#createDocument(java.lang.String, boolean)
     */
    public Document createDocument(String xml, boolean isHTML) throws PageException {
        try {
            return XMLUtil.parse(XMLUtil.toInputSource(null, xml),null,isHTML);
        } catch (Exception e) {
            throw Caster.toPageException(e);
        }
    }

    /**
     * @see railo.runtime.util.Creation#createDocument(java.io.InputStream, boolean)
     */
    public Document createDocument(InputStream is, boolean isHTML) throws PageException {
        try {
            return XMLUtil.parse(new InputSource(is),null,isHTML);
        } catch (Exception e) {
            throw Caster.toPageException(e);
        }
    }

	/**
	 * @see railo.runtime.util.Creation#createKey(java.lang.String)
	 */
	public Key createKey(String key) {
		return KeyImpl.init(key);
	}

	public SpoolerTask createRemoteClientTask(ExecutionPlan[] plans,RemoteClient remoteClient,Struct attrColl,String callerId, String type) {
		return new RemoteClientTask(plans,remoteClient,attrColl,callerId, type);
	}

	public ClusterEntry createClusterEntry(Key key,Serializable value, int offset) {
		return new ClusterEntryImpl(key,value,offset);
	}

	public Resource createResource(String path, boolean existing) throws PageException {
		if(existing)return ResourceUtil.toResourceExisting(ThreadLocalPageContext.get(), path);
		return ResourceUtil.toResourceNotExisting(ThreadLocalPageContext.get(), path);
	}


}
