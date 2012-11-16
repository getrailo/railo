package railo.runtime.db;

public interface DataSourcePro extends DataSource {
	/**
     * @return Returns the connection string with NOT replaced placeholders.
     */
    public String getConnectionString();

    /**
     * @return Returns the connection string with replaced placeholders.
     */
    public String getConnectionStringTranslated();

    /**
     * @return unique id of the DataSource
     */
    public String id();
}
