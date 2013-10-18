package railo.runtime.listener;

import java.util.Locale;
import java.util.TimeZone;

import railo.runtime.db.DataSource;
import railo.runtime.exp.PageException;

// FUTURE move to ApplicationContext

public interface ApplicationContextPro extends ApplicationContext {

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

	public TimeZone getTimeZone();
	public void setTimeZone(TimeZone timeZone);
	
}
