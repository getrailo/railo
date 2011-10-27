package railo.runtime.db;

import java.util.HashSet;
import java.util.Set;

import railo.runtime.sql.SQLParserException;
import railo.runtime.sql.SelectParser;
import railo.runtime.sql.Selects;
import railo.runtime.sql.exp.Column;

public class HSQLUtil2 {

	private Selects selects;

	public HSQLUtil2(SQL sql) throws SQLParserException {
		selects = new SelectParser().parse(sql.getSQLString());
	}

	public HSQLUtil2(Selects selects) {
		this.selects = selects;
	}

	public boolean isUnion() {
		return selects.getSelects().length>1;
	}

	public Set<String> getInvokedTables() {
		HashSet<String> set=new HashSet<String>();
		Column[] tables = selects.getTables();
		for(int i=0;i<tables.length;i++) {
			set.add(tables[i].getFullName());
		}		
		return set;
	}

}
