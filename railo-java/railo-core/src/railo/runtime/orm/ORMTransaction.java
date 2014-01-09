package railo.runtime.orm;
//FUTURE add to loader
public interface ORMTransaction {

	public void begin();
	public void commit();
	public void rollback();
	public void end();

}
