package coldfusion.server;

public interface ServiceMetaData {

	public abstract int getPropertyCount();

	public abstract String getPropertyLabel(int arg0);

	public abstract String getPropertyType(int arg0);

	public abstract boolean exists(String arg0);

}