package railo.runtime.op;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import railo.commons.date.DateTimeUtil;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.Pair;
import railo.loader.engine.CFMLEngine;
import railo.runtime.CFMLFactory;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.RemoteClient;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.SQL;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.struct.StructNew;
import railo.runtime.functions.system.ContractPath;
import railo.runtime.net.http.HttpServletRequestDummy;
import railo.runtime.net.http.HttpServletResponseDummy;
import railo.runtime.spooler.ExecutionPlan;
import railo.runtime.spooler.SpoolerTask;
import railo.runtime.spooler.remote.RemoteClientTask;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
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
import railo.runtime.type.util.ListUtil;
import railo.runtime.util.Creation;

/**
 * implemention of the ctration object
 */
public final class CreationImpl implements Creation,Serializable {

    private static CreationImpl singelton;

    private CreationImpl(CFMLEngine engine) {
    	// !!! do not store engine Object, the engine is not serializable
	}

	/**
     * @return singleton instance
     */
    public static Creation getInstance(CFMLEngine engine) { 
        if(singelton==null)singelton=new CreationImpl(engine);
        return singelton;
    }

    @Override
    public Array createArray() {
        return new ArrayImpl();
    }

	@Override
	public Array createArray(String list, String delimiter,boolean removeEmptyItem, boolean trim) {
		if(removeEmptyItem)return ListUtil.listToArrayRemoveEmpty(list, delimiter);
		if(trim)return ListUtil.listToArrayTrim(list, delimiter);
		return ListUtil.listToArray(list, delimiter);
	}
	
    @Override
    public Array createArray(int dimension) throws PageException {
        return new ArrayImpl(dimension);
    }

    @Override
    public Struct createStruct() {
        return new StructImpl();
    }

    @Override
    public Struct createStruct(int type) {
        return new StructImpl(type);
    }

    @Override
    public Struct createStruct(String type) throws ApplicationException {
    	return new StructImpl(StructNew.toType(type));
    }

    @Override
    public Query createQuery(String[] columns, int rows, String name) {
        return new QueryImpl(columns,rows,name);
    }

    @Override
    public Query createQuery(Collection.Key[] columns, int rows, String name) throws DatabaseException {
        return new QueryImpl(columns,rows,name);
    }
    
    public Query createQuery(DatasourceConnection dc, SQL sql, int maxrow, int fetchsize, int timeout, String name) throws PageException {
		return new QueryImpl(ThreadLocalPageContext.get(),dc,sql,maxrow,fetchsize,timeout,name);
	}
    
    @Override
    public DateTime createDateTime(long time) {
        return new DateTimeImpl(time,false);
    }

    @Override
    public TimeSpan createTimeSpan(int day,int hour,int minute,int second) {
        return new TimeSpanImpl(day,hour,minute,second);
    }

    @Override
    public Date createDate(long time) {
        return new DateImpl(time);
    }

    @Override
    public Time createTime(long time) {
        return new TimeImpl(time,false);
    }

    @Override
    public DateTime createDateTime(int year, int month, int day, int hour, int minute, int second, int millis) throws ExpressionException {
        return DateTimeUtil.getInstance().toDateTime(ThreadLocalPageContext.getTimeZone(),year,month,day,hour,minute,second,millis);
    }

    @Override
    public Date createDate(int year, int month, int day) throws ExpressionException {
        return new DateImpl(DateTimeUtil.getInstance().toDateTime(null,year,month,day, 0, 0, 0,0));
    }

    @Override
    public Time createTime(int hour, int minute, int second, int millis) {
        return new TimeImpl(
        		DateTimeUtil.getInstance().toTime(null,1899,12,30,hour,minute,second,millis,0),false);
    }

    @Override
    public Document createDocument() throws PageException {
        try {
            return XMLUtil.newDocument();
        } catch (Exception e) {
            throw Caster.toPageException(e);
        }
    }

    @Override
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

    @Override
    public Document createDocument(String xml, boolean isHTML) throws PageException {
        try {
            return XMLUtil.parse(XMLUtil.toInputSource(null, xml),null,isHTML);
        } catch (Exception e) {
            throw Caster.toPageException(e);
        }
    }

    @Override
    public Document createDocument(InputStream is, boolean isHTML) throws PageException {
        try {
            return XMLUtil.parse(new InputSource(is),null,isHTML);
        } catch (Exception e) {
            throw Caster.toPageException(e);
        }
    }

	@Override
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

	public HttpServletRequest createHttpServletRequest(File contextRoot,String serverName, String scriptName,String queryString, 
			Cookie[] cookies, Map<String,Object> headers, Map<String, String> parameters, Map<String,Object> attributes, HttpSession session) {

		// header
		Pair<String,Object>[] _headers=new Pair[headers.size()];
		{
			int index=0;
			Iterator<Entry<String, Object>> it = headers.entrySet().iterator();
			Entry<String, Object> entry;
			while(it.hasNext()){
				entry = it.next();
				_headers[index++]=new Pair<String,Object>(entry.getKey(), entry.getValue());
			}
		}
		// parameters
		Pair<String,Object>[] _parameters=new Pair[headers.size()];
		{
			int index=0;
			Iterator<Entry<String, String>> it = parameters.entrySet().iterator();
			Entry<String, String> entry;
			while(it.hasNext()){
				entry = it.next();
				_parameters[index++]=new Pair<String,Object>(entry.getKey(), entry.getValue());
			}
		}
		
		return new HttpServletRequestDummy(ResourceUtil.toResource(contextRoot), serverName, scriptName, queryString, cookies,
				_headers, _parameters, Caster.toStruct(attributes,null), session);
	}

	public HttpServletResponse createHttpServletResponse(OutputStream io) {
		return new HttpServletResponseDummy(io);
	}

	@Override
	public PageContext createPageContext(HttpServletRequest req, HttpServletResponse rsp, OutputStream out) {
		Config config = ThreadLocalPageContext.getConfig();
		if(!(config instanceof ConfigWeb)) throw new RuntimeException("need a web context to create a PageContext");
		CFMLFactory factory = ((ConfigWeb)config).getFactory();
		
		return (PageContext) factory.getPageContext(factory.getServlet(), req, rsp, null, false, -1, false);
	}

	@Override
	public Component createComponentFromName(PageContext pc, String fullName) throws PageException {
		return pc.loadComponent(fullName);
	}

	@Override
	public Component createComponentFromPath(PageContext pc, String path) throws PageException {	
		path=path.trim();
		String pathContracted=ContractPath.call(pc, path);
    	
		if(pathContracted.toLowerCase().endsWith(".cfc"))
			pathContracted=pathContracted.substring(0,pathContracted.length()-4);
		
    	pathContracted=pathContracted
			.replace(File.pathSeparatorChar, '.')
			.replace('/', '.')
			.replace('\\', '.');
    	
    	while(pathContracted.toLowerCase().startsWith("."))
			pathContracted=pathContracted.substring(1);
    	
		return createComponentFromName(pc, pathContracted);
	}


}
