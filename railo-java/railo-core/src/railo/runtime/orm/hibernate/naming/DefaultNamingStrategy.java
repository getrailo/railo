package railo.runtime.orm.hibernate.naming;

import railo.runtime.orm.naming.NamingStrategy;

public class DefaultNamingStrategy implements NamingStrategy {

	public static final NamingStrategy INSTANCE = new DefaultNamingStrategy();

	@Override
	public String convertTableName(String tableName) {
		return tableName;
	}

	@Override
	public String convertColumnName(String columnName) {
		return columnName;
	}

	@Override
	public String getType() {
		return "default";
	}

}
