package railo.runtime.orm.naming;

public class DefaultNamingStrategy implements NamingStrategy {

	public static final NamingStrategy INSTANCE = new DefaultNamingStrategy();

	/**
	 * @see railo.runtime.orm.NamingStrategy#convertTableName(java.lang.String)
	 */
	public String convertTableName(String tableName) {
		return tableName;
	}

	/**
	 * @see railo.runtime.orm.NamingStrategy#convertColumnName(java.lang.String)
	 */
	public String convertColumnName(String columnName) {
		return columnName;
	}

	/**
	 * @see railo.runtime.orm.NamingStrategy#getType()
	 */
	public String getType() {
		return "default";
	}

}
