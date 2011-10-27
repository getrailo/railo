package railo.runtime.orm.naming;

import railo.commons.lang.StringUtil;

public class SmartNamingStrategy implements NamingStrategy {
	
	public static final NamingStrategy INSTANCE = new SmartNamingStrategy();

	/**
	 * @see railo.runtime.orm.NamingStrategy#convertTableName(java.lang.String)
	 */
	public String convertTableName(String tableName) {
        return translate(tableName);
    }

    /**
     * @see railo.runtime.orm.NamingStrategy#convertColumnName(java.lang.String)
     */
    public String convertColumnName(String columnName) { 
        return translate(columnName);
    }

    private static String translate(String name) {
    	if(StringUtil.isEmpty(name)) return "";
        
    	int len=name.length();
    	StringBuilder sb = new StringBuilder();
    	char c,p,n;
        for(int i=0;i<len;i++) {
        	c=name.charAt(i);
        	if(i==0 || i+1==len) {
        		sb.append(Character.toUpperCase(c));
        		 continue;
        	}
        	p=name.charAt(i-1);
        	n=name.charAt(i+1);
            
            // is Camel
        	if(Character.isLowerCase(p) && Character.isUpperCase(c) && Character.isLowerCase(n)) {
        		sb.append('_');
        		sb.append(Character.toUpperCase(c));
        		sb.append(Character.toUpperCase(n));
        		i++;
        	}
        	else
        		sb.append(Character.toUpperCase(c));
        }
        return sb.toString();
    }

	/**
	 * @see railo.runtime.orm.NamingStrategy#getType()
	 */
	public String getType() {
		return "smart";
	}

}
