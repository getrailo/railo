package railo.runtime.listener;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import railo.runtime.db.DataSource;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.CustomType;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.TimeSpan;

// FUTURE move to ApplicationContext

public interface ApplicationContextPro extends ApplicationContext {

	public static final short WS_TYPE_AXIS1=1;
	public static final short WS_TYPE_JAX_WS=2;
	public static final short WS_TYPE_CXF=4;
	
    public DataSource[] getDataSources();
    public DataSource getDataSource(String dataSourceName) throws PageException;
    public DataSource getDataSource(String dataSourceName, DataSource defaultValue);

    public void setDataSources(DataSource[] dataSources);
    
    /**
     * default datasource name (String) or datasource (DataSource Object)
     * @return
     */
	public Object getDefDataSource();
	/**
     * orm datasource name (String) or datasource (DataSource Object)
     * @return
     */
	public Object getORMDataSource();
	

	public void setDefDataSource(Object datasource);
	public void setORMDataSource(Object string);
	


	public abstract boolean getBufferOutput();
	public abstract void setBufferOutput(boolean bufferOutput);

	public Locale getLocale();
	public void setLocale(Locale locale);
	
	public short getScopeCascading();
	public void  setScopeCascading(short scopeCascading);

	public TimeZone getTimeZone();
	public void setTimeZone(TimeZone timeZone);

	public Charset getWebCharset();
	public void setWebCharset(Charset charset);

	public Charset getResourceCharset();
	public void setResourceCharset(Charset charset);

	public boolean getTypeChecking();
	public void setTypeChecking(boolean typeChecking);
	
	Map<Collection.Key, Map<Collection.Key, Object>> getTagAttributeDefaultValues();
	public Map<Collection.Key, Object> getTagAttributeDefaultValues(String fullName);
	public void setTagAttributeDefaultValues(Struct sct);

	public TimeSpan getRequestTimeout();
	public void setRequestTimeout(TimeSpan timeout);

	public CustomType getCustomType(String strType);

	public boolean getAllowCompression();
	public void setAllowCompression(boolean allowCompression);

	public boolean getSuppressContent();
	public void setSuppressContent(boolean suppressContent);
	


	public short getWSType();
	public void setWSType(short wstype);
	
}
