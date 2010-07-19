package railo.runtime.orm.hibernate;

public class HibernateConstants {

	public static final int CASCADE_NONE = 0;
	public static final int CASCADE_ALL = 1;
	public static final int CASCADE_SAVE_UPDATE = 2;
	public static final int CASCADE_DELETE = 4;
	public static final int CASCADE_DELETE_ORPHAN = 8;
	public static final int CASCADE_ALL_DELETE_ORPHAN = 16;
	public static final int REFRESH = 32;

	public static final int COLLECTION_TYPE_ARRAY = 1;
	public static final int COLLECTION_TYPE_STRUCT = 2;
	
}
