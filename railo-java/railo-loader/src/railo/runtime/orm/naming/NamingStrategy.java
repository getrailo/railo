package railo.runtime.orm.naming;

public interface NamingStrategy {

	/** 
	* Defines the table name to be used for a specified table name. The specified table name is either 
	* the table name specified in the mapping or chosen using the entity name. 
	*/ 
	public String convertTableName(String tableName); 
	 
	/** 
	* Defines the column name to be used for a specified column name. The specified column name is either 
	* the column name specified in the mapping or chosen using the property name. 
	*/ 
	public String convertColumnName(String columnName); 
	
	
	public String getType();
	
}
