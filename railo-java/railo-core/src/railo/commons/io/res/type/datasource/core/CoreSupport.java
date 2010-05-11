package railo.commons.io.res.type.datasource.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;
import java.util.WeakHashMap;

import railo.commons.date.JREDateTimeUtil;
import railo.commons.io.res.type.datasource.Attr;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.engine.ThreadLocalPageContext;

public abstract class CoreSupport implements Core {
	public static final Attr ATTR_ROOT = new Attr(0,null,null,true,Attr.TYPE_DIRECTORY,0,0,(short)0777,(short)0,0);
	

	public static boolean isDirectory(int type) {
		return type==Attr.TYPE_DIRECTORY;
	}

	public static boolean isFile(int type) {
		return type==Attr.TYPE_FILE;
	}

	public static boolean isLink(int type) {
		return type==Attr.TYPE_LINK;
	}

	public static Calendar getCalendar() {
		return JREDateTimeUtil.newInstance(ThreadLocalPageContext.getTimeZone());
	}

	public static void log(String s1) {
		//aprint.out(s1);
	}
	public static void log(String s1, String s2) {
		//aprint.out(s1+";"+s2);
	}
	public static void log(String s1, String s2, String s3) {
		//aprint.out(s1+";"+s2+";"+s3);
	}
	public static void log(String s1, String s2, String s3, String s4) {
		//aprint.out(s1+";"+s2+";"+s3+";"+s4);
	}

	private Map statements=new WeakHashMap();
	
	PreparedStatement prepareStatement(DatasourceConnection dc,String sql) throws SQLException {
		PreparedStatement ps=(PreparedStatement) statements.get(sql);
		if(ps!=null && ps.getConnection()==dc.getConnection()){
			//print.out("c");
			
		}
		else{
			ps= dc.getConnection().prepareStatement(sql);
			statements.put(sql,ps);
			//print.out("n");
		}
		return ps;
	}

}
