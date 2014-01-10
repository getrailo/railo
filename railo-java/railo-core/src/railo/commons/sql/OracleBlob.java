package railo.commons.sql;

import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Connection;

import railo.commons.lang.ClassUtil;
import railo.runtime.op.Caster;

public class OracleBlob {

	private static Integer duration;
	private static Integer mode;
	private static Method createTemporary;
	private static Method open;
	private static Method setBytes;

	public static Blob createBlob(Connection conn,byte[] barr,Blob defaultValue) {
		try{
			Class clazz = ClassUtil.loadClass("oracle.sql.BLOB");
			
			// BLOB.DURATION_SESSION
			if(duration==null)	duration = Caster.toInteger(clazz.getField("DURATION_SESSION").getInt(null));
			// BLOB.MODE_READWRITE
			if(mode==null)		mode = Caster.toInteger(clazz.getField("MODE_READWRITE").getInt(null));
 
			//BLOB blob = BLOB.createTemporary(conn, false, BLOB.DURATION_SESSION);
			if(createTemporary==null || createTemporary.getDeclaringClass()!=clazz)
				createTemporary = clazz.getMethod("createTemporary", new Class[]{Connection.class,boolean.class,int.class});
			Object blob = createTemporary.invoke(null, new Object[]{conn,Boolean.FALSE,duration});
			
			//blob.open(BLOB.MODE_READWRITE);
			if(open==null || open.getDeclaringClass()!=clazz)
				open = clazz.getMethod("open", new Class[]{int.class});
			open.invoke(blob, new Object[]{mode});

			//blob.setBytes(1,barr);
			if(setBytes==null || setBytes.getDeclaringClass()!=clazz)
				setBytes = clazz.getMethod("setBytes", new Class[]{long.class,byte[].class});
			setBytes.invoke(blob, new Object[]{Long.valueOf(1),barr});

			return (Blob) blob;
		}
		catch(Throwable t){
			//print.printST(t);
		}
		return defaultValue;
	}

}
