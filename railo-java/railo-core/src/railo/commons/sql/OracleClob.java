package railo.commons.sql;

import java.lang.reflect.Method;
import java.sql.Clob;
import java.sql.Connection;

import railo.commons.lang.ClassUtil;
import railo.runtime.op.Caster;

public class OracleClob {

	private static Integer duration;
	private static Integer mode;
	private static Method createTemporary;
	private static Method open;
	private static Method setString;

	public static Clob createClob(Connection conn,String value,Clob defaultValue) {
		try{
			Class clazz = ClassUtil.loadClass("oracle.sql.CLOB");
			
			// CLOB.DURATION_SESSION;
			if(duration==null)	duration = Caster.toInteger(clazz.getField("DURATION_SESSION").getInt(null));
			// CLOB.MODE_READWRITE
			if(mode==null)		mode = Caster.toInteger(clazz.getField("MODE_READWRITE").getInt(null));
 
			//CLOB c = CLOB.createTemporary(conn, false, CLOB.DURATION_SESSION);
			if(createTemporary==null || createTemporary.getDeclaringClass()!=clazz)
				createTemporary = clazz.getMethod("createTemporary", new Class[]{Connection.class,boolean.class,int.class});
			Object clob = createTemporary.invoke(null, new Object[]{conn,Boolean.FALSE,duration});
			
			// c.open(CLOB.MODE_READWRITE);
			if(open==null || open.getDeclaringClass()!=clazz)
				open = clazz.getMethod("open", new Class[]{int.class});
			open.invoke(clob, new Object[]{mode});

			//c.setString(1,value);
			if(setString==null || setString.getDeclaringClass()!=clazz)
				setString = clazz.getMethod("setString", new Class[]{long.class,String.class});
			setString.invoke(clob, new Object[]{Long.valueOf(1),value});

			return (Clob) clob;
		}
		catch(Throwable t){
			//print.printST(t);
		}
		return defaultValue;
	}

}
